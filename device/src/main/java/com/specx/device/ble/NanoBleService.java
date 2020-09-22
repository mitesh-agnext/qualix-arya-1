package com.specx.device.ble;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

import timber.log.Timber;

/**
 * BLE service for interacting with a NIRScan Nano. This service is intended to be used as a
 * template for custom applications. This service create the link between the Nano and the SDK.
 * This service also serves as a link between the activities and the SDK
 * <p>
 * This service manages the BLE connection the a Nano, while the SDK provides the command interface.
 * This means that the service is in charge of
 * <p>
 * Commands are send from app to user using the functions in this class. Since the service handles
 * enumeration, it is important that the GATT operation return codes are checked to see if a
 * characteristic is null when issuing a command.
 * <p>
 * This SDK also contains the JNI functions and custom classes for interfacing
 * with TI's Spectrum C Library. This library is written in C, and requires the NDK to compile.
 * The native functions in this file must match the JNI signature defined in the interface.c file.
 * Along with a correct JNI signature, the classes used to return JNI objects must match the class
 * hierarchy in order to be properly used by the NDK
 */
public class NanoBleService extends Service {

    private ByteArrayOutputStream scanData = new ByteArrayOutputStream();
    private ByteArrayOutputStream refConf = new ByteArrayOutputStream();
    private ByteArrayOutputStream refMatrix = new ByteArrayOutputStream();
    private ByteArrayOutputStream scanConf = new ByteArrayOutputStream();
    private ByteArrayOutputStream spectrumCalCoeff = new ByteArrayOutputStream();
    private ByteArrayOutputStream currentScanConfigData = new ByteArrayOutputStream();

    //Scan and reference calibration information variables
    private int size;
    private int refSize;
    private int refSizeIndex;
    private int refMatrixSize;
    private int scanConfSize;
    private int scanConfIndexSize;
    private int scanConfIndex;
    private int storedSDScanSize;
    private int spectrumCalCoeffSize;
    private int currentScanConfigDataSize;
    private String scanName;
    private String scanType;
    private String scanDate;
    private String scanPktFmtVer;
    private byte[] storedScanName;

    //Device information variables
    private String manufName;
    private String modelNum;
    private String serialNum;
    private String hardwareRev;
    private String tivaRev;
    private String spectrumRev;

    //Device status variables
    private int battLevel;
    private float temp;
    private float humidity;
    private String devStatus;
    private String errStatus;
    private byte[] tempThresh;
    private byte[] humidThresh;

    //Logic control flags
    private boolean scanStarted = false;
    private boolean readingStoredScans = false;
    private boolean activeConfRequested = false;

    private boolean getbattery = false;

    public NanoBleService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    //Instance of the Bluetooth Manager. This is used to retrieve the Bluetooth Adapter from the
    // Android system
    private BluetoothManager mBluetoothManager;

    //String containing the Address of the device we wish to connect to
    private String mBluetoothDeviceAddress;

    //Array list that will hold all of the GATT characteristics retrieved from the connected device
    private ArrayList<byte[]> scanConfList = new ArrayList<>();
    private ArrayList<byte[]> storedScanList = new ArrayList<>();

    private static BroadcastReceiver mDataReceiver;
    private static BroadcastReceiver mInfoRequestReceiver;
    private static BroadcastReceiver mStatusRequestReceiver;
    private static BroadcastReceiver mScanConfRequestReceiver;
    private static BroadcastReceiver mStoredScanRequestReceiver;
    private static BroadcastReceiver mSetTimeReceiver;
    private static BroadcastReceiver mStartScanReceiver;
    private static BroadcastReceiver mDeleteScanReceiver;
    private static BroadcastReceiver mGetActiveScanConfReceiver;
    private static BroadcastReceiver mSetActiveScanConfReceiver;
    private static BroadcastReceiver mUpdateThresholdReceiver;
    private static BroadcastReceiver mRequestActiveConfReceiver;
    private static BroadcastReceiver mLampReceiver;
    private static BroadcastReceiver mPGAReceiver;
    private static BroadcastReceiver mRepeatReceiver;
    private static BroadcastReceiver mLamptimeReceiver;
    private static BroadcastReceiver mSaveReferencetReceiver;
    private static BroadcastReceiver mActivateStateReceiver;
    private static BroadcastReceiver mReadCurrentConfigReceiver;
    private static BroadcastReceiver mWriteScanConfigReceiver;
    private static BroadcastReceiver mReadActivateStateReceiver;
    private static BroadcastReceiver mUUIDRequestReceiver;
    private static BroadcastReceiver mBatteryRequestReceiver;

    public static final String ACTION_SCAN_STARTED = "com.isctechnologies.NanoScan.bluetooth.service.ACTION_SCAN_STARTED";

    //Initialize the current scan index to a four-byte zero array
    private byte[] scanIndex = {0x00, 0x00, 0x00, 0x00};

    //CCID UUID as a string. The hyphens and lower case letters are intentional and must remain as provided.
    UUID CCCD_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    /**
     * Implements callback methods for GATT events that the app cares about.  These include
     * connection/disconnection, services discovered, and characteristic read/write/notify.
     */
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        /**
         * Callback handler for connection state changes. If the new state is connected, a call to
         * discover services is made immediately
         *
         * @param gatt the Gatt of the Bluetooth Device that we care about
         * @param status The returned value of the connect/disconnect operation
         * @param newState The new connection state of the Bluetooth Device
         */
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Timber.i("Connected to Gatt server.");

