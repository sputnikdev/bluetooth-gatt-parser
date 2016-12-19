package org.bluetooth.gattparser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bluetooth.gattparser.num.TwosComplementNumberFormatter;
import org.bluetooth.gattparser.spec.Bit;
import org.bluetooth.gattparser.spec.Characteristic;
import org.bluetooth.gattparser.spec.Field;
import org.bluetooth.gattparser.spec.FieldFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GenericCharacteristicParserTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Field flagField;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ParserContext context;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Characteristic characteristic;

    private GenericCharacteristicParser parser = new GenericCharacteristicParser();

    @Before
    public void setUp() {
        when(context.getRealNumberFormatter()).thenReturn(new TwosComplementNumberFormatter());
        when(context.getSpecificationReader().getCharacteristic(any())).thenReturn(characteristic);
    }

    @Test
    public void testParseFlags() throws Exception {
        List<Bit> bits = new ArrayList<>();
        when(flagField.getBitField().getBits()).thenReturn(bits);
        bits.add(mockBit(0, 1, "C"));
        bits.add(mockBit(1, 2, "C"));
        bits.add(mockBit(2, 1, "C"));
        bits.add(mockBit(3, 3, "C"));
        bits.add(mockBit(4, 2, "C"));
        bits.add(mockBit(5, 2, "C"));
        bits.add(mockBit(6, 4, "C"));

        int[] flags = parser.parseFlags(context, flagField, new byte[] {(byte) 0b10100101, (byte) 0b01010001});
        assertArrayEquals(new int[] {1, 2, 0, 2, 3, 0, 10}, flags);
    }

    @Test(expected = CharacteristicFormatException.class)
    public void testParseNotValidForRead() throws CharacteristicFormatException {
        when(characteristic.isValidForRead()).thenReturn(Boolean.FALSE);
        parser.parse(context, "unparsable", new byte[]{ });
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

        Set<String> flags = parser.getFlags(context, characteristic, new byte[] {(byte) 0b10100101, (byte) 0b01010001});
        assertTrue(flags.contains("A1"));
        assertTrue(flags.contains("B2"));

        assertTrue(flags.contains("C0"));

        assertTrue(flags.contains("D2"));
        assertTrue(flags.contains("E3"));

        assertTrue(flags.contains("F0"));
        assertTrue(flags.contains("G10"));
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

}
