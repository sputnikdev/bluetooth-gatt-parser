package org.sputnikdev.bluetooth.gattparser.spec;

/*-
 * #%L
 * org.sputnikdev:bluetooth-gatt-parser
 * %%
 * Copyright (C) 2017 Sputnik Dev
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Collections;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 *
 * @author Vlad Kolotov
 */
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