                // Attempts to discover services after successful connection.
                Timber.i("Attempting to start service discovery: %s", NIRScanSDK.mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = NIRScanSDK.ACTION_GATT_DISCONNECTED;
                refresh();
                Timber.i("Disconnected from Gatt server.");
                broadcastUpdate(intentAction);
            }
        }

        /**
         * Callback handler for Gatt enumeration
         * @param gatt the Gatt profile that was enumerated after connection
         * @param status The status of the enumeration operation
         */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {

            boolean enumerated = NIRScanSDK.enumerateServices(gatt);

            /*If enumeration was a success, send a broadcast to indicate that the enumeration is
             * complete. This should also kick off the process of subscribing to characteristic
             * notifications.
             *
             * If enumeration is not a success, print a warning if debug is enabled
             */
            if (status == BluetoothGatt.GATT_SUCCESS && enumerated) {
                Timber.d("Services discovered successfully");
                broadcastUpdate(NIRScanSDK.ACTION_GATT_SERVICES_DISCOVERED);

                BluetoothGattDescriptor descriptor = NIRScanSDK.NanoGattCharacteristic.mBleGattCharGCISRetRefCalCoefficients.getDescriptor(CCCD_UUID);
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                NIRScanSDK.mBluetoothGatt.writeDescriptor(descriptor);
            } else {
                Timber.e("onServicesDiscovered received: %s", status);
            }
        }

        /*
         * Handle descriptor write events.
         *
         * This process is kick-started when the GATT enumeration is complete. This allows for a
         * sequential characteristic subscription process. After all notifications have been set
         * up, send a broadcast to indicate that an activity can now kick off another process.
         *
         * It is important that these processes occur without interruption, as the BLE stack can
         * only handle one event at a time.
         */
        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);

            Timber.d("Descriptor: %s. Characteristic: %s. Status: %s", descriptor.getUuid(), descriptor.getCharacteristic().getUuid(), status);

            if (descriptor.getCharacteristic().getUuid().compareTo(NIRScanSDK.NanoGATT.GCIS_RET_REF_CAL_COEFF) == 0) {
                Timber.d("Wrote Notify request for GCIS_RET_REF_CAL_COEFF");
                BluetoothGattDescriptor mDescriptor = NIRScanSDK.NanoGattCharacteristic.mBleGattCharGCISRetRefCalMatrix.getDescriptor(CCCD_UUID);
                mDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                NIRScanSDK.mBluetoothGatt.writeDescriptor(mDescriptor);
            } else if (descriptor.getCharacteristic().getUuid().compareTo(NIRScanSDK.NanoGATT.GCIS_RET_REF_CAL_MATRIX) == 0) {
                Timber.d("Wrote Notify request for GCIS_RET_REF_CAL_MATRIX");
                BluetoothGattDescriptor mDescriptor = NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISStartScanNotify.getDescriptor(CCCD_UUID);
                mDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                NIRScanSDK.mBluetoothGatt.writeDescriptor(mDescriptor);
            } else if (descriptor.getCharacteristic().getUuid().compareTo(NIRScanSDK.NanoGATT.GSDIS_START_SCAN) == 0) {
                Timber.d("Wrote Notify request for GSDIS_START_SCAN");
                // BluetoothGattDescriptor mDescriptor = NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISRetScanName.getDescriptor(CCCD_UUID);
                BluetoothGattDescriptor mDescriptor = NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISRetSerialScanDataStruct.getDescriptor(CCCD_UUID);
                mDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                NIRScanSDK.mBluetoothGatt.writeDescriptor(mDescriptor);
            } else if (descriptor.getCharacteristic().getUuid().compareTo(NIRScanSDK.NanoGATT.GSDIS_RET_SCAN_NAME) == 0) {
                Timber.d("Wrote Notify request for GSDIS_RET_SCAN_NAME");
                BluetoothGattDescriptor mDescriptor = NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISRetScanType.getDescriptor(CCCD_UUID);
                mDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                NIRScanSDK.mBluetoothGatt.writeDescriptor(mDescriptor);
            } else if (descriptor.getCharacteristic().getUuid().compareTo(NIRScanSDK.NanoGATT.GSDIS_RET_SCAN_TYPE) == 0) {
                Timber.d("Wrote Notify request for GSDIS_RET_SCAN_TYPE");
                BluetoothGattDescriptor mDescriptor = NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISRetScanDate.getDescriptor(CCCD_UUID);
                mDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                NIRScanSDK.mBluetoothGatt.writeDescriptor(mDescriptor);
            } else if (descriptor.getCharacteristic().getUuid().compareTo(NIRScanSDK.NanoGATT.GSDIS_RET_SCAN_DATE) == 0) {
                Timber.d("Wrote Notify request for GSDIS_RET_SCAN_DATE");
                BluetoothGattDescriptor mDescriptor = NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISRetPacketFormatVersion.getDescriptor(CCCD_UUID);
                mDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                NIRScanSDK.mBluetoothGatt.writeDescriptor(mDescriptor);
            } else if (descriptor.getCharacteristic().getUuid().compareTo(NIRScanSDK.NanoGATT.GSDIS_RET_PKT_FMT_VER) == 0) {
                Timber.d("Wrote Notify request for GSDIS_RET_PKT_FMT_VER");
                BluetoothGattDescriptor mDescriptor = NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISRetSerialScanDataStruct.getDescriptor(CCCD_UUID);
                mDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                NIRScanSDK.mBluetoothGatt.writeDescriptor(mDescriptor);
            } else if (descriptor.getCharacteristic().getUuid().compareTo(NIRScanSDK.NanoGATT.GSDIS_RET_SER_SCAN_DATA_STRUCT) == 0) {
                Timber.d("Wrote Notify request for GSDIS_RET_SER_SCAN_DATA_STRUCT");
                BluetoothGattDescriptor mDescriptor = NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSCISRetStoredConfList.getDescriptor(CCCD_UUID);
                mDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                NIRScanSDK.mBluetoothGatt.writeDescriptor(mDescriptor);
            } else if (descriptor.getCharacteristic().getUuid().compareTo(NIRScanSDK.NanoGATT.GSCIS_RET_STORED_CONF_LIST) == 0) {
                Timber.d("Wrote Notify request for GSCIS_RET_STORED_CONF_LIST");
                BluetoothGattDescriptor mDescriptor = NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISSDStoredScanIndicesListData.getDescriptor(CCCD_UUID);
                mDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                NIRScanSDK.mBluetoothGatt.writeDescriptor(mDescriptor);
            } else if (descriptor.getCharacteristic().getUuid().compareTo(NIRScanSDK.NanoGATT.GSDIS_SD_STORED_SCAN_IND_LIST_DATA) == 0) {
                Timber.d("Wrote Notify request for GSDIS_SD_STORED_SCAN_IND_LIST_DATA");
                // BluetoothGattDescriptor mDescriptor = NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISClearScanNotify.getDescriptor(CCCD_UUID);
                BluetoothGattDescriptor mDescriptor = NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSCISRetScanConfData.getDescriptor(CCCD_UUID);
                mDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                NIRScanSDK.mBluetoothGatt.writeDescriptor(mDescriptor);
            }/* else if (descriptor.getCharacteristic().getUuid().compareTo(NIRScanSDK.NanoGATT.GSDIS_CLEAR_SCAN) == 0) {
                Timber.d("Wrote Notify request for GSDIS_CLEAR_SCAN");
                BluetoothGattDescriptor mDescriptor = NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSCISRetScanConfData.getDescriptor(CCCD_UUID);
                mDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                NIRScanSDK.mBluetoothGatt.writeDescriptor(mDescriptor);
            }*/ else if (descriptor.getCharacteristic().getUuid().compareTo(NIRScanSDK.NanoGATT.GSCIS_RET_SCAN_CONF_DATA) == 0) {
                Timber.d("Wrote Notify request for GSCIS_RET_SCAN_CONF_DATA");
                BluetoothGattDescriptor mDescriptor = NIRScanSDK.NanoGattCharacteristic.mBleGattCharGCISRetSpecCalCoefficients.getDescriptor(CCCD_UUID);
                mDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                NIRScanSDK.mBluetoothGatt.writeDescriptor(mDescriptor);
            } else if (descriptor.getCharacteristic().getUuid().compareTo(NIRScanSDK.NanoGATT.GCIS_RET_SPEC_CAL_COEFF) == 0) {
                Timber.d("Wrote Notify request for GCIS_RET_SPEC_CAL_COEFF");
                BluetoothGattDescriptor mDescriptor = NIRScanSDK.NanoGattCharacteristic.mBleGattCharacteristicCharacteristicActivateStateNotify.getDescriptor(CCCD_UUID);
                mDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                NIRScanSDK.mBluetoothGatt.writeDescriptor(mDescriptor);
            } else if (descriptor.getCharacteristic().getUuid().compareTo(NIRScanSDK.NanoGATT.GSDIS_RETURN_ACTIVATE_STATE) == 0) {
                Timber.d("Wrote Notify request for GSDIS_ACTIVATE_STATE");
                BluetoothGattDescriptor mDescriptor = NIRScanSDK.NanoGattCharacteristic.mBleGattCharacteristicReturnCurrentScanConfigurationData.getDescriptor(CCCD_UUID);
                mDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                NIRScanSDK.mBluetoothGatt.writeDescriptor(mDescriptor);
            } else if (descriptor.getCharacteristic().getUuid().compareTo(NIRScanSDK.NanoGATT.GSDIS_RETURN_CURRENT_SCANCONFIG_DATA) == 0) {
                Timber.d("Wrote Notify request for GSDIS_RETURN_CURRENT_SCANCONFIG_DATA");
                BluetoothGattDescriptor mDescriptor = NIRScanSDK.NanoGattCharacteristic.mBleGattCharacteristicReturnWriteScanConfigurationData.getDescriptor(CCCD_UUID);
                mDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                NIRScanSDK.mBluetoothGatt.writeDescriptor(mDescriptor);
            } else if (descriptor.getCharacteristic().getUuid().compareTo(NIRScanSDK.NanoGATT.GSDIS_RETURN_WRITE_SCANCONFIG_DATA) == 0) {
                Timber.d("Wrote Notify request for GSDIS_RETURN_WRITE_SCANCONFIG_DATA");
                Intent notifyCompleteIntent = new Intent(NIRScanSDK.ACTION_NOTIFY_DONE);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(notifyCompleteIntent);
            }
        }

        /**
         * Callback handler for characteristic reads
         *
         * It is important to note that some characteristic reads will kick off others. This is
         * because the calling activity requires more information, and the number of broadcasts
         * needed is reduced if all of the needed information is attached to a single broadcast
         *
         * @param gatt the Gatt of the connected device
         * @param characteristic the characteristic that was written
         * @param status the returned value of the read operation
         */
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (characteristic.getUuid().equals(NIRScanSDK.NanoGATT.DIS_MANUF_NAME)) {
                    manufName = new String(characteristic.getValue());
                    NIRScanSDK.getModelNumber();
                } else if (characteristic.getUuid().equals(NIRScanSDK.NanoGATT.DIS_MODEL_NUMBER)) {
                    modelNum = new String(characteristic.getValue());
                    NIRScanSDK.getSerialNumber();
                } else if (characteristic.getUuid().equals(NIRScanSDK.NanoGATT.DIS_SERIAL_NUMBER)) {
                    serialNum = new String(characteristic.getValue());
                    NIRScanSDK.getHardwareRev();
                } else if (characteristic.getUuid().equals(NIRScanSDK.NanoGATT.DIS_HW_REV)) {
                    hardwareRev = new String(characteristic.getValue());
                    NIRScanSDK.getFirmwareRev();
                } else if (characteristic.getUuid().equals(NIRScanSDK.NanoGATT.DIS_TIVA_FW_REV)) {
                    tivaRev = new String(characteristic.getValue());
                    NIRScanSDK.getSpectrumCRev();
                } else if (characteristic.getUuid().equals(NIRScanSDK.NanoGATT.DIS_SPECC_REV)) {
                    spectrumRev = new String(characteristic.getValue());
                    final Intent intent = new Intent(NIRScanSDK.ACTION_INFO);
                    intent.putExtra(NIRScanSDK.EXTRA_MANUF_NAME, manufName);
                    intent.putExtra(NIRScanSDK.EXTRA_MODEL_NUM, modelNum);
                    intent.putExtra(NIRScanSDK.EXTRA_SERIAL_NUM, serialNum);
                    intent.putExtra(NIRScanSDK.EXTRA_HW_REV, hardwareRev);
                    intent.putExtra(NIRScanSDK.EXTRA_TIVA_REV, tivaRev);
                    intent.putExtra(NIRScanSDK.EXTRA_SPECTRUM_REV, spectrumRev);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                } else if (characteristic.getUuid().equals(NIRScanSDK.NanoGATT.BAS_BATT_LVL)) {
                    byte[] data = characteristic.getValue();
                    final StringBuilder stringBuilder = new StringBuilder(data.length);
                    for (byte byteChar : data) {
                        stringBuilder.append(String.format("%02X", byteChar));
                    }
                    Timber.d("Batt level: %s", stringBuilder.toString());
                    battLevel = data[0];
                    if (getbattery) {
                        Intent sendActiveConfIntent = new Intent(NIRScanSDK.SEND_BATTERY);
                        sendActiveConfIntent.putExtra(NIRScanSDK.EXTRA_BATTERY, battLevel);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(sendActiveConfIntent);
                    } else {
                        NIRScanSDK.getTemp();
                    }
                } else if (characteristic.getUuid().equals(NIRScanSDK.NanoGATT.GGIS_TEMP_MEASUREMENT)) {
                    byte[] data = characteristic.getValue();
                    final StringBuilder stringBuilder = new StringBuilder(data.length);
                    for (byte byteChar : data) {
                        stringBuilder.append(String.format("%02X", byteChar));
                    }
                    Timber.d("Temp level: %s", stringBuilder.toString());
                    temp = (float) (data[1] << 8 | (data[0] & 0xff)) / 100;
                    Timber.d("Temp level: %s", temp);
                    NIRScanSDK.getHumidity();
                } else if (characteristic.getUuid().equals(NIRScanSDK.NanoGATT.GGIS_HUMID_MEASUREMENT)) {
                    byte[] data = characteristic.getValue();
                    final StringBuilder stringBuilder = new StringBuilder(data.length);
                    for (byte byteChar : data) {
                        stringBuilder.append(String.format("%02X", byteChar));
                    }
                    Timber.d("Humid level: %s", stringBuilder.toString());
                    humidity = (float) (data[1] << 8 | (data[0] & 0xff)) / 100;
                    NIRScanSDK.getDeviceStatus();
                } else if (characteristic.getUuid().equals(NIRScanSDK.NanoGATT.GGIS_DEV_STATUS)) {
                    byte[] data = characteristic.getValue();
                    final StringBuilder stringBuilder = new StringBuilder(data.length);
                    for (byte byteChar : data) {
                        stringBuilder.append(String.format("%02X", byteChar));
                    }
                    Timber.d("Dev status: %s", stringBuilder.toString());
                    devStatus = stringBuilder.toString();
                    NIRScanSDK.getErrorStatus();
                } else if (characteristic.getUuid().equals(NIRScanSDK.NanoGATT.GGIS_ERR_STATUS)) {
                    byte[] data = characteristic.getValue();
                    final StringBuilder stringBuilder = new StringBuilder(data.length);
                    for (byte byteChar : data) {
                        stringBuilder.append(String.format("%02X", byteChar));
                    }
                    Timber.d("Error status: %s", stringBuilder.toString());
                    errStatus = stringBuilder.toString();

                    final Intent intent = new Intent(NIRScanSDK.ACTION_STATUS);
                    intent.putExtra(NIRScanSDK.EXTRA_BATT, battLevel);
                    intent.putExtra(NIRScanSDK.EXTRA_TEMP, temp);
                    intent.putExtra(NIRScanSDK.EXTRA_HUMID, humidity);
                    intent.putExtra(NIRScanSDK.EXTRA_DEV_STATUS, devStatus);
                    intent.putExtra(NIRScanSDK.EXTRA_ERR_STATUS, errStatus);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                } else if (characteristic.getUuid().equals(NIRScanSDK.NanoGATT.GSCIS_NUM_STORED_CONF)) {
                    byte[] data = characteristic.getValue();

                    scanConfIndex = 0;
                    scanConfIndexSize = (((data[1]) << 8) | (data[0] & 0xFF));
                    Intent scanConfSizeIntent = new Intent(NIRScanSDK.SCAN_CONF_SIZE);
                    scanConfSizeIntent.putExtra(NIRScanSDK.EXTRA_CONF_SIZE, scanConfIndexSize);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(scanConfSizeIntent);

                    Timber.d("Num stored scan configs: %s", scanConfIndexSize);

                    NIRScanSDK.requestStoredConfigurationList();
                } else if (characteristic.getUuid().equals(NIRScanSDK.NanoGATT.GSDIS_NUM_SD_STORED_SCANS)) {
                    byte[] data = characteristic.getValue();

                    storedSDScanSize = (((data[1]) << 8) | (data[0] & 0xFF));

                    Timber.d("Num stored SD scans: %s", storedSDScanSize);
                    Intent sdScanSizeIntent = new Intent(NIRScanSDK.SD_SCAN_SIZE);
                    sdScanSizeIntent.putExtra(NIRScanSDK.EXTRA_INDEX_SIZE, storedSDScanSize);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(sdScanSizeIntent);
                    NIRScanSDK.requestScanIndicesList();
                } else if (characteristic.getUuid().equals(NIRScanSDK.NanoGATT.GSCIS_ACTIVE_SCAN_CONF)) {
                    byte[] data = characteristic.getValue();
                    if (!activeConfRequested) {
                        final StringBuilder stringBuilder = new StringBuilder(data.length);
                        for (byte byteChar : data) {
                            stringBuilder.append(String.format("%02X", byteChar));
                        }
                        Timber.d("Active scan conf index: %s", stringBuilder.toString());
                        Intent sendActiveConfIntent = new Intent(NIRScanSDK.SEND_ACTIVE_CONF);
                        sendActiveConfIntent.putExtra(NIRScanSDK.EXTRA_ACTIVE_CONF, data);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(sendActiveConfIntent);
                    } else {
                        byte[] confIndex = {data[0], data[1]};
                        Timber.d("Writing request for scan conf at index:" + confIndex[0] + "-" + confIndex[1]);
                        NIRScanSDK.requestScanConfiguration(confIndex);
                    }
                } else if (characteristic.getUuid().equals(NIRScanSDK.NanoGATT.GSDIS_ACTIVATE_STATE)) {
                    byte[] data = characteristic.getValue();
                    Intent ReadActivateStateIntent = new Intent(NIRScanSDK.ACTION_RETURN_READ_ACTIVATE_STATE);
                    ReadActivateStateIntent.putExtra(NIRScanSDK.RETURN_READ_ACTIVATE_STATE, data);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(ReadActivateStateIntent);
                } else if (characteristic.getUuid().equals(NIRScanSDK.NanoGATT.DEVICE_UUID)) {
                    byte[] data = characteristic.getValue();
                    Intent sendActiveConfIntent = new Intent(NIRScanSDK.SEND_DEVICE_UUID);
                    sendActiveConfIntent.putExtra(NIRScanSDK.EXTRA_DEVICE_UUID, data);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(sendActiveConfIntent);
                } else {
                    Timber.d("Read from unknown characteristic: %s", characteristic.getUuid().toString());
                }
            }
        }

        /**
         * Callback handler for characteristic notify/indicate updates
         *
         * It is important to note that some characteristic reads will kick off others. This is
         * because the calling activity requires more information, and the number of broadcasts
         * needed is reduced if all of the needed information is attached to a single broadcast
         *
         * @param gatt the Gatt profile of the connected device
         * @param characteristic the characteristic that provided the notify/indicate
         */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

