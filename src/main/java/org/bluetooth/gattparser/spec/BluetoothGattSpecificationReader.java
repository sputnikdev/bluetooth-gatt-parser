package org.bluetooth.gattparser.spec;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bluetooth GATT specification reader. Capable of reading Bluetooth GATT specifications for services and characteristics
 * https://www.bluetooth.com/specifications/gatt.
 * Stateful but threadsafe.
 *
 */
public class BluetoothGattSpecificationReader {

    private final Logger logger = LoggerFactory.getLogger(BluetoothGattSpecificationReader.class);

    private static FilenameFilter XML_FILE_FILTER = new FilenameFilter() {
        @Override public boolean accept(File dir, String name) {
            return name.toLowerCase().endsWith(".xml");
        }
    };

    private Map<String, Service> services = new HashMap<>();
    private Map<String, Characteristic> characteristics = new HashMap<>();


    public BluetoothGattSpecificationReader() {
        loadFromClassPath();
        loadExtensionsFromClassPath();
    }

    public Service getService(String uid) {
        return services.get(uid);
    }

    public Characteristic getCharacteristic(String uid) {
        return characteristics.get(uid);
    }

    public Collection<Characteristic> getCharacteristics() {
        return new ArrayList<>(characteristics.values());
    }

    public Collection<Service> getServices() {
        return new ArrayList<>(services.values());
    }


    private void loadFromClassPath() {
        ClassLoader classLoader = getClass().getClassLoader();
        readServices(getFilesFromFolder(classLoader.getResource("gatt/service")));
        readCharacteristics(getFilesFromFolder(classLoader.getResource("gatt/characteristic")));
    }

    private void loadExtensionsFromClassPath() {
        ClassLoader classLoader = getClass().getClassLoader();
        readServices(getFilesFromFolder(classLoader.getResource("ext/gatt/service")));
        readCharacteristics(getFilesFromFolder(classLoader.getResource("ext/gatt/characteristic")));
    }

    private void addCharacteristic(Characteristic characteristic) {
        validate(characteristic);
        characteristics.put(characteristic.getUuid(), characteristic);
    }

    private void addService(Service service) {
        services.put(service.getUuid(), service);
    }

    private void validate(Characteristic characteristic) {
        Set<String> flags = getFlags(characteristic);
        Set<String> requirements = getRequirements(characteristic);
        requirements.removeAll(flags);
        if (requirements.isEmpty()) {
            characteristic.setValidForRead(true);
        } else {
            logger.warn("Characteristic \"{}\" is not valid for read operations due to unfulfilled requirements: {}.",
                    characteristic.getName(), requirements);
            characteristic.setValidForRead(false);
        }
    }

    private List<URL> getFilesFromFolder(URL folder) {
        if (folder == null) {
            return Collections.emptyList();
        }
        URL serviceRegistry = getClass().getClassLoader().getResource(folder.getPath() + "gatt_spec_files.txt");
        if (serviceRegistry != null) {
            return getFiles(folder, serviceRegistry);
        } else {
            return getAllFiles(folder);
        }
    }

    private List<URL> getFiles(URL rootFolder, URL fileList) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            List<URL> files = new ArrayList<>();
            String rootPath = rootFolder.getPath();
            String content = new Scanner(fileList.openStream(), "UTF-8").useDelimiter("\\A").next();
            for (String fileName : content.split("\\r?\\n")) {
                URL file = classLoader.getResource(rootPath + fileName.trim());
                if (file != null) {
                    files.add(file);
                }
            }
            return files;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private List<URL> getAllFiles(URL rootFolder) {
        try {
            List<URL> files = new ArrayList<>();
            for (File file : new File(rootFolder.toURI()).listFiles(XML_FILE_FILTER)) {
                files.add(file.toURI().toURL());
            }
            return files;
        } catch (URISyntaxException | MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }

    private void readServices(List<URL> files) {
        for (URL file : files) {
            Service service = getService(file);
            if (service != null) {
                addService(service);
            }
        }
    }

    private void readCharacteristics(List<URL> files) {
        for (URL file : files) {
            Characteristic characteristic = getCharacteristic(file);
            if (characteristic != null) {
                addCharacteristic(characteristic);
            }
        }
    }

    private void readCharacteristic(URL file) {
        Characteristic characteristic = getCharacteristic(file);
        if (characteristic != null) {
            addCharacteristic(characteristic);
        }
    }

    private void readService(URL file) {
        Service service = getService(file);
        if (service != null) {
            addService(service);
        }
    }


    private Service getService(URL file) {
        return getSpec(file);
    }

    private Characteristic getCharacteristic(URL file) {
        return getSpec(file);
    }

    private <T> T getSpec(URL file) {
        try {
            XStream xstream = new XStream(new DomDriver());
            xstream.autodetectAnnotations(true);
            xstream.processAnnotations(Bit.class);
            xstream.processAnnotations(BitField.class);
            xstream.processAnnotations(Characteristic.class);
            xstream.processAnnotations(Enumeration.class);
            xstream.processAnnotations(Enumerations.class);
            xstream.processAnnotations(Field.class);
            xstream.processAnnotations(InformativeText.class);
            xstream.processAnnotations(Service.class);
            xstream.processAnnotations(Value.class);
            xstream.processAnnotations(Reserved.class);
            xstream.processAnnotations(Examples.class);
            xstream.processAnnotations(CharacteristicAccess.class);
            xstream.processAnnotations(Characteristics.class);
            xstream.processAnnotations(Properties.class);
            xstream.ignoreUnknownElements();
            xstream.setClassLoader(Characteristic.class.getClassLoader());
            return (T) xstream.fromXML(file);
        } catch (Exception e) {
            logger.error("Could not read file: " + file, e);
        }
        return null;
    }

    Set<String> getFlags(Characteristic characteristic) {
        Set<String> result = new HashSet<>();
        if (characteristic.getValue() != null && characteristic.getValue().getFlags() != null) {
            Field flags = characteristic.getValue().getFlags();
            for (Bit bit : flags.getBitField().getBits()) {
                for (Enumeration enumeration : bit.getEnumerations().getEnumerations()) {
                    if (enumeration.getRequires() != null) {
                        result.add(enumeration.getRequires());
                    }
                }
            }
        }
        return result;
    }

    Set<String> getRequirements(Characteristic characteristic) {
        Set<String> result = new HashSet<>();
        if (characteristic.getValue() == null || characteristic.getValue().getFields() == null) {
            logger.warn("Characteristic \"{}\" does not have either Value or Fields tags, "
                    + "therefore reading the such characteristic will not be possible.", characteristic.getName());
            return result;
        }
        for (Iterator<Field> iterator = characteristic.getValue().getFields().iterator(); iterator.hasNext();) {
            Field field = iterator.next();
            if (field.getBitField() != null) {
                continue;
            }
            List<String> requirements = field.getRequirements();
            if (requirements == null || requirements.isEmpty()) {
                continue;
            }
            if (requirements.contains("Mandatory")) {
                continue;
            }
            if (requirements.size() == 1 && requirements.contains("Optional") && !iterator.hasNext()) {
                continue;
            }
            result.addAll(requirements);
        }
        return result;
    }

}
