package org.bluetooth.gattparser.spec;

import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class BluetoothGattSpecificationReaderTest {

    private BluetoothGattSpecificationReader reader = new BluetoothGattSpecificationReader();

    @Test
    public void testGetService() throws Exception {
        Service service = reader.getService("180F");
        assertNotNull(service);
        assertEquals("180F", service.getUuid());
        assertEquals("org.bluetooth.service.battery_service", service.getType());
        assertEquals("Battery Service", service.getName());
        assertEquals("The Battery Service exposes the state of a battery within a device.",
                service.getInformativeText().getAbstract().trim());
        assertEquals("The Battery Service exposes the Battery State and Battery Level of a single battery or set of batteries in\n            a device.",
                service.getInformativeText().getSummary().trim());
    }

    @Test
    public void testGetBasicCharacteristic() throws Exception {
        Characteristic characteristic = reader.getCharacteristic("2A19");
        assertNotNull(characteristic);
        assertEquals("Battery Level", characteristic.getName());
        assertEquals("2A19", characteristic.getUuid());
        assertEquals("org.bluetooth.characteristic.battery_level", characteristic.getType());
        assertEquals("The current charge level of a battery. 100% represents fully charged while 0% represents fully discharged.",
                characteristic.getInformativeText().getAbstract().trim());
        Value value = characteristic.getValue();
        assertNotNull(value);
        assertEquals(1, value.getFields().size());
        Field field = value.getFields().get(0);
        assertEquals("Level", field.getName());
        assertEquals("uint8", field.getFormat());
        assertEquals("Mandatory", field.getRequirement());
        assertEquals("org.bluetooth.unit.percentage", field.getUnit());
        assertNull(field.getDecimalExponent());
        assertEquals(0, field.getMinimum());
        assertEquals(100, field.getMaximum());
        assertNull(field.getInformativeText());
        assertNotNull(field.getEnumerations());
        assertNotNull(field.getEnumerations().getReserved());
        assertEquals(101, field.getEnumerations().getReserved().getStart());
        assertEquals(255, field.getEnumerations().getReserved().getEnd());
    }

    @Test
    public void testGetCharacteristic() throws Exception {
        Characteristic characteristic = reader.getCharacteristic("2A1C");
        assertNotNull(characteristic);
        assertEquals("Temperature Measurement", characteristic.getName());
        assertEquals("2A1C", characteristic.getUuid());
        assertEquals("org.bluetooth.characteristic.temperature_measurement", characteristic.getType());
        assertEquals("The Temperature Measurement characteristic is a variable length structure containing a Flags field, a\n"
                        + "            Temperature Measurement Value field and, based upon the contents of the Flags field, optionally a Time\n"
                        + "            Stamp field and/or a Temperature Type field.",
                characteristic.getInformativeText().getAbstract().trim());
        assertEquals("The flags is the first field sent followed by the Temperature Measurement Value.",
                characteristic.getInformativeText().getSummary().trim());
        assertEquals("The fields in the above table are in the order of LSO to MSO. Where LSO = Least Significant Octet and MSO\n"
                + "            = Most Significant Octet.", characteristic.getInformativeText().getNote().trim());
        Value value = characteristic.getValue();
        assertNotNull(value);
        assertEquals(5, value.getFields().size());

        List<Field> fields = value.getFields();
        assertEquals("Flags", fields.get(0).getName());

        Field field = fields.get(0);
        assertEquals("Mandatory", field.getRequirement());
        assertEquals("8bit", field.getFormat());
        assertNotNull(field.getBitField());

        BitField bitField = field.getBitField();
        assertEquals(3, bitField.getBits().size());

        assertTemperatureBit(0, "Temperature Units Flag",
                "Temperature Measurement Value in units of Celsius", "C1",
                "Temperature Measurement Value in units of Fahrenheit", "C2",
                bitField.getBits().get(0));
        assertTemperatureBit(1, "Time Stamp Flag",
                "Time Stamp field not present", null,
                "Time Stamp field present", "C3",
                bitField.getBits().get(1));
        assertTemperatureBit(2, "Temperature Type Flag",
                "Temperature Type field not present", null,
                "Temperature Type field present", "C4",
                bitField.getBits().get(2));

        field = fields.get(2);
        assertEquals("Temperature Measurement Value (Fahrenheit)", field.getName());
        assertEquals("This field is only included if the flags bit 0 is 1.", field.getInformativeText());
        assertEquals("C2", field.getRequirement());
        assertEquals("org.bluetooth.unit.thermodynamic_temperature.degree_fahrenheit", field.getUnit());
    }

    private void assertTemperatureBit(int index, String name, String enum1, String enumReq1, String enum2, String enumReq2, Bit bit) {
        assertEquals(index, bit.getIndex());
        assertEquals(1, bit.getSize());
        assertEquals(name, bit.getName());
        List<Enumeration> enumerations = bit.getEnumerations().getEnumerations();
        assertEquals(2, enumerations.size());
        assertEquals("0", enumerations.get(0).getKey());
        assertEquals(enum1, enumerations.get(0).getValue());
        assertEquals(enumReq1, enumerations.get(0).getRequires());
        assertEquals("1", enumerations.get(1).getKey());
        assertEquals(enum2, enumerations.get(1).getValue());
        assertEquals(enumReq2, enumerations.get(1).getRequires());
    }
}