//          Timber.d("onCharacteristic changed for characteristic: %s", characteristic.getUuid().toString());

            if (characteristic == NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISStartScanNotify) {
                scanData.reset();
                refConf.reset();
                refMatrix.reset();
                spectrumCalCoeff.reset();
                currentScanConfigData.reset();
                size = 0;
                refSize = 0;
                refMatrixSize = 0;
                spectrumCalCoeffSize = 0;
                currentScanConfigDataSize = 0;
                final byte[] data = characteristic.getValue();
                if (data[0] == (byte) 0xff) {
                    Timber.d("Scan data is ready to be read");
                    scanIndex[0] = data[1];
                    scanIndex[1] = data[2];
                    scanIndex[2] = data[3];
                    scanIndex[3] = data[4];

                    Timber.d("Scan index is:" + scanIndex[0] + " " + scanIndex[1] + " " + scanIndex[2] + " " + scanIndex[3]);

//                  NIRScanSDK.requestScanName(scanIndex);
                    NIRScanSDK.requestSerializedScanDataStruct(scanIndex);
                    Intent scanStartedIntent = new Intent(ACTION_SCAN_STARTED);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(scanStartedIntent);
                }
            } else if (characteristic == NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISRetScanName) {
                final byte[] data = characteristic.getValue();

                Timber.d("Received scan name: %s", new String(data));
                scanName = new String(data);

                if (readingStoredScans) {
                    storedScanName = data;
                    NIRScanSDK.requestScanDate(storedScanList.get(0));
                } else {
                    NIRScanSDK.requestScanType(scanIndex);
                }
            } else if (characteristic == NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISRetScanType) {
                final byte[] data = characteristic.getValue();
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data) {
                    stringBuilder.append(String.format("%02X", byteChar));
                }
                Timber.d("Received scan type: %s", stringBuilder.toString());
                scanType = stringBuilder.toString();
                NIRScanSDK.requestScanDate(scanIndex);
            } else if (characteristic == NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISRetScanDate) {
                final byte[] data = characteristic.getValue();
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data) {
                    stringBuilder.append(String.format(Locale.getDefault(), "%02d", byteChar));
                }
                Timber.d("Received scan date: %s", stringBuilder.toString());
                scanDate = stringBuilder.toString();
                if (readingStoredScans) {
                    broadcastUpdate(NIRScanSDK.STORED_SCAN_DATA, scanDate, storedScanList.get(0));
                    storedScanList.remove(0);
                    if (storedScanList.size() > 0) {
                        NIRScanSDK.requestScanName(storedScanList.get(0));
                    } else {
                        readingStoredScans = false;
                    }
                } else {
                    NIRScanSDK.requestPacketFormatVersion(scanIndex);
                }
            } else if (characteristic == NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISRetPacketFormatVersion) {
                final byte[] data = characteristic.getValue();
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data) {
                    stringBuilder.append(String.format("%02X ", byteChar));
                }
                Timber.d("Received packet format version: %s", stringBuilder.toString());
                scanPktFmtVer = stringBuilder.toString();
                NIRScanSDK.requestSerializedScanDataStruct(scanIndex);
            } else if (characteristic == NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISRetSerialScanDataStruct) {
                final byte[] data = characteristic.getValue();
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data) {
                    stringBuilder.append(String.format("%02X ", byteChar));
                }
