package org.zywx.wbpalmstar.plugin.uexbluetoothle.vo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ylt on 15/5/13.
 */
public class GattServiceVO implements Serializable {
    private static final long serialVersionUID = 3532586653853934910L;

    private String uuid;
    private List<CharacteristicVO> characteristics;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<CharacteristicVO> getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(List<CharacteristicVO> characteristics) {
        this.characteristics = characteristics;
    }
}
