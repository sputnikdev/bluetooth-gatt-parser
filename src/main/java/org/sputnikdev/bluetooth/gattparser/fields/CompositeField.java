package org.sputnikdev.bluetooth.gattparser.fields;

import org.sputnikdev.bluetooth.gattparser.CompositeFieldHolder;

public enum CompositeField {

    exact_time_256(LocalDateTimeFieldHolder.class),
    day_date_time(LocalDateTimeFieldHolder.class);

    private final Class<? extends CompositeFieldHolder<?>> holder;

    CompositeField(Class<? extends CompositeFieldHolder<?>> holder) {
        this.holder = holder;
    }

    public Class<? extends CompositeFieldHolder<?>> getHolder() {
        return holder;
    }

}
