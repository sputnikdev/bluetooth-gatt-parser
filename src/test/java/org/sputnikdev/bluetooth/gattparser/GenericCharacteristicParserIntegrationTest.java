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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
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

    @Test
    public void testOregonWeatherStation() {
        /*
        010d01ec00ff7fff7f7f7f7f7fffff7f7f7f7f7f
        827f7f7f2101f8003301ba00ff7fff7fff7fff7f

        indoor temp 26.9, max 28.9 min 24.8
        outdoor temp 23.6, max 30.7, min 18.6
                 */
        byte[] actual = {0x01, 0x0d, 0x01, (byte) 0xec, 0x00, (byte) 0xff, 0x7f, (byte) 0xff, 0x7f, 0x7f, 0x7f, 0x7f, 0x7f, (byte) 0xff, (byte) 0xff, 0x7f, 0x7f, 0x7f, 0x7f, 0x7f};
        byte[] minMax =  {(byte) 130, 0x7f, 0x7f, 0x7f, 0x21, 0x01, (byte) 0xf8, 0x00, 0x33, 0x01, (byte) 0xba, 0x00, (byte) 0xff, 0x7f, (byte) 0xff, 0x7f, (byte) 0xff, 0x7f, (byte) 0xff, 0x7f};

        assertTrue(parser.isKnownService("74E7FE00"));
        assertTrue(parser.isKnownCharacteristic("74E78E10"));

        GattResponse response1 = parser.parse("74E78E10", actual);
        assertEquals(26.9, response1.get("Base temp").getDouble(), 0.1);
        assertEquals(23.6, response1.get("Sensor 1 temp").getDouble(), 0.1);
        assertEquals(3276.7, response1.get("Sensor 2 temp").getDouble(), 0.1);
        assertEquals(3276.7, response1.get("Sensor 3 temp").getDouble(), 0.1);
        assertEquals(127, (int) response1.get("Base humidity").getInteger());
        assertEquals(127, (int) response1.get("Sensor 1 humidity").getInteger());
        assertEquals(127, (int) response1.get("Sensor 2 humidity").getInteger());
        assertEquals(127, (int) response1.get("Sensor 3 humidity").getInteger());
        assertEquals(255, (int) response1.get("Temperature trend").getInteger());
        assertEquals(255, (int) response1.get("Humidity trend").getInteger());
        assertEquals(127, (int) response1.get("Base humidity max").getInteger());
        assertEquals(127, (int) response1.get("Base humidity min").getInteger());
        assertEquals(127, (int) response1.get("Sensor 1 humidity max").getInteger());
        assertEquals(127, (int) response1.get("Sensor 1 humidity min").getInteger());
        assertEquals(127, (int) response1.get("Sensor 2 humidity max").getInteger());


        GattResponse response2 = parser.parse("74E78E10", minMax);
        assertEquals(127, (int) response2.get("Sensor 2 humidity min").getInteger());
        assertEquals(127, (int) response2.get("Sensor 3 humidity max").getInteger());
        assertEquals(127, (int) response2.get("Sensor 3 humidity min").getInteger());
        assertEquals(28.9, response2.get("Base max temp").getDouble(), 0.1);
        assertEquals(24.8, response2.get("Base min temp").getDouble(), 0.1);
        assertEquals(30.7, response2.get("Sensor 1 max temp").getDouble(), 0.1);
        assertEquals(18.6, response2.get("Sensor 1 min temp").getDouble(), 0.1);
        assertEquals(3276.7, response2.get("Sensor 2 max temp").getDouble(), 0.1);
        assertEquals(3276.7, response2.get("Sensor 2 min temp").getDouble(), 0.1);
        assertEquals(3276.7, response2.get("Sensor 3 max temp").getDouble(), 0.1);
        assertEquals(3276.7, response2.get("Sensor 3 min temp").getDouble(), 0.1);
    }

    @Test
    public void testMiflora() {

        /*
            approximate data:
            8000 lux
            moi 51
            28.7
            111 fert

            [1f, 01, 00, e7, 26, 00, 00, 35, 74, 00, 02, 3c, 00, fb, 34, 9b]
            [21, 01, 00, 3b, 27, 00, 00, 34, 77, 00, 02, 3c, 00, fb, 34, 9b]

            Battery and firmware:
            [64, 27, 33, 2e, 31, 2e, 38]

         */

        byte[] data = {0x1f, 0x01, 0x00, (byte) 0xe7, 0x26, 0x00, 0x00, 0x35, 0x74, 0x00, 0x02, 0x3c, 0x00, (byte) 0xfb, 0x34, (byte) 0x9b};
        byte[] batteryFirmware = {0x64, 0x27, 0x33, 0x2e, 0x31, 0x2e, 0x38};

        assertTrue(parser.isKnownService("1204")); // miflora service
        assertTrue(parser.isKnownCharacteristic("1A02")); // battery and firmware
        assertTrue(parser.isKnownCharacteristic("1A01")); // miflora characteristic

        GattResponse response = parser.parse("1A01", data);
        assertEquals(4, response.getSize());
        assertEquals(28.7, response.get("Temperature").getDouble(), 0.1);
        assertEquals(9959, (int) response.get("Sunlight").getInteger());
        assertEquals(53, (int) response.get("Moisture").getInteger());
        assertEquals(116, (int) response.get("Fertility").getInteger());

        response = parser.parse("1A02", batteryFirmware);
        assertEquals(2, response.getSize());
        assertEquals(100, (int) response.get("Battery level").getInteger());
        assertEquals("d'3.1.8", response.get("Firmware version").getString());
    }

    @Test
    public void testMifloraServiceData() {
        byte[] temperature = {0x71, 0x20, (byte) 0x98, 0x00, 0x0f, 0x4b, 0x07, 0x66, (byte) 0x8d, 0x7c, (byte) 0xc4, 0x0d, 0x04, 0x10, 0x02, 0x47, 0x01};
        byte[] sunlight =    {0x71, 0x20, (byte) 0x98, 0x00, 0x0f, 0x4b, 0x07, 0x66, (byte) 0x8d, 0x7c, (byte) 0xc4, 0x0d, 0x07, 0x10, 0x03, (byte) 0xfd, 0x20, 0x00};
        byte[] moisture =    {0x71, 0x20, (byte) 0x98, 0x00, 0x0f, 0x4b, 0x07, 0x66, (byte) 0x8d, 0x7c, (byte) 0xc4, 0x0d, 0x08, 0x10, 0x01, 0x19};
        byte[] fertility =   {0x71, 0x20, (byte) 0x98, 0x00, 0x0f, 0x4b, 0x07, 0x66, (byte) 0x8d, 0x7c, (byte) 0xc4, 0x0d, 0x09, 0x10, 0x02, (byte) 0xc5, 0x00};

        assertTrue(parser.isKnownCharacteristic("FE95"));

        GattResponse response = parser.parse("FE95", temperature);
        assertEquals(32.7, response.get("Temperature").getDouble(), 0.1);
        response = parser.parse("FE95", sunlight);
        assertEquals(8445, (int) response.get("Sunlight").getInteger());
        response = parser.parse("FE95", moisture);
        assertEquals(25, (int) response.get("Moisture").getInteger());
        response = parser.parse("FE95", fertility);
        assertEquals(197, (int) response.get("Fertility").getInteger());
    }

    @Test
    public void testMiTempAndHumiditySensor() {
        byte[] data = new byte[] {0x54, 0x3d, 0x32, 0x37, 0x2e, 0x36, 0x20, 0x48, 0x3d, 0x39, 0x32, 0x2e, 0x36, 0x00};

        assertTrue(parser.isKnownService("226C0000")); // temp and humidity service
        assertTrue(parser.isKnownCharacteristic("226CAA55")); // temp and humidity characteristic

        String tempAndHumidity = parser.parse("226CAA55", data).get("Temperature and humidity").getString();
        assertEquals("T=27.6 H=92.6", tempAndHumidity);

        Matcher matcher = Pattern.compile("^T=(?<temperature>([0-9]*[.])?[0-9]+) H=(?<humidity>([0-9]*[.])?[0-9]+)$")
                .matcher(tempAndHumidity);

        assertTrue(matcher.matches());

        assertEquals("27.6", matcher.group("temperature"));
        assertEquals("92.6", matcher.group("humidity"));
    }

    @Test
    public void testMiTempAndHumidityServiceData() {

        byte[] temperatureAndHumidity = {0x50, 0x20, (byte) 0xaa, 0x01, (byte) 0xeb, (byte) 0xee, 0x7a, (byte) 0xd0, (byte) 0xa8, 0x65, 0x4c,       0x0d, 0x10, 0x04, (byte) 0xef, 0x00, 0x39, 0x02};
        byte[] temperature =            {0x50, 0x20, (byte) 0xaa, 0x01, (byte) 0xef, (byte) 0xee, 0x7a, (byte) 0xd0, (byte) 0xa8, 0x65, 0x4c,       0x04, 0x10, 0x02, 0x03, 0x01};
        byte[] humidity =               {0x50, 0x20, (byte) 0xaa, 0x01, (byte) 0xf4, (byte) 0xee, 0x7a, (byte) 0xd0, (byte) 0xa8, 0x65, 0x4c,       0x06, 0x10, 0x02, 0x38, 0x02};
        byte[] battery =                {0x50, 0x20, (byte) 0xaa, 0x01, (byte) 0xf4, (byte) 0xee, 0x7a, (byte) 0xd0, (byte) 0xa8, 0x65, 0x4c,       0x0a, 0x10, 0x01, 0x64};

        assertTrue(parser.isKnownCharacteristic("FE95"));

        GattResponse response = parser.parse("FE95", temperatureAndHumidity);
        assertEquals(23.9, response.get("Temperature").getDouble(), 0.1);
        assertEquals(56.9, response.get("Humidity").getDouble(), 0.1);

        response = parser.parse("FE95", temperature);
        assertEquals(25.9, response.get("Temperature").getDouble(), 0.1);

        response = parser.parse("FE95", humidity);
        assertEquals(56.8, response.get("Humidity").getDouble(), 0.1);

        response = parser.parse("FE95", battery);
        assertEquals(100, (int) response.get("Battery").getInteger());
    }

    @Test
    public void testXiaomiScalesAdvertisedData() {
        byte[] data = {(byte) 0xa2, 0x4c, 0x63, (byte) 0xe2, 0x07, 0x02, 0x01, 0x0f, 0x2f, 0x1c};

        assertTrue(parser.isKnownCharacteristic("181D"));

        GattResponse response = parser.parse("181D", data);
        assertEquals(7, response.getSize());

        assertEquals(127.1, response.get("Weight - SI").getDouble(), 0.1);
        assertEquals(2018, (int) response.get("Year").getInteger(null));
        assertEquals(2, (int) response.get("Month").getInteger(null));
        assertEquals(1, (int) response.get("Day").getInteger(null));
        assertEquals(15, (int) response.get("Hours").getInteger(null));
        assertEquals(47, (int) response.get("Minutes").getInteger(null));
        assertEquals(28, (int) response.get("Seconds").getInteger(null));


        data = new byte [] {(byte) 0xa2, (byte) 0x80, 0x02, (byte) 0xe2, 0x07, 0x02, 0x01, 0x0f, 0x35, 0x04};
        response = parser.parse("181D", data);
        assertEquals(3.2, response.get("Weight - SI").getDouble(), 0.1);
        assertEquals(2018, (int) response.get("Year").getInteger(null));
        assertEquals(2, (int) response.get("Month").getInteger(null));
        assertEquals(1, (int) response.get("Day").getInteger(null));
        assertEquals(15, (int) response.get("Hours").getInteger(null));
        assertEquals(53, (int) response.get("Minutes").getInteger(null));
        assertEquals(4, (int) response.get("Seconds").getInteger(null));

    }

}
