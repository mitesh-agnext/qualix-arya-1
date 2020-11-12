package com.custom.app.ui.sampleBLE;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.custom.app.util.BLEInfo;
import com.custom.app.util.BLEInfoReader;
import com.custom.app.util.UartService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothClassicService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothConfiguration;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothDeviceDecorator;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothStatus;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class DeviceService extends Service implements BluetoothService.OnBluetoothScanCallback, BluetoothService.OnBluetoothEventCallback {

    private BluetoothService mService;
    private Boolean mScanning = false;
    private UartService mUartService;
    private UUID UUID_DEVICE = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    ArrayList<byte[]> bytedata = new ArrayList<>();
    ArrayList<String> encodedData = new ArrayList<>();
    private int mState = UART_PROFILE_DISCONNECTED;
    private static final int UART_PROFILE_CONNECTED = 20;
    private static final int UART_PROFILE_DISCONNECTED = 21;
    public DeviceService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        BluetoothConfiguration config = new BluetoothConfiguration();
        config.bluetoothServiceClass = BluetoothClassicService.class; //  BluetoothClassicService.class or BluetoothLeService.class

        config.context = getApplicationContext();
        config.bufferSize = 1024;
        config.characterDelimiter = '\n';
        config.callListenersInMainThread = true;
        config.uuid = UUID_DEVICE; // For Classic
        config.uuidService = null; // For BLE
        config.uuidCharacteristic = null; // For BLE

        config.connectionPriority = BluetoothGatt.CONNECTION_PRIORITY_HIGH; // Automatically request connection priority just after connection is through.
        BluetoothService.init(config);

        mService = BluetoothService.getDefaultInstance();
        mService.setOnScanCallback(this);
        mService.setOnEventCallback(this);


        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mService.startScan();
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        mService.setOnEventCallback(this);
    }

    @Override
    public void onDestroy() {
        mService.stopScan();
        super.onDestroy();
    }

    @Override
    public void onDataRead(byte[] buffer, int length) {

    }

    @Override
    public void onStatusChange(BluetoothStatus status) {
        if (status == BluetoothStatus.CONNECTED) {

        }
    }

    @Override
    public void onDeviceName(String deviceName) {

    }

    @Override
    public void onToast(String message) {

    }

    @Override
    public void onDataWrite(byte[] buffer) {

    }

    @Override
    public void onDeviceDiscovered(BluetoothDevice device, int rssi) {
        BluetoothDeviceDecorator dv = new BluetoothDeviceDecorator(device, rssi);

        if (device.getName().equals("DMMBLE141020")) {
            Bundle b = new Bundle();
            b.putString(BluetoothDevice.EXTRA_DEVICE, device.getAddress());
            Intent result = new Intent();
            result.putExtras(b);
//            setResult(RESULT_OK, result);
//            finish();
            setData(result);
        }

    }

    public void setData(Intent data) {
        String deviceAddress = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
        BluetoothDevice mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);

//        ((TextView) findViewById(R.id.deviceName)).setText(mDevice.getName() + " - connecting");
        mUartService.connect(deviceAddress);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            mUartService = ((UartService.LocalBinder) rawBinder).getService();
            if (!mUartService.initialize()) {
            }
        }

        public void onServiceDisconnected(ComponentName classname) {
            ////     mService.disconnect(mDevice);
            mUartService = null;
        }
    };

    private void service_init() {
        Intent bindIntent = new Intent(this, UartService.class);
        bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

        LocalBroadcastManager.getInstance(this).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
        return intentFilter;
    }

    private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            final Intent mIntent = intent;
            //*********************//
            if (action.equals(UartService.ACTION_GATT_CONNECTED)) {
                String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                mState = UART_PROFILE_CONNECTED;
            }

            //*********************//
            if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
                String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                mState = UART_PROFILE_DISCONNECTED;
                mUartService.close();

            }


            //*********************//
            if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
                mUartService.enableTXNotification();
            }
            //*********************//
            if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {
                final byte[] txValue = intent.getByteArrayExtra(UartService.EXTRA_DATA);
                bytedata.add(txValue);
                try {
                    String text = new String(txValue, "UTF-8");
                    String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
//                    encodedData.add(encodeHexString(txValue));
                } catch (Exception e) {
                }

                BLEInfoReader bleReader = new BLEInfoReader();
                BLEInfo bleInfo = bleReader.readBLEInfo(encodedData);

            }
            //*********************//
            if (action.equals(UartService.DEVICE_DOES_NOT_SUPPORT_UART)) {
                mUartService.disconnect();
            }

        }
    };

    @Override
    public void onStartScan() {

    }

    @Override
    public void onStopScan() {

    }
}