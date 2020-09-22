package com.custom.app.ui.createData.instlCenter.updateCenter

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.lifecycle.ViewModelProvider
import com.base.app.ui.base.BaseActivity
import com.core.app.util.AlertUtil
import com.custom.app.CustomApp
import com.custom.app.R
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.createData.adapters.CenterTypeDropdownAdapter
import com.custom.app.ui.createData.adapters.CustomerDropdownAdapter
import com.custom.app.ui.createData.adapters.SiteDropdownAdapter
import com.custom.app.ui.createData.adapters.UserDropdownAdapter
import com.custom.app.ui.createData.instlCenter.InstallationCenterInteractor
import com.custom.app.ui.createData.instlCenter.createInstallationCenter.InstallationCenterTypeRes
import com.custom.app.ui.createData.region.RegionSiteInteractor
import com.custom.app.ui.createData.region.site.create.RegionRes
import com.custom.app.ui.createData.region.site.create.adapters.RegionDropdownAdapter
import com.custom.app.ui.createData.region.site.list.SiteListRes
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerRes
import com.custom.app.ui.device.assign.InstallationCenterRes
import com.custom.app.ui.user.list.UserDataRes
import com.custom.app.ui.user.list.UserInteractor
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_add_device.progressAddDevices
import kotlinx.android.synthetic.main.activity_create_intl_centers.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.util.*
import javax.inject.Inject

class UpdateIntlCenters : BaseActivity() {

    @Inject
    lateinit var userInteractor: UserInteractor

    @Inject
    lateinit var customerInteractor: CustomerInteractor

    @Inject
    lateinit var installationCenterInteractor: InstallationCenterInteractor

    @Inject
    lateinit var regionSiteInteractor: RegionSiteInteractor

    private lateinit var viewModel: UpdateCenterViewModel
    private var selectedUserId: Int = 0
    private var selectedCustomerId: Int = 0
    private var selectedRegionId: Int = 0
    private var selectedCenterTypeId: Int = 0
    private var selectedSiteId: Int = 0

    private lateinit var centerTypeList: ArrayList<InstallationCenterTypeRes>
    private lateinit var siteList: ArrayList<SiteListRes>
    private lateinit var customerList: ArrayList<CustomerRes>
    private lateinit var userList: ArrayList<UserDataRes>
    private lateinit var regionList: ArrayList<RegionRes>

