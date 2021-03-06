package com.specx.device.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

public class NIRScanSDK {

    public static BluetoothGatt mBluetoothGatt;
    public static BluetoothAdapter mBluetoothAdapter;
    public static final String ACTION_GATT_CONNECTED = "com.isctechnologies.NanoScan.bluetooth.le.ACTION_GATT_CONNECTED";
    public static final String ACTION_GATT_DISCONNECTED = "com.isctechnologies.NanoScan.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public static final String ACTION_GATT_SERVICES_DISCOVERED = "com.isctechnologies.NanoScan.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public static final String ACTION_NOTIFY_DONE = "com.isctechnologies.NanoScan.bluetooth.le.ACTION_NOTIFY_DONE";
    public static final String EXTRA_DATA = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_DATA";
    public static final String SEND_DATA = "com.isctechnologies.NanoScan.bluetooth.le.SEND_DATA";
    public static final String SCAN_DATA = "com.isctechnologies.NanoScan.bluetooth.le.SCAN_DATA";
    public static final String REF_CONF_DATA = "com.isctechnologies.NanoScan.bluetooth.le.REF_CONF_DATA";
    public static final String SCAN_CONF_DATA = "com.isctechnologies.NanoScan.bluetooth.le.SCAN_CONF_DATA";
    public static final String STORED_SCAN_DATA = "com.isctechnologies.NanoScan.bluetooth.le.STORED_SCAN_DATA";
    public static final String EXTRA_REF_COEF_DATA = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_REF_COEF_DATA";
    public static final String EXTRA_REF_MATRIX_DATA = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_REF_MATRIX_DATA";
    public static final String EXTRA_SCAN_NAME = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_SCAN_NAME";
    public static final String EXTRA_SCAN_TYPE = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_SCAN_TYPE";
    public static final String EXTRA_SCAN_DATE = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_SCAN_DATE";
    public static final String EXTRA_SCAN_INDEX = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_SCAN_INDEX";
    public static final String EXTRA_INDEX_SIZE = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_INDEX_SIZE";
    public static final String EXTRA_CONF_SIZE = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_CONF_SIZE";
    public static final String EXTRA_ACTIVE_CONF = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_ACTIVE_CONF";
    public static final String EXTRA_SCAN_FMT_VER = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_SCAN_FMT_VER";
    public static final String GET_INFO = "com.isctechnologies.NanoScan.bluetooth.le.GET_INFO";
    public static final String GET_STATUS = "com.isctechnologies.NanoScan.bluetooth.le.GET_STATUS";
    public static final String GET_SCAN_CONF = "com.isctechnologies.NanoScan.bluetooth.le.GET_SCAN_CONF";
    public static final String GET_STORED_SCANS = "com.isctechnologies.NanoScan.bluetooth.le.GET_STORED_SCANS";
    public static final String SET_TIME = "com.isctechnologies.NanoScan.bluetooth.le.SET_TIME";
    public static final String START_SCAN = "com.isctechnologies.NanoScan.bluetooth.le.START_SCAN";
    public static final String DELETE_SCAN = "com.isctechnologies.NanoScan.bluetooth.le.DELETE_SCAN";
    public static final String SD_SCAN_SIZE = "com.isctechnologies.NanoScan.bluetooth.le.SD_SCAN_SIZE";
    public static final String SCAN_CONF_SIZE = "com.isctechnologies.NanoScan.bluetooth.le.SCAN_CONF_SIZE";
    public static final String GET_ACTIVE_CONF = "com.isctechnologies.NanoScan.bluetooth.le.GET_ACTIVE_CONF";
    public static final String SET_ACTIVE_CONF = "com.isctechnologies.NanoScan.bluetooth.le.SET_ACTIVE_CONF";
    public static final String SEND_ACTIVE_CONF = "com.isctechnologies.NanoScan.bluetooth.le.SEND_ACTIVE_CONF";
    public static final String UPDATE_THRESHOLD = "com.isctechnologies.NanoScan.bluetooth.le.UPDATE_THRESHOLD";
    public static final String REQUEST_ACTIVE_CONF = "com.isctechnologies.NanoScan.bluetooth.le.REQUEST_ACTIVE_CONF";
    public static final String ACTION_INFO = "com.isctechnologies.NanoScan.bluetooth.le.ACTION_INFO";
    public static final String ACTION_STATUS = "com.isctechnologies.NanoScan.bluetooth.le.ACTION_STATUS";
    public static final String EXTRA_MANUF_NAME = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_MANUF_NAME";
    public static final String EXTRA_MODEL_NUM = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_MODEL_NUM";
    public static final String EXTRA_SERIAL_NUM = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_SERIAL_NUM";
    public static final String EXTRA_HW_REV = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_HW_REV";
    public static final String EXTRA_TIVA_REV = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_TIVA_REV";
    public static final String EXTRA_SPECTRUM_REV = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_SPECTRUM_REV";
    public static final String EXTRA_BATT = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_BATT";
    public static final String EXTRA_TEMP = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_TEMP";
    public static final String EXTRA_HUMID = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_HUMID";
    public static final String EXTRA_DEV_STATUS = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_DEV_STATUS";
    public static final String EXTRA_ERR_STATUS = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_ERR_STATUS";
    public static final String EXTRA_TEMP_THRESH = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_TEMP_THRESH";
    public static final String EXTRA_HUMID_THRESH = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_HUMID_THRESH";
    public static final String ACTION_REQ_CAL_COEFF = "com.isctechnologies.NanoScan.bluetooth.le.ACTION_REQ_CAL_COEFF";
    public static final String ACTION_REQ_CAL_MATRIX = "com.isctechnologies.NanoScan.bluetooth.le.ACTION_REQ_CAL_MATRIX";
    public static final String EXTRA_REF_CAL_COEFF_SIZE = "com.isctechnologies.NanoScan.bluetooth.le.REF_CAL_COEFF_SIZE";
    public static final String EXTRA_REF_CAL_MATRIX_SIZE = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_REF_CAL_MATRIX_SIZE";
    public static final String EXTRA_REF_CAL_COEFF_SIZE_PACKET = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_REF_CAL_COEFF_SIZE_PACKET";
    public static final String EXTRA_REF_CAL_MATRIX_SIZE_PACKET = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_REF_CAL_MATRIX_SIZE_PACKET";
    public static final String SCAN_MODE_ON_OFF = "scan.mode.on.off";
    public static final String LAMP_ON_OFF = "lamp.on.off";
    public static final String ACTION_LAMP = "action.lamp";
    public static final String PGA_SET = "pga.set";
    public static final String ACTION_PGA = "action.pga";
    public static final String REPEAT_SET = "repeat.set";
    public static final String ACTION_REPEAT = "action.repeat";
    public static final String LAMP_TIME = "lamp.time";
    public static final String ACTION_LAMP_TIME = "action.lamp.time";
    public static final String QUICK_SET_VALUE = "action.quick.set.value";
    public static final String ACTION_SAVE_REFERENCE = "action.save.reference";
    public static final String EXTRA_SPEC_COEF_DATA = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_SPEC_COEF_DATA";
    public static final String SPEC_CONF_DATA = "com.isctechnologies.NanoScan.bluetooth.le.SPEC_CONF_DATA";
    public static final String ACTION_ACTIVATE_STATE = "action.activate.state";
    public static final String ACTIVATE_STATE_KEY = "activate.state.key";
    public static final String ACTION_RETURN_ACTIVATE = "action.return.activate";
    public static final String RETURN_ACTIVATE_STATUS = "return.activate.status";
    public static final String ACTION_READ_ACTIVATE_STATE = "action.read.activate.state";
    public static final String ACTION_RETURN_READ_ACTIVATE_STATE = "action.return.read.activate.state";
    public static final String RETURN_READ_ACTIVATE_STATE = "return.read.activate.state";
    public static final String ACTION_READ_CONFIG = "action.read.config";
    public static final String READ_CONFIG_DATA = "read.cureent.config.data"; // read current config in device(quickse or default)
    public static final String RETURN_CURRENT_CONFIG_DATA = "active.return.current.config.data";
    public static final String EXTRA_CURRENT_CONFIG_DATA = "com.extra.current.config.data";
    public static final String ACTION_WRITE_SCAN_CONFIG = "action.write.scan.config";
    public static final String WRITE_SCAN_CONFIG_VALUE = "write.scan.config.value";
    public static final String ACTION_RETURN_WRITE_SCAN_CONFIG_STATUS = "action.return.write.scan.config.status";
    public static final String RETURN_WRITE_SCAN_CONFIG_STATUS = "return.write.scan.config.status";
    public static final String GET_UUID = "com.isctechnologies.NanoScan.bluetooth.le.GET_UUID";
    public static final String SEND_DEVICE_UUID = "com.isctechnologies.NanoScan.bluetooth.le.SEND_DEVICE_UUID";
    public static final String EXTRA_DEVICE_UUID = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_DEVICE_UUID";
    public static final String GET_BATTERY = "com.isctechnologies.NanoScan.bluetooth.le.GET_BATTERY";
    public static final String SEND_BATTERY = "com.isctechnologies.NanoScan.bluetooth.le.SEND_BATTERY";
    public static final String EXTRA_BATTERY = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_BATTERY";

