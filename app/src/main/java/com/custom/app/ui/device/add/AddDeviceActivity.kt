package com.custom.app.ui.device.add

import android.app.Activity
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.base.app.ui.base.BaseActivity
import com.core.app.util.AlertUtil
import com.core.app.util.AlertUtil.showToast
import com.custom.app.CustomApp
import com.custom.app.R
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.device.add.adapter.DeviceGroupListAdapter
import com.custom.app.ui.device.add.adapter.DeviceSubTypeListAdapter
import com.custom.app.ui.device.add.adapter.DeviceTypeListAdapter
import com.custom.app.ui.device.add.adapter.SensorProfileListAdapter
import com.custom.app.ui.device.list.DeviceInteractor
import com.custom.app.util.Constants
import kotlinx.android.synthetic.main.activity_add_device.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class AddDeviceActivity : BaseActivity() {

    @Inject
    lateinit var interactor: DeviceInteractor

    private lateinit var viewModel: AddDevicesViewModel
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

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_device)

        viewModel = ViewModelProvider(this, AddDeviceViewModelFactory(interactor))[AddDevicesViewModel::class.java]
        viewModel.addDeviceState.observe(::getLifecycle, ::updateUI)

        initView()

        viewModel.onDeviceTypeGet()
    }

    fun initView() {
        toolbar.title = getString(R.string.add_device)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        btAddDevice.text = getString(R.string.add_device)

        btAddDevice.setOnClickListener {
            addDevice()
        }
        et_StartLife.setOnClickListener {
            datePickerDialog(et_StartLife)
        }
        et_EndLife.setOnClickListener {
            datePickerDialog(et_EndLife)
        }
    }

    private fun updateUI(screenState: ScreenState<AddDeviceState>?) {
        when (screenState) {
            ScreenState.Loading -> progressAddDevices.visibility = View.VISIBLE
            is ScreenState.Render -> processLoginState(screenState.renderState)
        }
    }

    private fun processLoginState(renderState: AddDeviceState) {
        progressAddDevices.visibility = View.GONE
        when (renderState) {
            AddDeviceState.EndLifeEmpty -> {
                AlertUtil.showSnackBar(addDevice_layout, "Please enter end life", 2000)
            }
            AddDeviceState.StartLifeEmpty -> {
                AlertUtil.showSnackBar(addDevice_layout, "Please enter start life", 2000)
            }
            AddDeviceState.VendorEmpty -> {
                AlertUtil.showSnackBar(addDevice_layout, "Please enter vendor name", 2000)
            }
            AddDeviceState.SerialNumberEmpty -> {
                AlertUtil.showSnackBar(addDevice_layout, "Please enter serial number", 2000)
            }
            AddDeviceState.AddDevicesFailure -> {
                AlertUtil.showSnackBar(addDevice_layout, "Unable to add devices", 2000)
            }
            AddDeviceState.AddDevicesSuccess -> {
                resetForm()
                AlertUtil.showActionAlertDialog(this, "", "New device has been added successfully !",
                        false, getString(R.string.btn_ok)) { dialog: DialogInterface?, which: Int ->
                    val intent = Intent()
                    intent.putExtra("result", "success")
                    setResult(RESULT_OK, intent);
                    finish()
                }
            }
            AddDeviceState.GetSensorProfileSuccess -> {
                deviceSensorProfile = viewModel.getSensorProfileResponse.value!!
                updateSensorSpinner(deviceSensorProfile)
            }
            AddDeviceState.GetDeviceTypeSuccess -> {
                deviceTypeList = viewModel.getDeviceTypeResponse.value!!
                updateDeviceTypeSpinner(deviceTypeList)
            }
            AddDeviceState.GetDeviceTypeGroupSuccess -> {
                deviceGroupList = viewModel.getDeviceGroupResponse.value!!
                updateDeviceGroupSpinner(deviceGroupList)
            }
            AddDeviceState.GetDeviceSubTypeSuccess -> {
                deviceSubTypeList = viewModel.getDeviceSubTypeResponse.value!!
                updateDeviceSubTypeSpinner(deviceSubTypeList)
            }
            AddDeviceState.GetDeviceSubTypeError -> {
                AlertUtil.showSnackBar(addDevice_layout, "Unable to get device sub type", 2000)
            }
            AddDeviceState.GetDeviceTypeGroupError -> {
                AlertUtil.showSnackBar(addDevice_layout, "Unable to get device group", 2000)
            }
            AddDeviceState.GetDeviceTypeError -> {
                AlertUtil.showSnackBar(addDevice_layout, "Unable to get device types", 2000)
            }
            AddDeviceState.GetSensorProfileError -> {
                AlertUtil.showSnackBar(addDevice_layout, "Unable to get sensor profiles", 2000)
            }
        }
    }

    private fun updateDeviceTypeSpinner(deviceTypeList: ArrayList<DeviceTypeRes>) {

        val adapter = DeviceTypeListAdapter(this, deviceTypeList)
        spDeviceType.adapter = adapter
        spDeviceType.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View,
                            position: Int,
                            id: Long) {

                        selectedDeviceTypeId = deviceTypeList[position].device_type_id!!

                        if (selectedDeviceTypeId == 3) {

                            viewModel.onDeviceGroupGet()
                            viewModel.onDeviceSubTypeGet()
                            viewModel.onSensorProfileGet()

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

                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Another interface callback
                    }
                }
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
                    override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View,
                            position: Int,
                            id: Long) {

                        selectedSensorProfileId = sensorProfileList[position].device_sensor_profile_id!!
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
                            startDate = date.time
                            et_StartLife.text = fmtOut.format(date)
                        }
                        R.id.et_EndLife -> {
                            endDate = date.time
                            et_EndLife.text = fmtOut.format(date)
                        }
                    }

                }, year, month, day
        )

        val datePicker = dpDialog.datePicker
        val calendar = Calendar.getInstance()
        datePicker.minDate = calendar.timeInMillis

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

    private fun addDevice() {

        val diff = startDate!!.compareTo(endDate!!)
        if (diff < 0) {
            viewModel.submitAddDevice(et_serialNumber.text.toString(), selectedDeviceTypeId, et_HwRevision.text.toString(), et_FwRevision.text.toString(),
                    startDate.toString(), endDate.toString(), selectedSensorProfileId, selectedSubTypeId, selectedGroupId, et_Vendor.text.toString(),
                    this
            )

        } else {

            showToast(this, "Please enter valid dates")

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === Constants.REQUEST_ADD_DEVICE) {
            if (resultCode === Activity.RESULT_OK) {
                setResult(AppCompatActivity.RESULT_OK);
                finish()
            }
        }
    }
}