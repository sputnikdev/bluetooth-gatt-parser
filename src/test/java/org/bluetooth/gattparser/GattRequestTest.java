package org.bluetooth.gattparser;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.bluetooth.gattparser.spec.Field;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class GattRequestTest {

    private static final String CHARACTERISTIC_UUID = "char_uuid";

    @Test
    public void testGetControlPointField() throws Exception {
        List<Field> fields = new ArrayList<>();
        Field opControl = MockUtils.mockControlField("Operation Control", false, "C1");
        fields.add(opControl);
        fields.add(MockUtils.mockField("Field1", "C2"));
        GattRequest gattRequest = new GattRequest(CHARACTERISTIC_UUID, fields);
        assertEquals(opControl, gattRequest.getControlPointFieldHolder().getField());
        assertTrue(gattRequest.hasControlPoint());

        fields.remove(opControl);
        gattRequest = new GattRequest(CHARACTERISTIC_UUID, fields);
        assertFalse(gattRequest.hasControlPoint());
    }

    @Test
    public void testSetField() throws Exception {
        List<Field> fields = new ArrayList<>();
        Field opControl = MockUtils.mockControlField("Operation Control", false, "C1");
        fields.add(opControl);
        Field intField = MockUtils.mockField("Field1", "C2");
        Field longField = MockUtils.mockField("Field2", "C3");
        Field bigIntegerField = MockUtils.mockField("Field3", "C4");
        Field floatField = MockUtils.mockField("Field4", "C5");
        Field doubleField = MockUtils.mockField("Field5", "C6");
        Field stringField = MockUtils.mockField("Field6", "C7");
        fields.add(intField);
        fields.add(longField);
        fields.add(bigIntegerField);
        fields.add(floatField);
        fields.add(doubleField);
        fields.add(stringField);
        GattRequest gattRequest = new GattRequest(CHARACTERISTIC_UUID, fields);

        gattRequest.setField("Operation Control", 12);
        gattRequest.setField("Field1", 1);
        gattRequest.setField("Field2", 2L);
        gattRequest.setField("Field3", BigInteger.ONE);
        gattRequest.setField("Field4", 3.1F);
        gattRequest.setField("Field5", 4.1D);
        gattRequest.setField("Field6", "test_value");

        Map<String, FieldHolder> holders = gattRequest.getHolders();

        assertEquals(7, holders.size());
        assertEquals(12, holders.get("Operation Control").getRawValue());
        assertEquals(0, holders.get("Operation Control").getIndex());

        assertEquals(1, holders.get("Field1").getRawValue());
        assertEquals(1, holders.get("Field1").getIndex());

        assertEquals(2L, holders.get("Field2").getRawValue());
        assertEquals(2, holders.get("Field2").getIndex());

        assertEquals(BigInteger.ONE, holders.get("Field3").getRawValue());
        assertEquals(3, holders.get("Field3").getIndex());

        assertEquals(3.1F, holders.get("Field4").getRawValue());
        assertEquals(4, holders.get("Field4").getIndex());

        assertEquals(4.1D, holders.get("Field5").getRawValue());
        assertEquals(5, holders.get("Field5").getIndex());

        assertEquals("test_value", holders.get("Field6").getRawValue());
        assertEquals(6, holders.get("Field6").getIndex());
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
        new GattRequest(CHARACTERISTIC_UUID, Collections.emptyList());
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

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidFieldName() {
        List<Field> fields = new ArrayList<>();
        Field field1 = MockUtils.mockField("Field1", "C2");
        fields.add(field1);
        GattRequest gattRequest = new GattRequest(CHARACTERISTIC_UUID, fields);
        gattRequest.setField("invalid name", 1);
    }


}
