package org.sputnikdev.bluetooth.gattparser;

import org.sputnikdev.bluetooth.gattparser.spec.Field;

public interface FieldHolder {

    Field getField();

    int size();

    default boolean isPrimitive() {
        return getField().isReference();
    }

    default <T> T cast() {
        return (T) this;
    }

}
