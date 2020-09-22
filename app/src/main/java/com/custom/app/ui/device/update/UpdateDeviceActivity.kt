package com.custom.app.ui.device.update

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.base.app.ui.base.BaseActivity
import com.core.app.util.AlertUtil
import com.custom.app.CustomApp
import com.custom.app.R
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.device.add.DeviceGroupRes
import com.custom.app.ui.device.add.DeviceSubTypeRes
import com.custom.app.ui.device.add.DeviceTypeRes
import com.custom.app.ui.device.add.SensorProfileRes
import com.custom.app.ui.device.add.adapter.DeviceGroupListAdapter
import com.custom.app.ui.device.add.adapter.DeviceSubTypeListAdapter
import com.custom.app.ui.device.add.adapter.SensorProfileListAdapter
import com.custom.app.ui.device.list.DeviceInteractor
import com.custom.app.ui.device.list.DevicesData
import com.custom.app.util.Utils.Companion.timeStampDate
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_add_device.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class UpdateDeviceActivity : BaseActivity() {

    @Inject
    lateinit var interactor: DeviceInteractor
    private lateinit var viewModel: UpdateDevicesViewModel
    private var selectedDeviceTypeId: Int = 0
    private var selectedGroupId: Int = 0
    private var selectedSubTypeId: Int = 0
    private var selectedSensorProfileId: Int = 0
    private var startDate: Long? = null
    private var endDate: Long? = null

    private lateinit var deviceTypeList: ArrayList<DeviceTypeRes>
    private lateinit var deviceSubTypeList: ArrayList<DeviceSubTypeRes>
    private lateinit var deviceGroupList: ArrayList<DeviceGroupRes>
    private lateinit var deviceSensorProfile: ArrayList<SensorProfileRes>
    var deviceId: Int? = null
    var testObject: DevicesData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_device)

        val selectObject = intent.getStringExtra("selectObject")
        val gson = Gson()
        if (selectObject != null) {
            val type = object : TypeToken<DevicesData>() {}.type
            testObject = gson.fromJson(selectObject, type)
        }

        viewModel = ViewModelProvider(this, UpdateDeviceViewModelFactory(interactor))[UpdateDevicesViewModel::class.java]

        viewModel.updateDevicesState.observe(::getLifecycle, ::updateUI)
        initview()

        viewModel.onDeviceTypeGet()
    }

    fun initview() {

        toolbar.title = getString(R.string.update_device)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        btAddDevice.text = getString(R.string.update_device)

        btAddDevice.setOnClickListener {
            updateDevice()
        }
        et_StartLife.setOnClickListener {
            datePickerDialog(et_StartLife)
        }
        et_EndLife.setOnClickListener {
            datePickerDialog(et_EndLife)
        }

        spDeviceType.isClickable = false
    }

    private fun updateUI(screenState: ScreenState<UpdateDevicesState>?) {
        when (screenState) {
            ScreenState.Loading -> progressAddDevices.visibility = View.VISIBLE
            is ScreenState.Render -> processLoginState(screenState.renderState)
        }
    }

    private fun processLoginState(renderState: UpdateDevicesState) {
        progressAddDevices.visibility = View.GONE
        when (renderState) {
            UpdateDevicesState.hasConnectionError -> {
                AlertUtil.showToast(this, "Connection error")
            }
            UpdateDevicesState.EndLifeEmpty -> {
                AlertUtil.showToast(this, "Please enter end life")
            }
            UpdateDevicesState.StartLifeEmpty -> {
                AlertUtil.showToast(this, "Please enter start life")
            }
            UpdateDevicesState.VendorEmpty -> {
                AlertUtil.showToast(this, "Please enter vendor name")
            }
            UpdateDevicesState.SerialNumberEmpty -> {
                AlertUtil.showToast(this, "Please enter serial number")
            }
            UpdateDevicesState.UpdateDevicesFailure -> {
                AlertUtil.showToast(this, "Unable to add devices")
            }
            UpdateDevicesState.UpdateDevicesSuccess -> {
                resetForm()
                val intent = Intent()
                intent.putExtra("result", "success")
                setResult(RESULT_OK, intent);
                finish()
                AlertUtil.showToast(this, "Device has been updated successfully !")
            }
            UpdateDevicesState.GetSensorProfileSuccess -> {
                deviceSensorProfile = viewModel.getSensorProfileResponse.value!!
                updateSensorSpinner(deviceSensorProfile)
            }
            UpdateDevicesState.GetDeviceTypeSuccess -> {
                deviceTypeList = viewModel.getDeviceTypeResponse.value!!
                updateDeviceTypeSpinner(deviceTypeList)
            }
            UpdateDevicesState.GetDeviceTypeGroupSuccess -> {
                deviceGroupList = viewModel.getDeviceGroupResponse.value!!
                updateDeviceGroupSpinner(deviceGroupList)
            }
            UpdateDevicesState.GetDeviceSubTypeSuccess -> {
                deviceSubTypeList = viewModel.getDeviceSubTypeResponse.value!!
                updateDeviceSubTypeSpinner(deviceSubTypeList)
            }
            UpdateDevicesState.GetDeviceSubTypeError -> {
                AlertUtil.showToast(this, "Unable to get device sub type")
            }
            UpdateDevicesState.GetDeviceTypeGroupError -> {
                AlertUtil.showToast(this, "Unable to get device group")
            }
            UpdateDevicesState.GetDeviceTypeError -> {
                AlertUtil.showToast(this, "Unable to get device types")
            }
            UpdateDevicesState.GetSensorProfileError -> {
                AlertUtil.showToast(this, "Unable to get sensor profiles")
            }
        }
    }

    private fun updateDeviceTypeSpinner(deviceTypeList: ArrayList<DeviceTypeRes>) {

        val deviceName = ArrayList<String>()
        deviceName.add(testObject!!.device_type!!)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, deviceName)
        spDeviceType.adapter = adapter
        selectedDeviceTypeId = testObject!!.device_type_id!!.toInt()

        spDeviceType.isEnabled = false
        if (selectedDeviceTypeId == 3) {
            viewModel.onDeviceGroupGet()
            tvDeviceGroup.visibility = View.VISIBLE
            spLayoutDeviceGroup.visibility = View.VISIBLE
            tvDeviceSubType.visibility = View.VISIBLE
            spLayoutDeviceSubType.visibility = View.VISIBLE
            tvSensorProfile.visibility = View.VISIBLE
            spLayoutSensorProfile.visibility = View.VISIBLE
        } else {
            tvDeviceGroup.visibility = View.GONE
            spLayoutDeviceGroup.visibility = View.GONE
            tvDeviceSubType.visibility = View.GONE
            spLayoutDeviceSubType.visibility = View.GONE
            tvSensorProfile.visibility = View.GONE
            spLayoutSensorProfile.visibility = View.GONE
            updateView(viewModel.singleDevice)
        }