//              Timber.d("Received serialized scan data struct: %s", stringBuilder.toString());
                if (data[0] == 0x00) {
                    size = (((data[2]) << 8) | (data[1] & 0xFF));
                } else {
                    for (int i = 1; i < data.length; i++) {
                        scanData.write(data[i]);
                    }
                }

                if (scanData.size() == size) {
                    size = 0;
                    Timber.d("Done collecting scan data, sending broadcast");
                    broadcastUpdate(NIRScanSDK.SCAN_DATA, scanData.toByteArray());
                }

//              Timber.d("New scan data size: %s", scanData.size());
            } else if (characteristic == NIRScanSDK.NanoGattCharacteristic.mBleGattCharGCISRetRefCalCoefficients) {
                final byte[] data = characteristic.getValue();
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data) {
                    stringBuilder.append(String.format("%02X ", byteChar));
                }
//              Timber.d("Received Reference calibration coefficients: %s", stringBuilder.toString());

                if (data[0] == 0x00) {
                    refConf.reset();
                    refSizeIndex = 0;
                    refSize = (((data[2]) << 8) | (data[1] & 0xFF));
                    Intent requestCalCoef = new Intent(NIRScanSDK.ACTION_REQ_CAL_COEFF);
                    requestCalCoef.putExtra(NIRScanSDK.EXTRA_REF_CAL_COEFF_SIZE, refSize);
                    requestCalCoef.putExtra(NIRScanSDK.EXTRA_REF_CAL_COEFF_SIZE_PACKET, true);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(requestCalCoef);
                } else {
                    for (int i = 1; i < data.length; i++) {
                        refConf.write(data[i]);
                    }
                    Intent requestCalCoef = new Intent(NIRScanSDK.ACTION_REQ_CAL_COEFF);
                    requestCalCoef.putExtra(NIRScanSDK.EXTRA_REF_CAL_COEFF_SIZE, data.length - 1);
                    requestCalCoef.putExtra(NIRScanSDK.EXTRA_REF_CAL_COEFF_SIZE_PACKET, false);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(requestCalCoef);
                }

                if (refConf.size() == refSize) {
                    refSize = 0;
                    Timber.d("Done collecting reference, sending broadcast");
                    NIRScanSDK.requestRefCalMatrix();
                }
            } else if (characteristic == NIRScanSDK.NanoGattCharacteristic.mBleGattCharGCISRetRefCalMatrix) {
                final byte[] data = characteristic.getValue();
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data) {
                    stringBuilder.append(String.format("%02X ", byteChar));
                }
