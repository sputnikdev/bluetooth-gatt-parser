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

import org.sputnikdev.bluetooth.gattparser.spec.Field;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FieldHolderTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Field field;

    @Before
    public void setUp() {

    }

    @Test
    public void testGetField() throws Exception {
        FieldHolder fieldHolder = new FieldHolder(field, new Object());
        assertEquals(field, fieldHolder.getField());
    }

    @Test
    public void testIsNumber() throws Exception {
        FieldHolder fieldHolder = new FieldHolder(field, new Object());
        when(field.getFormat().isNumber()).thenReturn(true);
        assertTrue(fieldHolder.isNumber());
        when(field.getFormat().isNumber()).thenReturn(false);
        assertFalse(fieldHolder.isNumber());
        verify(field.getFormat(), times(2)).isNumber();
    }

    @Test
    public void testIsBoolean() throws Exception {
        FieldHolder fieldHolder = new FieldHolder(field, new Object());
        when(field.getFormat().isBoolean()).thenReturn(true);
        assertTrue(fieldHolder.isBoolean());
        when(field.getFormat().isBoolean()).thenReturn(false);
        assertFalse(fieldHolder.isBoolean());
        verify(field.getFormat(), times(2)).isBoolean();
    }

    @Test
    public void testIsString() throws Exception {
        FieldHolder fieldHolder = new FieldHolder(field, new Object());
        when(field.getFormat().isString()).thenReturn(true);
        assertTrue(fieldHolder.isString());
        when(field.getFormat().isString()).thenReturn(false);
        assertFalse(fieldHolder.isString());
        verify(field.getFormat(), times(2)).isString();
    }

    @Test
    public void testIsStruct() throws Exception {
        FieldHolder fieldHolder = new FieldHolder(field, new Object());
        when(field.getFormat().isStruct()).thenReturn(true);
        assertTrue(fieldHolder.isStruct());
        when(field.getFormat().isStruct()).thenReturn(false);
        assertFalse(fieldHolder.isStruct());
        verify(field.getFormat(), times(2)).isStruct();
    }

    @Test
    public void testGetInteger() throws Exception {
//        assertGetInteger(0, null, null, null, 0);
//        assertGetInteger(1, null, null, null, 1);
//        assertGetInteger(10, null, null, null, 10L);
//        assertGetInteger(1, null, null, null, BigInteger.ONE);
//        assertGetInteger(1, null, null, null, 1.9F);
//        assertGetInteger(1, null, null, null, 1.4F);
//        assertGetInteger(1, null, null, null, 1.6D);
//
//        assertGetInteger(1000000000, 9, null, null, 1);
//        assertGetInteger((int) 10000000000L, 10, null, null, 1);
//        assertGetInteger(1, null, 0, null, 1);
//        assertGetInteger(Integer.MIN_VALUE, null, 31, null, 1);
//        assertGetInteger(1000000000, null, null, 1000000000, 1);
//
//        assertGetInteger(0, -1, null, null, 4);
//        assertGetInteger(1, -1, null, null, 5);
//
//        assertGetInteger(1, null, -1, null, 1);
//        assertGetInteger(2, null, -1, null, 4);
//
//        assertGetInteger(-1, null, null, -1, 1);

        assertGetInteger(null, 3, 4, 2, "test");
    }

    @Test
    public void testGetLong() throws Exception {
        assertGetLong(1L, null, null, null, 1);
        assertGetLong(10L, null, null, null, 10L);
        assertGetLong(1L, null, null, null, BigInteger.ONE);
        assertGetLong(1L, null, null, null, 1.9F);
        assertGetLong(1L, null, null, null, 1.4F);
        assertGetLong(1L, null, null, null, 1.6D);

        assertGetLong(1000000000000000000L, 18, null, null, 1);
        assertGetLong(1L, null, 0, null, 1);

        assertGetLong(Long.MAX_VALUE, null, 63, null, 1);
        assertGetLong(1000000000L, null, null, 1000000000, 1);

        assertGetLong(0L, -1, null, null, 4);
        assertGetLong(1L, -1, null, null, 5);

        assertGetLong(1L, null, -1, null, 1);
        assertGetLong(2L, null, -1, null, 4);

        assertGetLong(-1L, null, null, -1, 1);

        assertGetLong(null, null, null, null, "test");
    }

    @Test
    public void testGetBigInteger() throws Exception {
        assertGetBigInteger(BigInteger.ZERO, null, null, null, 0);
        assertGetBigInteger(BigInteger.valueOf(10), null, null, null, 10L);
        assertGetBigInteger(BigInteger.valueOf(1), null, null, null, BigInteger.ONE);
        //TODO rounding differs from getInteger and getLong
        assertGetBigInteger(BigInteger.valueOf(2), null, null, null, 1.9F);
        assertGetBigInteger(BigInteger.valueOf(1), null, null, null, 1.4F);
        assertGetBigInteger(BigInteger.valueOf(2), null, null, null, 1.6D);

        assertGetBigInteger(new BigInteger("1000000000000000000"), 18, null, null, BigInteger.ONE);
        assertGetBigInteger(BigInteger.ONE, null, 0, null, 1);

        assertGetBigInteger(BigInteger.ZERO, -1, null, null, new BigInteger("4"));
        assertGetBigInteger(BigInteger.ONE, -1, null, null, new BigInteger("5"));

        assertGetBigInteger(BigInteger.ONE, null, -1, null, 1);
        assertGetBigInteger(new BigInteger("2"), null, -1, null, 4);

        assertGetBigInteger(new BigInteger("-1"), null, null, -1, 1);

        assertGetBigInteger(null, null, null, null, "test");
    }

    @Test
    public void testGetBoolean() {
        assertGetBoolean(Boolean.TRUE, Boolean.TRUE);
        assertGetBoolean(Boolean.FALSE, Boolean.FALSE);
        assertGetBoolean(Boolean.FALSE, 0);
        assertGetBoolean(Boolean.TRUE, 1);
        assertGetBoolean(null, 2);
        assertGetBoolean(null, -1);

        assertGetBoolean(Boolean.TRUE, "true");
        assertGetBoolean(Boolean.FALSE, "false");
    }

    @Test
    public void testGetString() {
        assertGetString("test", "test");
        assertGetString("-1", -1);
        assertGetString("100", 100);
        assertGetString("123456.789", 123456.789D);
        assertGetString("1.0E100", Math.pow(10, 100));
        assertGetString("5.5", 5.5F);
        assertGetString("100", 100L);
        assertGetString("-123456789", new BigInteger("-123456789"));
    }

    @Test
    public void testGetRawValue() {
        Object value = new Object();
        assertEquals(value, new FieldHolder(field, value).getRawValue());
    }

    @Test
    public void testGetFloat() {
        assertGetFloat(0.1F, null, null, null, 0.1);
        assertGetFloat(0.1F, null, null, null, 0.1D);
        assertGetFloat(5F, null, null, null, 5);
        assertGetFloat(5F, null, null, null, 5L);
        assertGetFloat(5.5F, null, null, null, "5.5");
        assertGetFloat(12345F, null, null, null, new BigInteger("12345"));

        assertGetFloat(Float.MAX_VALUE, 38, null, null, 3.4028235);
        assertGetFloat(1F, null, 0, null, 1);

        assertGetFloat(Float.MAX_VALUE, null, 127, null, 0x1.fffffeP+0f);
        assertGetFloat(1000000000F, null, null, 1000000000, 1);

        assertGetFloat(0.4F, -1, null, null, 4);
        assertGetFloat(0.5F, -1, null, null, 5);

        assertGetFloat(0.5F, null, -1, null, 1);
        assertGetFloat(2F, null, -1, null, 4);

        assertGetFloat(-1F, null, null, -1, 1);

        assertGetFloat(null, null, null, null, "test");
    }

    @Test
    public void testGetDouble() {
        assertGetDouble(0.1D, null, null, null, 0.1);
        assertGetDouble(0.1D, null, null, null, 0.1D);
        assertGetDouble(5D, null, null, null, 5);
        assertGetDouble(5D, null, null, null, 5L);
        assertGetDouble(5.5D, null, null, null, "5.5");
        assertGetDouble(12345D, null, null, null, new BigInteger("12345"));

        assertGetDouble(Double.MAX_VALUE, 308, null, null, 1.7976931348623157);
        assertGetDouble(1D, null, 0, null, 1);

        assertGetDouble(Double.MAX_VALUE, null, 1023, null, 0x1.fffffffffffffP+0);
        assertGetDouble(1000000000D, null, null, 1000000000, 1);

        assertGetDouble(0.4D, -1, null, null, 4);
        assertGetDouble(0.5D, -1, null, null, 5);

        assertGetDouble(0.5D, null, -1, null, 1);
        assertGetDouble(2D, null, -1, null, 4);

        assertGetDouble(-1D, null, null, -1, 1);

        assertGetDouble(null, null, null, null, "test");
    }

    private void assertGetInteger(Integer expected, Integer decimalExponent, Integer binaryExponent,
            Integer multiplier, Object value) {
        FieldHolder fieldHolder = new FieldHolder(field, value);
        when(field.getDecimalExponent()).thenReturn(decimalExponent);
        when(field.getBinaryExponent()).thenReturn(binaryExponent);
        when(field.getMultiplier()).thenReturn(multiplier);
        assertEquals(expected, fieldHolder.getInteger(null));
    }

    private void assertGetLong(Long expected, Integer decimalExponent, Integer binaryExponent,
            Integer multiplier, Object value) {
        FieldHolder fieldHolder = new FieldHolder(field, value);
        when(field.getDecimalExponent()).thenReturn(decimalExponent);
        when(field.getBinaryExponent()).thenReturn(binaryExponent);
        when(field.getMultiplier()).thenReturn(multiplier);
        assertEquals(expected, fieldHolder.getLong(null));
    }

    private void assertGetFloat(Float expected, Integer decimalExponent, Integer binaryExponent,
            Integer multiplier, Object value) {
        FieldHolder fieldHolder = new FieldHolder(field, value);
        when(field.getDecimalExponent()).thenReturn(decimalExponent);
        when(field.getBinaryExponent()).thenReturn(binaryExponent);
        when(field.getMultiplier()).thenReturn(multiplier);
        if (expected == null) {
            assertNull(fieldHolder.getFloat(null));
        } else {
            assertEquals(expected, fieldHolder.getFloat(null), 0.00001);
        }
    }

    private void assertGetDouble(Double expected, Integer decimalExponent, Integer binaryExponent,
            Integer multiplier, Object value) {
        FieldHolder fieldHolder = new FieldHolder(field, value);
        when(field.getDecimalExponent()).thenReturn(decimalExponent);
        when(field.getBinaryExponent()).thenReturn(binaryExponent);
        when(field.getMultiplier()).thenReturn(multiplier);
        if (expected == null) {
            assertNull(fieldHolder.getDouble(null));
        } else {
            assertEquals(expected, fieldHolder.getDouble(null), 0.00001);
        }
    }

    private void assertGetBigInteger(BigInteger expected, Integer decimalExponent, Integer binaryExponent,
            Integer multiplier, Object value) {
        FieldHolder fieldHolder = new FieldHolder(field, value);
        when(field.getDecimalExponent()).thenReturn(decimalExponent);
        when(field.getBinaryExponent()).thenReturn(binaryExponent);
        when(field.getMultiplier()).thenReturn(multiplier);
        assertEquals(expected, fieldHolder.getBigInteger(null));
    }

    private void assertGetBoolean(Boolean expected, Object value) {
        FieldHolder fieldHolder = new FieldHolder(field, value);
        assertEquals(expected, fieldHolder.getBoolean(null));
    }

    private void assertGetString(String expected, Object value) {
        FieldHolder fieldHolder = new FieldHolder(field, value);
        assertEquals(expected, fieldHolder.getString(null));
    }

}