//        spDeviceType.onItemSelectedListener =
//                object : AdapterView.OnItemSelectedListener {
//                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
//
//                    }
//
//                    override fun onNothingSelected(parent: AdapterView<*>) {
//                        // Another interface callback
//                    }
//                }

    }

    private fun updateDeviceGroupSpinner(deviceGroupList: ArrayList<DeviceGroupRes>) {

        val adapter = DeviceGroupListAdapter(this, deviceGroupList)
        spDeviceGroup.adapter = adapter
        spDeviceGroup.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View,
                            position: Int,
                            id: Long) {

                        selectedGroupId = deviceGroupList[position].device_group_id!!
                        viewModel.onDeviceSubTypeGet()

                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Another interface callback
                    }
                }

    }

    private fun updateDeviceSubTypeSpinner(deviceSubTypeList: ArrayList<DeviceSubTypeRes>) {

        val adapter = DeviceSubTypeListAdapter(this, deviceSubTypeList)
        spDeviceSubType.adapter = adapter
        spDeviceSubType.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View,
                            position: Int,
                            id: Long) {

                        selectedSubTypeId = deviceSubTypeList[position].device_sub_type_id!!
                        viewModel.onSensorProfileGet()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Another interface callback
                    }
                }

    }

    private fun updateSensorSpinner(sensorProfileList: ArrayList<SensorProfileRes>) {

        val adapter = SensorProfileListAdapter(this, sensorProfileList)
        spSensorProfile.adapter = adapter
        spSensorProfile.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                        selectedSensorProfileId = sensorProfileList[position].device_sensor_profile_id!!
                        updateView(viewModel.singleDevice)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Another interface callback
                    }
                }

    }

    private fun datePickerDialog(textView: TextView) {

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year: Int, monthOfYear: Int, dayOfMonth: Int ->


                    val fmt = SimpleDateFormat("dd-MM-yyyy")
                    val month = monthOfYear + 1
                    val date = fmt.parse("$dayOfMonth-$month-$year")

                    val fmtOut = SimpleDateFormat("dd-MM-yyyy")

                    when (textView.id) {
                        R.id.et_StartLife -> {
//                            startDate = date.time
                            et_StartLife.text = fmtOut.format(date)
                        }
                        R.id.et_EndLife -> {
//                            endDate = date.time
                            et_EndLife.text = fmtOut.format(date)
                        }
                    }
                }, year, month, day
        )

        val datePicker = dpDialog.datePicker
        val calendar = Calendar.getInstance()
        datePicker.maxDate = calendar.timeInMillis

        dpDialog.show()

    }

    private fun resetForm() {

        et_serialNumber.setText("")
        et_HwRevision.setText("")
        et_FwRevision.setText("")
        et_StartLife.setText("")
        et_EndLife.setText("")
        et_Vendor.setText("")
    }

    private fun updateDevice() {

        startDate = epochTime(et_StartLife.text.toString())
        endDate = epochTime(et_EndLife.text.toString())

        viewModel.submitUpdateDevice(
                et_serialNumber.text.toString(), selectedDeviceTypeId, et_HwRevision.text.toString(), et_FwRevision.text.toString(), startDate.toString(), endDate.toString(),
                selectedSensorProfileId, selectedSubTypeId, selectedGroupId, et_Vendor.text.toString(), deviceId!!, this)
    }

    private fun updateView(singleDevice: DevicesData) {

        if (testObject!!.serial_number != null) {
            et_serialNumber.setText(testObject!!.serial_number)
        } else {
            et_serialNumber.setText("No record found")
        }
        if (testObject!!.hw_revision != null) {
            et_HwRevision.setText(testObject!!.hw_revision)
        } else {
            et_HwRevision.setText("No record found")
        }
        if (testObject!!.fw_revision != null) {
            et_FwRevision.setText(testObject!!.fw_revision)
        } else {
            et_FwRevision.setText("No record found")
        }

        if(testObject!!.device_type_id.equals("3")) {

            if (testObject!!.vendor_name != null) {
                et_Vendor.setText(testObject!!.vendor_name)
            } else {
                et_Vendor.setText("No record found")
            }

            if (deviceGroupList.isNotEmpty()) {
                for (i in 0 until deviceGroupList.size) {
                    spDeviceGroup.setSelection(deviceGroupList[i].device_group_id.toString().indexOf(testObject!!.device_group_id.toString()))
                }
            }

            if (deviceSubTypeList.isNotEmpty()) {
                for (i in 0 until deviceSubTypeList.size) {
                    spDeviceSubType.setSelection(deviceSubTypeList[i].device_sub_type_id!!.toString().indexOf(testObject!!.device_sub_type_id!!.toString()))
                }
            }

            if (deviceSensorProfile.isNotEmpty()) {
                for (i in 0 until deviceSensorProfile.size) {
                    spSensorProfile.setSelection(deviceSensorProfile[i].device_sensor_profile_id!!.toString().indexOf(testObject!!.sensor_profile_id!!.toString()))
                }
            }
        }

        et_StartLife.text = timeStampDate(testObject!!.start_of_life!!)
        et_EndLife.text = timeStampDate(testObject!!.end_of_life!!)
        deviceId = testObject!!.device_id

    }

    private fun epochTime(strDate: String): Long {

        val fmtOut = SimpleDateFormat("dd/MM/yyyy")
        val date = fmtOut.parse(strDate)
        val epoch = date.time

        return epoch
    }

}