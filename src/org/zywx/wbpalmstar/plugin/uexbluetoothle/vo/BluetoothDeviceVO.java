package org.zywx.wbpalmstar.plugin.uexbluetoothle.vo;

import java.io.Serializable;

/**
 * Created by yanlongtao on 2015/4/14.
 */
public class BluetoothDeviceVO implements Serializable {

    private static final long serialVersionUID = -3824616157200430607L;

    private String address;

    private String name;

    private int rssi;


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }
}
