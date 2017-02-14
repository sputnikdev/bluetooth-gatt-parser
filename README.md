# bluetooth-gatt-parser

A simple library/framework to work with GATT services and characteristics.

Have a look at an example of parsing a standard characteristic ([Battery Level 0x2A19](https://www.bluetooth.com/specifications/gatt/viewer?attributeXmlFile=org.bluetooth.characteristic.battery_level.xml)) value:

``BluetoothGattParserFactory.getDefault().parse("2A19", new byte[] {51}).get("Level").getInteger(null)``

This would print 51.

Features:

1. Support for 99% of the existing/standard [GATT services and characteristics specifications](https://www.bluetooth.com/specifications/gatt).
2. Parsing/reading single and multi field characteristics into a user-friendly data format.
3. Writing single and multi field characteristics.
4. Extensibility. User defined services and characteristics.
5. Support for all defined [format types](https://www.bluetooth.com/specifications/assigned-numbers/format-types).
6. Robustness. Unit test coverage - 90%.

A more complex example of parsing multi-field characteristics ([Heart Rate service](https://www.bluetooth.com/specifications/gatt/viewer?attributeXmlFile=org.bluetooth.service.heart_rate.xml)):


```java
BluetoothGattParser parser = BluetoothGattParserFactory.getDefault();

// Body Sensor Location (0x2A38) characteristic
byte[] data = new byte[] {1}; // 1 == Chest
GattResponse response = parser.parse("2A38", data);
String sensorLocation = response.get("Body Sensor Location").getInteger(null); // prints 1 (Chest)

// Heart Rate Measurement (0x2A37) characteristic
byte[] data = new byte[] {20, 74, 13, 3};
GattResponse response = parser.parse("2A37", data);
String heartRateValue = response.get("Heart Rate Measurement Value (uint8)").getInteger(null); // prints 74
String rrIntervalValue = response.get("RR-Interval").getInteger(null); // prints 781
```

