package org.sputnikdev.bluetooth.gattparser;

import org.sputnikdev.bluetooth.gattparser.spec.Field;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class CompositeFieldHolder<T> implements FieldHolder {

    private final Field field;
    private final List<FieldHolder> holders;
    private final int size;

    public CompositeFieldHolder(Field field, List<FieldHolder> holders, int size) {
        this.field = field;
        this.holders = Collections.unmodifiableList(holders);
        this.size = size;
    }

    @Override
    public Field getField() {
        return field;
    }

    public List<FieldHolder> getHolders() {
        return holders;
    }

    @Override
    public int size() {
        return size;
    }

    /**
     * Returns a map of all primitives that this holder holds (recursively).
     * @return a map of primitives
     */
    public Map<String, PrimitiveFieldHolder> getPrimitives() {
        return holders.stream().flatMap(this::flatten)
                .collect(Collectors.toMap(holder -> holder.getField().getName(), Function.identity()));
    }

    /**
     * Returns composite value.
     * @return composite value
     */
    public abstract T getValue();

    /**
     * Set composite value.
     * @param value composite value
     */
    public abstract void setValue(T value);

    private Stream<PrimitiveFieldHolder> flatten(FieldHolder holder) {
        if (holder.isPrimitive()) {
            return Stream.of((PrimitiveFieldHolder) holder);
        } else {
            return ((CompositeFieldHolder<?>) holder).holders.stream().flatMap(this::flatten);
        }
    }

}
