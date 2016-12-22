package org.bluetooth.gattparser;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bluetooth.gattparser.num.FloatingPointNumberFormatter;
import org.bluetooth.gattparser.num.RealNumberFormatter;
import org.bluetooth.gattparser.spec.Bit;
import org.bluetooth.gattparser.spec.Characteristic;
import org.bluetooth.gattparser.spec.Field;
import org.bluetooth.gattparser.spec.FieldFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyByte;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(BluetoothGattParserFactory.class)
public class GenericCharacteristicParserTest {

    private final static String CHARACTERISTIC_UUID = "2A19";

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Field flagField;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Characteristic characteristic;
    @Mock
    private RealNumberFormatter twosComplementNumberFormatter;
    @Mock
    private FloatingPointNumberFormatter ieee754NumberFormatter;
    @Mock
    private FloatingPointNumberFormatter ieee11073NumberFormatter;

    private GenericCharacteristicParser parser = new GenericCharacteristicParser();

    @Before
    public void setUp() {
        PowerMockito.mockStatic(BluetoothGattParserFactory.class);
        when(characteristic.getUuid()).thenReturn(CHARACTERISTIC_UUID);
        when(BluetoothGattParserFactory.getTwosComplementNumberFormatter()).thenReturn(twosComplementNumberFormatter);
        when(BluetoothGattParserFactory.getIEEE754FloatingPointNumberFormatter()).thenReturn(ieee754NumberFormatter);
        when(BluetoothGattParserFactory.getIEEE11073FloatingPointNumberFormatter()).thenReturn(ieee11073NumberFormatter);
    }

    @Test
    public void testParseFlags() throws Exception {
        List<Bit> bits = new ArrayList<>();
        when(flagField.getBitField().getBits()).thenReturn(bits);
        when(flagField.getFormat().getSize()).thenReturn(15);
        bits.add(mockBit(0, 1, "C"));
        bits.add(mockBit(1, 2, "C"));
        bits.add(mockBit(2, 1, "C"));
        bits.add(mockBit(3, 3, "C"));
        bits.add(mockBit(4, 2, "C"));
        bits.add(mockBit(5, 2, "C"));
        bits.add(mockBit(6, 4, "C"));

        when(twosComplementNumberFormatter.deserializeInteger(BitSet.valueOf(new byte[]{0b1}), 1, false)).thenReturn(1);
        when(twosComplementNumberFormatter.deserializeInteger(BitSet.valueOf(new byte[]{0b10}), 2, false)).thenReturn(2);
        when(twosComplementNumberFormatter.deserializeInteger(BitSet.valueOf(new byte[]{0b0}), 1, false)).thenReturn(0);
        when(twosComplementNumberFormatter.deserializeInteger(BitSet.valueOf(new byte[]{0b010}), 3, false)).thenReturn(2);
        when(twosComplementNumberFormatter.deserializeInteger(BitSet.valueOf(new byte[]{0b11}), 2, false)).thenReturn(3);
        when(twosComplementNumberFormatter.deserializeInteger(BitSet.valueOf(new byte[]{0b0}), 2, false)).thenReturn(0);
        when(twosComplementNumberFormatter.deserializeInteger(BitSet.valueOf(new byte[]{0b1010}), 4, false)).thenReturn(10);

        int[] flags = parser.parseFlags(flagField, new byte[] {(byte) 0b10100101, (byte) 0b01010001});
        assertArrayEquals(new int[] {1, 2, 0, 2, 3, 0, 10}, flags);
    }

    @Test(expected = CharacteristicFormatException.class)
    public void testParseNotValidForRead() throws CharacteristicFormatException {
        when(characteristic.isValidForRead()).thenReturn(Boolean.FALSE);
        parser.parse(characteristic, new byte[]{ });
        verify(characteristic.isValidForRead(), times(1));
    }

