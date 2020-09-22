package com.custom.app.ui.device.assign

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.base.app.ui.base.BaseActivity
import com.core.app.util.AlertUtil
import com.custom.app.CustomApp
import com.custom.app.R
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.createData.adapters.ColdstoreDropdownAdapter
import com.custom.app.ui.createData.adapters.DeviationDropdownAdapter
import com.custom.app.ui.createData.adapters.UserDropdownAdapter
import com.custom.app.ui.createData.coldstore.coldstoreList.ColdstoreRes
import com.custom.app.ui.createData.coldstore.createColdstore.ColdstoreInteractor
import com.custom.app.ui.customer.list.CustomerRes
import com.custom.app.ui.device.assign.adapter.CentersListAdapter
import com.custom.app.ui.device.assign.adapter.CustomerListAdapter
import com.custom.app.ui.device.list.DeviceInteractor
import com.custom.app.ui.device.list.DevicesData
import com.custom.app.ui.user.list.UserDataRes
import com.custom.app.util.Utils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_add_device.et_EndLife
import kotlinx.android.synthetic.main.activity_add_device.et_StartLife
import kotlinx.android.synthetic.main.activity_device_provision.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class DeviceProvisionActivity : BaseActivity() {

    @Inject
    lateinit var interactor: DeviceInteractor

    @Inject
    lateinit var coldstoreInteractor: ColdstoreInteractor

    private lateinit var viewModel: ProvisioningDevicesViewModel
    private var selectedCustomerId: String = ""
    private var selectedUserId: String = ""
    private var selectedInstallationId: String = ""

    private var selectedDeviationId: String = ""
    private var selectedColdstoreId: String = ""

    private lateinit var customerList: ArrayList<CustomerRes>
    private lateinit var installationCenterList: ArrayList<InstallationCenterRes>

    private var startServiceDate: Long? = null
    private var endServiceDate: Long? = null

    var deviceId: Int? = null
    var testObject: DevicesData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_provision)

        val selectObject = intent.getStringExtra("selectObject")
        val gson = Gson()
        if (selectObject != null) {
            val type = object : TypeToken<DevicesData>() {}.type
            testObject = gson.fromJson(selectObject, type)
        }

        viewModel = ViewModelProvider(this, ProvisioningDeviceViewModelFactory(interactor, coldstoreInteractor))[ProvisioningDevicesViewModel::class.java]

        viewModel.provisioningDevicesState.observe(::getLifecycle, ::updateUI)

        if (testObject!!.device_type_id.equals("3")) {
            tvDeviceGroup.visibility = View.VISIBLE
            tvDeviceSubType.visibility = View.VISIBLE
            tvDeviationProfile.visibility = View.VISIBLE
            spLayoutDeviationProfile.visibility = View.VISIBLE
            tvColdstores.visibility = View.VISIBLE
            spLayoutColdstores.visibility = View.VISIBLE
            spLayoutDeviceGroup.visibility = View.VISIBLE
            spLayoutDeviceSubType.visibility = View.VISIBLE
            if (userManager.customerType.equals("SERVICE_PROVIDER")) {
                titleCustomer.visibility = View.VISIBLE
                sp_Customer_Layout.visibility = View.VISIBLE
                viewModel.onGetCustomer()
            } else {
                titleCustomer.visibility = View.GONE
                sp_Customer_Layout.visibility = View.GONE
                selectedCustomerId = userManager.customerId
                viewModel.onGetDeviationProfile(selectedCustomerId.toInt())
                viewModel.onGetColdstore(selectedCustomerId.toInt())
            }

        } else {
            tvDeviceGroup.visibility = View.GONE
            tvDeviceSubType.visibility = View.GONE
            tvDeviationProfile.visibility = View.GONE
            spLayoutDeviationProfile.visibility = View.GONE
            tvColdstores.visibility = View.GONE
            spLayoutColdstores.visibility = View.GONE
            spLayoutDeviceGroup.visibility = View.GONE
            spLayoutDeviceSubType.visibility = View.GONE
            if (userManager.customerType.equals("SERVICE_PROVIDER")) {
                titleCustomer.visibility = View.VISIBLE
                sp_Customer_Layout.visibility = View.VISIBLE
                viewModel.onGetCustomer()
            } else {
                titleCustomer.visibility = View.GONE
                sp_Customer_Layout.visibility = View.GONE
                selectedCustomerId = userManager.customerId
                viewModel.onGetInstallationCenters(selectedCustomerId.toInt())
                viewModel.onGetUser(selectedCustomerId)
            }
        }

        spDeviceType_provisioning.text = testObject!!.device_type
        spDeviceGroup.text = testObject!!.device_group_id
        spDeviceSubType.text = testObject!!.device_sub_type_id
        et_serialNumber.text = testObject!!.serial_number
        et_HwRevision_provisioning.setText(testObject!!.hw_revision.toString())
        et_FwRevisionProvisioning.setText(testObject!!.fw_revision.toString())
        et_StartLife.text = Utils.timeStampDate(testObject!!.start_of_life!!.toLong())
        et_EndLife.text = Utils.timeStampDate(testObject!!.end_of_life!!.toLong())
        deviceId = testObject!!.device_id

        initview()

    }

    fun initview() {
        toolbar.title = getString(R.string.device_provisioning)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        btdeviceProvisioning.setOnClickListener {
            if (startServiceDate!=null && endServiceDate!= null) {
                deviceProvisioning()
            }
            else {
                AlertUtil.showSnackBar(deviceProvisioning_layout, "Please enter service dates", 2000)
            }
        }
        et_StartServices.setOnClickListener {
            datePickerDialog(et_StartServices)
        }
        et_EndServices.setOnClickListener {
            datePickerDialog(et_EndServices)
        }
    }

    private fun updateUI(screenState: ScreenState<ProvisioningDevicesState>?) {
        when (screenState) {
            ScreenState.Loading -> progressProvisioningDevice.visibility = View.VISIBLE
            is ScreenState.Render -> processLoginState(screenState.renderState)
        }
    }

    private fun processLoginState(renderState: ProvisioningDevicesState) {
        progressProvisioningDevice.visibility = View.GONE
        when (renderState) {
            ProvisioningDevicesState.HasConnectionError -> {
                AlertUtil.showSnackBar(deviceProvisioning_layout, "Connection error", 2000)
            }
            ProvisioningDevicesState.EndServiceEmpty -> {
                AlertUtil.showSnackBar(deviceProvisioning_layout, "Please enter end service", 2000)
            }
            ProvisioningDevicesState.StartServiceEmpty -> {
                AlertUtil.showSnackBar(deviceProvisioning_layout, "Please enter start service", 2000)
            }
            ProvisioningDevicesState.ProvisioningDevicesFailure -> {
                AlertUtil.showSnackBar(deviceProvisioning_layout, "Error occured", 2000)
            }
            ProvisioningDevicesState.ProvisioningDevicesSuccess -> {
                resetForm()
                val intent = Intent()
                intent.putExtra("result", "success")
                setResult(RESULT_OK, intent);
                finish()
                AlertUtil.showToast(this, "Device has been provisioned successfully !")
            }
            ProvisioningDevicesState.GetInstallationCentersSuccess -> {
                installationCenterList = viewModel.getInstallationCenterResponse.value!!
                updateInstallationCentersSpinner(installationCenterList)
            }
            ProvisioningDevicesState.GetCustomerSuccess -> {
                customerList = viewModel.getCustomerResponse.value!!
                updateCustomerSpinner(customerList)
            }
            ProvisioningDevicesState.GetInstallationsCentersFailure -> {
                AlertUtil.showSnackBar(deviceProvisioning_layout, viewModel.errorMessage, 2000)
            }
            ProvisioningDevicesState.GetCustomerFailure -> {
                AlertUtil.showSnackBar(deviceProvisioning_layout, viewModel.errorMessage, 2000)
            }
            ProvisioningDevicesState.GetDeviationProfileSuccess -> {
                updateDeviationProfile(viewModel.getDeviationProfile.value!!)
            }
            ProvisioningDevicesState.GetDeviationProfileFailure -> {
                AlertUtil.showSnackBar(deviceProvisioning_layout, viewModel.errorMessage, 2000)
            }
            ProvisioningDevicesState.GetColdstoreSuccess -> {
                updateColdstores(viewModel.getColdstores.value!!)
            }
            ProvisioningDevicesState.GetColdstoreFailure -> {
                AlertUtil.showSnackBar(deviceProvisioning_layout, viewModel.errorMessage, 2000)
            }
            ProvisioningDevicesState.GetUserSuccess ->{
                updateUserSpinner(viewModel.getUserResponse.value!!)
            }
            ProvisioningDevicesState.GetUserFailure ->{
                AlertUtil.showSnackBar(deviceProvisioning_layout, viewModel.errorMessage, 2000)
            }
        }
    }

    private fun updateInstallationCentersSpinner(installationCenterList: ArrayList<InstallationCenterRes>) {

        val adapter = CentersListAdapter(this, installationCenterList)
        spInstallationCenters.adapter = adapter
        spInstallationCenters.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                        selectedInstallationId = installationCenterList[position].installation_center_id!!.toString()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Another interface callback
                    }
                }
    }

    private fun updateCustomerSpinner(customerList: ArrayList<CustomerRes>) {
        val adapter = CustomerListAdapter(this, customerList)
        sp_Customer.adapter = adapter
        sp_Customer.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                        selectedCustomerId = customerList[position].customer_id!!.toInt().toString()
                        viewModel.onGetInstallationCenters(selectedCustomerId.toInt())
                        viewModel.onGetDeviationProfile(selectedCustomerId.toInt())
                        viewModel.onGetColdstore(selectedCustomerId.toInt())
                        viewModel.onGetUser(selectedCustomerId)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Another interface callback
                    }
                }
    }

    private fun updateUserSpinner(userList: ArrayList<UserDataRes>) {
        val adapter = UserDropdownAdapter(this, userList)
        spUsers.adapter = adapter
        spUsers.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                        selectedUserId = userList[position].user_id!!.toInt().toString()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Another interface callback
                    }
                }
    }

    private fun updateDeviationProfile(deviationProfileList: ArrayList<DeviationProfileRes>) {

        val adapter = DeviationDropdownAdapter(this, deviationProfileList)
        spDeviationProfiles.adapter = adapter
        spDeviationProfiles.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                        selectedDeviationId = deviationProfileList[position].deviation_profile_id!!.toString()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Another interface callback
                    }
                }
    }

    private fun updateColdstores(coldstoreListList: ArrayList<ColdstoreRes>) {

        val adapter = ColdstoreDropdownAdapter(this, coldstoreListList)
        spColdstores.adapter = adapter
        spColdstores.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                        selectedColdstoreId = coldstoreListList[position].cold_store_id!!.toString()
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
                        R.id.et_StartServices -> {
                            startServiceDate = date.time
                            et_StartServices.text = fmtOut.format(date)
                        }
                        R.id.et_EndServices -> {
                            endServiceDate = date.time
                            et_EndServices.text = fmtOut.format(date)
                        }
                    }

                }, year, month, day
        )

        val datePicker = dpDialog.datePicker
        val calendar = Calendar.getInstance()
        datePicker.minDate = calendar!!.timeInMillis

        dpDialog.show()

    }

    private fun resetForm() {
        et_StartServices.setText("")
        et_EndServices.setText("")
    }

    private fun deviceProvisioning() {
        val diff = startServiceDate!!.compareTo(endServiceDate!!)
        if (diff < 0){
            viewModel.onDeviceProvisioning(et_HwRevision_provisioning.text.toString(), et_FwRevisionProvisioning.text.toString(), startServiceDate.toString(),
                    endServiceDate.toString(), selectedCustomerId, selectedInstallationId, deviceId!!, selectedColdstoreId, selectedDeviationId, selectedUserId,this
            )

        }
        else{
            AlertUtil.showSnackBar(deviceProvisioning_layout, "Please enter valid service dates", 2000)
        }
        
    }
}