package org.sputnikdev.bluetooth.gattparser;

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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.sputnikdev.bluetooth.gattparser.spec.Field;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.sputnikdev.bluetooth.gattparser.spec.FieldType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class GattRequestTest {

    private static final String CHARACTERISTIC_UUID = "char_uuid";

    @Test
    public void testGetOpCodesField() throws Exception {
        List<Field> fields = new ArrayList<>();
        Field opControl = MockUtils.mockControlField("Op Codes", false, "C1");
        fields.add(opControl);
        fields.add(MockUtils.mockField("Field1", "C2"));
        GattRequest gattRequest = new GattRequest(CHARACTERISTIC_UUID, fields);
        assertEquals(opControl, gattRequest.getOpCodesFieldHolder().getField());
        assertTrue(gattRequest.hasOpCodesField());

        fields.remove(opControl);
        gattRequest = new GattRequest(CHARACTERISTIC_UUID, fields);
        assertFalse(gattRequest.hasOpCodesField());
    }

    @Test
    public void testSetField() throws Exception {
        List<Field> fields = new ArrayList<>();
        Field opControl = MockUtils.mockControlField("Op Code", false, "C3");
        fields.add(opControl);
        Field intField = MockUtils.mockField("Field1", FieldType.SINT, 32, "C2");
        Field longField = MockUtils.mockField("Field2", FieldType.SINT, 64, "C3");
        Field bigIntegerField = MockUtils.mockField("Field3", FieldType.SINT, 88, "C4");
        Field floatField = MockUtils.mockField("Field4", FieldType.FLOAT_IEE754, 32, "C5");
        Field doubleField = MockUtils.mockField("Field5", FieldType.FLOAT_IEE754, 64,"C6");
        Field stringField = MockUtils.mockField("Field6", "C7");
        fields.add(intField);
        fields.add(longField);
        fields.add(bigIntegerField);
        fields.add(floatField);
        fields.add(doubleField);
        fields.add(stringField);
        GattRequest gattRequest = new GattRequest(CHARACTERISTIC_UUID, fields);

        gattRequest.setField("Field1", 1);
        gattRequest.setField("Field2", 2L);
        gattRequest.setField("Field3", BigInteger.ONE);
        gattRequest.setField("Field4", 3.1F);
        gattRequest.setField("Field5", 4.1D);
        gattRequest.setField("Field6", "test_value");

        Map<String, FieldHolder> holders = gattRequest.getHolders();

        assertEquals(7, holders.size());

        // OpCode should be set to 1 as it corresponds to C3 flag
        assertEquals(1, holders.get("Op Code").getRawValue());

        assertEquals(1, holders.get("Field1").getRawValue());
        assertEquals(2L, holders.get("Field2").getRawValue());
        assertEquals(BigInteger.ONE, holders.get("Field3").getRawValue());
        assertEquals(3.1F, holders.get("Field4").getRawValue());
        assertEquals(4.1D, holders.get("Field5").getRawValue());
        assertEquals("test_value", holders.get("Field6").getRawValue());
    }


    @Test
    public void testGetCharacteristicUUID() throws Exception {
        List<Field> fields = new ArrayList<>();
        Field opControl = MockUtils.mockControlField("Operation Control", false);
        fields.add(opControl);
        GattRequest gattRequest = new GattRequest(CHARACTERISTIC_UUID, fields);
        assertEquals(CHARACTERISTIC_UUID, gattRequest.getCharacteristicUUID());
    }


    @Test(expected = IllegalArgumentException.class)
    public void testConstructor() {
        new GattRequest(CHARACTERISTIC_UUID, Collections.<Field>emptyList());
    }

    @Test
    public void testGetHolders() throws Exception {
        List<Field> fields = new ArrayList<>();
        Field field1 = MockUtils.mockField("Field1", "C2");
        Field field2 = MockUtils.mockField("Field2", "C2");
        Field field3 = MockUtils.mockField("Field3", "C3");
        Field field4 = MockUtils.mockField("Field4", "Mandatory");
        fields.add(field1);
        fields.add(field2);
        fields.add(field3);
        fields.add(field4);
        GattRequest gattRequest = new GattRequest(CHARACTERISTIC_UUID, fields);
        List<FieldHolder> holders = gattRequest.getRequiredHolders("C2");
        assertEquals(2, holders.size());
        assertEquals("C2", holders.get(0).getField().getRequirements().get(0));
        assertEquals("C2", holders.get(1).getField().getRequirements().get(0));

        holders = gattRequest.getRequiredHolders("C3");
        assertEquals("C3", holders.get(0).getField().getRequirements().get(0));

        holders = gattRequest.getRequiredHolders("Mandatory");
        assertEquals("Mandatory", holders.get(0).getField().getRequirements().get(0));
    }

    @Test
    public void testGetRequiredHolders() throws Exception {
        List<Field> fields = new ArrayList<>();
        Field field1 = MockUtils.mockField("Field1", "C2");
        Field field2 = MockUtils.mockField("Field2", "C2");
        Field field3 = MockUtils.mockField("Field3", "C3");
        Field field4 = MockUtils.mockField("Field4", "Mandatory");
        fields.add(field1);
        fields.add(field2);
        fields.add(field3);
        fields.add(field4);
        GattRequest gattRequest = new GattRequest(CHARACTERISTIC_UUID, fields);
        List<FieldHolder> holders = gattRequest.getRequiredFieldHolders();
        assertEquals(1, holders.size());
        assertEquals("Mandatory", holders.get(0).getField().getRequirements().get(0));

        Field controlField = MockUtils.mockControlField("Op Codes", true, "C1", "C2", "C3");
        fields.add(0, controlField);

        gattRequest = new GattRequest(CHARACTERISTIC_UUID, fields);
        holders = gattRequest.getRequiredFieldHolders();
        assertEquals(2, holders.size());
        assertEquals("Op Codes", holders.get(0).getField().getName());
        assertEquals("Field4", holders.get(1).getField().getName());

        // set requirement for C2
        gattRequest.getOpCodesFieldHolder().setInteger(2);
        holders = gattRequest.getRequiredFieldHolders();
        assertEquals(4, holders.size());
        assertEquals("Op Codes", holders.get(0).getField().getName());
        assertEquals("Field4", holders.get(1).getField().getName());
        assertEquals("Field1", holders.get(2).getField().getName());
        assertEquals("Field2", holders.get(3).getField().getName());

        // set requirement for C3
        gattRequest.getOpCodesFieldHolder().setInteger(3);
        holders = gattRequest.getRequiredFieldHolders();
        assertEquals(3, holders.size());
        assertEquals("Op Codes", holders.get(0).getField().getName());
        assertEquals("Field4", holders.get(1).getField().getName());
        assertEquals("Field3", holders.get(2).getField().getName());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidFieldName() {
        List<Field> fields = new ArrayList<>();
        Field field1 = MockUtils.mockField("Field1", "C2");
        fields.add(field1);
        GattRequest gattRequest = new GattRequest(CHARACTERISTIC_UUID, fields);
        gattRequest.setField("invalid name", 1);
    }

}
