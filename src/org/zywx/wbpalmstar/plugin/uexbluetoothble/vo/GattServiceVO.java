package org.zywx.wbpalmstar.plugin.uexbluetoothble.vo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ylt on 15/5/13.
 */
public class GattServiceVO implements Serializable {
    private static final long serialVersionUID = 3532586653853934910L;

    private String uuid;
    private List<CharacteristicVO> characteristicVOs;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<CharacteristicVO> getCharacteristicVOs() {
        return characteristicVOs;
    }

    public void setCharacteristicVOs(List<CharacteristicVO> characteristicVOs) {
        this.characteristicVOs = characteristicVOs;
    }
}
