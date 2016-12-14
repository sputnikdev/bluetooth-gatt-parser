package org.bluetooth.gattparser.spec;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FieldFormatTest {

    @Test
    public void testValueOf() throws Exception {
        assertFieldType("boolean", FieldType.BOOLEAN, 1, FieldFormat.valueOf("bOoLean"));
        assertFieldType("nibble", FieldType.UINT, 4, FieldFormat.valueOf("nIbblE"));
        assertFieldType("float32", FieldType.FLOAT, 32, FieldFormat.valueOf("fLoat32"));
        assertFieldType("float64", FieldType.FLOAT, 64, FieldFormat.valueOf("fLoAt64"));
        assertFieldType("SFLOAT", FieldType.FLOAT, 16, FieldFormat.valueOf("SFLOAT"));
        assertFieldType("FLOAT", FieldType.FLOAT, 32, FieldFormat.valueOf("FLOAT"));
        assertFieldType("duint16", FieldType.FLOAT, 16, FieldFormat.valueOf("duint16"));
        assertFieldType("utf8s", FieldType.UTF8S, 0, FieldFormat.valueOf("uTf8s"));
        assertFieldType("utf16s", FieldType.UTF16S, 0, FieldFormat.valueOf("Utf16s"));

        assertFieldType("7bit", FieldType.UINT, 7, FieldFormat.valueOf("7Bit"));
        assertFieldType("700bit", FieldType.UINT, 700, FieldFormat.valueOf("700biT"));
        assertFieldType("uint10", FieldType.UINT, 10, FieldFormat.valueOf("uiNT10"));
        assertFieldType("uint3", FieldType.UINT, 3, FieldFormat.valueOf("uiNT3"));

        assertFieldType("sint3", FieldType.SINT, 3, FieldFormat.valueOf("siNT3"));
        assertFieldType("sint65", FieldType.SINT, 65, FieldFormat.valueOf("siNT65"));
    }

    @Test(expected = IllegalStateException.class)
    public void testValueOfInvalidFormat() {
        FieldFormat.valueOf("siNTunknown");
    }

    private void assertFieldType(String name, FieldType fieldType, int size, FieldFormat actual) {
        assertEquals(name, actual.getName());
        assertEquals(fieldType, actual.getType());
        assertEquals(size, actual.getSize());
    }
}