    public NIRScanSDK() {
    }

    public static native int GetMaxPatternJNI(int scan_type, int start_nm, int end_nm, int width_index, int num_repeat, byte SpectrumCalCoefficients[]);

    public static native int dlpSpecScanInterpReference(byte scanData[], byte CalCoefficients[], byte RefCalMatrix[], double wavelength[], int intensity[], int uncalibratedIntensity[]);

    public static native int dlpSpecScanReadConfiguration(byte ConfigData[], int scanType[], int scanConfigIndex[], byte[] scanConfigSerialNumber, byte configName[], byte bufnumSections[], byte sectionScanType[],
                                                          byte sectionWidthPx[], int sectionWavelengthStartNm[], int sectionWavelengthEndNm[], int sectionNumPatterns[], int sectionNumRepeats[], int sectionExposureTime[]);

    public static native int dlpSpecScanReadOneSectionConfiguration(byte ConfigData[], int scanType[], byte ScanType[],
                                                                    byte WidthPx[], int WavelengthStartNm[], int WavelengthEndNm[], int NumPatterns[], int NumRepeats[], int ExposureTime[], int scanConfigIndex[], byte[] scanConfigSerialNumber, byte configName[]);

    public static native int dlpSpecScanWriteConfiguration(int scanType, int scanConfigIndex, int numRepeat, byte[] scanConfigSerialNumber, byte[] configName, byte numSections,
                                                           byte[] sectionScanType, byte[] sectionWidthPx, int[] sectionWavelengthStartNm, int[] sectionWavelengthEndNm, int[] sectionNumPatterns
            , int[] sectionExposureTime, byte[] EXTRA_DATA);

    public native int dlpSpecScanInterpConfigInfo(byte scanData[], int scanType[], byte[] scanConfigSerialNumber, byte configName[], byte bufnumSections[], byte sectionScanType[],
                                                  byte sectionWidthPx[], int sectionWavelengthStartNm[], int sectionWavelengthEndNm[], int sectionNumPatterns[], int sectionNumRepeats[], int sectionExposureTime[], int pga[], int systemp[], int syshumidity[],
                                                  int lampintensity[], double shift_vector_coff[], double pixel_coff[], int day[]);

    public native int dlpSpecScanInterpReferenceInfo(byte scanData[], byte CalCoefficients[], byte RefCalMatrix[], int refsystemp[], int refsyshumidity[],
                                                     int reflampintensity[], int numpattren[], int width[], int numrepeat[], int refday[]);

    public static NIRScanSDK.ScanConfiguration GetScanConfiguration(byte[] EXTRA_DATA) {
        int scanType = 0;
        int scanConfigIndex = 0;
        byte[] scanConfigSerialNumber = new byte[8];
        byte[] configName = new byte[40];
        byte numSections = 0;
        byte[] sectionScanType = new byte[5];
        byte[] sectionWidthPx = new byte[5];
        int[] sectionWavelengthStartNm = new int[5];
        int[] sectionWavelengthEndNm = new int[5];
        int[] sectionNumPatterns = new int[5];
        int[] sectionNumRepeats = new int[5];
        int[] sectionExposureTime = new int[5];
        //-------------------------------------------------
        int[] bufscanType = new int[1];
        int[] bufscanConfigIndex = new int[1];
        byte[] bufnumSections = new byte[1];

        dlpSpecScanReadConfiguration(EXTRA_DATA, bufscanType, bufscanConfigIndex, scanConfigSerialNumber, configName, bufnumSections,
                sectionScanType, sectionWidthPx, sectionWavelengthStartNm, sectionWavelengthEndNm, sectionNumPatterns, sectionNumRepeats, sectionExposureTime);
        scanConfigIndex = bufscanConfigIndex[0];
        scanType = bufscanType[0];
        numSections = bufnumSections[0];
        NIRScanSDK.ScanConfiguration config = new NIRScanSDK.ScanConfiguration(scanType, scanConfigIndex, scanConfigSerialNumber, configName, numSections,
                sectionScanType, sectionWidthPx, sectionWavelengthStartNm, sectionWavelengthEndNm, sectionNumPatterns, sectionNumRepeats, sectionExposureTime);
        return config;
    }

    public static NIRScanSDK.ScanConfiguration GetOneSectionScanConfiguration(byte[] EXTRA_DATA) {
        int scanType = 0;
        byte[] ScanType = new byte[1];
        byte[] WidthPx = new byte[1];
        int[] WavelengthStartNm = new int[1];
        int[] WavelengthEndNm = new int[1];
        int[] NumPatterns = new int[1];
        int[] NumRepeats = new int[1];
        int[] ExposureTime = new int[1];
        int scanConfigIndex = 0;
        int[] bufscanConfigIndex = new int[1];
        byte[] scanConfigSerialNumber = new byte[8];
        byte[] configName = new byte[40];
        //-------------------------------------------------
        int[] bufscanType = new int[1];

        dlpSpecScanReadOneSectionConfiguration(EXTRA_DATA, bufscanType,
                ScanType, WidthPx, WavelengthStartNm, WavelengthEndNm, NumPatterns, NumRepeats, ExposureTime, bufscanConfigIndex, scanConfigSerialNumber, configName);
        scanType = bufscanType[0];
        scanConfigIndex = bufscanConfigIndex[0];
        NIRScanSDK.ScanConfiguration config = new NIRScanSDK.ScanConfiguration(scanType, scanConfigIndex, scanConfigSerialNumber, configName, WavelengthStartNm[0],
                WavelengthEndNm[0], WidthPx[0], NumPatterns[0], NumRepeats[0]);
        return config;
    }