    @Test
    public void testGetFlags() {
        List<Bit> bits = new ArrayList<>();

        bits.add(mockBit(0, 1, "A"));
        bits.add(mockBit(1, 2, "B"));
        bits.add(mockBit(2, 1, "C"));
        bits.add(mockBit(3, 3, "D"));

        bits.add(mockBit(4, 2, "E"));
        bits.add(mockBit(5, 2, "F"));
        bits.add(mockBit(6, 4, "G"));

        when(characteristic.getValue().getFlags()).thenReturn(flagField);
        when(flagField.getBitField().getBits()).thenReturn(bits);
        when(flagField.getFormat()).thenReturn(FieldFormat.valueOf("15bit"));
        when(twosComplementNumberFormatter.deserializeInteger(BitSet.valueOf(new byte[]{0b1}), 1, false)).thenReturn(1);
        when(twosComplementNumberFormatter.deserializeInteger(BitSet.valueOf(new byte[]{0b10}), 2, false)).thenReturn(2);
        when(twosComplementNumberFormatter.deserializeInteger(BitSet.valueOf(new byte[]{0b0}), 1, false)).thenReturn(0);
        when(twosComplementNumberFormatter.deserializeInteger(BitSet.valueOf(new byte[]{0b010}), 3, false)).thenReturn(2);
        when(twosComplementNumberFormatter.deserializeInteger(BitSet.valueOf(new byte[]{0b11}), 2, false)).thenReturn(3);
        when(twosComplementNumberFormatter.deserializeInteger(BitSet.valueOf(new byte[]{0b0}), 2, false)).thenReturn(0);
        when(twosComplementNumberFormatter.deserializeInteger(BitSet.valueOf(new byte[]{0b1010}), 4, false)).thenReturn(10);

        Set<String> flags = parser.getFlags(characteristic, new byte[] {(byte) 0b10100101, (byte) 0b01010001});
        assertTrue(flags.contains("A1"));
        assertTrue(flags.contains("B2"));
        assertTrue(flags.contains("C0"));
        assertTrue(flags.contains("D2"));
        assertTrue(flags.contains("E3"));
        assertTrue(flags.contains("F0"));
        assertTrue(flags.contains("G10"));
    }

    @Test
    public void testParseFormats() throws CharacteristicFormatException, UnsupportedEncodingException {
        when(ieee754NumberFormatter.deserializeSFloat(any())).thenReturn(0.0F);
        when(ieee754NumberFormatter.deserializeFloat(any())).thenReturn(0.0F);
        when(ieee754NumberFormatter.deserializeDouble(any())).thenReturn(0.0D);

        when(ieee11073NumberFormatter.deserializeSFloat(any())).thenReturn(0.0F);
        when(ieee11073NumberFormatter.deserializeFloat(any())).thenReturn(0.0F);

        when(twosComplementNumberFormatter.deserializeInteger(any(), anyByte(), anyBoolean())).thenReturn(0);
        when(twosComplementNumberFormatter.deserializeLong(any(), anyByte(), anyBoolean())).thenReturn(0L);
        when(twosComplementNumberFormatter.deserializeBigInteger(any(), anyByte(), anyBoolean())).thenReturn(BigInteger.ZERO);


        byte[] data = new byte[] {(byte) 0b0};
        BitSet bits = BitSet.valueOf(data);
        assertParseFormat(false, "boolean", new byte[] {(byte) 0b0});
        assertParseFormat(true, "boolean", new byte[] {(byte) 0b1});

        assertParseFormat(0, "2bit", data);
        verify(twosComplementNumberFormatter, times(1)).deserializeInteger(bits, 2, false);
        assertParseFormat(0, "8bit", data);
        verify(twosComplementNumberFormatter, times(1)).deserializeInteger(bits, 8, false);
        assertParseFormat(0, "uint8", data);
        verify(twosComplementNumberFormatter, times(2)).deserializeInteger(bits, 8, false);
        assertParseFormat(0, "sint8", data);
        verify(twosComplementNumberFormatter, times(1)).deserializeInteger(bits, 8, true);

        assertParseFormat(0, "sint31", data);
        verify(twosComplementNumberFormatter, times(1)).deserializeInteger(bits, 31, true);
        assertParseFormat(0, "uint31", data);
        verify(twosComplementNumberFormatter, times(1)).deserializeInteger(bits, 31, false);
        assertParseFormat(0, "sint32", data);
        verify(twosComplementNumberFormatter, times(1)).deserializeInteger(bits, 32, true);
        assertParseFormat(0L, "uint32", data);
        verify(twosComplementNumberFormatter, times(1)).deserializeLong(bits, 32, false);
        assertParseFormat(0L, "sint33", data);
        verify(twosComplementNumberFormatter, times(1)).deserializeLong(bits, 33, true);

        assertParseFormat(0L, "sint63", data);
        verify(twosComplementNumberFormatter, times(1)).deserializeLong(bits, 63, true);
        assertParseFormat(0L, "uint63", data);
        verify(twosComplementNumberFormatter, times(1)).deserializeLong(bits, 63, false);
        assertParseFormat(0L, "sint64", data);
        verify(twosComplementNumberFormatter, times(1)).deserializeLong(bits, 64, true);
        assertParseFormat(BigInteger.ZERO, "uint64", data);
        verify(twosComplementNumberFormatter, times(1)).deserializeBigInteger(bits, 64, false);
        assertParseFormat(BigInteger.ZERO, "sint65", data);
        verify(twosComplementNumberFormatter, times(1)).deserializeBigInteger(bits, 65, true);

        assertParseFormat(0.0F, "float32", data);
        verify(ieee754NumberFormatter, times(1)).deserializeFloat(bits);
        assertParseFormat(0.0D, "float64", data);
        verify(ieee754NumberFormatter, times(1)).deserializeDouble(bits);
        assertParseFormat(0.0F, "sfloat", data);
        verify(ieee11073NumberFormatter, times(1)).deserializeSFloat(bits);
        assertParseFormat(0.0F, "float", data);
        verify(ieee11073NumberFormatter, times(1)).deserializeFloat(bits);

        assertParseFormat("test8", "utf8s", "test8".getBytes());
        assertParseFormat("test16", "utf16s", "test16".getBytes("UTF-16"));
    }

