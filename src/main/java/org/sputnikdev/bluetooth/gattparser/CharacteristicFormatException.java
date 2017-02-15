package org.sputnikdev.bluetooth.gattparser;

public class CharacteristicFormatException extends RuntimeException {

    public CharacteristicFormatException() {
        super();
    }

    public CharacteristicFormatException(String message) {
        super(message);
    }

    public CharacteristicFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public CharacteristicFormatException(Throwable cause) {
        super(cause);
    }

    protected CharacteristicFormatException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
