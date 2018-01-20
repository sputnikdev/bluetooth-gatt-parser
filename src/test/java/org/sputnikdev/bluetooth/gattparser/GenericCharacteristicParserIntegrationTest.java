package org.sputnikdev.bluetooth.gattparser;

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

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GenericCharacteristicParserIntegrationTest {

    private BluetoothGattParser parser = BluetoothGattParserFactory.getDefault();

    @Test
    public void testWahooHeartRateSensor() {

        GattResponse response = parser.parse("2A19", new byte[] {51});
        assertEquals(1, response.getSize());
        assertTrue(response.contains("Level"));
        assertEquals(51, (int) response.get("Level").getInteger(null));

        response = parser.parse("2A38", new byte[] {1});
        assertEquals(1, response.getSize());
        assertTrue(response.contains("Body Sensor Location"));
        assertEquals(1, (int) response.get("Body Sensor Location").getInteger(null));


        response = parser.parse("2A26", new byte[] {50, 46, 49});
        assertEquals(1, response.getSize());
        assertTrue(response.contains("Firmware Revision"));
        assertEquals("2.1", response.get("Firmware Revision").getString(null));

        response = parser.parse("2A23", new byte[] {85, -86, 85, -86, 85, -86, 85, -86});
        assertEquals(2, response.getSize());
        assertTrue(response.contains("Manufacturer Identifier"));
        assertEquals(367929961045L, (long) response.get("Manufacturer Identifier").getLong(null));
        assertTrue(response.contains("Organizationally Unique Identifier"));
        assertEquals(11163050L, (long) response.get("Organizationally Unique Identifier").getLong(null));

        response = parser.parse("2A29", new byte[] {87, 97, 104, 111, 111, 32, 70, 105, 116, 110, 101, 115, 115, 0, 0, 0, 0, 0, 0, 0});
        assertEquals(1, response.getSize());
        assertTrue(response.contains("Manufacturer Name"));
        assertEquals("Wahoo Fitness", response.get("Manufacturer Name").getString(null));

        response = parser.parse("2A25", new byte[] {49, 53, 56, 55});
        assertEquals(1, response.getSize());
        assertTrue(response.contains("Serial Number"));
        assertEquals("1587", response.get("Serial Number").getString(null));

        response = parser.parse("2A27", new byte[] {68, 120});
        assertEquals(1, response.getSize());
        assertTrue(response.contains("Hardware Revision"));
        assertEquals("Dx", response.get("Hardware Revision").getString(null));

        response = parser.parse("2A37", new byte[] {4, 74});
        assertEquals(1, response.getSize());
        assertTrue(response.contains("Heart Rate Measurement Value (uint8)"));
        assertEquals(74, (int) response.get("Heart Rate Measurement Value (uint8)").getInteger(null));

        response = parser.parse("2A37", new byte[] {20, 74, 13, 3});
        assertEquals(2, response.getSize());
        assertTrue(response.contains("Heart Rate Measurement Value (uint8)"));
        assertEquals(74, (int) response.get("Heart Rate Measurement Value (uint8)").getInteger(null));
        assertTrue(response.contains("RR-Interval"));
        assertEquals(781, (int) response.get("RR-Interval").getInteger(null));

    }

    @Test
    public void testCurrentTime() {
        /*
            Fields:

            Year (uint16),
            Month (uint8),
            Day (uint8),
            Hours (uint8),
            Minutes (uint8),
            Seconds (uint8)

            Day of Week (uint8)

            Fractions256 (uint8)

            Adjust Reason (8bit)
         */

        GattResponse response = parser.parse("2A2B",
                new byte[] {(byte)2017, 2017 >> 8, 1, 4, 11, 38, 45, 3, 1, 2});
        assertEquals(9, response.getSize());
        assertEquals(2017, (int) response.get("Year").getInteger(null));
        assertEquals(1, (int) response.get("Month").getInteger(null));
        assertEquals(4, (int) response.get("Day").getInteger(null));
        assertEquals(11, (int) response.get("Hours").getInteger(null));
        assertEquals(38, (int) response.get("Minutes").getInteger(null));
        assertEquals(45, (int) response.get("Seconds").getInteger(null));
        assertEquals(3, (int) response.get("Day of Week").getInteger(null));
        assertEquals(1, (int) response.get("Fractions256").getInteger(null));
        assertEquals(2, (int) response.get("Adjust Reason").getInteger(null));
    }

    @Test
    public void testWriteHeartRateSensor() {
        GattRequest request = parser.prepare("2A39");
        request.setField("Heart Rate Control Point", 1);

        byte[] data = parser.serialize(request);
        assertArrayEquals(new byte[]{1}, data);

    }

}
