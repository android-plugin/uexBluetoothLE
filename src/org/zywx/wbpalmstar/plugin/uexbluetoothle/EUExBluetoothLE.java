package org.zywx.wbpalmstar.plugin.uexbluetoothle;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.zywx.wbpalmstar.base.BDebug;
import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;
import org.zywx.wbpalmstar.plugin.uexbluetoothle.vo.BluetoothDeviceVO;
import org.zywx.wbpalmstar.plugin.uexbluetoothle.vo.CharacteristicVO;
import org.zywx.wbpalmstar.plugin.uexbluetoothle.vo.ConnectedVO;
import org.zywx.wbpalmstar.plugin.uexbluetoothle.vo.DescriptorInputVO;
import org.zywx.wbpalmstar.plugin.uexbluetoothle.vo.GattDescriptorVO;
import org.zywx.wbpalmstar.plugin.uexbluetoothle.vo.ResultVO;
import org.zywx.wbpalmstar.plugin.uexbluetoothle.vo.SearchForCharacteristicInputVO;
import org.zywx.wbpalmstar.plugin.uexbluetoothle.vo.SearchForCharacteristicOutputVO;
import org.zywx.wbpalmstar.plugin.uexbluetoothle.vo.SearchForDescriptorInputVO;
import org.zywx.wbpalmstar.plugin.uexbluetoothle.vo.SearchForDescriptorOutputVO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class EUExBluetoothLE extends EUExBase {

    private static final String BUNDLE_DATA = "data";
    private static final int MSG_INIT = 1;
    private static final int MSG_CONNECT = 2;
    private static final int MSG_DISCONNECT = 3;
    private static final int MSG_SCAN_DEVICE = 4;
    private static final int MSG_STOP_SCAN_DEVICE = 5;
    private static final int MSG_WRITE_CHARACTERISTIC = 6;
    private static final int MSG_READ_CHARACTERISTIC = 7;
    private static final int MSG_SEARCH_FOR_CHARACTERISTIC = 8;
    private static final int MSG_SEARCH_FOR_DESCRIPTOR = 9;
    private static final int MSG_READ_DESCRIPTOR = 10;
    private static final int MSG_WRITE_DESCRIPTOR = 11;

    private String mBluetoothDeviceAddress;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private Gson mGson;

    private String mCharFormat=null;
    private List<BluetoothGattService> mGattServices;

    private static final String TAG="appcan";

    private EBrowserView mCallbackView;
    private static final int REQUEST_ENABLE_BT=1;

    public EUExBluetoothLE(Context context, EBrowserView eBrowserView) {
        super(context, eBrowserView);
    }

    @Override
    protected boolean clean() {
        return false;
    }


    public void init(String[] params) {
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_INIT;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void initMsg(String[] params) {
        mGson=new Gson();
        String json;
        try {
            if (params!=null&&params.length>0) {
                json = params[0];
                JSONObject jsonObject = new JSONObject(json);
                mCharFormat = jsonObject.optString("charFormat");
            }
        } catch (JSONException e) {
        }
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) mContext.
                    getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {

            }
        }
        mCallbackView=mBrwView;
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        // 确保蓝牙在设备上可以开启
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }else{
            callBackResult(true);
        }
        Log.i(TAG, "plugin init");
    }

    private void callBackResult(boolean result){
        ResultVO resultVO=new ResultVO();
        resultVO.setResultCode(result?ResultVO.RESULT_OK:ResultVO.RESULT_FAILD);
        callBackPluginJs(JsConst.CALLBACK_INIT, mGson.toJson(resultVO));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_ENABLE_BT){
            if (resultCode== Activity.RESULT_OK){
                callBackResult(true);
            }else {
                callBackResult(false);
            }
        }
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            ResultVO resultVO=new ResultVO();
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                resultVO.setResultCode(ResultVO.RESULT_OK);
                mBluetoothGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                resultVO.setResultCode(ResultVO.RESULT_FAILD);
            }
            callBackPluginJs(JsConst.ON_CONNECTION_STATE_CHANGE,mGson.toJson(resultVO));
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                mGattServices =mBluetoothGatt.getServices();
                try {
                    displayGattServices(mGattServices);
                } catch (InterruptedException e) {
                }
            } else {

            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            ResultVO resultVO=new ResultVO();
            if (status == BluetoothGatt.GATT_SUCCESS) {
                resultVO.setResultCode(ResultVO.RESULT_OK);
            }else if (status==BluetoothGatt.GATT_FAILURE){
                resultVO.setResultCode(ResultVO.RESULT_FAILD);
            }
            resultVO.setData(transformDataFromCharacteristic(characteristic));
            callBackPluginJs(JsConst.ON_CHARACTERISTIC_READ, mGson.toJson(resultVO));
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            callBackPluginJs(JsConst.ON_CHARACTERISTIC_CHANGED,mGson.toJson(transformDataFromCharacteristic(characteristic)));
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            ResultVO<CharacteristicVO> resultVO=new ResultVO<CharacteristicVO>();
            if (status == BluetoothGatt.GATT_SUCCESS) {
                resultVO.setResultCode(ResultVO.RESULT_OK);
                resultVO.setData(transformDataFromCharacteristic(characteristic));
            }else if (status==BluetoothGatt.GATT_FAILURE){
                resultVO.setResultCode(ResultVO.RESULT_FAILD);
            }
            callBackPluginJs(JsConst.ON_CHARACTERISTIC_WRITE, mGson.toJson(resultVO));
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            ResultVO resultVO=new ResultVO();
            if (status==BluetoothGatt.GATT_SUCCESS){
                resultVO.setResultCode(ResultVO.RESULT_OK);
                resultVO.setData(transfromDescriptor(descriptor));
            }else{
                resultVO.setResultCode(ResultVO.RESULT_FAILD);
            }
            callBackPluginJs(JsConst.CALLBACK_READ_DESCRIPTOR,mGson.toJson(resultVO));
        }


        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            ResultVO resultVO=new ResultVO();
            if (status==BluetoothGatt.GATT_SUCCESS){
                resultVO.setResultCode(ResultVO.RESULT_OK);
                resultVO.setData(transfromDescriptor(descriptor));
            }else{
                resultVO.setResultCode(ResultVO.RESULT_FAILD);
            }
            callBackPluginJs(JsConst.CALLBACK_WRITE_DESCRIPTOR, mGson.toJson(resultVO));
        }

    };

    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) throws InterruptedException {
        if (gattServices == null) return;
        List<String> serviceUUIDs=new ArrayList<String>();
        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            String uuid = gattService.getUuid().toString();
            serviceUUIDs.add(uuid);
        }
        ConnectedVO connectedVO=new ConnectedVO();
        connectedVO.services=serviceUUIDs;
        BDebug.i(TAG,mGson.toJson(connectedVO));
        callBackPluginJs(JsConst.CALLBACK_CONNECT, mGson.toJson(connectedVO));
    }

    public CharacteristicVO transformDataFromCharacteristic(BluetoothGattCharacteristic characteristic){
        CharacteristicVO characteristicVO=new CharacteristicVO();
        final byte[] data=characteristic.getValue();
        if (data!=null) {
            if (!TextUtils.isEmpty(mCharFormat)) {
                StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data) {
                    stringBuilder.append(String.format(mCharFormat, byteChar));
                }
                characteristicVO.setValue(stringBuilder.toString());
                characteristicVO.setNeedDecode(false);
            } else {
                characteristicVO.setValue(Base64.encodeToString(data,Base64.DEFAULT));
                characteristicVO.setNeedDecode(true);
            }
        }
        characteristicVO.setPermissions(characteristic.getPermissions());
        characteristicVO.setWriteType(characteristic.getWriteType());
        characteristicVO.setUUID(characteristic.getUuid().toString());
        characteristicVO.setServiceUUID(characteristic.getService().getUuid().toString());
        List<BluetoothGattDescriptor> descriptors=characteristic.getDescriptors();
        List<GattDescriptorVO> gattDescriptorVOs=new ArrayList<GattDescriptorVO>();
        if (descriptors!=null&&!descriptors.isEmpty()){
            for (BluetoothGattDescriptor descriptor:descriptors){
                gattDescriptorVOs.add(transfromDescriptor(descriptor));
            }
        }
        characteristicVO.setDescriptors(gattDescriptorVOs);
        return characteristicVO;
    }

    private GattDescriptorVO transfromDescriptor(BluetoothGattDescriptor descriptor){
        GattDescriptorVO gattDescriptorVO=new GattDescriptorVO();
        if (descriptor.getUuid()!=null) {
            gattDescriptorVO.setUUID(descriptor.getUuid().toString());
        }
        if (descriptor.getValue()!=null) {
            gattDescriptorVO.setValue(Base64.encodeToString(descriptor.getValue(), Base64.DEFAULT));
            gattDescriptorVO.setNeedDecode(true);
        }
        gattDescriptorVO.setServiceUUID(descriptor.getCharacteristic().getService().getUuid().toString());
        gattDescriptorVO.setCharacteristicUUID(descriptor.getCharacteristic().getUuid().toString());
        gattDescriptorVO.setPermissions(descriptor.getPermissions());
        return gattDescriptorVO;
    }

    public void connect(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_CONNECT;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void connectMsg(String[] params) {
        String json = params[0];
        String address=null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            address=jsonObject.optString("address").toUpperCase();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (mBluetoothAdapter == null || address == null) {
            return;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress) && mBluetoothGatt != null) {
            mBluetoothGatt.connect();
            return;
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(mContext, false, mGattCallback);

        mBluetoothDeviceAddress = address;

    }

    public void disconnect(String[] params) {
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_DISCONNECT;
        mHandler.sendMessage(msg);
    }

    private void disconnectMsg(String[] params) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.disconnect();
    }

    public void scanDevice(String[] params) {
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_SCAN_DEVICE;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void scanDeviceMsg(String[] params) {
        if (params!=null&&params.length>0) {
            String json = params[0];
            List<String> uuidStrings = mGson.fromJson(json, new TypeToken<List<String>>() {
            }.getType());
            UUID[] uUIDs=new UUID[uuidStrings.size()];
            for (int i = 0; i < uuidStrings.size(); i++) {
                uUIDs[i]=UUID.fromString(uuidStrings.get(i));
            }
            mBluetoothAdapter.startLeScan(uUIDs,mLeScanCallback);
        }else{
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            BluetoothDeviceVO deviceVO=new BluetoothDeviceVO();
            deviceVO.setAddress(device.getAddress());
            deviceVO.setName(device.getName());
            callBackPluginJs(JsConst.ON_LE_SCAN, mGson.toJson(deviceVO));
        }
    };

    public void stopScanDevice(String[] params) {
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_STOP_SCAN_DEVICE;
        mHandler.sendMessage(msg);
    }

    private void stopScanDeviceMsg(String[] params) {
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }

    public void writeCharacteristic(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_WRITE_CHARACTERISTIC;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void writeCharacteristicMsg(String[] params) {
        String json = params[0];
        String serviceUUID=null;
        String characteristicUUID=null;
        String value=null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            serviceUUID=jsonObject.optString("serviceUUID");
            characteristicUUID=jsonObject.optString("characteristicUUID");
            value=jsonObject.optString("value");
        } catch (JSONException e) {
        }
        writeCharacteristicByUUID(serviceUUID, characteristicUUID, value);
    }

    private void writeCharacteristicByUUID(String serviceUUID,String characteristicUUID,String value){
        if (mGattServices==null){
            return;
        }
        for (int i = 0; i < mGattServices.size(); i++) {
            BluetoothGattService bluetoothGattService = mGattServices.get(i);
            if (serviceUUID.equals(bluetoothGattService.getUuid().toString())){
                BluetoothGattCharacteristic gattCharacteristic=bluetoothGattService.
                        getCharacteristic(UUID.fromString(characteristicUUID));
                gattCharacteristic.setValue(Base64.decode(value,Base64.DEFAULT));
                mBluetoothGatt.writeCharacteristic(gattCharacteristic);
               break;
            }
         }
    }

    public void readCharacteristic(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_READ_CHARACTERISTIC;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void readCharacteristicMsg(String[] params) {
        String json = params[0];
        String serviceUUID=null;
        String characteristicUUID=null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            serviceUUID=jsonObject.optString("serviceUUID");
            characteristicUUID=jsonObject.optString("characteristicUUID");
        } catch (JSONException e) {
        }
        readCharacteristicByUUID(serviceUUID, characteristicUUID);
    }

    private void readCharacteristicByUUID(String serviceUUID,String characteristicUUID){
        if (mGattServices==null){
            return;
        }
        for (int i = 0; i < mGattServices.size(); i++) {
            BluetoothGattService bluetoothGattService = mGattServices.get(i);
            if (serviceUUID.equals(bluetoothGattService.getUuid().toString())){
                BluetoothGattCharacteristic gattCharacteristic=bluetoothGattService.
                        getCharacteristic(UUID.fromString(characteristicUUID));
                mBluetoothGatt.readCharacteristic(gattCharacteristic);
              break;
        }
          }
    }

    public void searchForCharacteristic(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_SEARCH_FOR_CHARACTERISTIC;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void searchForCharacteristicMsg(String[] params) {
        String json = params[0];
        List<BluetoothGattCharacteristic> characteristicList=new ArrayList<BluetoothGattCharacteristic>();
        SearchForCharacteristicInputVO inputVO=mGson.fromJson(json,SearchForCharacteristicInputVO.class);
        characteristicList=getServiceByID(inputVO.getServiceUUID()).getCharacteristics();
        List<CharacteristicVO> characteristicVOs=new ArrayList<CharacteristicVO>();
        if (characteristicList!=null){
            for (BluetoothGattCharacteristic characteristic:characteristicList){
                characteristicVOs.add(transformDataFromCharacteristic(characteristic));
            }
        }
        SearchForCharacteristicOutputVO outputVO=new SearchForCharacteristicOutputVO();
        outputVO.setServiceUUID(inputVO.getServiceUUID());
        outputVO.setCharacteristics(characteristicVOs);
        callBackPluginJs(JsConst.CALLBACK_SEARCH_FOR_CHARACTERISTIC, mGson.toJson(outputVO));
    }

    private BluetoothGattService getServiceByID(String UUID){
        for (BluetoothGattService service:mGattServices){
            if (service.getUuid().toString().equals(UUID)){
                return  service;
            }
        }
        return null;
    }

    private BluetoothGattCharacteristic getCharacteristicByID(String serviceUUID, String characteristicUUID){
        return getServiceByID(serviceUUID).getCharacteristic(UUID.fromString(characteristicUUID));
    }

    public void searchForDescriptor(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_SEARCH_FOR_DESCRIPTOR;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void searchForDescriptorMsg(String[] params) {
        String json = params[0];
        List<GattDescriptorVO> descriptorVOs=new ArrayList<GattDescriptorVO>();
        SearchForDescriptorInputVO inputVO=mGson.fromJson(json,SearchForDescriptorInputVO.class);
        List<BluetoothGattDescriptor> descriptors=getCharacteristicByID(inputVO.getServiceUUID(),inputVO.getCharacteristicUUID()).getDescriptors();
        if (descriptors!=null){
            for (BluetoothGattDescriptor descriptor:descriptors){
                descriptorVOs.add(transfromDescriptor(descriptor));
            }
        }
        SearchForDescriptorOutputVO outputVO=new SearchForDescriptorOutputVO();
        outputVO.setServiceUUID(inputVO.getServiceUUID());
        outputVO.setCharacteristicUUID(inputVO.getCharacteristicUUID());
        outputVO.setDescriptors(descriptorVOs);
        callBackPluginJs(JsConst.CALLBACK_SEARCH_FOR_DESCRIPTOR, mGson.toJson(outputVO));
    }

    public void readDescriptor(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_READ_DESCRIPTOR;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void readDescriptorMsg(String[] params) {
        String json = params[0];
        DescriptorInputVO inputVO=mGson.fromJson(json,DescriptorInputVO.class);
        BluetoothGattDescriptor descriptor = getCharacteristicByID(inputVO.getServiceUUID(), inputVO
                .getCharacteristicUUID())
                .getDescriptor(UUID
                        .fromString(inputVO.getDescriptorUUID()));
        mBluetoothGatt.readDescriptor(descriptor);
    }

    public void writeDescriptor(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_WRITE_DESCRIPTOR;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void writeDescriptorMsg(String[] params) {
        String json = params[0];
        DescriptorInputVO inputVO=mGson.fromJson(json,DescriptorInputVO.class);
        BluetoothGattDescriptor descriptor=getCharacteristicByID(inputVO.getServiceUUID(),inputVO
                .getCharacteristicUUID())
                .getDescriptor(UUID
                        .fromString(inputVO.getDescriptorUUID()));
        descriptor.setValue(Base64.decode(inputVO.getValue(), Base64.DEFAULT));
        mBluetoothGatt.writeDescriptor(descriptor);
    }

    @Override
    public void onHandleMessage(Message message) {
        if(message == null){
            return;
        }
        Bundle bundle=message.getData();
        switch (message.what) {

            case MSG_INIT:
                initMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_CONNECT:
                connectMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_DISCONNECT:
                disconnectMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_SCAN_DEVICE:
                scanDeviceMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_STOP_SCAN_DEVICE:
                stopScanDeviceMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_WRITE_CHARACTERISTIC:
                writeCharacteristicMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_READ_CHARACTERISTIC:
                readCharacteristicMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_SEARCH_FOR_CHARACTERISTIC:
                searchForCharacteristicMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_SEARCH_FOR_DESCRIPTOR:
                searchForDescriptorMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_READ_DESCRIPTOR:
                readDescriptorMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_WRITE_DESCRIPTOR:
                writeDescriptorMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            default:
                super.onHandleMessage(message);
        }
    }

    private void callBackPluginJs(String methodName, String jsonData){
        if (mCallbackView==null){
            return;
        }
        String js = SCRIPT_HEADER + "if(" + methodName + "){"
                + methodName + "('" + jsonData + "');}";
        mCallbackView.addUriTask(js);
//        onCallback(js);
    }

}
