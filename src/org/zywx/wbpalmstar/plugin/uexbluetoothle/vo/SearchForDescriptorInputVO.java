package org.zywx.wbpalmstar.plugin.uexbluetoothle.vo;

import java.io.Serializable;

/**
 * Created by ylt on 15/7/31.
 */
public class SearchForDescriptorInputVO implements Serializable {

    private static final long serialVersionUID = 2506636583229409484L;
    private String serviceUUID;
    private String characteristicUUID;

    public String getServiceUUID() {
        return serviceUUID;
    }

    public void setServiceUUID(String serviceUUID) {
        this.serviceUUID = serviceUUID;
    }

    public String getCharacteristicUUID() {
        return characteristicUUID;
    }

    public void setCharacteristicUUID(String characteristicUUID) {
        this.characteristicUUID = characteristicUUID;
    }
}
