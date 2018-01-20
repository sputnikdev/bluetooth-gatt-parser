[![Maven Central](https://img.shields.io/maven-central/v/org.sputnikdev/bluetooth-gatt-parser.svg)](https://mvnrepository.com/artifact/org.sputnikdev/bluetooth-gatt-parser)
[![Build Status](https://travis-ci.org/sputnikdev/bluetooth-gatt-parser.svg?branch=master)](https://travis-ci.org/sputnikdev/bluetooth-gatt-parser)
[![Coverage Status](https://coveralls.io/repos/github/sputnikdev/bluetooth-gatt-parser/badge.svg?branch=master)](https://coveralls.io/github/sputnikdev/bluetooth-gatt-parser?branch=master)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/42d4968bc4ae4474b2cda4d01f8e4d56)](https://www.codacy.com/app/vkolotov/bluetooth-gatt-parser?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=sputnikdev/bluetooth-gatt-parser&amp;utm_campaign=Badge_Grade)
[![Join the chat at https://gitter.im/sputnikdev/bluetooth-gatt-parser](https://badges.gitter.im/sputnikdev/bluetooth-gatt-parser.svg)](https://gitter.im/sputnikdev/bluetooth-gatt-parser?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
# bluetooth-gatt-parser

A **simple** library/framework to work with Bluetooth Smart (BLE) GATT services and characteristics.

Have a look at an example of parsing a standard characteristic ([Battery Level 0x2A19](https://www.bluetooth.com/specifications/gatt/viewer?attributeXmlFile=org.bluetooth.characteristic.battery_level.xml)) value:
```java
BluetoothGattParserFactory.getDefault().parse("2A19", new byte[] {51}).get("Level").getInteger(null);
```
This would print 51.

**Features:**

1. Supports 99% of the existing/standard [GATT services and characteristics specifications](https://www.bluetooth.com/specifications/gatt).
2. Parse/read single and multi field characteristics into a user-friendly data format.
3. Writing single and multi field characteristics.
4. Validating input data whether it conforms to GATT specifications (format types and mandatory fields).
5. Extensibility. User defined services and characteristics.
6. Support for all defined [format types](https://www.bluetooth.com/specifications/assigned-numbers/format-types).

**Start using the library by including a maven dependency in your project:**
```xml
<dependency>
  <groupId>org.sputnikdev</groupId>
  <artifactId>bluetooth-gatt-parser</artifactId>
  <version>X.Y.Z</version>
</dependency>
```

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

See more examples in the integration tests: [GenericCharacteristicParserIntegrationTest](src/test/java/org/bluetooth/gattparser/GenericCharacteristicParserIntegrationTest.java)

---
**Extending the library with user defined services and characteristics**

The gatt-parser library is designed to be able to add support for some new custom services/characteristics or to override an existing ("approved") [service and characteristic](https://www.bluetooth.com/specifications/gatt). This can be done by just providing a new GATT XML file which specifies your service and characteristic (have a look at the standard definition for the [Battery Level characteristic](src/main/resources/gatt/characteristic/org.bluetooth.characteristic.battery_level.xml)). The library will read your custom files and build internal rules/conditions for parsing and serialization of your custom characteristics. This means you don't have to write any code to parse/serialize simple or complex custom characteristics.

_Loading XML GATT specification files (GATT-like specifications) from a folder:_

```java
BluetoothGattParser parser = BluetoothGattParserFactory.getDefault();
File extensionsFolderFile = new File(..);
gattParser.loadExtensionsFromFolder(extensions);
```

**A custom parser can be added for a characteristic if you are not satisfied with the default one**

See the default one for a hint and a reference: [GenericCharacteristicParser](src/main/java/org/bluetooth/gattparser/GenericCharacteristicParser.java)
```java
BluetoothGattParser parser = BluetoothGattParserFactory.getDefault();
CharacteristicParser customParser = new ...; // your own implementation
parser.registerParser(CHARACTERISTIC_UUID, customParser);
```

---
## Contribution

You are welcome to contribute to the project, the project environment is designed to make it easy by using:
* Travis CI to release artifacts directly to the Maven Central repository.
* Code style rules to support clarity and supportability. The results can be seen in the Codacy. 
* Code coverage reports in the Coveralls to maintain sustainability. 100% of code coverage with unittests is the target.

The build process is streamlined by using standard maven tools. 

To build the project with maven:
```bash
mvn clean install
```

To cut a new release and upload it to the Maven Central Repository:
```bash
mvn release:prepare -B
mvn release:perform
```
Travis CI process will take care of everything, you will find a new artifact in the Maven Central repository when the release process finishes successfully.
