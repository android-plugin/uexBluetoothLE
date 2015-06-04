package org.zywx.wbpalmstar.plugin.uexbluetoothble.vo;

import java.io.Serializable;

/**
 * Created by ylt on 15/5/13.
 */
public class GattDescriptorVO implements Serializable {
    private static final long serialVersionUID = -1668957615902445914L;

    private  String uuid;

    private String value;

    private int permissions;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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
}
