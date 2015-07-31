package org.zywx.wbpalmstar.plugin.uexbluetoothle.vo;

import java.io.Serializable;


/**
 * Created by ylt on 15/7/31.
 */
public class SearchForCharacteristicInputVO implements Serializable {

    private static final long serialVersionUID = -2223885401438309728L;
    private String serviceUUID;

    public String getServiceUUID() {
        return serviceUUID;
    }

    public void setServiceUUID(String serviceUUID) {
        this.serviceUUID = serviceUUID;
    }
}
