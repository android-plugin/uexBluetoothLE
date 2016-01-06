/*
 * Copyright (c) 2016.  The AppCan Open Source Project.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package org.zywx.wbpalmstar.plugin.uexbluetoothle.vo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ylt on 16/1/6.
 */
public class SearchForCharacteristicOutputVO implements Serializable {

    private String serviceUUID;

    private List<CharacteristicVO> characteristics;

    public List<CharacteristicVO> getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(List<CharacteristicVO> characteristics) {
        this.characteristics = characteristics;
    }

    public String getServiceUUID() {
        return serviceUUID;
    }

    public void setServiceUUID(String serviceUUID) {
        this.serviceUUID = serviceUUID;
    }
}