//              Timber.d("Received reference calibration matrix: %s", stringBuilder.toString());

                if (data[0] == 0x00) {
                    refMatrix.reset();
                    refMatrixSize = (((data[2]) << 8) | (data[1] & 0xFF));
                    Intent requestCalMatrix = new Intent(NIRScanSDK.ACTION_REQ_CAL_MATRIX);
                    requestCalMatrix.putExtra(NIRScanSDK.EXTRA_REF_CAL_MATRIX_SIZE, refMatrixSize);
                    requestCalMatrix.putExtra(NIRScanSDK.EXTRA_REF_CAL_MATRIX_SIZE_PACKET, true);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(requestCalMatrix);
                } else {
                    for (int i = 1; i < data.length; i++) {
                        refMatrix.write(data[i]);
                    }
                    Intent requestCalCoef = new Intent(NIRScanSDK.ACTION_REQ_CAL_MATRIX);
                    requestCalCoef.putExtra(NIRScanSDK.EXTRA_REF_CAL_MATRIX_SIZE, data.length - 1);
                    requestCalCoef.putExtra(NIRScanSDK.EXTRA_REF_CAL_MATRIX_SIZE_PACKET, false);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(requestCalCoef);
                }

                if (refMatrix.size() == refMatrixSize) {
                    refSize = 0;

                    Timber.d("Done collecting reference Matrix, sending broadcast");
                    broadcastUpdate(NIRScanSDK.REF_CONF_DATA, refConf.toByteArray(), refMatrix.toByteArray());
                }
            } else if (characteristic == NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSCISRetStoredConfList) {
                final byte[] data = characteristic.getValue();
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data) {
                    stringBuilder.append(String.format("%02X ", byteChar));
                }
                Timber.d("Received scan conf index: %s", stringBuilder.toString());

                scanConfIndex++;
                scanConfList.add(data);

                //calculate should receive data size
                int receiveConfigSize = 0;
                //the first data  + data header + data two bytes
                //ex:12 scan data,{0,24,0,0,0},{1,102,0,103,0,104,0,105,0,106,0,107,0,108,0,109,0,110,0,111},{2,0,112,0,113,0}
                int totalConfigSize = 4 + scanConfList.size() + scanConfIndexSize * 2;
                for (int i = 0; i < scanConfList.size(); i++) {
                    receiveConfigSize += scanConfList.get(i).length;
                }

                if (scanConfIndexSize == 1 && scanConfList.size() > 1) {

                    scanConfIndex = 1;
                    byte[] confIndex = {0, 0};
                    confIndex[0] = scanConfList.get(scanConfIndex)[1];
                    confIndex[1] = scanConfList.get(scanConfIndex)[2];

                    Timber.d("Writing request for scan conf at index:" + confIndex[0] + "-" + confIndex[1]);
                    NIRScanSDK.requestScanConfiguration(confIndex);
                }

                if (receiveConfigSize == totalConfigSize && scanConfIndexSize != 1) {
                    scanConfIndex = 1;
                    byte[] confIndex = {0, 0};
                    confIndex[0] = scanConfList.get(scanConfIndex)[1];
                    confIndex[1] = scanConfList.get(scanConfIndex)[2];

                    Timber.d("Writing request for scan conf at index:" + confIndex[0] + "-" + confIndex[1]);
                    NIRScanSDK.requestScanConfiguration(confIndex);
                }
            } else if (characteristic == NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISSDStoredScanIndicesListData) {
                final byte[] data = characteristic.getValue();
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data) {
                    stringBuilder.append(String.format("%02X ", byteChar));
                }
                Timber.d("Received SD scan indices list: %s", stringBuilder.toString());
                for (int index = 0; index < data.length / 4; index++) {
                    byte[] sdIndex = {data[index * 4], data[(index * 4) + 1], data[(index * 4) + 2], data[(index * 4) + 3]};

                    storedScanList.add(sdIndex);

                    Timber.d("New stored scan list size: %s", storedScanList.size());
                }
                if (storedScanList.size() == storedSDScanSize) {
                    byte[] indexData = storedScanList.get(0);
                    readingStoredScans = true;
                    NIRScanSDK.requestScanName(indexData);
                }
            } else if (characteristic == NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSCISRetScanConfData) {
                final byte[] data = characteristic.getValue();
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data) {
                    stringBuilder.append(String.format("%02X ", byteChar));
                }
