package org.bluetooth.gattparser;

import java.net.URL;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class GattSpecificationService {

    private Map<String, Service> services;
    private Map<String, Characteristic> characteristics;

//    GattSpecificationService(Bundle bundle) {
//        this.services = Collections.unmodifiableMap(readServiceSpecs(bundle));
//        this.characteristics = Collections.unmodifiableMap(readCharacteristicSpecs(bundle));
//    }

    public Service getService(String uid) {
        return services.get(uid);
    }

    public Characteristic getCharacteristic(String uid) {
        return characteristics.get(uid);
    }

    public Map<String, Object> getFieldValues(String uid, byte[] raw) {
        Map<String, Object> result = new HashMap<>();
        Characteristic characteristic = getCharacteristic(uid);

        int offset = 0;
        for (Field field : characteristic.getValue().getFields()) {

            String formatName = field.getFormat();
            FieldFormat fieldFormat = FieldFormat.getFieldFormat(formatName);
            int size = FieldFormat.getSize(formatName);
            if (size == 0) {
                size = raw.length - offset;
            }
            result.put(field.getName(), getFieldValue(fieldFormat, raw, offset, size));
            offset += size;
        }
        return result;
    }

    private Object getFieldValue(FieldFormat fieldFormat, byte[] raw, int offset, int size) {
        BitSet bitSet = BitSet.valueOf(raw);
        bitSet = bitSet.get(offset, size);
        return fieldFormat.getValue(bitSet);
    }

//    private Map<String, Service> readServiceSpecs(Bundle bundle) {
//        final Map<String, Service> services = new HashMap<>();
//        Enumeration<URL> files = bundle.findEntries("gatt/service", "*.xml", true);
//        while (files.hasMoreElements()) {
//            URL file = files.nextElement();
//            Service service = getService(file);
//            if (service != null) {
//                services.put(service.getUuid(), service);
//            }
//        }
//        return services;
//    }
//
//    private Map<String, Characteristic> readCharacteristicSpecs(Bundle bundle) {
//        final Map<String, Characteristic> characteristics = new HashMap<>();
//        Enumeration<URL> files = bundle.findEntries("gatt/characteristic", "*.xml", true);
//        while (files.hasMoreElements()) {
//            URL file = files.nextElement();
//            Characteristic characteristic = getCharacteristic(file);
//            if (characteristic != null) {
//                characteristics.put(characteristic.getUuid(), characteristic);
//            }
//        }
//        return characteristics;
//    }


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
            xstream.processAnnotations(org.bluetooth.gattparser.Enumeration.class);
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
