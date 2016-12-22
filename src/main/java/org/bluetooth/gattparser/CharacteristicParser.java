package org.bluetooth.gattparser;

import java.util.Map;

import org.bluetooth.gattparser.spec.Characteristic;

public interface CharacteristicParser {

    Map<String, FieldHolder> parse(Characteristic characteristic, byte[] raw) throws CharacteristicFormatException;

}
