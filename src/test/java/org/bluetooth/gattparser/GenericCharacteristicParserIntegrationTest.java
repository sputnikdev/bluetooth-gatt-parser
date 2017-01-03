package org.bluetooth.gattparser;

import java.util.LinkedHashMap;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GenericCharacteristicParserIntegrationTest {

    private BluetoothGattParser parser = BluetoothGattParserFactory.getDefault();

    @Test
    public void testWahooHeartRateSensor() {

        LinkedHashMap<String, FieldHolder> holders = parser.parse("2A19", new byte[] {51});
        assertEquals(1, holders.size());
        assertTrue(holders.containsKey("Level"));
        assertEquals(51, (int) holders.get("Level").getInteger(null));

        holders = parser.parse("2A38", new byte[] {1});
        assertEquals(1, holders.size());
        assertTrue(holders.containsKey("Body Sensor Location"));
        assertEquals(1, (int) holders.get("Body Sensor Location").getInteger(null));


        holders = parser.parse("2A26", new byte[] {50, 46, 49});
        assertEquals(1, holders.size());
        assertTrue(holders.containsKey("Firmware Revision"));
        assertEquals("2.1", holders.get("Firmware Revision").getString(null));

        holders = parser.parse("2A23", new byte[] {85, -86, 85, -86, 85, -86, 85, -86});
        assertEquals(2, holders.size());
        assertTrue(holders.containsKey("Manufacturer Identifier"));
        assertEquals(367929961045L, (long) holders.get("Manufacturer Identifier").getLong(null));
        assertTrue(holders.containsKey("Organizationally Unique Identifier"));
        assertEquals(11163050L, (long) holders.get("Organizationally Unique Identifier").getLong(null));

        holders = parser.parse("2A29", new byte[] {87, 97, 104, 111, 111, 32, 70, 105, 116, 110, 101, 115, 115, 0, 0, 0, 0, 0, 0, 0});
        assertEquals(1, holders.size());
        assertTrue(holders.containsKey("Manufacturer Name"));
        assertEquals("Wahoo Fitness", holders.get("Manufacturer Name").getString(null));

        holders = parser.parse("2A25", new byte[] {49, 53, 56, 55});
        assertEquals(1, holders.size());
        assertTrue(holders.containsKey("Serial Number"));
        assertEquals("1587", holders.get("Serial Number").getString(null));

        holders = parser.parse("2A27", new byte[] {68, 120});
        assertEquals(1, holders.size());
        assertTrue(holders.containsKey("Hardware Revision"));
        assertEquals("Dx", holders.get("Hardware Revision").getString(null));

        holders = parser.parse("2A37", new byte[] {4, 74});
        assertEquals(1, holders.size());
        assertTrue(holders.containsKey("Heart Rate Measurement Value (uint8)"));
        assertEquals(74, (int) holders.get("Heart Rate Measurement Value (uint8)").getInteger(null));

        holders = parser.parse("2A37", new byte[] {20, 74, 13, 3});
        assertEquals(2, holders.size());
        assertTrue(holders.containsKey("Heart Rate Measurement Value (uint8)"));
        assertEquals(74, (int) holders.get("Heart Rate Measurement Value (uint8)").getInteger(null));
        assertTrue(holders.containsKey("RR-Interval"));
        assertEquals(781, (int) holders.get("RR-Interval").getInteger(null));

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

        LinkedHashMap<String, FieldHolder> holders = parser.parse("2A2B",
                new byte[] {(byte)2017, 2017 >> 8, 1, 4, 11, 38, 45, 3, 1, 2});
        assertEquals(9, holders.size());
        assertEquals(2017, (int) holders.get("Year").getInteger(null));
        assertEquals(1, (int) holders.get("Month").getInteger(null));
        assertEquals(4, (int) holders.get("Day").getInteger(null));
        assertEquals(11, (int) holders.get("Hours").getInteger(null));
        assertEquals(38, (int) holders.get("Minutes").getInteger(null));
        assertEquals(45, (int) holders.get("Seconds").getInteger(null));
        assertEquals(3, (int) holders.get("Day of Week").getInteger(null));
        assertEquals(1, (int) holders.get("Fractions256").getInteger(null));
        assertEquals(2, (int) holders.get("Adjust Reason").getInteger(null));
    }


}