    var centerId: Int? = null
    var testObject: InstallationCenterRes? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_intl_centers)

        val selectObject = intent.getStringExtra("selectObject")
        val gson = Gson()
        if (selectObject != null) {
            val type = object : TypeToken<InstallationCenterRes>() {}.type
            testObject = gson.fromJson(selectObject, type)
        }

        viewModel = ViewModelProvider(this,
                UpdateCenterViewModelFactory(installationCenterInteractor, customerInteractor, regionSiteInteractor))[UpdateCenterViewModel::class.java]

        viewModel.updateCenterState.observe(::getLifecycle, ::updateUI)
        initView()

        if (userManager.customerType.equals("SERVICE_PROVIDER")) {
            titleCustomers.visibility = View.VISIBLE
            spLayoutCustomer.visibility = View.VISIBLE
            viewModel.onGetCustomer()
        }
        else {
            titleCustomers.visibility = View.GONE
            spLayoutCustomer.visibility = View.GONE
            selectedCustomerId = userManager.customerId.toInt()
            viewModel.onGetRegion(selectedCustomerId)
            viewModel.onGetUser(selectedCustomerId)
        }

    }

    fun initView() {
        toolbar.title = getString(R.string.update_installation_centers)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        btCreateInstallationCenter.text = "Update installation center"
        btCreateInstallationCenter.setOnClickListener {
            updateCenter()
        }
    }

    private fun updateUI(screenState: ScreenState<UpdateCenterState>?) {
        when (screenState) {
            ScreenState.Loading -> progressAddDevices.visibility = View.VISIBLE
            is ScreenState.Render -> processLoginState(screenState.renderState)
        }
    }

    private fun processLoginState(renderState: UpdateCenterState) {
        progressAddDevices.visibility = View.GONE
        when (renderState) {
            UpdateCenterState.GetCommercialTypeError -> {
//                AlertUtil.showToast(this, "Please select installation center type")
            }
            UpdateCenterState.UpdateInstallationCenterFailure -> {
                AlertUtil.showSnackBar(listLayout_center, "Unable to update installation center", 2000)
            }
            UpdateCenterState.UpdateInstallationCenterSuccess -> {
                resetForm()
                AlertUtil.showActionAlertDialog(this, "", "Installation center has been updated successfully !",
                        false, getString(R.string.btn_ok)) { dialog: DialogInterface?, which: Int ->
                    val intent = Intent()
                    intent.putExtra("result", "success")
                    setResult(RESULT_OK, intent);
                    finish()
                }

            }
            UpdateCenterState.InstallationCenterNameEmpty -> {
                AlertUtil.showSnackBar(listLayout_center, "Please enter installation center name", 2000)
            }
            UpdateCenterState.GetCommercialTypeSuccess -> {
                centerTypeList = viewModel.getCenterTypeResponse.value!!
                updateCenterTypeSpinner(centerTypeList)
            }
            UpdateCenterState.GetRegionError -> {
                AlertUtil.showSnackBar(listLayout_center, "Please select region", 2000)
            }
            UpdateCenterState.GetRegionSuccess -> {
                regionList = viewModel.getRegionResponse.value!!
                updateRegionSpinner(regionList)
            }
            UpdateCenterState.GetSiteError -> {
                AlertUtil.showSnackBar(listLayout_center, "Please select site", 2000)
            }
            UpdateCenterState.GetSiteSuccess -> {
                siteList = viewModel.getSiteResponse.value!!
                updateSiteSpinner(siteList)
            }
            UpdateCenterState.GetCustomerError -> {
                AlertUtil.showSnackBar(listLayout_center, "Unable to get customers", 2000)
            }
            UpdateCenterState.GetCustomerSuccess -> {
                customerList = viewModel.getCustomerList.value!!
                updateCustomerSpinner(customerList)
            }
            UpdateCenterState.GetUserError -> {
                AlertUtil.showSnackBar(listLayout_center, "Unable to get users", 2000)
            }
            UpdateCenterState.GetUserSuccess -> {
                userList = viewModel.getUserList.value!!
                updateUserSpinner(userList)
            }
        }
    }

    private fun updateCenterTypeSpinner(centerTypeList: ArrayList<InstallationCenterTypeRes>) {

        val adapter = CenterTypeDropdownAdapter(this, centerTypeList)
        spInstallationCentersType.adapter = adapter
        spInstallationCentersType.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View,
                            position: Int,
                            id: Long) {

                        selectedCenterTypeId = centerTypeList[position].commercial_location_type_id!!
                        updateView(viewModel.singleCenter)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Another interface callback
                    }
                }
    }

    private fun updateSiteSpinner(siteList: ArrayList<SiteListRes>) {

        val adapter = SiteDropdownAdapter(this, siteList)
        spSite_center.adapter = adapter
        spSite_center.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View,
                            position: Int,
                            id: Long) {

                        selectedSiteId = siteList[position].site_id!!
                        viewModel.onGetCenterType(selectedRegionId)


                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Another interface callback
                    }
                }
    }

    private fun updateCustomerSpinner(customerList: ArrayList<CustomerRes>) {

        val adapter = CustomerDropdownAdapter(this, customerList)
        spCustomers_createCenter.adapter = adapter
        spCustomers_createCenter.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View,
                            position: Int,
                            id: Long) {


                        selectedCustomerId = customerList[position].customer_id!!.toInt()
                        viewModel.onGetRegion(selectedCustomerId)
                        viewModel.onGetUser(selectedCustomerId)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Another interface callback
                    }
                }
    }

    private fun updateUserSpinner(userList: ArrayList<UserDataRes>) {

        val adapter = UserDropdownAdapter(this, userList)
        spUsers_createCenter.adapter = adapter
        spUsers_createCenter.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View,
                            position: Int,
                            id: Long) {

                        selectedUserId = userList[position].user_id!!.toInt()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Another interface callback
                    }
                }
    }

    private fun updateRegionSpinner(regionList: ArrayList<RegionRes>) {

        val adapter = RegionDropdownAdapter(this, regionList)
        spRegion_center.adapter = adapter
        spRegion_center.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                        selectedRegionId = regionList[position].region_id!!.toInt()
                        viewModel.onGetSite("", selectedRegionId)

                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }
    }

    private fun resetForm() {
        et_intallationName.setText("")
    }

    private fun updateView(singleDevice: InstallationCenterRes) {

        if (siteList.isNotEmpty()) {
            for (i in 0 until siteList.size) {
                spSite_center.setSelection(siteList[i].site_name!!.indexOf(testObject!!.site_name!!))
            }

        }

        if (customerList.isNotEmpty()) {
            for (i in 0 until customerList.size) {
                spCustomers_createCenter.setSelection(customerList[i].customer_id!!.toString().indexOf(testObject!!.customer_id.toString()))
            }
        }
        if (centerTypeList.isNotEmpty()) {
            for (i in 0 until centerTypeList.size) {
                spInstallationCentersType.setSelection(centerTypeList[i].inst_center_type_desc!!.indexOf(testObject!!.commercial_location_type_desc!!))
            }
        }

        if (regionList.isNotEmpty()) {
            for (i in 0 until regionList.size) {
                spRegion_center.setSelection(regionList[i].region_id.toString().indexOf(testObject!!.region_id.toString()))
            }
        }

        if (userList.isNotEmpty()) {
            for (i in 0 until userList.size) {
                spUsers_createCenter.setSelection(userList[i].user_id.toString().indexOf(testObject!!.user_id.toString()))
            }
        }

        et_intallationName.setText(testObject!!.inst_center_name)
        et_note_center.setText(testObject!!.notes)

        centerId = testObject!!.installation_center_id

    }

    private fun updateCenter() {
        viewModel.onUpdateCenter(
                centerId!!,
                et_intallationName.text.toString(),
                selectedCenterTypeId,
                selectedCustomerId,
                selectedSiteId,
                selectedRegionId,
                selectedUserId,
                et_note_center.text.toString()
        )
    }
}