    private static boolean characteristicError() {
        if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharDISHardwareRev == null) {
            Timber.e("Failed to enumerate UUID: %s", NIRScanSDK.NanoGATT.DIS_HW_REV.toString());
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharDISManufName == null) {
            Timber.e("Failed to enumerate UUID: %s", NIRScanSDK.NanoGATT.DIS_MANUF_NAME.toString());
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharDISModelNumber == null) {
            Timber.e("Failed to enumerate UUID: %s", NIRScanSDK.NanoGATT.DIS_MODEL_NUMBER.toString());
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharDISSerialNumber == null) {
            Timber.e("Failed to enumerate UUID: %s", NIRScanSDK.NanoGATT.DIS_SERIAL_NUMBER.toString());
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharDISSpectrumCRev == null) {
            Timber.e("Failed to enumerate UUID: %s", NIRScanSDK.NanoGATT.DIS_SPECC_REV.toString());
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharDISTivaFirmwareRev == null) {
            Timber.e("Failed to enumerate UUID: %s", NIRScanSDK.NanoGATT.DIS_TIVA_FW_REV.toString());
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharBASBattLevel == null) {
            Timber.e("Failed to enumerate UUID: %s", NIRScanSDK.NanoGATT.BAS_BATT_LVL.toString());
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharGGISBatteryRechargeCycles == null) {
            Timber.e("Failed to enumerate UUID: %s", NIRScanSDK.NanoGATT.GGIS_NUM_BATT_RECHARGE.toString());
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharGGISDevStatus == null) {
            Timber.e("Failed to enumerate UUID: %s", NIRScanSDK.NanoGATT.GGIS_DEV_STATUS.toString());
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharGGISErrorStatus == null) {
            Timber.e("Failed to enumerate UUID: %s", NIRScanSDK.NanoGATT.GGIS_ERR_STATUS.toString());
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharGGISHoursOfUse == null) {
            Timber.e("Failed to enumerate UUID: %s", NIRScanSDK.NanoGATT.GGIS_HOURS_OF_USE.toString());
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharGGISHumidMeasurement == null) {
            Timber.e("Failed to enumerate UUID: %s", NIRScanSDK.NanoGATT.GGIS_HUMID_MEASUREMENT.toString());
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharGGISHumidThreshold == null) {
            Timber.e("Failed to enumerate UUID: %s", NIRScanSDK.NanoGATT.GGIS_HUMID_THRESH.toString());
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharGGISLampHours == null) {
            Timber.e("Failed to enumerate UUID: %s", NIRScanSDK.NanoGATT.GGIS_LAMP_HOURS.toString());
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharGGISTempMeasurement == null) {
            Timber.e("Failed to enumerate UUID: %s", NIRScanSDK.NanoGATT.GGIS_TEMP_MEASUREMENT.toString());
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharGGISTempThreshold == null) {
            Timber.e("Failed to enumerate UUID: %s", NIRScanSDK.NanoGATT.GGIS_TEMP_THRESH.toString());
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharGDTSTime == null) {
            Timber.e("Failed to enumerate UUID: %s", NIRScanSDK.NanoGATT.GDTS_TIME.toString());
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharGCISReqRefCalCoefficients == null) {
            Timber.e("Failed to enumerate UUID: %s", NIRScanSDK.NanoGATT.GCIS_REQ_REF_CAL_COEFF.toString());
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharGCISReqRefCalMatrix == null) {
            Timber.e("Failed to enumerate UUID: %s", NIRScanSDK.NanoGATT.GCIS_REQ_REF_CAL_MATRIX.toString());
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharGCISReqSpecCalCoefficients == null) {
            Timber.e("Failed to enumerate UUID: %s", NIRScanSDK.NanoGATT.GCIS_REQ_SPEC_CAL_COEFF.toString());
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharGCISRetRefCalCoefficients == null) {
            Timber.e("Failed to enumerate UUID: %s", NIRScanSDK.NanoGATT.GCIS_RET_REF_CAL_COEFF.toString());
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharGCISRetRefCalMatrix == null) {
            Timber.e("Failed to enumerate UUID: %s", NIRScanSDK.NanoGATT.GCIS_RET_REF_CAL_MATRIX.toString());
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSCISActiveScanConf == null) {
            Timber.e("Failed to enumerate UUID: %s", NIRScanSDK.NanoGATT.GSCIS_ACTIVE_SCAN_CONF.toString());
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSCISNumberStoredConf == null) {
            Timber.e("Failed to enumerate UUID: %s", NIRScanSDK.NanoGATT.GSCIS_NUM_STORED_CONF.toString());
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSCISReqScanConfData == null) {
            Timber.e("Failed to enumerate UUID: %s", NIRScanSDK.NanoGATT.GSCIS_REQ_SCAN_CONF_DATA.toString());
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSCISReqStoredConfList == null) {
            Timber.e("Failed to enumerate UUID: %s", NIRScanSDK.NanoGATT.GSCIS_REQ_STORED_CONF_LIST.toString());
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSCISRetScanConfData == null) {
            Timber.e("Failed to enumerate UUID: %s", NIRScanSDK.NanoGATT.GSCIS_RET_SCAN_CONF_DATA.toString());
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSCISRetStoredConfList == null) {
            Timber.e("Failed to enumerate UUID: %s", NIRScanSDK.NanoGATT.GSCIS_RET_STORED_CONF_LIST.toString());
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISNumberSDStoredScans == null) {
            Timber.e("Failed to enumerate UUID: %s", NIRScanSDK.NanoGATT.GSDIS_NUM_SD_STORED_SCANS.toString());
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISSDStoredScanIndicesList == null) {
            Timber.e("Failed to enumerate UUID: %s", NIRScanSDK.NanoGATT.GSDIS_SD_STORED_SCAN_IND_LIST.toString());
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISSDStoredScanIndicesListData == null) {
            Timber.e("Failed to enumerate UUID: %s", NIRScanSDK.NanoGATT.GSDIS_SD_STORED_SCAN_IND_LIST_DATA.toString());
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISSetScanNameStub == null) {
            Timber.e("Failed to enumerate UUID: %s", NIRScanSDK.NanoGATT.GSDIS_SET_SCAN_NAME_STUB.toString());
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISStartScanWrite == null) {
            Timber.e("Failed to enumerate UUID: %s %s", NIRScanSDK.NanoGATT.GSDIS_START_SCAN.toString(), "(Write)");
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISStartScanNotify == null) {
            Timber.e("Failed to enumerate UUID: %s %s", NIRScanSDK.NanoGATT.GSDIS_START_SCAN.toString(), "(Notify)");
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISClearScanWrite == null) {
            Timber.e("Failed to enumerate UUID: %s %s", NIRScanSDK.NanoGATT.GSDIS_CLEAR_SCAN.toString(), "(Write)");
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISClearScanNotify == null) {
            Timber.e("Failed to enumerate UUID: %s %s", NIRScanSDK.NanoGATT.GSDIS_CLEAR_SCAN.toString(), "(Notify)");
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISReqScanName == null) {
            Timber.e("Failed to enumerate UUID: %s", NIRScanSDK.NanoGATT.GSDIS_REQ_SCAN_NAME.toString());
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISRetScanName == null) {
            Timber.e("Failed to enumerate UUID: %s", NIRScanSDK.NanoGATT.GSDIS_RET_SCAN_NAME.toString());
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISReqScanType == null) {
            Timber.e("Failed to enumerate UUID: %s", NIRScanSDK.NanoGATT.GSDIS_REQ_SCAN_TYPE.toString());
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISRetScanType == null) {
            Timber.e("Failed to enumerate UUID: %s", NIRScanSDK.NanoGATT.GSDIS_RET_SCAN_TYPE.toString());
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISReqScanDate == null) {
            Timber.e("Failed to enumerate UUID: %s", NIRScanSDK.NanoGATT.GSDIS_REQ_SCAN_DATE.toString());
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISRetScanDate == null) {
            Timber.e("Failed to enumerate UUID: %s", NIRScanSDK.NanoGATT.GSDIS_RET_SCAN_DATE.toString());
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISReqPacketFormatVersion == null) {
            Timber.e("Failed to enumerate UUID: %s", NIRScanSDK.NanoGATT.GSDIS_REQ_PKT_FMT_VER.toString());
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISRetPacketFormatVersion == null) {
            Timber.e("Failed to enumerate UUID: %s", NIRScanSDK.NanoGATT.GSDIS_RET_PKT_FMT_VER.toString());
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISReqSerialScanDataStruct == null) {
            Timber.e("Failed to enumerate UUID: %s", NIRScanSDK.NanoGATT.GSDIS_REQ_SER_SCAN_DATA_STRUCT.toString());
            return true;
        } else if (NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISRetSerialScanDataStruct == null) {
            Timber.e("Failed to enumerate UUID: %s", NIRScanSDK.NanoGATT.GSDIS_RET_SER_SCAN_DATA_STRUCT.toString());
            return true;
        } else if (NanoGattCharacteristic.mBleGattCharacteristicCharacteristicActivateState == null) {
            Timber.e("Failed to enumerate UUID: %s", NanoGATT.GSDIS_ACTIVATE_STATE.toString());
            return true;
        } else if (NanoGattCharacteristic.mBleGattCharacteristicCharacteristicActivateStateNotify == null) {
            Timber.e("Failed to enumerate UUID: %s", NanoGATT.GSDIS_RETURN_ACTIVATE_STATE.toString());
            return true;
        } else if (NanoGattCharacteristic.mBleGattCharacteristicReadCurrentScanConfigurationData == null) {
            Timber.e("Failed to enumerate UUID: %s", NanoGATT.GSDIS_READ_CURRENT_SCANCONFIG_DATA.toString());
            return true;
        } else if (NanoGattCharacteristic.mBleGattCharacteristicReturnCurrentScanConfigurationData == null) {
            Timber.e("Failed to enumerate UUID: %s", NanoGATT.GSDIS_RETURN_CURRENT_SCANCONFIG_DATA.toString());
            return true;
        } else if (NanoGattCharacteristic.mBleGattCharacteristicWriteScanConfigurationData == null) {
            Timber.e("Failed to enumerate UUID: %s", NanoGATT.GSDIS_WRITE_SCANCONFIG_DATA.toString());
            return true;
        } else if (NanoGattCharacteristic.mBleGattCharacteristicReturnWriteScanConfigurationData == null) {
            Timber.e("Failed to enumerate UUID: %s", NanoGATT.GSDIS_RETURN_WRITE_SCANCONFIG_DATA.toString());
            return true;
        } else if (NanoGattCharacteristic.mBleGattCharUUID == null) {
            Timber.e("Failed to enumerate UUID: %s", NanoGATT.DEVICE_UUID.toString());
            return true;
        } else {
            return false;
        }
    }

    public static void initialize() {
    }

    public static boolean enumerateServices(BluetoothGatt gatt) {
        List<BluetoothGattService> gattServices = mBluetoothGatt.getServices();
        Iterator var2 = gattServices.iterator();

        while (var2.hasNext()) {
            BluetoothGattService gattService = (BluetoothGattService) var2.next();
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            Iterator var5 = gattCharacteristics.iterator();

            while (var5.hasNext()) {
                BluetoothGattCharacteristic gattCharacteristic = (BluetoothGattCharacteristic) var5.next();
                if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.DIS_MANUF_NAME) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharDISManufName = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.DIS_MODEL_NUMBER) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharDISModelNumber = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.DIS_SERIAL_NUMBER) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharDISSerialNumber = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.DIS_HW_REV) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharDISHardwareRev = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.DIS_TIVA_FW_REV) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharDISTivaFirmwareRev = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.DIS_SPECC_REV) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharDISSpectrumCRev = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.BAS_BATT_LVL) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharBASBattLevel = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.GGIS_TEMP_MEASUREMENT) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharGGISTempMeasurement = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.GGIS_HUMID_MEASUREMENT) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharGGISHumidMeasurement = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.GGIS_DEV_STATUS) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharGGISDevStatus = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.GGIS_ERR_STATUS) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharGGISErrorStatus = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.GGIS_TEMP_THRESH) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharGGISTempThreshold = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.GGIS_HUMID_THRESH) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharGGISHumidThreshold = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.GGIS_HOURS_OF_USE) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharGGISHoursOfUse = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.GGIS_NUM_BATT_RECHARGE) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharGGISBatteryRechargeCycles = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.GGIS_LAMP_HOURS) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharGGISLampHours = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.GGIS_ERR_LOG) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharGGISErrorLog = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.GDTS_TIME) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharGDTSTime = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.GCIS_REQ_SPEC_CAL_COEFF) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharGCISReqSpecCalCoefficients = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NanoGATT.GCIS_RET_SPEC_CAL_COEFF) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharGCISRetSpecCalCoefficients = gattCharacteristic;
                    gatt.setCharacteristicNotification(NIRScanSDK.NanoGattCharacteristic.mBleGattCharGCISRetSpecCalCoefficients, true);
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.GCIS_REQ_REF_CAL_COEFF) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharGCISReqRefCalCoefficients = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.GCIS_RET_REF_CAL_COEFF) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharGCISRetRefCalCoefficients = gattCharacteristic;
                    gatt.setCharacteristicNotification(NIRScanSDK.NanoGattCharacteristic.mBleGattCharGCISRetRefCalCoefficients, true);
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.GCIS_REQ_REF_CAL_MATRIX) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharGCISReqRefCalMatrix = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.GCIS_RET_REF_CAL_MATRIX) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharGCISRetRefCalMatrix = gattCharacteristic;
                    gatt.setCharacteristicNotification(NIRScanSDK.NanoGattCharacteristic.mBleGattCharGCISRetRefCalMatrix, true);
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.GSCIS_NUM_STORED_CONF) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSCISNumberStoredConf = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.GSCIS_REQ_STORED_CONF_LIST) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSCISReqStoredConfList = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.GSCIS_RET_STORED_CONF_LIST) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSCISRetStoredConfList = gattCharacteristic;
                    gatt.setCharacteristicNotification(NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSCISRetStoredConfList, true);
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.GSCIS_REQ_SCAN_CONF_DATA) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSCISReqScanConfData = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.GSCIS_RET_SCAN_CONF_DATA) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSCISRetScanConfData = gattCharacteristic;
                    gatt.setCharacteristicNotification(NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSCISRetScanConfData, true);
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.GSCIS_ACTIVE_SCAN_CONF) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSCISActiveScanConf = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.GSDIS_NUM_SD_STORED_SCANS) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISNumberSDStoredScans = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.GSDIS_SD_STORED_SCAN_IND_LIST) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISSDStoredScanIndicesList = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.GSDIS_SD_STORED_SCAN_IND_LIST_DATA) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISSDStoredScanIndicesListData = gattCharacteristic;
                    gatt.setCharacteristicNotification(NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISSDStoredScanIndicesListData, true);
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.GSDIS_SET_SCAN_NAME_STUB) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISSetScanNameStub = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.GSDIS_START_SCAN) == 0) {
                    if (gattCharacteristic.getProperties() == 8) {
                        NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISStartScanWrite = gattCharacteristic;
                    } else if (gattCharacteristic.getProperties() == 16) {
                        NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISStartScanNotify = gattCharacteristic;
                        gatt.setCharacteristicNotification(NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISStartScanNotify, true);
                    }
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.GSDIS_CLEAR_SCAN) == 0) {
                    if (gattCharacteristic.getProperties() == 8) {
                        NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISClearScanWrite = gattCharacteristic;
                    } else if (gattCharacteristic.getProperties() == 16) {
                        NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISClearScanNotify = gattCharacteristic;
                        // gatt.setCharacteristicNotification(NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISClearScanNotify, true);
                    }
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.GSDIS_REQ_SCAN_NAME) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISReqScanName = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.GSDIS_RET_SCAN_NAME) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISRetScanName = gattCharacteristic;
                    gatt.setCharacteristicNotification(NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISRetScanName, true);
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.GSDIS_REQ_SCAN_TYPE) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISReqScanType = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.GSDIS_RET_SCAN_TYPE) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISRetScanType = gattCharacteristic;
                    gatt.setCharacteristicNotification(NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISRetScanType, true);
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.GSDIS_REQ_SCAN_DATE) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISReqScanDate = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.GSDIS_RET_SCAN_DATE) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISRetScanDate = gattCharacteristic;
                    gatt.setCharacteristicNotification(NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISRetScanDate, true);
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.GSDIS_REQ_PKT_FMT_VER) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISReqPacketFormatVersion = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.GSDIS_RET_PKT_FMT_VER) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISRetPacketFormatVersion = gattCharacteristic;
                    gatt.setCharacteristicNotification(NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISRetPacketFormatVersion, true);
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.GSDIS_REQ_SER_SCAN_DATA_STRUCT) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISReqSerialScanDataStruct = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NIRScanSDK.NanoGATT.GSDIS_RET_SER_SCAN_DATA_STRUCT) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISRetSerialScanDataStruct = gattCharacteristic;
                    gatt.setCharacteristicNotification(NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISRetSerialScanDataStruct, true);
                } else if (gattCharacteristic.getUuid().compareTo(NanoGATT.GSDIS_ACTIVATE_STATE) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharacteristicCharacteristicActivateState = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NanoGATT.GSDIS_RETURN_ACTIVATE_STATE) == 0) {
                    NanoGattCharacteristic.mBleGattCharacteristicCharacteristicActivateStateNotify = gattCharacteristic;
                    gatt.setCharacteristicNotification(NIRScanSDK.NanoGattCharacteristic.mBleGattCharacteristicCharacteristicActivateStateNotify, true);
                } else if (gattCharacteristic.getUuid().compareTo(NanoGATT.GSDIS_READ_CURRENT_SCANCONFIG_DATA) == 0) {
                    NanoGattCharacteristic.mBleGattCharacteristicReadCurrentScanConfigurationData = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NanoGATT.GSDIS_RETURN_CURRENT_SCANCONFIG_DATA) == 0) {
                    NanoGattCharacteristic.mBleGattCharacteristicReturnCurrentScanConfigurationData = gattCharacteristic;
                    gatt.setCharacteristicNotification(NIRScanSDK.NanoGattCharacteristic.mBleGattCharacteristicReturnCurrentScanConfigurationData, true);
                } else if (gattCharacteristic.getUuid().compareTo(NanoGATT.GSDIS_WRITE_SCANCONFIG_DATA) == 0) {
                    NanoGattCharacteristic.mBleGattCharacteristicWriteScanConfigurationData = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NanoGATT.GSDIS_RETURN_WRITE_SCANCONFIG_DATA) == 0) {
                    NanoGattCharacteristic.mBleGattCharacteristicReturnWriteScanConfigurationData = gattCharacteristic;
                    gatt.setCharacteristicNotification(NIRScanSDK.NanoGattCharacteristic.mBleGattCharacteristicReturnWriteScanConfigurationData, true);
                } else if (gattCharacteristic.getUuid().compareTo(NanoGATT.DEVICE_UUID) == 0) {
                    NanoGattCharacteristic.mBleGattCharUUID = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NanoGATT.GSDIS_LAMP_MODE) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharacteristicLampMode = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NanoGATT.GSDIS_LAMP_DELAY_TIME) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharacteristicLampDelayTime = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NanoGATT.GSDIS_SET_PGA) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharacteristicSetPGA = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NanoGATT.GSDIS_SET_SCAN_AVERAGE) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharacteristicSetScanAverage = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NanoGATT.GSDIS_SAVE_REFERENCE) == 0) {
                    NIRScanSDK.NanoGattCharacteristic.mBleGattCharacteristicSaveReference = gattCharacteristic;
                }
            }
        }

        boolean error = characteristicError();
        return !error;
    }

    public static void setStub(byte[] data) {
        writeCharacteristic(NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISSetScanNameStub, data);
    }

    public static void startScan(byte[] saveToSD) {
        writeCharacteristic(NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISStartScanWrite, saveToSD);
    }

    public static void setTime() {
        Calendar currentTime = Calendar.getInstance();
        byte[] dateBytes = new byte[]{(byte) (currentTime.get(1) - 2000), (byte) (currentTime.get(2) + 1), (byte) currentTime.get(5), (byte) (currentTime.get(7) - 1), (byte) currentTime.get(11), (byte) currentTime.get(12), (byte) currentTime.get(13)};
        writeCharacteristic(NIRScanSDK.NanoGattCharacteristic.mBleGattCharGDTSTime, dateBytes);
    }

    public static void setTemperatureThreshold(byte[] threshold) {
        writeCharacteristic(NIRScanSDK.NanoGattCharacteristic.mBleGattCharGGISTempThreshold, threshold);
    }

    public static void setHumidityThreshold(byte[] threshold) {
        writeCharacteristic(NIRScanSDK.NanoGattCharacteristic.mBleGattCharGGISHumidThreshold, threshold);
    }

    public static void setActiveConf(byte[] index) {
        writeCharacteristic(NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSCISActiveScanConf, index);
    }

    public static void getActiveConf() {
        readCharacteristic(NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSCISActiveScanConf);
    }

    public static void deleteScan(byte[] index) {
        writeCharacteristic(NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISClearScanWrite, index);
    }

    public static void getModelNumber() {
        readCharacteristic(NIRScanSDK.NanoGattCharacteristic.mBleGattCharDISModelNumber);
    }

    public static void getSerialNumber() {
        readCharacteristic(NIRScanSDK.NanoGattCharacteristic.mBleGattCharDISSerialNumber);
    }

    public static void getHardwareRev() {
        readCharacteristic(NIRScanSDK.NanoGattCharacteristic.mBleGattCharDISHardwareRev);
    }

    public static void getFirmwareRev() {
        readCharacteristic(NIRScanSDK.NanoGattCharacteristic.mBleGattCharDISTivaFirmwareRev);
    }

    public static void getSpectrumCRev() {
        readCharacteristic(NIRScanSDK.NanoGattCharacteristic.mBleGattCharDISSpectrumCRev);
    }

    public static void getTemp() {
        readCharacteristic(NIRScanSDK.NanoGattCharacteristic.mBleGattCharGGISTempMeasurement);
    }

    public static void getHumidity() {
        readCharacteristic(NIRScanSDK.NanoGattCharacteristic.mBleGattCharGGISHumidMeasurement);
    }

    public static void getManufacturerName() {
        readCharacteristic(NIRScanSDK.NanoGattCharacteristic.mBleGattCharDISManufName);
    }

    public static void getUUID() {
        readCharacteristic(NIRScanSDK.NanoGattCharacteristic.mBleGattCharUUID);
    }

    public static void getBatteryLevel() {
        readCharacteristic(NIRScanSDK.NanoGattCharacteristic.mBleGattCharBASBattLevel);
    }

    public static void getNumberStoredConfigurations() {
        readCharacteristic(NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSCISNumberStoredConf);
    }

    public static void getNumberStoredScans() {
        readCharacteristic(NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISNumberSDStoredScans);
    }

    public static void getDeviceStatus() {
        readCharacteristic(NIRScanSDK.NanoGattCharacteristic.mBleGattCharGGISDevStatus);
    }

    public static void getErrorStatus() {
        readCharacteristic(NIRScanSDK.NanoGattCharacteristic.mBleGattCharGGISErrorStatus);
    }

    public static void requestStoredConfigurationList() {
        byte[] writeData = new byte[]{0};
        writeCharacteristic(NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSCISReqStoredConfList, writeData);
    }

    public static void requestScanName(byte[] index) {
        writeCharacteristic(NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISReqScanName, index);
    }

    public static void requestScanConfiguration(byte[] index) {
        writeCharacteristic(NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSCISReqScanConfData, index);
    }

    public static void requestRefCalCoefficients() {
        byte[] data = new byte[]{0};
        writeCharacteristic(NIRScanSDK.NanoGattCharacteristic.mBleGattCharGCISReqRefCalCoefficients, data);
    }

    public static void requestRefCalMatrix() {
        byte[] data = new byte[]{0};
        writeCharacteristic(NIRScanSDK.NanoGattCharacteristic.mBleGattCharGCISReqRefCalMatrix, data);
    }

    public static void requestSpectrumCalCoefficients() {
        byte[] data = new byte[]{0};
        writeCharacteristic(NIRScanSDK.NanoGattCharacteristic.mBleGattCharGCISReqSpecCalCoefficients, data);
    }

    public static void requestScanIndicesList() {
        byte[] writeData = new byte[]{0};
        writeCharacteristic(NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISSDStoredScanIndicesList, writeData);
    }

    public static void requestScanDate(byte[] scanIndex) {
        writeCharacteristic(NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISReqScanDate, scanIndex);
    }

    public static void requestScanType(byte[] scanIndex) {
        writeCharacteristic(NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISReqScanType, scanIndex);
    }

    public static void requestPacketFormatVersion(byte[] scanIndex) {
        writeCharacteristic(NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISReqPacketFormatVersion, scanIndex);
    }

    public static void requestSerializedScanDataStruct(byte[] scanIndex) {
        writeCharacteristic(NIRScanSDK.NanoGattCharacteristic.mBleGattCharGSDISReqSerialScanDataStruct, scanIndex);
    }

    public static void setPGA(byte[] index) {
        try {
            Thread.sleep(100);
        } catch (Exception e) {

        }
        ;
        writeCharacteristic(NanoGattCharacteristic.mBleGattCharacteristicSetPGA, index);
        System.out.println("__BT_SERVICE setPGA");
    }

    public static void setLampTime(byte[] index) {

        writeCharacteristic(NanoGattCharacteristic.mBleGattCharacteristicLampDelayTime, index);
        System.out.println("__BT_SERVICE setLampTime");
    }

    public static void setLampMode(byte[] index) {

        try {
            Thread.sleep(200);
        } catch (Exception e) {

        }
        ;
        writeCharacteristic(NanoGattCharacteristic.mBleGattCharacteristicLampMode, index);
        System.out.println("__BT_SERVICE setLampMode");
    }

    public static void setScanAverage(byte[] index) {

        try {
            Thread.sleep(100);
        } catch (Exception e) {

        }
        ;
        writeCharacteristic(NanoGattCharacteristic.mBleGattCharacteristicSetScanAverage, index);
        System.out.println("__BT_SERVICE setScanAverage");
    }

    public static void SaveReference(byte[] index) {

        writeCharacteristic(NanoGattCharacteristic.mBleGattCharacteristicSaveReference, index);
        System.out.println("__BT_SERVICE SaveReference");
    }


    public static void writeScanConfig(byte[] index) {

        try {
            Thread.sleep(200);
        } catch (Exception e) {

        }
        writeCharacteristic(NanoGattCharacteristic.mBleGattCharacteristicWriteScanConfigurationData, index);
        System.out.println("__BT_SERVICE writeScanConfig");
    }

    public static void SetActiveStateKey(byte[] index) {

        writeCharacteristic(NanoGattCharacteristic.mBleGattCharacteristicCharacteristicActivateState, index);
        System.out.println("__BT_SERVICE: Set ActivateState key");
        if (index.length == 12) {
            Boolean unactivate = true;
            for (int i = 0; i < 12; i++) {
                if (index[i] != 0) {
                    unactivate = false;
                }
            }
            if (unactivate) {
                try {
                    Thread.sleep(200);
                } catch (Exception e) {

                }
                ReadActiveState();
            }
        }
    }

    public static void ReadCurrentConfig(byte[] index) {

        writeCharacteristic(NanoGattCharacteristic.mBleGattCharacteristicReadCurrentScanConfigurationData, index);
        System.out.println("__BT_SERVICE: ReadCurrentConfig");
    }

    public static void ReadScanConfigDataStatus() {
        readCharacteristic(NanoGattCharacteristic.mBleGattCharacteristicReturnWriteScanConfigurationData);
        System.out.println("__BT_SERVICE: ReadScanConfigDataStatus");
    }

    public static void ReadActiveState() {
        readCharacteristic(NanoGattCharacteristic.mBleGattCharacteristicCharacteristicActivateState);
        System.out.println("__BT_SERVICE: ReadActiveState");
    }

    public static void writeCharacteristic(BluetoothGattCharacteristic characteristic, byte[] data) {
        if (characteristic == null) {
            Timber.e("Error writing NULL characteristic");
        } else {
            characteristic.setValue(data);
            if (mBluetoothAdapter != null && mBluetoothGatt != null) {
                mBluetoothGatt.writeCharacteristic(characteristic);
            } else {
                Timber.e("ERROR: mBluetoothAdapter is null");
            }
        }
    }

    public static void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter != null && mBluetoothGatt != null) {
            if (characteristic != null) {
                mBluetoothGatt.readCharacteristic(characteristic);
            } else {
                Timber.e("ERROR: Reading NULL characteristic");
            }

        }
    }

    static {
        System.loadLibrary("native-lib");
    }

    static {
        System.loadLibrary("dlpspectrum");
    }

    public static class SlewScanSection implements Serializable {
        byte sectionScanType;
        byte widthPx;
        int wavelengthStartNm;
        int wavelengthEndNm;
        int numPatterns;
        int numRepeats;
        int exposureTime;

        public SlewScanSection(byte sectionScanType, byte widthPx, int wavelengthStartNm, int wavelengthEndNm, int numPatterns, int numRepeats, int exposureTime) {
            this.sectionScanType = sectionScanType;
            this.widthPx = widthPx;
            this.wavelengthStartNm = wavelengthStartNm;
            this.wavelengthEndNm = wavelengthEndNm;
            this.numPatterns = numPatterns;
            this.numRepeats = numRepeats;
            this.exposureTime = exposureTime;
        }

        public int getExposureTime() {
            return this.exposureTime;
        }

        public void setExposureTime(int exposureTime) {
            this.exposureTime = exposureTime;
        }

        public int getNumPatterns() {
            return this.numPatterns;
        }

        public void setNumPatterns(int numPatterns) {
            this.numPatterns = numPatterns;
        }

        public int getNumRepeats() {
            return this.numRepeats;
        }

        public void setNumRepeats(int numRepeats) {
            this.numRepeats = numRepeats;
        }

        public byte getSectionScanType() {
            return this.sectionScanType;
        }

        public void setSectionScanType(byte sectionScanType) {
            this.sectionScanType = sectionScanType;
        }

        public int getWavelengthEndNm() {
            return this.wavelengthEndNm;
        }

        public void setWavelengthEndNm(int wavelengthEndNm) {
            this.wavelengthEndNm = wavelengthEndNm;
        }

        public int getWavelengthStartNm() {
            return this.wavelengthStartNm;
        }

        public void setWavelengthStartNm(int wavelengthStartNm) {
            this.wavelengthStartNm = wavelengthStartNm;
        }

        public byte getWidthPx() {
            return this.widthPx;
        }

        public void setWidthPx(byte widthPx) {
            this.widthPx = widthPx;
        }
    }

    public static class ScanListManager {
        private String infoTitle;
        private String infoBody;

        public ScanListManager(String infoTitle, String infoBody) {
            this.infoTitle = infoTitle;
            this.infoBody = infoBody;
        }

        public String getInfoTitle() {
            return this.infoTitle;
        }

        public String getInfoBody() {
            return this.infoBody;
        }
    }

    public static class ScanConfiguration implements Serializable {
        private static final int SCAN_CFG_FILENAME_LEN = 8;
        int scanType;
        int scanConfigIndex;
        byte[] scanConfigSerialNumber;
        byte[] configName;
        int wavelengthStartNm;
        int wavelengthEndNm;
        int widthPx;
        int numPatterns;
        int numRepeats;
        boolean active;
        byte[] sectionScanType;
        byte[] sectionWidthPx;
        int[] sectionWavelengthStartNm;
        int[] sectionWavelengthEndNm;
        int[] sectionNumPatterns;
        int[] sectionNumRepeats;
        int[] sectionExposureTime;
        byte numSections;

        public ScanConfiguration(int scanType, int scanConfigIndex, byte[] scanConfigSerialNumber, byte[] configName, int wavelengthStartNm, int wavelengthEndNm, int widthPx, int numPatterns, int numRepeats) {
            this.scanType = scanType;
            this.scanConfigIndex = scanConfigIndex;
            this.scanConfigSerialNumber = scanConfigSerialNumber;
            this.configName = configName;
            this.wavelengthStartNm = wavelengthStartNm;
            this.wavelengthEndNm = wavelengthEndNm;
            this.widthPx = widthPx;
            this.numPatterns = numPatterns;
            this.numRepeats = numRepeats;
        }

        public ScanConfiguration(int scanType, int scanConfigIndex, byte[] scanConfigSerialNumber, byte[] configName, byte numSections, byte[] sectionScanType, byte[] sectionWidthPx, int[] sectionWavelengthStartNm, int[] sectionWavelengthEndNm, int[] sectionNumPatterns, int[] sectionNumRepeats, int[] sectionExposureTime) {
            this.scanType = scanType;
            this.scanConfigIndex = scanConfigIndex;
            this.scanConfigSerialNumber = scanConfigSerialNumber;
            this.configName = configName;
            this.sectionScanType = sectionScanType;
            this.sectionWidthPx = sectionWidthPx;
            this.sectionWavelengthStartNm = sectionWavelengthStartNm;
            this.sectionWavelengthEndNm = sectionWavelengthEndNm;
            this.sectionNumPatterns = sectionNumPatterns;
            this.sectionNumRepeats = sectionNumRepeats;
            this.sectionExposureTime = sectionExposureTime;
            this.numSections = numSections;
        }

        public String getScanType() {
            return this.scanType == 1 ? "Hadamard" : (this.scanType == 2 ? "Slew" : "Column");
        }

        public boolean isActive() {
            return this.active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public void setScanType(int scanType) {
            this.scanType = scanType;
        }

        public int getScanConfigIndex() {
            return this.scanConfigIndex;
        }

        public void setScanConfigIndex(int scanConfigIndex) {
            this.scanConfigIndex = scanConfigIndex;
        }

        public String getScanConfigSerialNumber() {
            String s = null;

            try {
                s = new String(this.scanConfigSerialNumber, "UTF-8");
            } catch (UnsupportedEncodingException var3) {
                var3.printStackTrace();
            }

            return s;
        }

        public void setScanConfigSerialNumber(byte[] scanConfigSerialNumber) {
            this.scanConfigSerialNumber = scanConfigSerialNumber;
        }

        public String getConfigName() {
            byte[] byteChars = new byte[40];
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] var3 = byteChars;
            int i = byteChars.length;

            for (int var5 = 0; var5 < i; ++var5) {
                byte b = var3[var5];
                byteChars[b] = 0;
            }

            String s = null;

            for (i = 0; i < this.configName.length; ++i) {
                byteChars[i] = this.configName[i];
                if (this.configName[i] == 0) {
                    break;
                }

                os.write(this.configName[i]);
            }

            try {
                s = new String(os.toByteArray(), "UTF-8");
            } catch (UnsupportedEncodingException var7) {
                var7.printStackTrace();
            }

            return s;
        }

        public void setConfigName(byte[] configName) {
            this.configName = configName;
        }

        public int getWavelengthStartNm() {
            return this.wavelengthStartNm;
        }

        public void setWavelengthStartNm(int wavelengthStartNm) {
            this.wavelengthStartNm = wavelengthStartNm;
        }

        public int getWavelengthEndNm() {
            return this.wavelengthEndNm;
        }

        public void setWavelengthEndNm(int wavelengthEndNm) {
            this.wavelengthEndNm = wavelengthEndNm;
        }

        public int getWidthPx() {
            return this.widthPx;
        }

        public void setWidthPx(int widthPx) {
            this.widthPx = widthPx;
        }

        public int getNumPatterns() {
            return this.numPatterns;
        }

        public void setNumPatterns(int numPatterns) {
            this.numPatterns = numPatterns;
        }

        public int getNumRepeats() {
            return this.numRepeats;
        }

        public void setNumRepeats(int numRepeats) {
            this.numRepeats = numRepeats;
        }

        public int[] getSectionExposureTime() {
            return this.sectionExposureTime;
        }

        public void setSectionExposureTime(int[] sectionExposureTime) {
            this.sectionExposureTime = sectionExposureTime;
        }

        public int[] getSectionNumPatterns() {
            return this.sectionNumPatterns;
        }

        public void setSectionNumPatterns(int[] sectionNumPatterns) {
            this.sectionNumPatterns = sectionNumPatterns;
        }

        public int[] getSectionNumRepeats() {
            return this.sectionNumRepeats;
        }

        public void setSectionNumRepeats(int[] sectionNumRepeats) {
            this.sectionNumRepeats = sectionNumRepeats;
        }

        public byte[] getSectionScanType() {
            return this.sectionScanType;
        }

        public void setSectionScanType(byte[] sectionScanType) {
            this.sectionScanType = sectionScanType;
        }

        public int[] getSectionWavelengthEndNm() {
            return this.sectionWavelengthEndNm;
        }

        public void setSectionWavelengthEndNm(int[] sectionWavelengthEndNm) {
            this.sectionWavelengthEndNm = sectionWavelengthEndNm;
        }

        public int[] getSectionWavelengthStartNm() {
            return this.sectionWavelengthStartNm;
        }

        public void setSectionWavelengthStartNm(int[] sectionWavelengthStartNm) {
            this.sectionWavelengthStartNm = sectionWavelengthStartNm;
        }

        public byte[] getSectionWidthPx() {
            return this.sectionWidthPx;
        }

        public void setSectionWidthPx(byte[] sectionWidthPx) {
            this.sectionWidthPx = sectionWidthPx;
        }

        public byte getSlewNumSections() {
            return this.numSections;
        }

        public void setSlewNumSections(byte numSections) {
            this.numSections = numSections;
        }

    }

    public static class ScanResults {
        double[] wavelength;
        int[] intensity;
        int[] uncalibratedIntensity;
        int length;

        public double[] getWavelength() {
            return this.wavelength;
        }

        public void setWavelength(double[] wavelength) {
            this.wavelength = wavelength;
        }

        public int[] getIntensity() {
            return this.intensity;
        }

        public void setIntensity(int[] intensity) {
            this.intensity = intensity;
        }

        public int getLength() {
            return this.length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public int[] getUncalibratedIntensity() {
            return this.uncalibratedIntensity;
        }

        public void setUncalibratedIntensity(int[] uncalibratedIntensity) {
            this.uncalibratedIntensity = uncalibratedIntensity;
        }

        public ScanResults(double[] wavelength, int[] intensity, int[] uncalibratedIntensity, int length) {
            this.wavelength = wavelength;
            this.intensity = intensity;
            this.uncalibratedIntensity = uncalibratedIntensity;
            this.length = length;
        }
    }

    public static class ReferenceCalibration implements Serializable {
        public static final String REF_FILENAME = "refcals";
        public static ArrayList<ReferenceCalibration> currentCalibration;
        private byte[] refCalCoefficients;
        private byte[] refCalMatrix;

        public ReferenceCalibration(byte[] refCalCoefficients, byte[] refCalMatrix) {
            this.refCalCoefficients = refCalCoefficients;
            this.refCalMatrix = refCalMatrix;
        }

        public static boolean refCalFileExists(Context context) {
            ObjectInputStream in;
            try {
                FileInputStream fis = context.openFileInput("refcals");
                in = new ObjectInputStream(fis);
            } catch (IOException var5) {
                var5.printStackTrace();
                return false;
            }

            try {
                in.close();
            } catch (IOException var4) {
                var4.printStackTrace();
            }

            return true;
        }

        public byte[] getRefCalCoefficients() {
            return this.refCalCoefficients;
        }

        public byte[] getRefCalMatrix() {
            return this.refCalMatrix;
        }

        public static boolean writeRefCalFile(Context context, ArrayList<ReferenceCalibration> list) {
            currentCalibration = list;
            ObjectOutputStream out = null;

            try {
                FileOutputStream fos = context.openFileOutput("refcals", 0);
                out = new ObjectOutputStream(fos);
                Iterator var4 = list.iterator();

                while (var4.hasNext()) {
                    NIRScanSDK.ReferenceCalibration tempObject = (NIRScanSDK.ReferenceCalibration) var4.next();
                    out.writeObject(tempObject);
                }

                out.close();
                return true;
            } catch (IOException var7) {
                var7.printStackTrace();
                Timber.e(var7, "__REFS %s", "IO exception when writing groups file:");

                try {
                    assert out != null;

                    out.close();
                } catch (IOException var6) {
                    var6.printStackTrace();
                    Timber.e(var6, "__REFS %s", "IO exception when closing groups file:");
                }

                return false;
            }
        }
    }

    public static class NanoDevice {
        private BluetoothDevice device;
        private int rssi;
        byte[] scanRecord;
        private String nanoName;
        private String nanoMac;

        public NanoDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
            this.device = device;
            this.rssi = rssi;
            this.scanRecord = scanRecord;
            this.nanoName = device.getName();
            this.nanoMac = device.getAddress();
        }

        public void setRssi(int rssi) {
            this.rssi = rssi;
        }

        public String getRssiString() {
            return String.valueOf(this.rssi);
        }

        public String getNanoName() {
            return this.nanoName;
        }

        public String getNanoMac() {
            return this.nanoMac;
        }
    }

    public static class NanoGattCharacteristic {

        public static BluetoothGattCharacteristic mBleGattCharDISManufName;
        public static BluetoothGattCharacteristic mBleGattCharDISModelNumber;
        public static BluetoothGattCharacteristic mBleGattCharDISSerialNumber;
        public static BluetoothGattCharacteristic mBleGattCharDISHardwareRev;
        public static BluetoothGattCharacteristic mBleGattCharDISTivaFirmwareRev;
        public static BluetoothGattCharacteristic mBleGattCharDISSpectrumCRev;
        public static BluetoothGattCharacteristic mBleGattCharBASBattLevel;
        public static BluetoothGattCharacteristic mBleGattCharGGISTempMeasurement;
        public static BluetoothGattCharacteristic mBleGattCharGGISHumidMeasurement;
        public static BluetoothGattCharacteristic mBleGattCharGGISDevStatus;
        public static BluetoothGattCharacteristic mBleGattCharGGISErrorStatus;
        public static BluetoothGattCharacteristic mBleGattCharGGISTempThreshold;
        public static BluetoothGattCharacteristic mBleGattCharGGISHumidThreshold;
        public static BluetoothGattCharacteristic mBleGattCharGGISHoursOfUse;
        public static BluetoothGattCharacteristic mBleGattCharGGISBatteryRechargeCycles;
        public static BluetoothGattCharacteristic mBleGattCharGGISLampHours;
        public static BluetoothGattCharacteristic mBleGattCharGGISErrorLog;
        public static BluetoothGattCharacteristic mBleGattCharGDTSTime;
        public static BluetoothGattCharacteristic mBleGattCharGCISReqSpecCalCoefficients;
        public static BluetoothGattCharacteristic mBleGattCharGCISRetSpecCalCoefficients;
        public static BluetoothGattCharacteristic mBleGattCharGCISReqRefCalCoefficients;
        public static BluetoothGattCharacteristic mBleGattCharGCISRetRefCalCoefficients;
        public static BluetoothGattCharacteristic mBleGattCharGCISReqRefCalMatrix;
        public static BluetoothGattCharacteristic mBleGattCharGCISRetRefCalMatrix;
        public static BluetoothGattCharacteristic mBleGattCharGSCISNumberStoredConf;
        public static BluetoothGattCharacteristic mBleGattCharGSCISReqStoredConfList;
        public static BluetoothGattCharacteristic mBleGattCharGSCISRetStoredConfList;
        public static BluetoothGattCharacteristic mBleGattCharGSCISReqScanConfData;
        public static BluetoothGattCharacteristic mBleGattCharGSCISRetScanConfData;
        public static BluetoothGattCharacteristic mBleGattCharGSCISActiveScanConf;
        public static BluetoothGattCharacteristic mBleGattCharGSDISNumberSDStoredScans;
        public static BluetoothGattCharacteristic mBleGattCharGSDISSDStoredScanIndicesList;
        public static BluetoothGattCharacteristic mBleGattCharGSDISSDStoredScanIndicesListData;
        public static BluetoothGattCharacteristic mBleGattCharGSDISSetScanNameStub;
        public static BluetoothGattCharacteristic mBleGattCharGSDISStartScanWrite;
        public static BluetoothGattCharacteristic mBleGattCharGSDISStartScanNotify;
        public static BluetoothGattCharacteristic mBleGattCharGSDISClearScanWrite;
        public static BluetoothGattCharacteristic mBleGattCharGSDISClearScanNotify;
        public static BluetoothGattCharacteristic mBleGattCharGSDISReqScanName;
        public static BluetoothGattCharacteristic mBleGattCharGSDISRetScanName;
        public static BluetoothGattCharacteristic mBleGattCharGSDISReqScanType;
        public static BluetoothGattCharacteristic mBleGattCharGSDISRetScanType;
        public static BluetoothGattCharacteristic mBleGattCharGSDISReqScanDate;
        public static BluetoothGattCharacteristic mBleGattCharGSDISRetScanDate;
        public static BluetoothGattCharacteristic mBleGattCharGSDISReqPacketFormatVersion;
        public static BluetoothGattCharacteristic mBleGattCharGSDISRetPacketFormatVersion;
        public static BluetoothGattCharacteristic mBleGattCharGSDISReqSerialScanDataStruct;
        public static BluetoothGattCharacteristic mBleGattCharGSDISRetSerialScanDataStruct;
        public static BluetoothGattCharacteristic mBleGattCharacteristicCharacteristicActivateState;
        public static BluetoothGattCharacteristic mBleGattCharacteristicCharacteristicActivateStateNotify;
        public static BluetoothGattCharacteristic mBleGattCharacteristicReadCurrentScanConfigurationData;
        public static BluetoothGattCharacteristic mBleGattCharacteristicReturnCurrentScanConfigurationData;
        public static BluetoothGattCharacteristic mBleGattCharacteristicWriteScanConfigurationData;
        public static BluetoothGattCharacteristic mBleGattCharacteristicReturnWriteScanConfigurationData;
        public static BluetoothGattCharacteristic mBleGattCharUUID;
        public static BluetoothGattCharacteristic mBleGattCharacteristicLampMode;
        public static BluetoothGattCharacteristic mBleGattCharacteristicLampDelayTime;
        public static BluetoothGattCharacteristic mBleGattCharacteristicSetPGA;
        public static BluetoothGattCharacteristic mBleGattCharacteristicSetScanAverage;
        public static BluetoothGattCharacteristic mBleGattCharacteristicSaveReference;

        public NanoGattCharacteristic() {
        }
    }

    public static class NanoGATT {

        public static final UUID DIS_MANUF_NAME = UUID.fromString("00002A29-0000-1000-8000-00805F9B34FB");
        public static final UUID DIS_MODEL_NUMBER = UUID.fromString("00002A24-0000-1000-8000-00805F9B34FB");
        public static final UUID DIS_SERIAL_NUMBER = UUID.fromString("00002A25-0000-1000-8000-00805F9B34FB");
        public static final UUID DIS_HW_REV = UUID.fromString("00002A27-0000-1000-8000-00805F9B34FB");
        public static final UUID DIS_TIVA_FW_REV = UUID.fromString("00002A26-0000-1000-8000-00805F9B34FB");
        public static final UUID DIS_SPECC_REV = UUID.fromString("00002A28-0000-1000-8000-00805F9B34FB");
        public static final UUID BAS_BATT_LVL = UUID.fromString("00002A19-0000-1000-8000-00805F9B34FB");
        public static final UUID GGIS_TEMP_MEASUREMENT = UUID.fromString("43484101-444C-5020-4E49-52204E616E6F");
        public static final UUID GGIS_HUMID_MEASUREMENT = UUID.fromString("43484102-444C-5020-4E49-52204E616E6F");
        public static final UUID GGIS_DEV_STATUS = UUID.fromString("43484103-444C-5020-4E49-52204E616E6F");
        public static final UUID GGIS_ERR_STATUS = UUID.fromString("43484104-444C-5020-4E49-52204E616E6F");
        public static final UUID GGIS_TEMP_THRESH = UUID.fromString("43484105-444C-5020-4E49-52204E616E6F");
        public static final UUID GGIS_HUMID_THRESH = UUID.fromString("43484106-444C-5020-4E49-52204E616E6F");
        public static final UUID GGIS_HOURS_OF_USE = UUID.fromString("43484107-444C-5020-4E49-52204E616E6F");
        public static final UUID GGIS_NUM_BATT_RECHARGE = UUID.fromString("43484108-444C-5020-4E49-52204E616E6F");
        public static final UUID GGIS_LAMP_HOURS = UUID.fromString("43484109-444C-5020-4E49-52204E616E6F");
        public static final UUID GGIS_ERR_LOG = UUID.fromString("4348410A-444C-5020-4E49-52204E616E6F");
        public static final UUID GDTS_TIME = UUID.fromString("4348410C-444C-5020-4E49-52204E616E6F");
        public static final UUID GCIS_REQ_SPEC_CAL_COEFF = UUID.fromString("4348410D-444C-5020-4E49-52204E616E6F");
        public static final UUID GCIS_RET_SPEC_CAL_COEFF = UUID.fromString("4348410E-444C-5020-4E49-52204E616E6F");
        public static final UUID GCIS_REQ_REF_CAL_COEFF = UUID.fromString("4348410F-444C-5020-4E49-52204E616E6F");
        public static final UUID GCIS_RET_REF_CAL_COEFF = UUID.fromString("43484110-444C-5020-4E49-52204E616E6F");
        public static final UUID GCIS_REQ_REF_CAL_MATRIX = UUID.fromString("43484111-444C-5020-4E49-52204E616E6F");
        public static final UUID GCIS_RET_REF_CAL_MATRIX = UUID.fromString("43484112-444C-5020-4E49-52204E616E6F");
        public static final UUID GSCIS_NUM_STORED_CONF = UUID.fromString("43484113-444C-5020-4E49-52204E616E6F");
        public static final UUID GSCIS_REQ_STORED_CONF_LIST = UUID.fromString("43484114-444C-5020-4E49-52204E616E6F");
        public static final UUID GSCIS_RET_STORED_CONF_LIST = UUID.fromString("43484115-444C-5020-4E49-52204E616E6F");
        public static final UUID GSCIS_REQ_SCAN_CONF_DATA = UUID.fromString("43484116-444C-5020-4E49-52204E616E6F");
        public static final UUID GSCIS_RET_SCAN_CONF_DATA = UUID.fromString("43484117-444C-5020-4E49-52204E616E6F");
        public static final UUID GSCIS_ACTIVE_SCAN_CONF = UUID.fromString("43484118-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_NUM_SD_STORED_SCANS = UUID.fromString("43484119-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_SD_STORED_SCAN_IND_LIST = UUID.fromString("4348411A-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_SD_STORED_SCAN_IND_LIST_DATA = UUID.fromString("4348411B-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_SET_SCAN_NAME_STUB = UUID.fromString("4348411C-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_START_SCAN = UUID.fromString("4348411D-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_CLEAR_SCAN = UUID.fromString("4348411E-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_REQ_SCAN_NAME = UUID.fromString("4348411F-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_RET_SCAN_NAME = UUID.fromString("43484120-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_REQ_SCAN_TYPE = UUID.fromString("43484121-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_RET_SCAN_TYPE = UUID.fromString("43484122-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_REQ_SCAN_DATE = UUID.fromString("43484123-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_RET_SCAN_DATE = UUID.fromString("43484124-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_REQ_PKT_FMT_VER = UUID.fromString("43484125-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_RET_PKT_FMT_VER = UUID.fromString("43484126-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_REQ_SER_SCAN_DATA_STRUCT = UUID.fromString("43484127-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_RET_SER_SCAN_DATA_STRUCT = UUID.fromString("43484128-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_ACTIVATE_STATE = UUID.fromString("43484130-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_RETURN_ACTIVATE_STATE = UUID.fromString("43484131-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_READ_CURRENT_SCANCONFIG_DATA = UUID.fromString("43484140-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_RETURN_CURRENT_SCANCONFIG_DATA = UUID.fromString("43484141-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_WRITE_SCANCONFIG_DATA = UUID.fromString("43484142-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_RETURN_WRITE_SCANCONFIG_DATA = UUID.fromString("43484143-444C-5020-4E49-52204E616E6F");
        public static final UUID DEVICE_UUID = UUID.fromString("00002A23-0000-1000-8000-00805F9B34FB");
        public static final UUID GSDIS_LAMP_MODE = UUID.fromString("43484144-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_LAMP_DELAY_TIME = UUID.fromString("43484145-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_SET_PGA = UUID.fromString("43484146-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_SET_SCAN_AVERAGE = UUID.fromString("43484147-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_SAVE_REFERENCE = UUID.fromString("43484132-444C-5020-4E49-52204E616E6F");

        public NanoGATT() {
        }
    }
}