//              Timber.d("Received scan conf data: %s", stringBuilder.toString());

                if (data[0] == 0x00) {
                    scanConf.reset();
                    scanConfSize = (((data[2]) << 8) | (data[1] & 0xFF));
                } else {
                    for (int i = 1; i < data.length; i++) {
                        scanConf.write(data[i]);
                    }
                }

                if (scanConf.size() == scanConfSize) {
                    if (!activeConfRequested) {
                        scanConfSize = 0;

                        Timber.d("Done collecting scanConfiguration, sending broadcast");
                        broadcastScanConfig(NIRScanSDK.SCAN_CONF_DATA, scanConf.toByteArray());

                        if (scanConfIndex < scanConfIndexSize) {
                            scanConfIndex++;
                            byte[] confIndex = {0, 0};

                            Timber.d("Retrieving scan at index:" + scanConfIndex + " Size is:" + scanConfList.size());
                            //<10 data
                            //data set  scan data,{0,24,0,0,0},{1,102,0,103,0,104,0,105,0,106,0,107,0,108,0,109,0,110,0,111},{2,0,112,0,113,0}
                            if (scanConfIndex < 10) {
                                confIndex[0] = scanConfList.get(1)[(scanConfIndex - 1) * 2 + 1];
                                confIndex[1] = scanConfList.get(1)[(scanConfIndex - 1) * 2 + 2];
                            } else if (scanConfIndex == 10) {
                                confIndex[0] = scanConfList.get(1)[(scanConfIndex - 1) * 2 + 1];
                                confIndex[1] = scanConfList.get(2)[1];
                            } else if (scanConfIndex == 20) {
                                confIndex[0] = scanConfList.get(3)[1];
                                confIndex[1] = scanConfList.get(3)[2];
                            }
                            //>10 data
                            else {
                                confIndex[0] = scanConfList.get(2)[(scanConfIndex - 10 - 1) * 2 + 2];
                                confIndex[1] = scanConfList.get(2)[(scanConfIndex - 10 - 1) * 2 + 3];
                            }
                            //When receive all data,should clear scanConfList,this fix scan configuration twice can't download scan config
                            if (scanConfIndex == scanConfIndexSize) {
                                scanConfList.clear();
                            }
                            Timber.d("Writing request for scan conf at index:" + confIndex[0] + "-" + confIndex[1]);
                            NIRScanSDK.requestScanConfiguration(confIndex);
                        } else {
                            scanConfIndex = 0;
                        }
                    } else {
                        scanConfSize = 0;

                        Timber.d("Done collecting active scanConfiguration");
                        broadcastScanConfig(NIRScanSDK.SCAN_CONF_DATA, scanConf.toByteArray());
                        scanConfIndex = 0;
                        activeConfRequested = false;
                    }
                }
            } else if (characteristic == NIRScanSDK.NanoGattCharacteristic.mBleGattCharGCISRetSpecCalCoefficients) {
                final byte[] data = characteristic.getValue();
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                Timber.d("Receive spectrumCalCoeff: %s", stringBuilder.toString());

                if (data[0] == 0x00) {
                    spectrumCalCoeffSize = 0;
                    spectrumCalCoeff.reset();
                    spectrumCalCoeffSize = (((data[2]) << 8) | (data[1] & 0xFF));
                } else {
                    for (int i = 1; i < data.length; i++) {
                        spectrumCalCoeff.write(data[i]);
                    }
                }
                if (spectrumCalCoeff.size() == spectrumCalCoeffSize) {
                    spectrumCalCoeffSize = 0;
                    Timber.d("Done collecting reference spectrumCalCoeff");
                    broadcastUpdateSpec(NIRScanSDK.SPEC_CONF_DATA, spectrumCalCoeff.toByteArray());
                }
            } else if (characteristic == NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISClearScanNotify) {
                final byte[] data = characteristic.getValue();
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data) {
                    stringBuilder.append(String.format("%02X ", byteChar));
                }
                Timber.d("Received status from clear scan: %s", stringBuilder.toString());
            } else if (characteristic == NIRScanSDK.NanoGattCharacteristic.mBleGattCharacteristicCharacteristicActivateStateNotify) {
                final byte[] data = characteristic.getValue();
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data) {
                    stringBuilder.append(String.format("%02X ", byteChar));
                }
                Timber.d("Received Activate State status: %s", stringBuilder.toString());
                broadcastReturnActivateState(NIRScanSDK.ACTION_RETURN_ACTIVATE, data);
            } else if (characteristic == NIRScanSDK.NanoGattCharacteristic.mBleGattCharacteristicReturnCurrentScanConfigurationData) {
                final byte[] data = characteristic.getValue();
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data) {
                    stringBuilder.append(String.format("%02X ", byteChar));
                }
                Timber.d("Receive Current ScanConfiguration Data: %s", stringBuilder.toString());

                if (data[0] == 0x00) {
                    currentScanConfigDataSize = 0;
                    currentScanConfigData.reset();
                    currentScanConfigDataSize = (((data[2]) << 8) | (data[1] & 0xFF));
                    Timber.d("iris currentScanConfigData Size %s", currentScanConfigDataSize);
                } else {
                    for (int i = 1; i < data.length; i++) {
                        currentScanConfigData.write(data[i]);
                    }
                }
                if (currentScanConfigData.size() == currentScanConfigDataSize) {
                    currentScanConfigDataSize = 0;
                    Timber.d("Done collecting reference currentScanConfigData");
                    broadcastUpdateCurrentConfig(NIRScanSDK.RETURN_CURRENT_CONFIG_DATA, currentScanConfigData.toByteArray());
                }

            } else if (characteristic == NIRScanSDK.NanoGattCharacteristic.mBleGattCharacteristicReturnWriteScanConfigurationData) {
                final byte[] data = characteristic.getValue();
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data) {
                    stringBuilder.append(String.format("%02X ", byteChar));
                }
                Timber.d("Received Write scan config status: %s", stringBuilder.toString());
                broadcastUpdateWriteScanConfigStatus(NIRScanSDK.ACTION_RETURN_WRITE_SCAN_CONFIG_STATUS, data);
            } else {
                Timber.d("Received notify/indicate from unknown characteristic: %s", characteristic.getUuid().toString());
            }
        }

        /**
         * Callback handler for characteristic writes
         *
         * It is important to note that some characteristic reads will kick off others. This is
         * because the calling activity requires more information, and the number of broadcasts
         * needed is reduced if all of the needed information is attached to a single broadcast
         *
         * @param gatt the Gatt profile of the connected device
         * @param characteristic the characteristic that was written
         * @param status the status of the write operation
         */
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);

            if (characteristic.getUuid().equals(NIRScanSDK.NanoGATT.GSDIS_START_SCAN)) {
                Timber.d("Wrote start scan! status = %s", status);
            } else if (characteristic.getUuid().equals(NIRScanSDK.NanoGATT.GSDIS_REQ_SER_SCAN_DATA_STRUCT)) {
                Timber.d("Wrote Request for Scan Data Struct! status = %s", status);
            } else if (characteristic.getUuid().equals(NIRScanSDK.NanoGATT.GSDIS_REQ_SCAN_TYPE)) {
                Timber.d("Wrote Request for Scan Type! status = %s", status);
            } else if (characteristic.getUuid().equals(NIRScanSDK.NanoGATT.GSDIS_REQ_SCAN_NAME)) {
                Timber.d("Wrote Request for Scan Name! status = %s", status);
            } else if (characteristic.getUuid().equals(NIRScanSDK.NanoGATT.GSDIS_REQ_SCAN_DATE)) {
                Timber.d("Wrote Request for Scan Date! status = %s", status);
            } else if (characteristic.getUuid().equals(NIRScanSDK.NanoGATT.GSDIS_REQ_PKT_FMT_VER)) {
                Timber.d("Wrote Request for Packet Format Version! status = %s", status);
            } else if (characteristic.getUuid().equals(NIRScanSDK.NanoGATT.GCIS_REQ_REF_CAL_COEFF)) {
                Timber.d("Wrote Request for Reference Calibration Coefficients! status = %s", status);
            } else if (characteristic.getUuid().equals(NIRScanSDK.NanoGATT.GCIS_REQ_REF_CAL_MATRIX)) {
                Timber.d("Wrote Request for Reference Calibration Matrix! status = %s", status);
            } else if (characteristic.getUuid().equals(NIRScanSDK.NanoGATT.GSCIS_REQ_STORED_CONF_LIST)) {
                Timber.d("Wrote Request for Scan configuration list! status = %s", status);
            } else if (characteristic.getUuid().equals(NIRScanSDK.NanoGATT.GSDIS_SD_STORED_SCAN_IND_LIST)) {
                Timber.d("Wrote Request for SD Stored scan indices list! status = %s", status);
            } else if (characteristic.getUuid().equals(NIRScanSDK.NanoGATT.GSCIS_REQ_SCAN_CONF_DATA)) {
                Timber.d("Wrote Request for Scan Conf data! status = %s", status);
            } else if (characteristic.getUuid().equals(NIRScanSDK.NanoGATT.GDTS_TIME)) {
                Timber.d("Wrote Time! status = %s", status);
                String dataString = "Data";
                byte[] data = new StringBuilder(dataString).reverse().toString().getBytes();

                Timber.d("Writing scan stub to: %s", dataString);
                NIRScanSDK.setStub(data);
            } else if (characteristic.getUuid().equals(NIRScanSDK.NanoGATT.GSDIS_SET_SCAN_NAME_STUB)) {
                Timber.d("Wrote scan name stub! status  = %s", status);
                if (!scanStarted) {
                    Timber.d("Requesting calibration data");
                    NIRScanSDK.requestRefCalCoefficients();
                } else {
                    scanStarted = false;
                    Timber.d("Starting scan");
                    byte[] data = {0x00};
                    Timber.d("Save to SD not selected, writing 0");
                    scanData.reset();
                    refConf.reset();
                    refMatrix.reset();
                    size = 0;
                    refSize = 0;
                    refMatrixSize = 0;

                    NIRScanSDK.startScan(data);
                }
            } else if (characteristic.getUuid().equals(NIRScanSDK.NanoGATT.GSDIS_CLEAR_SCAN)) {
                Timber.d("wrote clear scan! status = %s", status);
            } else if (characteristic.getUuid().equals(NIRScanSDK.NanoGATT.GSCIS_ACTIVE_SCAN_CONF)) {
                Timber.d("Wrote set active scan conf! status = %s", status);
                NIRScanSDK.getActiveConf();
            } else if (characteristic.getUuid().equals(NIRScanSDK.NanoGATT.GGIS_TEMP_THRESH)) {
                Timber.d("Wrote temperature threshold! status = %s", status);
                NIRScanSDK.setHumidityThreshold(humidThresh);
            } else if (characteristic.getUuid().equals(NIRScanSDK.NanoGATT.GGIS_HUMID_THRESH)) {
                Timber.d("Wrote humidity threshold! status = %s", status);
            } else if (characteristic.getUuid().equals(NIRScanSDK.NanoGATT.GSDIS_READ_CURRENT_SCANCONFIG_DATA)) {
                Timber.d("Read current scanconfig data status = %s", status);
            } else if (characteristic.getUuid().equals(NIRScanSDK.NanoGATT.GSDIS_ACTIVATE_STATE)) {
                Timber.d("Write activate state = %s", status);
            } else if (characteristic.getUuid().equals(NIRScanSDK.NanoGATT.GSDIS_WRITE_SCANCONFIG_DATA)) {
                Timber.d("Write Scan config data! status = %s", status);
                //  NIRScanSDK.ReadScanConfigDataStatus();
            } else {
                Timber.d("Unknown characteristic %s", characteristic.getUuid());
            }
        }
    };

    /**
     * Sends the desired broadcast action without any extras
     *
     * @param action the action to broadcast
     */
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    /**
     * Sends the desired broadcast action with the data read from the specified characteristic as
     * an extra. If a particular characteristic has a particular format, the characteristic can be
     * examined to determine how to properly format and send the data from the characteristic
     *
     * @param action         the action to broadcast
     * @param characteristic the characteristic to retrieve data from to send
     */
    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        // For all other profiles, writes the data formatted in HEX.
        final byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data) {
                stringBuilder.append(String.format("%02X ", byteChar));
            }
            Timber.d("Notify characteristic:" + characteristic.getUuid().toString() + " -- Notify data:" + stringBuilder.toString());
            intent.putExtra(NIRScanSDK.EXTRA_DATA, stringBuilder.toString());
        }

        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    /**
     * Sends the desired broadcast action with the data provided by scanData
     *
     * @param action                the action to broadcast
     * @param WriteScanConfigStatus the data to add to the broadcast
     */
    private void broadcastUpdateWriteScanConfigStatus(final String action, byte[] WriteScanConfigStatus) {
        final Intent intent = new Intent(action);
        intent.putExtra(NIRScanSDK.RETURN_WRITE_SCAN_CONFIG_STATUS, WriteScanConfigStatus);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    /**
     * Sends the desired broadcast action with the data provided by scanData
     *
     * @param action the action to broadcast
     * @param state  the data to add to the broadcast
     */
    private void broadcastReturnActivateState(final String action, byte[] state) {
        final Intent intent = new Intent(action);
        intent.putExtra(NIRScanSDK.RETURN_ACTIVATE_STATUS, state);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    /**
     * /**
     * Sends the desired broadcast action with the data provided by scanData
     *
     * @param action   the action to broadcast
     * @param SPECData the data to add to the broadcast
     */
    private void broadcastUpdateSpec(final String action, byte[] SPECData) {
        final Intent intent = new Intent(action);
        intent.putExtra(NIRScanSDK.EXTRA_SPEC_COEF_DATA, SPECData);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    /**
     * Sends the desired broadcast action with the data provided by scanData
     *
     * @param action            the action to broadcast
     * @param currentconfigdata the data to add to the broadcast
     */
    private void broadcastUpdateCurrentConfig(final String action, byte[] currentconfigdata) {
        final Intent intent = new Intent(action);
        intent.putExtra(NIRScanSDK.EXTRA_CURRENT_CONFIG_DATA, currentconfigdata);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    /**
     * Sends the desired broadcast action with the data provided by scanData
     *
     * @param action   the action to broadcast
     * @param scanData the data to add to the broadcast
     */
    private void broadcastUpdate(final String action, byte[] scanData) {
        final Intent intent = new Intent(action);
        intent.putExtra(NIRScanSDK.EXTRA_DATA, scanData);
        intent.putExtra(NIRScanSDK.EXTRA_SCAN_NAME, scanName);
        intent.putExtra(NIRScanSDK.EXTRA_SCAN_TYPE, scanType);
        intent.putExtra(NIRScanSDK.EXTRA_SCAN_DATE, scanDate);
        intent.putExtra(NIRScanSDK.EXTRA_SCAN_FMT_VER, scanPktFmtVer);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    /**
     * Sends the desired broadcast action with the data provided parameters
     *
     * @param action   the action to broadcast
     * @param scanData byte array of data to broadcast
     */
    private void broadcastScanConfig(final String action, byte[] scanData) {
        final Intent intent = new Intent(action);
        intent.putExtra(NIRScanSDK.EXTRA_DATA, scanData);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    /**
     * Sends the desired broadcast action with the data provided parameters
     *
     * @param action    the action to broadcast
     * @param refCoeff  byte array of reference coefficients
     * @param refMatrix byte array of reference calibration matrix
     */
    private void broadcastUpdate(final String action, byte[] refCoeff, byte[] refMatrix) {
        final Intent intent = new Intent(action);
        intent.putExtra(NIRScanSDK.EXTRA_DATA, scanData.toByteArray());
        intent.putExtra(NIRScanSDK.EXTRA_REF_COEF_DATA, refCoeff);
        intent.putExtra(NIRScanSDK.EXTRA_REF_MATRIX_DATA, refMatrix);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    /**
     * Sends the desired broadcast action with the data provided parameters
     *
     * @param action   the action to broadcast
     * @param scanDate the scan date to be added to the broadcast
     * @param index    the scan index to be added to the broadcast
     */
    private void broadcastUpdate(final String action, String scanDate, byte[] index) {
        final Intent intent = new Intent(action);
        intent.putExtra(NIRScanSDK.EXTRA_SCAN_NAME, nameToUTF8(storedScanName));
        intent.putExtra(NIRScanSDK.EXTRA_SCAN_DATE, scanDate);
        intent.putExtra(NIRScanSDK.EXTRA_SCAN_INDEX, index);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    /**
     * Instance of the binder to be used when binding to the service in app
     */
    public class LocalBinder extends Binder {
        public NanoBleService getService() {
            return NanoBleService.this;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        Timber.d("onUnbind called");
        //disconnect();
        close();
        return true;
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        Timber.d("Initializing BLE");
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Timber.e("Unable to initialize BluetoothManager.");
                return false;
            }
        }

        NIRScanSDK.mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (NIRScanSDK.mBluetoothAdapter == null) {
            Timber.e("Unable to obtain a BluetoothAdapter.");
            return false;
        }
        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public boolean connect(final String address) {
        if (NIRScanSDK.mBluetoothAdapter == null || address == null) {
            Timber.w("BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device. Try to reconnect.
        if (address.equals(mBluetoothDeviceAddress) && NIRScanSDK.mBluetoothGatt != null) {
            Timber.d("Trying to use an existing mBluetoothGatt for connection.");
            return NIRScanSDK.mBluetoothGatt.connect();
        }

        final BluetoothDevice device = NIRScanSDK.mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Timber.w("Device not found. Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Timber.d("Using LE Transport");
            NIRScanSDK.mBluetoothGatt = device.connectGatt(this, false, mGattCallback, BluetoothDevice.TRANSPORT_LE);
        } else {
            NIRScanSDK.mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        }

        Timber.d("Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.d("onCreate called");

        mDataReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    Timber.d("Starting scan");
                    byte[] data = {0x00};
                    Timber.d("Save to SD not selected, writing 0");
                    scanData.reset();
                    refConf.reset();
                    refMatrix.reset();
                    size = 0;
                    refSize = 0;
                    refMatrixSize = 0;

                    NIRScanSDK.startScan(data);
                }
            }
        };

        mInfoRequestReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    Timber.d("Requesting device info");
                    NIRScanSDK.getManufacturerName();
                }
            }
        };

        mUUIDRequestReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    Timber.d("Requesting UUID info");
                    NIRScanSDK.getUUID();
                }
            }
        };

        mBatteryRequestReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    Timber.d("Requesting device info");
                    getbattery = true;
                    NIRScanSDK.getBatteryLevel();
                }
            }
        };

        mStatusRequestReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    Timber.d("Requesting device status");
                    getbattery = false;
                    NIRScanSDK.getBatteryLevel();
                }
            }
        };

        mScanConfRequestReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    Timber.d("Requesting device status");
                    NIRScanSDK.getNumberStoredConfigurations();
                }
            }
        };

        mStoredScanRequestReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    Timber.d("Requesting stored scans");
                    NIRScanSDK.getNumberStoredScans();
                }
            }
        };

        mSetTimeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    Timber.d("Writing time to nano");
                    NIRScanSDK.setTime();
                }
            }
        };

        mStartScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                scanStarted = true;
                String dataString = "Nano";
                byte[] data = new StringBuilder(dataString).reverse().toString().getBytes();
                Timber.d("Writing scan stub to: %s", dataString);
                NIRScanSDK.setStub(data);
            }
        };

        mDeleteScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                byte[] index = intent.getByteArrayExtra(NIRScanSDK.EXTRA_SCAN_INDEX);
                Timber.d("deleting index:" + index[0] + "-" + index[1] + "-" + index[2] + "-" + index[3]);
                NIRScanSDK.deleteScan(index);
            }
        };

        mGetActiveScanConfReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Timber.d("Reading active scan conf");
                NIRScanSDK.getActiveConf();
            }
        };

        mSetActiveScanConfReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Timber.d("Setting active scan conf");
                byte[] data = intent.getByteArrayExtra(NIRScanSDK.EXTRA_SCAN_INDEX);
                NIRScanSDK.setActiveConf(data);
            }
        };
        mUpdateThresholdReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Timber.d("Updating Thresholds");
                tempThresh = intent.getByteArrayExtra(NIRScanSDK.EXTRA_TEMP_THRESH);
                humidThresh = intent.getByteArrayExtra(NIRScanSDK.EXTRA_HUMID_THRESH);
                NIRScanSDK.setTemperatureThreshold(tempThresh);
            }
        };

        mRequestActiveConfReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                activeConfRequested = true;
                NIRScanSDK.getActiveConf();
            }
        };

        mLampReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Timber.d("Setting Lamp");
                byte[] data = new byte[1];

                int value = intent.getIntExtra(NIRScanSDK.LAMP_ON_OFF, 0);
                if (value == 1) //on---1
                {
                    data[0] = (byte) 0x01;
                } else if (value == 0)//Auto----0
                {
                    data[0] = (byte) 0x00;
                } else//off ---2
                {
                    data[0] = (byte) 0x02;
                }
                NIRScanSDK.setLampMode(data);
            }
        };

        mPGAReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Timber.d("Setting pga");

                int value = intent.getIntExtra(NIRScanSDK.PGA_SET, 0);
                byte[] data = new byte[1];
                data[0] = (byte) ((value & 0x000000ff));
                NIRScanSDK.setPGA(data);
            }
        };

        mRepeatReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Timber.d("Setting repeat");
                byte[] data = new byte[1];

                int value = intent.getIntExtra(NIRScanSDK.REPEAT_SET, 0);
                data[0] = (byte) ((value & 0x000000ff));
                NIRScanSDK.setScanAverage(data);
            }
        };

        mLamptimeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Timber.d("Setting lamp time");
                byte[] data = new byte[4];
                int value = intent.getIntExtra(NIRScanSDK.LAMP_TIME, 0);
                data[0] = (byte) value;
                data[1] = (byte) (value >> 8);
                data[2] = (byte) (value >> 16);
                data[3] = (byte) (value >> 24);
                NIRScanSDK.setLampTime(data);
            }
        };

        mSaveReferencetReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Timber.d("Save reference");
                byte[] data = new byte[1];
                data[0] = (byte) 0x5A;
                NIRScanSDK.SaveReference(data);// delay
            }
        };

        mActivateStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Timber.d("Setting activate state key");
                byte[] data = intent.getByteArrayExtra(NIRScanSDK.ACTIVATE_STATE_KEY);
                NIRScanSDK.SetActiveStateKey(data);
            }
        };

        mReadActivateStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Timber.d("Read activate state");
                NIRScanSDK.ReadActiveState();
            }
        };

        mReadCurrentConfigReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Timber.d("Read current config");
                byte[] data = intent.getByteArrayExtra(NIRScanSDK.READ_CONFIG_DATA);
                NIRScanSDK.ReadCurrentConfig(data);
            }
        };

        mWriteScanConfigReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Timber.d("Write scan config");
                byte[] data = intent.getByteArrayExtra(NIRScanSDK.WRITE_SCAN_CONFIG_VALUE);
                NIRScanSDK.writeScanConfig(data);
            }
        };

        //Register all needed receivers
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mDataReceiver, new IntentFilter(NIRScanSDK.SEND_DATA));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mInfoRequestReceiver, new IntentFilter(NIRScanSDK.GET_INFO));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mStatusRequestReceiver, new IntentFilter(NIRScanSDK.GET_STATUS));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mScanConfRequestReceiver, new IntentFilter(NIRScanSDK.GET_SCAN_CONF));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mStoredScanRequestReceiver, new IntentFilter(NIRScanSDK.GET_STORED_SCANS));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mSetTimeReceiver, new IntentFilter(NIRScanSDK.SET_TIME));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mStartScanReceiver, new IntentFilter(NIRScanSDK.START_SCAN));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mDeleteScanReceiver, new IntentFilter(NIRScanSDK.DELETE_SCAN));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mGetActiveScanConfReceiver, new IntentFilter(NIRScanSDK.GET_ACTIVE_CONF));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mSetActiveScanConfReceiver, new IntentFilter(NIRScanSDK.SET_ACTIVE_CONF));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mUpdateThresholdReceiver, new IntentFilter(NIRScanSDK.UPDATE_THRESHOLD));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mRequestActiveConfReceiver, new IntentFilter(NIRScanSDK.REQUEST_ACTIVE_CONF));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mLampReceiver, new IntentFilter(NIRScanSDK.ACTION_LAMP));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mPGAReceiver, new IntentFilter(NIRScanSDK.ACTION_PGA));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mRepeatReceiver, new IntentFilter(NIRScanSDK.ACTION_REPEAT));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mLamptimeReceiver, new IntentFilter(NIRScanSDK.ACTION_LAMP_TIME));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mSaveReferencetReceiver, new IntentFilter(NIRScanSDK.ACTION_SAVE_REFERENCE));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mActivateStateReceiver, new IntentFilter(NIRScanSDK.ACTION_ACTIVATE_STATE));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mReadCurrentConfigReceiver, new IntentFilter(NIRScanSDK.ACTION_READ_CONFIG));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mWriteScanConfigReceiver, new IntentFilter(NIRScanSDK.ACTION_WRITE_SCAN_CONFIG));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mReadActivateStateReceiver, new IntentFilter(NIRScanSDK.ACTION_READ_ACTIVATE_STATE));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mUUIDRequestReceiver, new IntentFilter(NIRScanSDK.GET_UUID));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mBatteryRequestReceiver, new IntentFilter(NIRScanSDK.GET_BATTERY));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Timber.d("onDestroy called");

        //Clean up the registered receivers
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mDataReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mInfoRequestReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mStatusRequestReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mScanConfRequestReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mStoredScanRequestReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mSetTimeReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mStartScanReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mDeleteScanReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mGetActiveScanConfReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mSetActiveScanConfReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mUpdateThresholdReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mRequestActiveConfReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mLampReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mPGAReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mRepeatReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mLamptimeReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mSaveReferencetReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mActivateStateReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mReadCurrentConfigReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mWriteScanConfigReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mReadActivateStateReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mUUIDRequestReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mBatteryRequestReceiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.d("onStartCommand called");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Timber.d("onTaskRemoved called");
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (NIRScanSDK.mBluetoothAdapter == null) {
            Timber.w("BluetoothAdapter is null");
        }
        if (NIRScanSDK.mBluetoothGatt == null) {
            Timber.w("BluetoothGatt is null");
        }
        if (NIRScanSDK.mBluetoothAdapter == null || NIRScanSDK.mBluetoothGatt == null) {
            Timber.w("BluetoothAdapter not initialized");
            return;
        }
        NIRScanSDK.mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (NIRScanSDK.mBluetoothGatt == null) {
            return;
        }
        NIRScanSDK.mBluetoothGatt.close();
        NIRScanSDK.mBluetoothGatt = null;
    }

    /**
     * Convert byte array of name to UTF8 string
     *
     * @param data the scan name as a byte array
     * @return String in UTF8 of scan name bytes
     */
    private String nameToUTF8(byte[] data) {

        byte[] byteChars = new byte[data.length];
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        for (byte b : byteChars) {
            byteChars[b] = 0x00;
        }
        String s;
        for (int i = 0; i < data.length; i++) {

            byteChars[i] = data[i];

            if (data[i] == 0x00) {
                break;
            }

            os.write(data[i]);
        }

        s = new String(os.toByteArray(), StandardCharsets.UTF_8);
        return s;
    }

    /**
     * @param gatt the Bluetooth GATT object to call the refresh reflection method on
     * @return Boolean status of refresh operation; true = success, false = failure.
     */
    private boolean refreshDeviceCache(BluetoothGatt gatt) {
        try {
            BluetoothGatt localBluetoothGatt = gatt;
            Method localMethod = localBluetoothGatt.getClass().getMethod("refresh", new Class[0]);
            if (localMethod != null) {
                boolean bool = ((Boolean) localMethod.invoke(localBluetoothGatt, new Object[0])).booleanValue();
                return bool;
            }
        } catch (Exception e) {
            Timber.e(e, "An exception occurred while refreshing device");
        }
        return false;
    }

    /**
     * Method to refresh GATT device cache using known BluetoothGatt {@link NIRScanSDK#mBluetoothGatt}
     */
    private void refresh() {
        refreshDeviceCache(NIRScanSDK.mBluetoothGatt);
    }
}
