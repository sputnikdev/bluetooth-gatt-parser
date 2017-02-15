package org.sputnikdev.bluetooth.gattparser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class URLTest {

    @Test
    public void testFullURLTest() {
        URL url = new URL("/54:60:09:95:86:01/11:22:33:44:55:66/0000180f-0000-1000-8000-00805f9b34fb/00002a19-0000-1000-8000-00805f9b34fb/Level");

        assertEquals("54:60:09:95:86:01", url.getAdapterAddress());
        assertEquals("11:22:33:44:55:66", url.getDeviceAddress());
        assertEquals("0000180f-0000-1000-8000-00805f9b34fb", url.getServiceUUID());
        assertEquals("00002a19-0000-1000-8000-00805f9b34fb", url.getCharacteristicUUID());
        assertEquals("Level", url.getFieldName());
    }

    @Test
    public void testShortUUIDs() {
        URL url = new URL("/54:60:09:95:86:01/11:22:33:44:55:66/0000180f/00002a19/Level");
        assertEquals("54:60:09:95:86:01", url.getAdapterAddress());
        assertEquals("11:22:33:44:55:66", url.getDeviceAddress());
        assertEquals("0000180f", url.getServiceUUID());
        assertEquals("00002a19", url.getCharacteristicUUID());
        assertEquals("Level", url.getFieldName());

        url = new URL("/54:60:09:95:86:01/11:22:33:44:55:66/180f/2a19/Level");
        assertEquals("54:60:09:95:86:01", url.getAdapterAddress());
        assertEquals("11:22:33:44:55:66", url.getDeviceAddress());
        assertEquals("180f", url.getServiceUUID());
        assertEquals("2a19", url.getCharacteristicUUID());
        assertEquals("Level", url.getFieldName());
    }

    @Test
    public void testFieldName() {
        URL url = new URL("/Level");
        assertNull(url.getAdapterAddress());
        assertNull(url.getDeviceAddress());
        assertNull(url.getServiceUUID());
        assertNull(url.getCharacteristicUUID());
        assertEquals("Level", url.getFieldName());
    }

    @Test
    public void testCharacteristicUUID() {
        URL url = new URL("/00002a19-0000-1000-8000-00805f9b34fb/Level");
        assertNull(url.getAdapterAddress());
        assertNull(url.getDeviceAddress());
        assertNull(url.getServiceUUID());
        assertEquals("00002a19-0000-1000-8000-00805f9b34fb", url.getCharacteristicUUID());
        assertEquals("Level", url.getFieldName());

        url = new URL("/00002a19/Level");
        assertNull(url.getAdapterAddress());
        assertNull(url.getDeviceAddress());
        assertNull(url.getServiceUUID());
        assertEquals("00002a19", url.getCharacteristicUUID());
        assertEquals("Level", url.getFieldName());

        url = new URL("/2a19/Level");
        assertNull(url.getAdapterAddress());
        assertNull(url.getDeviceAddress());
        assertNull(url.getServiceUUID());
        assertEquals("2a19", url.getCharacteristicUUID());
        assertEquals("Level", url.getFieldName());

        url = new URL("/2a19");
        assertNull(url.getAdapterAddress());
        assertNull(url.getDeviceAddress());
        assertNull(url.getServiceUUID());
        assertEquals("2a19", url.getCharacteristicUUID());
        assertEquals(null, url.getFieldName());
    }

    @Test
    public void testServiceUUID() {
        URL url = new URL("/0000180f-0000-1000-8000-00805f9b34fb/00002a19-0000-1000-8000-00805f9b34fb/Level");
        assertNull(url.getAdapterAddress());
        assertNull(url.getDeviceAddress());
        assertEquals("0000180f-0000-1000-8000-00805f9b34fb", url.getServiceUUID());
        assertEquals("00002a19-0000-1000-8000-00805f9b34fb", url.getCharacteristicUUID());
        assertEquals("Level", url.getFieldName());

        url = new URL("/0000180f/00002a19-0000-1000-8000-00805f9b34fb/Level");
        assertNull(url.getAdapterAddress());
        assertNull(url.getDeviceAddress());
        assertEquals("0000180f", url.getServiceUUID());
        assertEquals("00002a19-0000-1000-8000-00805f9b34fb", url.getCharacteristicUUID());
        assertEquals("Level", url.getFieldName());

        url = new URL("/180f/00002a19-0000-1000-8000-00805f9b34fb/Level");
        assertNull(url.getAdapterAddress());
        assertNull(url.getDeviceAddress());
        assertEquals("180f", url.getServiceUUID());
        assertEquals("00002a19-0000-1000-8000-00805f9b34fb", url.getCharacteristicUUID());
        assertEquals("Level", url.getFieldName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCharacteristicUUIDShort() {
        new URL("/180f/2a1/Level");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCharacteristicUUIDIncomplete() {
        new URL("/180f/00002a19-0000-10/Level");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCharacteristicUUIDLarge() {
        new URL("/180f/00002a190/Level");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidServiceUUIDShort() {
        new URL("/180/2a19/Level");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidServiceUUIDIncomplete() {
        new URL("/0000180f-3345/2a19/Level");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidServiceUUIDLarge() {
        new URL("/0000180f0/2a19/Level");
    }

    @Test
    public void testDeviceAddress() {
        URL url = new URL("/54:60:09:95:86:01/11:22:33:44:55:66");
        assertEquals("54:60:09:95:86:01", url.getAdapterAddress());
        assertEquals("11:22:33:44:55:66", url.getDeviceAddress());
        assertNull(url.getServiceUUID());
        assertNull(url.getCharacteristicUUID());
        assertNull(url.getFieldName());

        url = new URL("/11:22:33:44:55:66");
        assertNull(url.getDeviceAddress());
        assertEquals("11:22:33:44:55:66", url.getAdapterAddress());
        assertNull(url.getServiceUUID());
        assertNull(url.getCharacteristicUUID());
        assertNull(url.getFieldName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidDeviceAddressIncomplete() {
        new URL("/11:22:33:44:5");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidDeviceAddressLarge() {
        new URL("/11:22:33:44:55:66:77");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidAdapterAddressIncomplete() {
        new URL("/54:60:09:95:/11:22:33:44:55:66");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidAdapterAddressLarge() {
        new URL("/54:60:09:95:86:01:02/11:22:33:44:55:66");
    }

    @Test
    public void testToString() {

        URL url = new URL("/54:60:09:95:86:01/11:22:33:44:55:66/0000180f-0000-1000-8000-00805f9b34fb/00002a19-0000-1000-8000-00805f9b34fb/Level");
        assertEquals("/54:60:09:95:86:01/11:22:33:44:55:66/0000180f-0000-1000-8000-00805f9b34fb/00002a19-0000-1000-8000-00805f9b34fb/Level",
                url.toString());

        url = new URL("/54:60:09:95:86:01/11:22:33:44:55:66/0000180f-0000-1000-8000-00805f9b34fb/00002a19-0000-1000-8000-00805f9b34fb");
        assertEquals("/54:60:09:95:86:01/11:22:33:44:55:66/0000180f-0000-1000-8000-00805f9b34fb/00002a19-0000-1000-8000-00805f9b34fb",
                url.toString());

        url = new URL("/54:60:09:95:86:01/11:22:33:44:55:66/0000180f-0000-1000-8000-00805f9b34fb");
        assertEquals("/54:60:09:95:86:01/11:22:33:44:55:66/0000180f-0000-1000-8000-00805f9b34fb",
                url.toString());

        url = new URL("/54:60:09:95:86:01/11:22:33:44:55:66");
        assertEquals("/54:60:09:95:86:01/11:22:33:44:55:66",
                url.toString());

        url = new URL("/54:60:09:95:86:01");
        assertEquals("/54:60:09:95:86:01",
                url.toString());

        url = new URL("/Level");
        assertEquals("/Level", url.toString());

        url = new URL("/0000180f-0000-1000-8000-00805f9b34fb");
        assertEquals("/0000180f-0000-1000-8000-00805f9b34fb", url.toString());
    }

}
