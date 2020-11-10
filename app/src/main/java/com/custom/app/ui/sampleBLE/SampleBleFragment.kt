package com.custom.app.ui.sampleBLE

import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.content.*
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.base.app.ui.base.BaseFragment
import com.custom.app.R
import com.custom.app.data.model.farmer.upload.FarmerItem
import com.custom.app.ui.sampleBleResult.BleResult
import com.custom.app.ui.sampleBleResult.SampleBleResultFragment
import com.custom.app.ui.scan.select.SelectScanFragment
import com.custom.app.util.*
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothClassicService
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothConfiguration
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService
import kotlinx.android.synthetic.main.fragment_sample_ble.*
import org.parceler.Parcels
import java.text.DateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class SampleBleFragment : BaseFragment(), View.OnClickListener, AdapterView.OnItemSelectedListener {
    private var deviceId = 0
    private var scanId: String? = null
    private var deviceName: String? = null
    private lateinit var farmer: FarmerItem
    private lateinit var viewModel: SampleBleViewModel

    private val REQUEST_SELECT_DEVICE = 1
    private val REQUEST_ENABLE_BT = 2
    val TAG = "nRFUART"
    private val UART_PROFILE_CONNECTED = 20
    private val UART_PROFILE_DISCONNECTED = 21

    private var mState = UART_PROFILE_DISCONNECTED
    private var mService: UartService? = null
    private var mDevice: BluetoothDevice? = null
    private var mBtAdapter: BluetoothAdapter? = null
//    private var messageListView: ListView? = null
//    private var listAdapter: ArrayAdapter<String>? = null
//    private var btnSend: Button? = null
//    private var edtMessage: EditText? = null
    private val UUID_DEVICE = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
    var bytedata = java.util.ArrayList<ByteArray>()
    var encodedData = java.util.ArrayList<String>()

    @Inject
    lateinit var interactor: SampleBleInteractor

    companion object {
        @JvmStatic
        fun newInstance(scanId: String?, deviceId: Int, deviceName: String, farmer: FarmerItem): SampleBleFragment {
            val fragment = SampleBleFragment()
            val args = Bundle()
            args.putString(Constants.KEY_SCAN_ID, scanId)
            args.putInt(com.specx.device.util.Constants.KEY_DEVICE_ID, deviceId)
            args.putString(com.specx.device.util.Constants.KEY_DEVICE_NAME, deviceName)
            args.putParcelable(com.specx.scan.util.Constants.KEY_FARMER, Parcels.wrap(farmer))
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val config = BluetoothConfiguration()
        config.bluetoothServiceClass = BluetoothClassicService::class.java //  BluetoothClassicService.class or BluetoothLeService.class


        config.context = requireActivity()
        config.bufferSize = 1024
        config.characterDelimiter = '\n'
        config.deviceName = "Bluetooth Sample"
        config.callListenersInMainThread = true

        //config.uuid = null; // When using BluetoothLeService.class set null to show all devices on scan.

        //config.uuid = null; // When using BluetoothLeService.class set null to show all devices on scan.
        config.uuid = UUID_DEVICE // For Classic


        config.uuidService = null // For BLE

        config.uuidCharacteristic = null // For BLE

//        config.transport = BluetoothDevice.TRANSPORT_LE // Only for dual-mode devices

        // For BLE
        //        config.transport = BluetoothDevice.TRANSPORT_LE // Only for dual-mode devices

        // For BLE
        config.connectionPriority = BluetoothGatt.CONNECTION_PRIORITY_HIGH // Automatically request connection priority just after connection is through.

        //or request connection priority manually, mService.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);

        //or request connection priority manually, mService.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);
        BluetoothService.init(config)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_sample_ble, container, false)
        setStep(1)

        viewModel = ViewModelProvider(this, SampleBleViewModel.SampleBleViewModelFactory(interactor))[SampleBleViewModel::class.java]
        viewModel.sampleBleStateState.observe(::getLifecycle, ::setViewState)
        return view
    }

    fun bleCode() {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBtAdapter == null) {
            Toast.makeText(requireContext(), "Bluetooth is not available", Toast.LENGTH_LONG).show()
//            finish()
            return
        }
//        messageListView = findViewById<View>(R.id.listMessage) as ListView
//        listAdapter = ArrayAdapter(this, R.layout.message_detail)
//        messageListView.setAdapter(listAdapter)
//        messageListView.setDivider(null)
//        btnConnectDisconnect = findViewById<View>(R.id.btn_select) as Button
//        btnSend = findViewById<View>(R.id.sendButton) as Button
//        edtMessage = findViewById<View>(R.id.sendText) as EditText
        service_init()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            scanId = requireArguments().getString(Constants.KEY_SCAN_ID)
            deviceId = requireArguments().getInt(com.specx.device.util.Constants.KEY_DEVICE_ID)
            deviceName = requireArguments().getString(com.specx.device.util.Constants.KEY_DEVICE_NAME)
            farmer = Parcels.unwrap(requireArguments().getParcelable(com.specx.scan.util.Constants.KEY_FARMER))
        }
        bnProceed.setOnClickListener(this)
        spLocation.onItemSelectedListener = this
        spCommodity.onItemSelectedListener = this
        viewModel.getLocation()
        viewModel.getCommodity()
        bleCode()

    }

    private fun setViewState(state: SampleBleState) {
        when (state) {
            SampleBleState.loading -> progress.visibility = View.VISIBLE
            SampleBleState.locationSuccess -> {
                progress.visibility = View.GONE
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, viewModel.locationArray)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spLocation.adapter = adapter
            }
            SampleBleState.locationFailure -> {
                progress.visibility = View.GONE
                showMessage("Error to get Locations")
            }
            SampleBleState.commoditySuccess -> {
                progress.visibility = View.GONE
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, viewModel.commodityArray)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spCommodity.adapter = adapter
            }
            SampleBleState.commodityFailure -> {
                progress.visibility = View.GONE
                showMessage("Error to get Commodity")
            }
            SampleBleState.tokenExpired -> {
            }
        }
    }

    private fun setStep(step: Int) {
        val fragment = parentFragmentManager.findFragmentByTag(Constants.SELECT_SCAN_FRAGMENT)
        if (fragment != null) {
            (fragment as SelectScanFragment).setStep(step)
        }
    }

    override fun showMessage(msg: String?) {
        super.showMessage(msg)
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun showProgressBar() {
        super.showProgressBar()
        progress.visibility = View.VISIBLE
    }

    override fun onClick(view: View?) {
        when (view) {
            bnProceed -> {
                if (!mBtAdapter!!.isEnabled()) {
                    val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    startActivityForResult(enableIntent, REQUEST_ENABLE_BT)
                } else {
                    if (bnProceed!!.getText() == "Connect") {

                        //Connect button pressed, open DeviceListActivity class, with popup windows that scan for devices
                        val newIntent = Intent(requireActivity(), Devices::class.java)
                        startActivityForResult(newIntent, REQUEST_SELECT_DEVICE)
                    } else {
                        //Disconnect button pressed
                        if (mDevice != null) {
                            mService!!.disconnect()
                        }
                    }
                }
            }
        }
    }

    fun getResults(bleInfo: BLEInfo) {
        showProgressBar()
        showMessage("Connecting to BLE device")
        val bleResult1 = BleResult("Commodity", bleInfo.commodity)
        val bleResult2 = BleResult("Weight", etQuantity.text.toString())
        val bleResult3 = BleResult("Mositure", bleInfo.moisture)
        val bleResult4 = BleResult("Temperature", bleInfo.temperature)
        val resultList = ArrayList<BleResult>()
        resultList.add(bleResult1)
        resultList.add(bleResult2)
        resultList.add(bleResult3)
        resultList.add(bleResult4)
        Handler(Looper.getMainLooper()).postDelayed({

            fragmentTransition(R.id.layout_content,
                    SampleBleResultFragment.newInstance("ScanID", deviceId, resultList), Constants.SAMPLE_BLE_RESULT)
        }, 3000)

    }

    override fun onItemSelected(p0: AdapterView<*>?, spinner: View?, pos: Int, p3: Long) {
        when (spinner) {
            spLocation -> {
                tvLocId.text = viewModel.locationArray[pos].code
//                when (pos) {
//                    0 -> {
//                    }
//                }
            }
            spCommodity -> {
                when (pos) {
                    0 -> {
                    }
                }
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    //UART service connected/disconnected
    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, rawBinder: IBinder) {
            mService = (rawBinder as UartService.LocalBinder).getService()
            Log.d(TAG, "onServiceConnected mService= $mService")
            if (!mService!!.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth")
//                finish()
            }
        }

        override fun onServiceDisconnected(classname: ComponentName) {
            ////     mService.disconnect(mDevice);
            mService = null
        }
    }

    private val UARTStatusChangeReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            val mIntent = intent
            //*********************//
            if (action == UartService.ACTION_GATT_CONNECTED) {
                activity!!.runOnUiThread(Runnable {
                    val currentDateTimeString = DateFormat.getTimeInstance().format(Date())
                    Log.d(TAG, "UART_CONNECT_MSG")
                    bnProceed!!.text = "Disconnect"
//                    edtMessage!!.isEnabled = true
//                    btnSend!!.isEnabled = true
//                    (findViewById<View>(R.id.deviceName) as TextView).text = mDevice!!.name + " - ready"
//                    listAdapter!!.add("[" + currentDateTimeString + "] Connected to: " + mDevice!!.name)
//                    messageListView!!.smoothScrollToPosition(listAdapter!!.count - 1)
                    mState = UART_PROFILE_CONNECTED
                })
            }

            //*********************//
            if (action == UartService.ACTION_GATT_DISCONNECTED) {
                activity!!.runOnUiThread(Runnable {
                    val currentDateTimeString = DateFormat.getTimeInstance().format(Date())
                    Log.d(TAG, "UART_DISCONNECT_MSG")
                    bnProceed!!.text = "Connect"
//                    edtMessage!!.isEnabled = false
//                    btnSend!!.isEnabled = false
//                    (findViewById<View>(R.id.deviceName) as TextView).text = "Not Connected"
//                    listAdapter!!.add("[" + currentDateTimeString + "] Disconnected to: " + mDevice!!.name)
                    mState = UART_PROFILE_DISCONNECTED
                    mService!!.close()
                    mService!!.close()
                    //setUiState();
                })
            }


            //*********************//
            if (action == UartService.ACTION_GATT_SERVICES_DISCOVERED) {
                mService!!.enableTXNotification()
            }
            //*********************//
            if (action == UartService.ACTION_DATA_AVAILABLE) {
                val txValue = intent.getByteArrayExtra(UartService.EXTRA_DATA)
                bytedata.add(txValue)
                activity!!.runOnUiThread(Runnable {
                    try {
                        val text = String(txValue, charset("UTF-8"))
                        val currentDateTimeString = DateFormat.getTimeInstance().format(Date())
//                        listAdapter!!.add("[$currentDateTimeString] RX: $text")
//                        messageListView!!.smoothScrollToPosition(listAdapter!!.count - 1)
                        encodedData.add(encodeHexString(txValue)!!)
                    } catch (e: Exception) {
                        Log.e(TAG, e.toString())
                    }
                })
                val bleReader = BLEInfoReader()
                val bleInfo = bleReader.readBLEInfo(encodedData)

                getResults(bleInfo)
//                tvDevice!!.text = "Device " + bleInfo.machineId
//                tvCommodity!!.text = "Commodity " + bleInfo.commodity
//                tvHum!!.text = "Humidity " + bleInfo.moisture.toString() + " %"
//                tvTemp!!.text = "Temperature " + bleInfo.temperature.toString() + " °C"
//                tvWeight!!.text = "Weight " + bleInfo.weight.toString() + " Gram"
            }
            //*********************//
            if (action == UartService.DEVICE_DOES_NOT_SUPPORT_UART) {
                showMessage("Device doesn't support UART. Disconnecting")
                mService!!.disconnect()
            }
        }
    }

    private fun service_init() {
        val bindIntent = Intent(requireActivity(), UartService::class.java)
        requireActivity().bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE)
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter()!!)
    }

    private fun makeGattUpdateIntentFilter(): IntentFilter? {
        val intentFilter = IntentFilter()
        intentFilter.addAction(UartService.ACTION_GATT_CONNECTED)
        intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED)
        intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED)
        intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE)
        intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART)
        return intentFilter
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(UARTStatusChangeReceiver)
        } catch (ignore: Exception) {
        }
        requireActivity().unbindService(mServiceConnection)
        mService!!.stopSelf()
        mService = null
    }

    override fun onResume() {
        super.onResume()
        if (!mBtAdapter!!.isEnabled) {
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_SELECT_DEVICE ->                 //When the DeviceListActivity return, with the selected device address
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val deviceAddress = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE)
                    mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress)
//                    (findViewById<View>(R.id.deviceName) as TextView).text = mDevice!!.getName() + " - connecting"
                    mService!!.connect(deviceAddress)
                }
            REQUEST_ENABLE_BT ->                 // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(requireContext(), "Bluetooth has turned on ", Toast.LENGTH_SHORT).show()
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Toast.makeText(requireContext(), "Problem in BT Turning ON ", Toast.LENGTH_SHORT).show()
//                    finish()
                }
        }
    }

    fun onBackPressed() {
        if (mState == UART_PROFILE_CONNECTED) {
            val startMain = Intent(Intent.ACTION_MAIN)
            startMain.addCategory(Intent.CATEGORY_HOME)
            startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(startMain)
            showMessage("nRFUART's running in background.\n             Disconnect to exit")
        } else {
            AlertDialog.Builder(requireContext())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.popup_title)
                    .setMessage(R.string.popup_message)
                    .setPositiveButton(R.string.popup_yes) { dialog, which ->
                        {
//                        finish()
                        }
                    }
                    .setNegativeButton(R.string.popup_no, null)
                    .show()
        }
    }
}