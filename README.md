# bluetooth-gatt-parser

A **simple** library/framework to work with Bluetooth Smart (BLE) GATT services and characteristics.

Have a look at an example of parsing a standard characteristic ([Battery Level 0x2A19](https://www.bluetooth.com/specifications/gatt/viewer?attributeXmlFile=org.bluetooth.characteristic.battery_level.xml)) value:
```java
BluetoothGattParserFactory.getDefault().parse("2A19", new byte[] {51}).get("Level").getInteger(null);
```
This would print 51.

**Features:**

1. Support for 99% of the existing/standard [GATT services and characteristics specifications](https://www.bluetooth.com/specifications/gatt).
2. Parsing/reading single and multi field characteristics into a user-friendly data format.
3. Writing single and multi field characteristics.
4. Validating input data to conform GATT specifications (format types and mandatory fields).
5. Extensibility. User defined services and characteristics.
6. Support for all defined [format types](https://www.bluetooth.com/specifications/assigned-numbers/format-types).
7. Robustness. Unit test coverage - 90%.

A more complex example of parsing multi-field characteristics ([Heart Rate service](https://www.bluetooth.com/specifications/gatt/viewer?attributeXmlFile=org.bluetooth.service.heart_rate.xml)):

```java
// Getting a default implementation which is capable of reading/writing the standard GATT services and characteristics
BluetoothGattParser parser = BluetoothGattParserFactory.getDefault();

// Reading Body Sensor Location (0x2A38) characteristic (sigle field)
byte[] data = new byte[] {1}; // 1 == Chest
GattResponse response = parser.parse("2A38", data);
String sensorLocation = response.get("Body Sensor Location").getInteger(null); // prints 1 (Chest)

// Reading Heart Rate Measurement (0x2A37) characteristic (multi field)
byte[] data = new byte[] {20, 74, 13, 3};
GattResponse response = parser.parse("2A37", data);
String heartRateValue = response.get("Heart Rate Measurement Value (uint8)").getInteger(null); // prints 74
String rrIntervalValue = response.get("RR-Interval").getInteger(null); // prints 781

// Writing Heart Rate Control Point (0x2A39) characteristic
GattRequest request = parser.prepare("2A39");
request.setField("Heart Rate Control Point", 1); // control value to be sent to a bluetooth device
byte[] data = parser.serialize(request);
```

---
**Extending the library with user defined services and characteristics**

The gatt-parser library is designed to be able to add support for some custom services/characteristics or to override the existing services/characteristics.

_Loading XML GATT specification files (GATT-like specifications) from a folder:_

```java
BluetoothGattParser parser = BluetoothGattParserFactory.getDefault();
File extensionsFolderFile = new File(..);
gattParser.loadExtensionsFromFolder(extensions);
```

_Loading XML GATT specifications from an extension folder in the class path:_

The gatt-parser library loads automatically user defined specifications for services and characteristics from a classpath folder "ext/gatt/service" and "ext/gatt/characteristic" for services and characteristics respectively. Place your custom GATT XML definitions into those folders to add/override services and characteristics.

**A custom parser can be added for a characteristic if you are not satisfied with the default one:**

See the default one for a hint and a reference: [GenericCharacteristicParser](src/main/java/org/bluetooth/gattparser/GenericCharacteristicParser.java)
```java
BluetoothGattParser parser = BluetoothGattParserFactory.getDefault();
CharacteristicParser customParser = new ...; // your own implementation
parser.registerParser(CHARACTERISTIC_UUID, customParser);
```

Please feel free to use and extend the library. It is distributed with Apache 2.0 license.
