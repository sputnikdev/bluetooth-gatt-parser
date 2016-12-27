package org.bluetooth.gattparser.spec;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

    public BluetoothGattSpecificationReader(java.util.Enumeration<URL> services,
            java.util.Enumeration<URL> characteristics) {
        load(services, characteristics);
    }

    public BluetoothGattSpecificationReader(File[] services, File[] characteristics) {
        load(services, characteristics);
    }

    public BluetoothGattSpecificationReader() {
        loadFromClassPath();
        loadExtensionsFromClassPath();
    }

    public BluetoothGattSpecificationReader(File servicesDir, File characteristicDir) {
        this(servicesDir.listFiles(XML_FILE_FILTER), characteristicDir.listFiles(XML_FILE_FILTER));
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

    private void load(File[] services, File[] characteristics) {
        load(getEnumeration(services), getEnumeration(characteristics));
    }

    private void load(File servicesDir, File characteristicDir) {
        load(servicesDir.listFiles(XML_FILE_FILTER), characteristicDir.listFiles(XML_FILE_FILTER));
    }

    private void loadServices(File servicesDir) {
        readServiceSpecs(getEnumeration(servicesDir.listFiles(XML_FILE_FILTER)));
    }
    private void loadCharacteristics(File characteristicDir) {
        readCharacteristicSpecs(getEnumeration(characteristicDir.listFiles(XML_FILE_FILTER)));
    }

    private synchronized void load(java.util.Enumeration<URL> services, java.util.Enumeration<URL> characteristics) {
        readServiceSpecs(services);
        readCharacteristicSpecs(characteristics);
    }

    private void loadFromClassPath() {
        load(new File(getClass().getClassLoader().getResource("gatt/service").getFile()),
                new File(getClass().getClassLoader().getResource("gatt/characteristic").getFile()));
    }

    private void loadExtensionsFromClassPath() {
        URL extService = getClass().getClassLoader().getResource("ext/gatt/service");
        if (extService != null) {
            loadServices(new File(extService.getFile()));
        }
        URL extCharacteristic = getClass().getClassLoader().getResource("ext/gatt/characteristic");
        if (extCharacteristic != null) {
            loadCharacteristics(new File(extCharacteristic.getFile()));
        }
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

    private java.util.Enumeration<URL> getEnumeration(File[] files) {
        try {
            List<URL> urls = new ArrayList<>(files.length);
            for (File file : files) {
                urls.add(file.toURI().toURL());
            }
            return Collections.enumeration(urls);
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }

    private void readServiceSpecs(java.util.Enumeration<URL> files) {
        while (files.hasMoreElements()) {
            URL file = files.nextElement();
            Service service = getService(file);
            if (service != null) {
                addService(service);
            }
        }
    }

    private void readCharacteristicSpecs(java.util.Enumeration<URL> files) {
        while (files.hasMoreElements()) {
            URL file = files.nextElement();
            Characteristic characteristic = getCharacteristic(file);
            if (characteristic != null) {
                addCharacteristic(characteristic);
            }
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
