package org.sputnikdev.bluetooth.gattparser.fields;

import org.sputnikdev.bluetooth.gattparser.CompositeFieldHolder;
import org.sputnikdev.bluetooth.gattparser.FieldHolder;
import org.sputnikdev.bluetooth.gattparser.PrimitiveFieldHolder;
import org.sputnikdev.bluetooth.gattparser.spec.Field;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class LocalDateTimeFieldHolder extends CompositeFieldHolder<LocalDateTime> {

    private static final double FRACTION_256 = 256.0D;
    private static final double NANOS_PER_SECOND = 1000000000.0D;
    private static final double NANOS_FRACTION_256 = NANOS_PER_SECOND / FRACTION_256;

    public LocalDateTimeFieldHolder(Field field, List<FieldHolder> holders, int size) {
        super(field, holders, size);
    }

    @Override
    public LocalDateTime getValue() {
        Map<String, PrimitiveFieldHolder> components = getPrimitives();
        LocalDateTime dateTime = LocalDateTime.of(
                components.get("Year").getInteger(),
                components.get("Month").getInteger(),
                components.get("Day").getInteger(),
                components.get("Hours").getInteger(),
                components.get("Minutes").getInteger(),
                components.get("Seconds").getInteger());

        if (components.containsKey("Fractions256")) {
            dateTime = dateTime.plusNanos((long) (NANOS_FRACTION_256 * components.get("Fractions256").getDouble()));
        }

        return dateTime;
    }

    @Override
    public void setValue(LocalDateTime value) {
        Map<String, PrimitiveFieldHolder> components = getPrimitives();
        components.get("Year").setInteger(value.getYear());
        components.get("Month").setInteger(value.getMonthValue());
        components.get("Day").setInteger(value.getDayOfMonth());
        components.get("Hours").setInteger(value.getHour());
        components.get("Minutes").setInteger(value.getMinute());
        components.get("Seconds").setInteger(value.getSecond());

        if (components.containsKey("Fractions256")) {
            components.get("Fractions256").setInteger((int) (FRACTION_256 / (NANOS_PER_SECOND / value.getNano())));
        }
    }

}
