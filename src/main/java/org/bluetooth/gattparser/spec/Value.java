package org.bluetooth.gattparser.spec;

import java.util.Collections;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("Value")
public class Value {

    @XStreamImplicit
    private List<Field> fields;

    public List<Field> getFields() {
        return Collections.unmodifiableList(fields);
    }

    public Field getFlags() {
        for (Field field : fields) {
            if ("flags".equalsIgnoreCase(field.getName()) && field.getBitField() != null) {
                return field;
            }
        }
        return null;
    }
}
