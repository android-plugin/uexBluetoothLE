package org.zywx.wbpalmstar.plugin.uexbluetoothle.vo;

import java.io.Serializable;

/**
 * Created by ylt on 15/5/13.
 */
public class GattDescriptorVO implements Serializable {
    private static final long serialVersionUID = -1668957615902445914L;

    private String serviceUUID;
    private String characteristicUUID;


    private  String UUID;

    private String value;
    private boolean needDecode;

    private int permissions;

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getPermissions() {
        return permissions;
    }

    public void setPermissions(int permissions) {
        this.permissions = permissions;
    }

    public boolean isNeedDecode() {
        return needDecode;
    }

    public void setNeedDecode(boolean needDecode) {
        this.needDecode = needDecode;
    }

    public String getCharacteristicUUID() {
        return characteristicUUID;
    }

    public void setCharacteristicUUID(String characteristicUUID) {
        this.characteristicUUID = characteristicUUID;
    }

    public String getServiceUUID() {
        return serviceUUID;
    }

    public void setServiceUUID(String serviceUUID) {
        this.serviceUUID = serviceUUID;
    }
}
