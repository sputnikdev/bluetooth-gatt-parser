package org.bluetooth.gattparser.spec;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BitTest {

    @Mock
    private Enumerations enumerations;
    @InjectMocks
    private Bit bit = new Bit();

    @Test
    public void testGetRequires() throws Exception {
        List<Enumeration> enums = new ArrayList<>();
        enums.add(mockEnumeration(1, "C1"));
        enums.add(mockEnumeration(2, "C2"));
        enums.add(mockEnumeration(3, null));
        when(enumerations.getEnumerations()).thenReturn(enums);

        assertEquals("C1", bit.getRequires((byte) 1));
        assertEquals("C2", bit.getRequires((byte) 2));
        assertEquals(null, bit.getRequires((byte) 3));
        assertEquals(null, bit.getRequires((byte) 4));
    }

    private Enumeration mockEnumeration(Integer key, String requires) {
        Enumeration enumeration = mock(Enumeration.class);
        when(enumeration.getKey()).thenReturn(key);
        when(enumeration.getRequires()).thenReturn(requires);
        return enumeration;
    }
}
