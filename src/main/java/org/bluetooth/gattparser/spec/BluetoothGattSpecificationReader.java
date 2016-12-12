package org.bluetooth.gattparser.spec;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class BluetoothGattSpecificationReader {

    private static FilenameFilter XML_FILE_FILTER = new FilenameFilter() {
        @Override public boolean accept(File dir, String name) {
            return name.toLowerCase().endsWith(".xml");
        }
    };

    private Map<String, Service> services;
    private Map<String, Characteristic> characteristics;

    BluetoothGattSpecificationReader(java.util.Enumeration<URL> services, java.util.Enumeration<URL> characteristics) {
        load(services, characteristics);
    }

    BluetoothGattSpecificationReader(File[] services, File[] characteristics) {
        load(services, characteristics);
    }

    BluetoothGattSpecificationReader() {
        loadFromClassPath();
    }

    BluetoothGattSpecificationReader(File servicesDir, File characteristicDir) {
        this(servicesDir.listFiles(XML_FILE_FILTER), characteristicDir.listFiles(XML_FILE_FILTER));
    }

    private void load(File[] services, File[] characteristics) {
        load(getEnumeration(services), getEnumeration(characteristics));
    }

    private void load(File servicesDir, File characteristicDir) {
        load(servicesDir.listFiles(XML_FILE_FILTER), characteristicDir.listFiles(XML_FILE_FILTER));
    }

    private void load(java.util.Enumeration<URL> services, java.util.Enumeration<URL> characteristics) {
        this.services = Collections.unmodifiableMap(readServiceSpecs(services));
        this.characteristics = Collections.unmodifiableMap(readCharacteristicSpecs(characteristics));
    }

    private void loadFromClassPath() {
        load(new File(getClass().getClassLoader().getResource("gatt/service").getFile()),
                new File(getClass().getClassLoader().getResource("gatt/characteristic").getFile()));
    }

    public Service getService(String uid) {
        return services.get(uid);
    }

    public Characteristic getCharacteristic(String uid) {
        return characteristics.get(uid);
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

    private Map<String, Service> readServiceSpecs(java.util.Enumeration<URL> files) {
        final Map<String, Service> services = new HashMap<>();
        while (files.hasMoreElements()) {
            URL file = files.nextElement();
            Service service = getService(file);
            if (service != null) {
                services.put(service.getUuid(), service);
            }
        }
        return services;
    }

    private Map<String, Characteristic> readCharacteristicSpecs(java.util.Enumeration<URL> files) {
        final Map<String, Characteristic> characteristics = new HashMap<>();
        while (files.hasMoreElements()) {
            URL file = files.nextElement();
            Characteristic characteristic = getCharacteristic(file);
            if (characteristic != null) {
                characteristics.put(characteristic.getUuid(), characteristic);
            }
        }
        return characteristics;
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
            xstream.ignoreUnknownElements();
            xstream.setClassLoader(Characteristic.class.getClassLoader());
            return (T) xstream.fromXML(file);
        } catch (Exception e) {
            //TODO logging
        }
        return null;
    }

}
