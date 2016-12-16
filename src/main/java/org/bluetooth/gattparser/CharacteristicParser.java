package org.bluetooth.gattparser;

import java.util.Map;

public interface CharacteristicParser {

    Map<String, Object> parse(ParserContext context, String characteristicUUID, byte[] raw)
            throws CharacteristicFormatException;

}
