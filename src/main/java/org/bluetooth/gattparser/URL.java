package org.bluetooth.gattparser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URL {

    public static final Pattern URL_PATTERN =
            Pattern.compile("^(/(?<adapter>(\\w\\w:){5}\\w\\w))?(/(?<device>(\\w\\w:){5}\\w\\w))?(/(?<service>[0-9a-f]{4,8}(-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})?))?(/(?<characteristic>[0-9a-f]{4,8}(-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})?))?(/(?<field>\\w+))?$");

    private final String adapterAddress;
    private final String deviceAddress;
    private final String serviceUUID;
    private final String characteristicUUID;
    private final String fieldName;

    /**
     * /B8:27:EB:60:0C:43/54:60:09:95:86:01/0000180f-0000-1000-8000-00805f9b34fb/00002a19-0000-1000-8000-00805f9b34fb/Level
     * /B8:27:EB:60:0C:43/54:60:09:95:86:01/0000180f-0000-1000-8000-00805f9b34fb/00002a19-0000-1000-8000-00805f9b34fb
     * /B8:27:EB:60:0C:43/54:60:09:95:86:01/0000180f-0000-1000-8000-00805f9b34fb
     * /B8:27:EB:60:0C:43/54:60:09:95:86:01
     * /B8:27:EB:60:0C:43
     *
     *
     * /B8:27:EB:60:0C:43/54:60:09:95:86:01/0000180f/00002a19/Level
     * /B8:27:EB:60:0C:43/54:60:09:95:86:01/180f/2a19/Level
     *
     * /B8:27:EB:60:0C:43/54:60:09:95:86:01
     *
     * /54:60:09:95:86:01/0000180f/00002a19/Level
     *
     * @param url
     */
    public URL(String url) {
        Matcher matcher = URL_PATTERN.matcher(url);
        if (matcher.find()) {
            String adapterAddress = matcher.group("adapter");
            String deviceAddress = matcher.group("device");
            String serviceUUID = matcher.group("service");
            String characteristicUUID = matcher.group("characteristic");
            this.fieldName = matcher.group("field");

//            if (adapterAddress != null && deviceAddress == null) {
//                deviceAddress = adapterAddress;
//                adapterAddress = null;
//            }

            if (serviceUUID != null && characteristicUUID == null) {
                characteristicUUID = serviceUUID;
                serviceUUID = null;
            }

            if (adapterAddress == null && deviceAddress == null && serviceUUID == null
                    && characteristicUUID == null && this.fieldName == null) {
                throw new IllegalArgumentException("Invalid URL: " + url);
            }

            this.adapterAddress = adapterAddress;
            this.deviceAddress = deviceAddress;
            this.serviceUUID = serviceUUID;
            this.characteristicUUID = characteristicUUID;
        } else {
            throw new IllegalArgumentException("Invalid URL: " + url);
        }
    }

    public URL(String adapterAddress, String deviceAddress) {
        this(adapterAddress, deviceAddress, null, null, null);
    }

    public URL(String adapterAddress, String deviceAddress, String serviceUUID, String characteristicUUID) {
        this(adapterAddress, deviceAddress, serviceUUID, characteristicUUID, null);
    }

    public URL(String adapterAddress, String deviceAddress, String serviceUUID, String characteristicUUID,
            String fieldName) {
        this.adapterAddress = adapterAddress;
        this.deviceAddress = deviceAddress;
        this.serviceUUID = serviceUUID;
        this.characteristicUUID = characteristicUUID;
        this.fieldName = fieldName;
    }

    public URL copyWith(String serviceUUID, String characteristicUUID,String fieldName) {
        return new URL(this.adapterAddress, this.deviceAddress, serviceUUID, characteristicUUID, fieldName);
    }

    public URL copyWith(String serviceUUID, String characteristicUUID) {
        return new URL(this.adapterAddress, this.deviceAddress, serviceUUID, characteristicUUID);
    }

    public URL copyWith(String fieldName) {
        return new URL(this.adapterAddress, this.deviceAddress, this.serviceUUID, this.characteristicUUID, fieldName);
    }


    public URL getDeviceURL() {
        return new URL(adapterAddress, deviceAddress);
    }

    public URL getCharacteristicURL() {
        return new URL(adapterAddress, deviceAddress, serviceUUID, characteristicUUID);
    }

    public URL getAdapterURL() {
        return new URL(adapterAddress, null);
    }

    public String getAdapterAddress() {
        return adapterAddress;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public String getServiceUUID() {
        return serviceUUID;
    }

    public String getCharacteristicUUID() {
        return characteristicUUID;
    }

    public String getFieldName() {
        return fieldName;
    }

    @Override
    public String toString() {
        String result = "";
        if (adapterAddress != null) {
            result += "/" + adapterAddress;
        }
        if (deviceAddress != null) {
            result += "/" + deviceAddress;
        }
        if (serviceUUID != null) {
            result += "/" + serviceUUID;
        }
        if (characteristicUUID != null) {
            result += "/" + characteristicUUID;
        }
        if (fieldName != null) {
            result += "/" + fieldName;
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        URL url = (URL) o;

        if (adapterAddress != null ? !adapterAddress.equals(url.adapterAddress) : url.adapterAddress != null) {
            return false;
        }
        if (deviceAddress != null ? !deviceAddress.equals(url.deviceAddress) : url.deviceAddress != null) {
            return false;
        }
        if (serviceUUID != null ? !serviceUUID.equals(url.serviceUUID) : url.serviceUUID != null) {
            return false;
        }
        if (characteristicUUID != null ?
                !characteristicUUID.equals(url.characteristicUUID) :
                url.characteristicUUID != null) {
            return false;
        }
        return fieldName != null ? fieldName.equals(url.fieldName) : url.fieldName == null;

    }

    @Override
    public int hashCode() {
        int result = adapterAddress != null ? adapterAddress.hashCode() : 0;
        result = 31 * result + (deviceAddress != null ? deviceAddress.hashCode() : 0);
        result = 31 * result + (serviceUUID != null ? serviceUUID.hashCode() : 0);
        result = 31 * result + (characteristicUUID != null ? characteristicUUID.hashCode() : 0);
        result = 31 * result + (fieldName != null ? fieldName.hashCode() : 0);
        return result;
    }
}
