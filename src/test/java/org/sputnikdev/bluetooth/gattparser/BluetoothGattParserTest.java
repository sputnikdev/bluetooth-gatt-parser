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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.sputnikdev.bluetooth.gattparser.spec.BluetoothGattSpecificationReader;
import org.sputnikdev.bluetooth.gattparser.spec.Characteristic;
import org.sputnikdev.bluetooth.gattparser.spec.Field;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BluetoothGattParserTest {

    private static final String CHARACTERISTIC_UUID = "2AA7";

    @Mock
    private BluetoothGattSpecificationReader specificationReader;
    @Mock
    private CharacteristicParser defaultParser;
    @Mock
    private Characteristic characteristic;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private GattRequest gattRequest;

    private byte[] data = new byte[]{0x0};

    @InjectMocks
    @Spy
    private BluetoothGattParser parser;

    @Before
    public void setUp() {
        byte[] data = new byte[]{0x0};
        LinkedHashMap<String, FieldHolder> holders = mock(LinkedHashMap.class);

        when(specificationReader.getCharacteristicByUUID(CHARACTERISTIC_UUID)).thenReturn(characteristic);
        when(characteristic.isValidForRead()).thenReturn(true);
        when(characteristic.isValidForWrite()).thenReturn(true);

        when(defaultParser.parse(characteristic, data)).thenReturn(holders);

        when(gattRequest.getCharacteristicUUID()).thenReturn(CHARACTERISTIC_UUID);
    }

    @Test
    public void testParse() {
        GattResponse response = parser.parse(CHARACTERISTIC_UUID, data);
        assertNotNull(response);

        verify(defaultParser, times(1)).parse(characteristic, data);
        verify(specificationReader, times(2)).getCharacteristicByUUID(CHARACTERISTIC_UUID);
    }

    @Test(expected = CharacteristicFormatException.class)
    public void testParseNoValid() {
        when(characteristic.isValidForRead()).thenReturn(false);

        parser.parse(CHARACTERISTIC_UUID, data);
    }

    @Test
    public void testParseCustomParser() {
        CharacteristicParser customParser = mock(CharacteristicParser.class);
        parser.registerParser(CHARACTERISTIC_UUID, customParser);

        GattResponse response = parser.parse(CHARACTERISTIC_UUID, data);
        assertNotNull(response);

        verify(defaultParser, times(0)).parse(characteristic, data);
        verify(specificationReader, times(2)).getCharacteristicByUUID(CHARACTERISTIC_UUID);
        verify(customParser, times(1)).parse(characteristic, data);
    }

    @Test
    public void testSerialize() {
        doReturn(true).when(parser).validate(gattRequest);
        parser.serialize(gattRequest, true);

        verify(parser, times(1)).validate(gattRequest);
        verify(defaultParser, times(1)).serialize(gattRequest.getAllFieldHolders());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSerializeRequestNotValid() {
        doReturn(false).when(parser).validate(gattRequest);
        parser.serialize(gattRequest, true);

        verify(parser, times(1)).validate(gattRequest);
    }

    @Test(expected = CharacteristicFormatException.class)
    public void testSerializeRequestNotValidForWrite() {
        when(characteristic.isValidForWrite()).thenReturn(false);
        doReturn(true).when(parser).validate(gattRequest);
        parser.serialize(gattRequest, true);

        verify(parser, times(1)).validate(gattRequest);
    }

    @Test
    public void testSerializeCustomParser() {
        CharacteristicParser customParser = mock(CharacteristicParser.class);
        parser.registerParser(CHARACTERISTIC_UUID, customParser);

        parser.serialize(gattRequest);

        verify(defaultParser, times(0)).serialize(gattRequest.getAllFieldHolders());
        verify(specificationReader, times(1)).getCharacteristicByUUID(CHARACTERISTIC_UUID);
        verify(customParser, times(1)).serialize(gattRequest.getAllFieldHolders());
    }

    @Test
    public void testGetCharacteristic() {
        assertEquals(characteristic, parser.getCharacteristic(CHARACTERISTIC_UUID));
        verify(specificationReader, times(1)).getCharacteristicByUUID(CHARACTERISTIC_UUID);
    }

    @Test
    public void prepareGattRequest() {

        List<Field> fields = new ArrayList<>();
        Field opControl = MockUtils.mockControlField("Operation Control", false);
        fields.add(opControl);
        when(specificationReader.getFields(characteristic)).thenReturn(fields);

        GattRequest request = parser.prepare(CHARACTERISTIC_UUID);
        assertEquals(CHARACTERISTIC_UUID, request.getCharacteristicUUID());

        verify(specificationReader, times(1)).getCharacteristicByUUID(CHARACTERISTIC_UUID);
        verify(specificationReader, times(1)).getFields(characteristic);
    }

    @Test
    public void testValidateOpControl() throws Exception {
        // optional op control field
        List<Field> fields = new ArrayList<>();
        Field opControl = MockUtils.mockControlField("Operation Control", false);
        fields.add(opControl);
        GattRequest gattRequest = new GattRequest(CHARACTERISTIC_UUID, fields);
        assertTrue(parser.validate(gattRequest));
        gattRequest.setField("Operation Control", 1);
        assertTrue(parser.validate(gattRequest));

        // mandatory op control field
        fields = new ArrayList<>();
        opControl = MockUtils.mockControlField("Operation Control", true);
        fields.add(opControl);
        gattRequest = new GattRequest(CHARACTERISTIC_UUID, fields);
        assertFalse(parser.validate(gattRequest));
        gattRequest.setField("Operation Control", 1);
        assertTrue(parser.validate(gattRequest));

        // dependants

        fields = new ArrayList<>();
        opControl = MockUtils.mockControlField("Operation Control", true, "C1", "C2", "C3");
        fields.add(opControl);
        gattRequest = new GattRequest(CHARACTERISTIC_UUID, fields);
        assertFalse(parser.validate(gattRequest));
        gattRequest.setField("Operation Control", 1);
        assertFalse(parser.validate(gattRequest));

        fields = new ArrayList<>();
        opControl = MockUtils.mockControlField("Operation Control", true, "C1", "C2", "C3");
        fields.add(opControl);
        fields.add(MockUtils.mockField("Field1", "C1"));
        gattRequest = new GattRequest(CHARACTERISTIC_UUID, fields);
        gattRequest.setField("Operation Control", 1);
        assertFalse(parser.validate(gattRequest));
        gattRequest.setField("Field1", 1);
        assertTrue(parser.validate(gattRequest));
        gattRequest.setField("Operation Control", 2);
        assertFalse(parser.validate(gattRequest));

        fields.add(MockUtils.mockField("Field2", "C2"));
        gattRequest = new GattRequest(CHARACTERISTIC_UUID, fields);
        gattRequest.setField("Operation Control", 2);
        assertFalse(parser.validate(gattRequest));
        gattRequest.setField("Field2", 2);
        assertTrue(parser.validate(gattRequest));
    }

    @Test
    public void testValidateMandatoryFields() throws Exception {
        List<Field> fields = new ArrayList<>();
        fields.add(MockUtils.mockField("Field1", "Mandatory"));
        fields.add(MockUtils.mockField("Field2", "C1", "C2"));
        GattRequest gattRequest = new GattRequest(CHARACTERISTIC_UUID, fields);
        assertFalse(parser.validate(gattRequest));
        gattRequest.setField("Field1", 1);
        assertTrue(parser.validate(gattRequest));

        gattRequest.setField("Field2", 2);
        assertTrue(parser.validate(gattRequest));
    }

    @Test
    public void testParseSimple() {
        byte[] data = new byte[] {0x54, 0x3d, 0x32, 0x37, 0x2e, 0x36, 0x20, 0x48, 0x3d, 0x39, 0x32, 0xe, 0x36, 0x00};
        assertEquals("[54, 3d, 32, 37, 2e, 36, 20, 48, 3d, 39, 32, e, 36, 0]", parser.parse(data, 16));
    }

    @Test
    public void testSerializeSimple() {
        byte[] data = new byte[] {0x54, 0x3d, 0x32, 0x37, 0x2e, 0x36, 0x20, 0x48, 0x3d, 0x39, 0x32, 0xe, 0x36, 0x00};
        assertArrayEquals(data, parser.serialize("[54, 3d, 32, 37, 2e, 36, 20, 48, 3d, 39, 32, e, 36, 0]", 16));
    }

}
