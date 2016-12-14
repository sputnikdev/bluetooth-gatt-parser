package org.bluetooth.gattparser;

import java.util.ArrayList;
import java.util.List;

import org.bluetooth.gattparser.spec.Bit;
import org.bluetooth.gattparser.spec.Field;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BluetoothGattParserTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Field flagField;

    private BluetoothGattParser parser = BluetoothGattParserFactory.getDefault();

    @Test
    public void testParseFlags() throws Exception {
        List<Bit> bits = new ArrayList<>();
        when(flagField.getBitField().getBits()).thenReturn(bits);
        bits.add(mockBit(0, 1));
        bits.add(mockBit(1, 2));
        bits.add(mockBit(2, 1));
        bits.add(mockBit(3, 3));
        bits.add(mockBit(4, 2));
        bits.add(mockBit(5, 2));
        bits.add(mockBit(6, 4));

        int[] flags = parser.parseFlags(flagField, new byte[] {(byte) 0b10100101, (byte) 0b01010001});
        assertArrayEquals(new int[] {1, 2, 0, 2, 3, 0, 0}, flags);
    }

    private Bit mockBit(int index, int size) {
        Bit bit = mock(Bit.class);
        when(bit.getIndex()).thenReturn(index);
        when(bit.getSize()).thenReturn(size);
        return bit;
    }
}
