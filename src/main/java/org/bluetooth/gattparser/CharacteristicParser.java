package org.bluetooth.gattparser;

import java.util.LinkedHashMap;

import org.bluetooth.gattparser.spec.Characteristic;

public interface CharacteristicParser {

    LinkedHashMap<String, FieldHolder> parse(Characteristic characteristic, byte[] raw)
            throws CharacteristicFormatException;

}
