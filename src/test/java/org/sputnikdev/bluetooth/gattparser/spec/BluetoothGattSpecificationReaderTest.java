package org.sputnikdev.bluetooth.gattparser.spec;

/*-
 * #%L
 * org.sputnikdev:bluetooth-gatt-parser
 * %%
 * Copyright (C) 2017 Sputnik Dev
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class BluetoothGattSpecificationReaderTest {

    @Spy
    private BluetoothGattSpecificationReader reader = new BluetoothGattSpecificationReader();

    @Test
    public void testGetService() throws Exception {
        Service service = reader.getService("180D");
        assertNotNull(service);
        assertEquals("180D", service.getUuid());
        assertEquals("org.bluetooth.service.heart_rate", service.getType());
        assertEquals("Heart Rate", service.getName());
        assertEquals("This service exposes heart rate and other data from a Heart Rate Sensor intended for fitness applications.",
                service.getInformativeText().getAbstract().trim());
        assertEquals("The HEART RATE Service exposes heart rate and other data related to a heart rate sensor intended for\n"
                        + "            fitness applications.",
                service.getInformativeText().getSummary().trim());
        List<CharacteristicAccess> characteristics = service.getCharacteristics().getCharacteristics();
        assertEquals(3, characteristics.size());
        assertEquals("Heart Rate Measurement", characteristics.get(0).getName());
        assertEquals("org.bluetooth.characteristic.heart_rate_measurement", characteristics.get(0).getType());
        assertCharacteristicAccess("Excluded", "Excluded", "Excluded", "Excluded", "Excluded", "Mandatory", "Excluded",
                "Excluded", "Excluded", characteristics.get(0));
        assertEquals("Body Sensor Location", characteristics.get(1).getName());
        assertEquals("org.bluetooth.characteristic.body_sensor_location", characteristics.get(1).getType());
        assertCharacteristicAccess("Mandatory", "Excluded", "Excluded", "Excluded", "Excluded", "Excluded", "Excluded",
                "Excluded", "Excluded", characteristics.get(1));
        assertEquals("Heart Rate Control Point", characteristics.get(2).getName());
        assertEquals("org.bluetooth.characteristic.heart_rate_control_point", characteristics.get(2).getType());
        assertCharacteristicAccess("Excluded", "Mandatory", "Excluded", "Excluded", "Excluded", "Excluded", "Excluded",
                "Excluded", "Excluded", characteristics.get(2));
    }

    @Test
    public void testGetBasicCharacteristic() throws Exception {
        Characteristic characteristic = reader.getCharacteristicByUUID("2A19");
        assertNotNull(characteristic);
        assertEquals("Battery Level", characteristic.getName());
        assertEquals("2A19", characteristic.getUuid());
        assertEquals("org.bluetooth.characteristic.battery_level", characteristic.getType());
        assertEquals("The current charge level of a battery. 100% represents fully charged while 0% represents fully discharged.",
                characteristic.getInformativeText().getAbstract().trim());
        Value value = characteristic.getValue();
        assertNotNull(value);
        assertNull(FlagUtils.getFlags(value.getFields()));
        assertEquals(1, value.getFields().size());
        Field field = value.getFields().get(0);
        assertEquals("Level", field.getName());
        FieldFormat fieldFormat = field.getFormat();
        assertEquals("uint8", fieldFormat.getName());
        assertEquals(8, fieldFormat.getSize());
        assertEquals(FieldType.UINT, fieldFormat.getType());
        assertEquals("Mandatory", field.getRequirements().get(0));
        assertEquals("org.bluetooth.unit.percentage", field.getUnit());
        assertNull(field.getDecimalExponent());
        assertEquals(0.0D, field.getMinimum(), 0.0);
        assertEquals(100D, field.getMaximum(), 0.0);
        assertNull(field.getInformativeText());
        assertNotNull(field.getEnumerations());
        assertNotNull(field.getEnumerations().getReserves());
        assertEquals(1, field.getEnumerations().getReserves().size());
        assertEquals(101, field.getEnumerations().getReserves().get(0).getStart());
        assertEquals(255, field.getEnumerations().getReserves().get(0).getEnd());
    }

    @Test
    public void testGetCharacteristic() throws Exception {
        Characteristic characteristic = reader.getCharacteristicByUUID("2A1C");
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
        assertEquals(fields.get(0), FlagUtils.getFlags(value.getFields()));

        Field field = fields.get(0);
        assertEquals("Mandatory", field.getRequirements().get(0));
        FieldFormat fieldFormat = field.getFormat();
        assertEquals("8bit", fieldFormat.getName());
        assertEquals(8, fieldFormat.getSize());
        assertEquals(FieldType.UINT, fieldFormat.getType());
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
        assertEquals("C2", field.getRequirements().get(0));
        assertEquals("org.bluetooth.unit.thermodynamic_temperature.degree_fahrenheit", field.getUnit());
    }

    @Test
    public void testGetCharacteristicWithExponents() throws Exception {
        Characteristic characteristic = reader.getCharacteristicByUUID("2A9C");
        assertNotNull(characteristic);
        assertEquals("Body Composition Measurement", characteristic.getName());
        Field field = characteristic.getValue().getFields().get(6);
        assertEquals("Muscle Mass - Kilograms", field.getName());
        assertEquals(-3, (int) field.getDecimalExponent());
        assertEquals(5, (int) field.getMultiplier());
        characteristic = reader.getCharacteristicByUUID("2A12");
        assertNotNull(characteristic);
        assertEquals("Time Accuracy", characteristic.getName());
        field = characteristic.getValue().getFields().get(0);
        assertEquals("Accuracy", field.getName());
        assertEquals(-3, (int) field.getBinaryExponent());

    }

    @Test
    public void testGetFlags() {
        Characteristic characteristic = reader.getCharacteristicByUUID("2A1C");
        Set<String> flags = FlagUtils.getAllFlags(FlagUtils.getFlags(characteristic.getValue().getFields()));
        assertEquals(4, flags.size());
        assertTrue(flags.contains("C1"));
        assertTrue(flags.contains("C2"));
        assertTrue(flags.contains("C3"));
        assertTrue(flags.contains("C4"));
    }

    @Test
    public void testGetRequirements() {
        Characteristic characteristic = reader.getCharacteristicByUUID("2A63");
        List<Field> fields = characteristic.getValue().getFields();
        Field flags = FlagUtils.getFlags(fields);
        Set<String> requirements = reader.getRequirements(fields, flags);
        assertEquals(6, requirements.size());
        assertTrue(requirements.containsAll(Arrays.asList("Optional", "C1", "C2", "C3", "C4", "C5")));

        characteristic = reader.getCharacteristicByUUID("2A46");
        fields = characteristic.getValue().getFields();
        flags = FlagUtils.getFlags(fields);
        requirements = reader.getRequirements(fields, flags);
        assertEquals(0, requirements.size());
    }

    @Test
    public void testValidate() {
        assertTrue(reader.getCharacteristicByUUID("2A19").isValidForRead());
        assertTrue(reader.getCharacteristicByUUID("2A46").isValidForRead());
        assertFalse(reader.getCharacteristicByUUID("2AA4").isValidForRead());
        assertFalse(reader.getCharacteristicByUUID("2A63").isValidForRead());
    }

    @Test
    public void testLoadReferences() {
        Characteristic characteristic = reader.getCharacteristicByUUID("2A0A");
        assertEquals("org.bluetooth.characteristic.date_time",
                characteristic.getValue().getFields().get(0).getReference().trim());
    }

    @Test
    public void testGetFields() {
        List<Field> fields = reader.getFields(reader.getCharacteristicByUUID("2A2B"));
        assertEquals(9, fields.size());
        assertEquals("Year", fields.get(0).getName());
        assertEquals("Month", fields.get(1).getName());
        assertEquals("Day", fields.get(2).getName());
        assertEquals("Hours", fields.get(3).getName());
        assertEquals("Minutes", fields.get(4).getName());
        assertEquals("Seconds", fields.get(5).getName());
        assertEquals("Day of Week", fields.get(6).getName());
        assertEquals("Fractions256", fields.get(7).getName());
        assertEquals("Adjust Reason", fields.get(8).getName());
    }

    private void assertCharacteristicAccess(String read, String write, String writeWithoutResponse, String signedWrite,
            String reliableWrite, String notify, String indicate, String writableAuxiliaries, String broadcast,
            CharacteristicAccess characteristicAccess) {
        Properties properties = characteristicAccess.getProperties().get(0);
        assertEquals(read, properties.getRead());
        assertEquals(write, properties.getWrite());
        assertEquals(writeWithoutResponse, properties.getWriteWithoutResponse());
        assertEquals(signedWrite, properties.getSignedWrite());
        assertEquals(reliableWrite, properties.getReliableWrite());
        assertEquals(notify, properties.getNotify());
        assertEquals(indicate, properties.getIndicate());
        assertEquals(writableAuxiliaries, properties.getWritableAuxiliaries());
        assertEquals(broadcast, properties.getBroadcast());
    }

    private void assertTemperatureBit(int index, String name, String enum1, String enumReq1, String enum2, String enumReq2, Bit bit) {
        assertEquals(index, bit.getIndex());
        assertEquals(1, bit.getSize());
        assertEquals(name, bit.getName());
        List<Enumeration> enumerations = bit.getEnumerations().getEnumerations();
        assertEquals(2, enumerations.size());
        assertEquals(BigInteger.ZERO, enumerations.get(0).getKey());
        assertEquals(enum1, enumerations.get(0).getValue());
        assertEquals(enumReq1, enumerations.get(0).getRequires());
        assertEquals(BigInteger.ONE, enumerations.get(1).getKey());
        assertEquals(enum2, enumerations.get(1).getValue());
        assertEquals(enumReq2, enumerations.get(1).getRequires());
    }
}
