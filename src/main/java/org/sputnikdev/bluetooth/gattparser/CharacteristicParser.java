package org.sputnikdev.bluetooth.gattparser;

import java.util.Collection;
import java.util.LinkedHashMap;

import org.sputnikdev.bluetooth.gattparser.spec.Characteristic;

public interface CharacteristicParser {

    LinkedHashMap<String, FieldHolder> parse(Characteristic characteristic, byte[] raw)
            throws CharacteristicFormatException;

    byte[] serialize(Collection<FieldHolder> fieldHolders) throws CharacteristicFormatException;

}
