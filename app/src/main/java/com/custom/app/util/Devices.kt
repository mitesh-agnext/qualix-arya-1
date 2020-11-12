package com.custom.app.util

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.base.app.ui.base.BaseActivity
import com.custom.app.R
import com.custom.app.ui.createData.region.site.list.SiteListRes
import com.custom.app.ui.sampleBleResult.BleResult
import com.custom.app.ui.sampleBleResult.SampleBleActivity
import com.custom.app.util.DeviceItemAdapter.OnAdapterItemClickListener
import com.github.douglasjunior.bluetoothclassiclibrary.*
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService.OnBluetoothEventCallback
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService.OnBluetoothScanCallback
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_sample_ble.*
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList

class Devices : BaseActivity(), OnBluetoothScanCallback, OnBluetoothEventCallback, OnAdapterItemClickListener {
    private var pgBar: ProgressBar? = null
    private var mMenu: Menu? = null
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: DeviceItemAdapter? = null
    private var mService: BluetoothService? = null
    private var mUartService: UartService? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mScanning = false
    private val UUID_DEVICE = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")

    private var mState = UART_PROFILE_DISCONNECTED
    var bytedata = ArrayList<ByteArray>()
    var encodedData = ArrayList<String>()
    var scanId: String? = null
    var deviceId: String? = null
    var sampleId: String? = null
    var quantity: String? = null
    var truckNumber: String? = null

    var bleInfo: BLEInfo? = BLEInfo()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_devices)

        val config = BluetoothConfiguration()
        config.bluetoothServiceClass = BluetoothClassicService::class.java //  BluetoothClassicService.class or BluetoothLeService.class
        config.context = applicationContext
        config.bufferSize = 1024
        config.characterDelimiter = '\n'
        config.callListenersInMainThread = true
        config.uuid = UUID_DEVICE // For Classic
        config.uuidService = null // For BLE
        config.uuidCharacteristic = null // For BLE
        config.connectionPriority = BluetoothGatt.CONNECTION_PRIORITY_HIGH // Automatically request connection priority just after connection is through.
        BluetoothService.init(config)

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        pgBar = findViewById<View>(R.id.pg_bar) as ProgressBar
        pgBar!!.visibility = View.GONE

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        mRecyclerView = findViewById<View>(R.id.rv) as RecyclerView
        mRecyclerView!!.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        mAdapter = DeviceItemAdapter(this, mBluetoothAdapter!!.getBondedDevices())
        mAdapter!!.setOnAdapterItemClickListener(this)
        mRecyclerView!!.adapter = mAdapter

        mService = BluetoothService.getDefaultInstance()
        mService!!.setOnScanCallback(this)
        mService!!.setOnEventCallback(this)

        scanId = intent.getStringExtra(Constants.KEY_SCAN_ID)
        deviceId = intent.getStringExtra(com.specx.device.util.Constants.KEY_DEVICE_ID)
        sampleId = intent.getStringExtra("SampleId")
        truckNumber = intent.getStringExtra("TruckNumber")
        quantity = intent.getStringExtra("Quantity")
        service_init()
    }

    public override fun onResume() {
        super.onResume()
        mService!!.setOnEventCallback(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        mMenu = menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_scan) {
            startStopScan()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun startStopScan() {
        if (!mScanning) {
            mService!!.startScan()
        } else {
            mService!!.stopScan()
        }
    }

    override fun onDataRead(buffer: ByteArray, length: Int) {}
    override fun onStatusChange(status: BluetoothStatus) {
        Toast.makeText(this, status.toString(), Toast.LENGTH_SHORT).show()
        if (status == BluetoothStatus.CONNECTED) {
            val colors = arrayOf<CharSequence>("Try text", "Try picture")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Select")
            builder.setItems(colors
            ) { dialog, which ->
                if (which == 0) {
//                                Intent intent = new Intent(Devices.this, HomeActivity.class);
                } else {
                    startActivity(Intent(this@Devices, BitmapActivity::class.java))
                }
            }
            builder.setCancelable(false)
            builder.show()
        }
    }

    override fun onDeviceDiscovered(device: BluetoothDevice, rssi: Int) {
        val dv = BluetoothDeviceDecorator(device, rssi)
        val index = mAdapter!!.devices.indexOf(dv)
        if (index < 0) {
            mAdapter!!.devices.add(dv)
            mAdapter!!.notifyItemInserted(mAdapter!!.devices.size - 1)
        } else {
            mAdapter!!.devices[index].device = device
            mAdapter!!.devices[index].rssi = rssi
            mAdapter!!.notifyItemChanged(index)
        }
    }

    override fun onStartScan() {
        mScanning = true
        pgBar!!.visibility = View.VISIBLE
        mMenu!!.findItem(R.id.action_scan).setTitle(R.string.action_stop)
    }

    override fun onStopScan() {
        mScanning = false
        pgBar!!.visibility = View.GONE
        mMenu!!.findItem(R.id.action_scan).setTitle(R.string.action_scan)
    }

    override fun onItemClick(device: BluetoothDeviceDecorator, position: Int) {
        val b = Bundle()
        b.putString(BluetoothDevice.EXTRA_DEVICE, device.device.address)
        val result = Intent()
        result.putExtras(b)
        //        setResult(RESULT_OK, result);
        setData(result)

    }

    override fun onPointerCaptureChanged(hasCapture: Boolean) {}
    private fun service_init() {
        val bindIntent = Intent(this, UartService::class.java)
        bindService(bindIntent, mServiceConnection, BIND_AUTO_CREATE)
        LocalBroadcastManager.getInstance(this).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter())
    }

    //UART service connected/disconnected
    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, rawBinder: IBinder) {
            mUartService = (rawBinder as UartService.LocalBinder).service
            if (!mUartService!!.initialize()) {
                finish()
            }
        }

        override fun onServiceDisconnected(classname: ComponentName) {
            ////     mService.disconnect(mDevice);
            mUartService = null
        }
    }

    fun setData(data: Intent) {
        val deviceAddress = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE)
        val mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress)