    @Test
    public void testParseExponent() throws CharacteristicFormatException, UnsupportedEncodingException {
        when(ieee754NumberFormatter.deserializeSFloat(any())).thenReturn(1.0F);
        when(ieee754NumberFormatter.deserializeFloat(any())).thenReturn(1.0F);
        when(ieee754NumberFormatter.deserializeDouble(any())).thenReturn(1.0D);

        when(ieee11073NumberFormatter.deserializeSFloat(any())).thenReturn(1.0F);
        when(ieee11073NumberFormatter.deserializeFloat(any())).thenReturn(1.0F);

        when(twosComplementNumberFormatter.deserializeInteger(any(), anyByte(), anyBoolean())).thenReturn(1);
        when(twosComplementNumberFormatter.deserializeLong(any(), anyByte(), anyBoolean())).thenReturn(1L);
        when(twosComplementNumberFormatter.deserializeBigInteger(any(), anyByte(), anyBoolean())).thenReturn(BigInteger.ONE);



    }

    private void assertParseFormat(Object expected, String format, byte[] bytes) throws CharacteristicFormatException {
        assertParseFormat(expected, format, bytes, null);
    }

    private void assertParseFormat(Object expected, String format, byte[] bytes, Integer exponent)
            throws CharacteristicFormatException {
        List<Field> fields = new ArrayList<>();
        String name = format + "Field";
        Field field = mockField(name, format);
        when(field.getDecimalExponent()).thenReturn(exponent);
        fields.add(field);
        when(characteristic.getValue().getFlags()).thenReturn(null);
        when(characteristic.getValue().getFields()).thenReturn(fields);
        when(characteristic.isValidForRead()).thenReturn(true);
        Map<String, FieldHolder> values = parser.parse(characteristic, bytes);
        assertEquals(1, values.size());
        assertTrue(values.containsKey(name));
        assertEquals(expected, values.get(name).getRawValue());
    }

    private Bit mockBit(int index, int size, String flagPrefix) {
        Bit bit = mock(Bit.class);
        when(bit.getIndex()).thenReturn(index);
        when(bit.getSize()).thenReturn(size);
        for (int i = 0; i <= Math.pow(2, size); i++) {
            when(bit.getRequires((byte) i)).thenReturn(flagPrefix + i);
        }
        return bit;
    }

    private Field mockField(String name, String format) {
        Field field = mock(Field.class);
        when(field.getFormat()).thenReturn(FieldFormat.valueOf(format));
        when(field.getName()).thenReturn(name);
        return field;
    }

}
