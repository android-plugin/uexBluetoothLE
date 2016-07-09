package org.zywx.wbpalmstar.plugin.uexbluetoothle.vo;

import java.io.Serializable;

/**
 * Created by ylt on 16/7/9.
 */

public class SetCharacteristicNotificationInputVO implements Serializable {

    public String serviceUUID;
    public String characteristicUUID;
    public boolean enable;
}
