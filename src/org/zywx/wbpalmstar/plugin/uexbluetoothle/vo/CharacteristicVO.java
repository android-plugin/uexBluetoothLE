package org.zywx.wbpalmstar.plugin.uexbluetoothle.vo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yanlongtao on 2015/4/14.
 */
public class CharacteristicVO implements Serializable {

    private static final long serialVersionUID = -5993349996621831752L;
    private String value;
    private String UUID;
    private String serviceUUID;
    private boolean needDecode;
    private int permissions;
    private int writeType;

    private List<GattDescriptorVO> descriptors;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public List<GattDescriptorVO> getDescriptors() {
        return descriptors;
    }

    public void setDescriptors(List<GattDescriptorVO> descriptors) {
        this.descriptors = descriptors;
    }

    public int getPermissions() {
        return permissions;
    }

    public void setPermissions(int permissions) {
        this.permissions = permissions;
    }

    public int getWriteType() {
        return writeType;
    }

    public void setWriteType(int writeType) {
        this.writeType = writeType;
    }

    public boolean isNeedDecode() {
        return needDecode;
    }

    public void setNeedDecode(boolean needDecode) {
        this.needDecode = needDecode;
    }

    public String getServiceUUID() {
        return serviceUUID;
    }

    public void setServiceUUID(String serviceUUID) {
        this.serviceUUID = serviceUUID;
    }
}
