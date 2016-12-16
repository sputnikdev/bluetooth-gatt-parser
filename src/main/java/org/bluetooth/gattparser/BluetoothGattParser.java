package org.bluetooth.gattparser;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bluetooth.gattparser.spec.Characteristic;
import org.bluetooth.gattparser.spec.Field;

public class BluetoothGattParser {

    private final ParserContext context;
    private final Map<String, CharacteristicParser> customParsers = new HashMap<>();
    private final CharacteristicParser defaultParser;

    BluetoothGattParser(ParserContext parserContext, CharacteristicParser defaultParser) {
        this.context = parserContext;
        this.defaultParser = defaultParser;
    }

    public Map<String, Object> parse(String characteristicUUID, byte[] raw)
            throws CharacteristicFormatException {
        synchronized (customParsers) {
            if (customParsers.containsKey(characteristicUUID)) {
                return customParsers.get(characteristicUUID).parse(context, characteristicUUID, raw);
            }
            return defaultParser.parse(context, characteristicUUID, raw);
        }
    }

    public List<Field> getFields(String characteristicUUID) {
        Characteristic characteristic = context.getSpecificationReader().getCharacteristic(characteristicUUID);
        if (characteristic != null && characteristic.getValue() != null) {
            return context.getSpecificationReader().getCharacteristic(characteristicUUID).getValue().getFields();
        }
        return Collections.emptyList();
    }

    public void registerParser(String characteristicUUID, CharacteristicParser parser) {
        synchronized (customParsers) {
            customParsers.put(characteristicUUID, parser);
        }
    }

}
