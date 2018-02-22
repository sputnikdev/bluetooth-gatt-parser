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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author Vlad Kolotov
 */
@XStreamAlias("Enumerations")
public class Enumerations {

    @XStreamImplicit
    private List<Enumeration> enumerations;
    @XStreamImplicit
    private List<Reserved> reserves;

    public List<Enumeration> getEnumerations() {
        return enumerations != null ? Collections.unmodifiableList(enumerations) : null;
    }

    public List<Reserved> getReserves() {
        return reserves != null ? Collections.unmodifiableList(reserves) : reserves;
    }
}
