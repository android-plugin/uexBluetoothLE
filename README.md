# uexBluetoothBLE 蓝牙BLE插件接口文档


### 平台支持

```
Android 4.3+(API 18)
iOS 6.0+
```


### 初始化接口

```
init（param）
var param={
    charFormat://可选。读取Characteristic里面内容时用于format byte
};

```

### 扫描蓝牙设备
扫描到设备后通过onLeScan回调结果

```
scanDevice ();

```

### 停止扫描设备

```
stopScanDevice();
```

### 连接指定蓝牙设备
```
connect（param）
var param={
    address://要连接的蓝牙地址
};
```

### 断开蓝牙连接
```
disconnect();
```


### 扫描到设备回调
每扫描到一个设备都会回调一次，找到需要链接设备时应停止扫描

```
onLeScan(param)
var param={
	address:,//蓝牙设备地址
	name://蓝牙设备名称
}
```

### 读取Characteristic


```
readCharacteristic(param)
var param={
	serviceUUID://service的UUID
	characteristicUUID://characteristic的UUID
}
```

### 写入数据到Characteristic

```
writeCharacteristic(param)
var param={
	serviceUUID://service的UUID
	characteristicUUID://characteristic的UUID
	value://要写入的值
}
```

### 连接状态变化回调

```
onConnectionStateChange(param)
var param={
	resultCode://0-已连接上，1-已断开
}
```
### ServicesDiscovered回调

```
onServicesDiscovered(param)

回调结果为List<GattServiceVO> 的Json格式字符串

```

GattServiceVO中的字段为：

```
  String uuid;
  
  List<CharacteristicVO> characteristicVOs;
```
CharacteristicVO中得字段为：

```
String valueString;

String uuid;

int permissions;

int writeType;

List<GattDescriptorVO> gattDescriptorVOs;

```
GattDescriptorVO中的字段为：

```
String uuid;

String value;

int permissions;
```

### onCharacteristicRead回调

```
onCharacteristicRead(param)
var param={
	resultCode://0-成功，1-失败
	data:CharacteristicVO的Json格式
}
CharacteristicVO字段同上
```

### onCharacteristicChanged回调

```
onCharacteristicChanged(param);
返回内容为CharacteristicVO 的Json格式
CharacteristicVO字段同上
```

### onCharacteristicWrite回调

```
onCharacteristicWrite(param)
var param={
	resultCode://0-成功，1-失败
	data:CharacteristicVO的Json格式
}
CharacteristicVO字段同上

```

### 