//        ((TextView) findViewById(R.id.deviceName)).setText(mDevice.getName() + " - connecting");
        mUartService!!.connect(deviceAddress)
    }

    private val UARTStatusChangeReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            val mIntent = intent
            //*********************//
            if (action == UartService.ACTION_GATT_CONNECTED) {
                runOnUiThread {
                    val currentDateTimeString = DateFormat.getTimeInstance().format(Date())
                    mState = UART_PROFILE_CONNECTED
                }
            }

            //*********************//
            if (action == UartService.ACTION_GATT_DISCONNECTED) {
                runOnUiThread {
                    val currentDateTimeString = DateFormat.getTimeInstance().format(Date())
                    mState = UART_PROFILE_DISCONNECTED
                    mUartService!!.close()
                    //setUiState();
                }
            }

            //*********************//
            if (action == UartService.ACTION_GATT_SERVICES_DISCOVERED) {
                mUartService!!.enableTXNotification()
            }
            //*********************//
            if (action == UartService.ACTION_DATA_AVAILABLE) {
                val txValue = intent.getByteArrayExtra(UartService.EXTRA_DATA)
                bytedata.add(txValue)
                runOnUiThread {
                    try {
                        val text = String(txValue, charset("UTF-8"))
                        val currentDateTimeString = DateFormat.getTimeInstance().format(Date())
                        encodedData.add(encodeHexString(txValue))
                    } catch (e: Exception) {
                    }
                }
                val bleReader = BLEInfoReader()
                bleInfo = bleReader.readBLEInfo(encodedData)
            }
            //*********************//
            if (action == UartService.DEVICE_DOES_NOT_SUPPORT_UART) {
                mUartService!!.disconnect()
            }

            if (bleInfo != null && encodedData.size > 0) {
                if (bleInfo!!.machineId != null && bleInfo!!.commodity != null && bleInfo!!.moisture != null && bleInfo!!.temperature != null && bleInfo!!.weight != null ){
                    mUartService!!.disconnect()
                    getResults(bleInfo!!)
                }
            }
            else {
                showMessage("No data recieved")
            }
        }
    }

    fun getResults(bleInfo: BLEInfo) {
        showMessage("Connecting to BLE device")
        var bleResult1: BleResult? = null
        var bleResult2: BleResult? = null
        var bleResult3: BleResult? = null
        var bleResult4: BleResult? = null
        if (bleInfo.commodity != null) {
            bleResult1 = BleResult("Commodity", bleInfo.commodity)
        }
        if (bleInfo.weight != null){
            bleResult2 = BleResult("Weight", bleInfo.weight)
        }
        if (bleInfo.moisture != null){
            bleResult3 = BleResult("Mositure", bleInfo.moisture)
        }
        if (bleInfo.temperature != null){
            bleResult4 = BleResult("Temperature", bleInfo.temperature)
        }

        val resultList = ArrayList<BleResult>()
        if (bleResult1 != null && bleResult2 != null && bleResult3 != null && bleResult4 != null) {
            resultList.add(bleResult1)
            resultList.add(bleResult2)
            resultList.add(bleResult3)
            resultList.add(bleResult4)
            val intent = Intent(this@Devices, SampleBleActivity::class.java)
            intent.putExtra("KEY_SCAN_ID", scanId)
            intent.putExtra("KEY_DEVICE_ID", deviceId)
            val gson = Gson()
            val type = object : TypeToken<ArrayList<BleResult>>() {}.type
            val json = gson.toJson(resultList, type)
            intent.putExtra("selectObject", json)
            intent.putExtra("SampleId", sampleId)
            intent.putExtra("TruckNumber", truckNumber)
            intent.putExtra("Quantity", quantity)

            startActivity(intent)
            finish()
        }
    }

    override fun onDeviceName(deviceName: String) {}
    override fun onToast(message: String) {}
    override fun onDataWrite(buffer: ByteArray) {}

    companion object {
        private const val UART_PROFILE_CONNECTED = 20
        private const val UART_PROFILE_DISCONNECTED = 21
        private fun makeGattUpdateIntentFilter(): IntentFilter {
            val intentFilter = IntentFilter()
            intentFilter.addAction(UartService.ACTION_GATT_CONNECTED)
            intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED)
            intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED)
            intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE)
            intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART)
            return intentFilter
        }
    }
}