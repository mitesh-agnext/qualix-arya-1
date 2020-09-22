package com.custom.app.ui.createData.instlCenter.createInstallationCenter

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
import com.custom.app.ui.createData.region.RegionSiteInteractor
import com.custom.app.ui.createData.region.site.create.RegionRes
import com.custom.app.ui.createData.region.site.create.adapters.RegionDropdownAdapter
import com.custom.app.ui.createData.region.site.list.SiteListRes
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerRes
import com.custom.app.ui.user.list.UserDataRes
import com.custom.app.ui.user.list.UserInteractor
import kotlinx.android.synthetic.main.activity_create_intl_centers.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.util.*
import javax.inject.Inject

class CreateIntlCenters : BaseActivity() {

    @Inject
    lateinit var userInteractor: UserInteractor

    @Inject
    lateinit var customerInteractor: CustomerInteractor

    @Inject
    lateinit var installationCenterInteractor: InstallationCenterInteractor

    @Inject
    lateinit var regionSiteInteractor: RegionSiteInteractor

    private lateinit var viewModel: CreateCenterViewModel
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

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_intl_centers)

        viewModel = ViewModelProvider(this, CreateCenterViewModelFactory(installationCenterInteractor, customerInteractor, regionSiteInteractor))[CreateCenterViewModel::class.java]

        viewModel.createCenterState.observe(::getLifecycle, ::updateUI)
        initView()

        if (userManager.customerType.equals("SERVICE_PROVIDER")) {
            titleCustomers.visibility = View.VISIBLE
            spLayoutCustomer.visibility = View.VISIBLE
            viewModel.onGetCustomer()
        } else {
            titleCustomers.visibility = View.GONE
            spLayoutCustomer.visibility = View.GONE
            selectedCustomerId = userManager.customerId.toInt()
            viewModel.onGetRegion(selectedCustomerId)
            viewModel.onGetUser(selectedCustomerId)
        }
        viewModel.onGetCenterType()

    }

    fun initView() {
        toolbar.title = getString(R.string.create_installation_centers)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        btCreateInstallationCenter.setOnClickListener {
            addCenter()
        }
    }

    private fun updateUI(screenState: ScreenState<CreateInstallationCentersState>?) {
        when (screenState) {
            ScreenState.Loading -> progressAddDevices.visibility = View.VISIBLE
            is ScreenState.Render -> processLoginState(screenState.renderState)
        }
    }

    private fun processLoginState(renderState: CreateInstallationCentersState) {
        progressAddDevices.visibility = View.GONE
        when (renderState) {
            CreateInstallationCentersState.InstallationCenterNameEmpty -> {
                AlertUtil.showSnackBar(
                        listLayout_center,
                        "Please enter installation center name",
                        2000
                )
            }
            CreateInstallationCentersState.GetCommercialTypeError -> {
                AlertUtil.showSnackBar(
                        listLayout_center,
                        viewModel.errorMessage,
                        2000
                )
            }
            CreateInstallationCentersState.GetCommercialTypeSuccess -> {
                centerTypeList = viewModel.getCenterTypeResponse.value!!
                updateCenterTypeSpinner(centerTypeList)
            }
            CreateInstallationCentersState.GetRegionError -> {
                AlertUtil.showSnackBar(
                        listLayout_center,
                        viewModel.errorMessage,
                        2000
                )
            }
            CreateInstallationCentersState.GetRegionSuccess -> {
                regionList = viewModel.getRegionResponse.value!!
                updateRegionSpinner(regionList)
            }
            CreateInstallationCentersState.GetSiteError -> {
                AlertUtil.showSnackBar(
                        listLayout_center,
                        viewModel.errorMessage,
                        2000
                )
            }
            CreateInstallationCentersState.GetSiteSuccess -> {
                siteList = viewModel.getSiteResponse.value!!
                updateSiteSpinner(siteList)
            }
            CreateInstallationCentersState.GetCustomerError -> {
                AlertUtil.showSnackBar(
                        listLayout_center,
                        viewModel.errorMessage,
                        2000
                )
            }
            CreateInstallationCentersState.GetCustomerSuccess -> {
                customerList = viewModel.getCustomerList.value!!
                updateCustomerSpinner(customerList)
            }
            CreateInstallationCentersState.GetUserError -> {
                AlertUtil.showSnackBar(
                        listLayout_center,
                        viewModel.errorMessage,
                        2000
                )
            }
            CreateInstallationCentersState.GetUserSuccess -> {
                userList = viewModel.getUserList.value!!
                updateUserSpinner(userList)
            }
            CreateInstallationCentersState.CreateInstallationCenterFailure -> {
                AlertUtil.showSnackBar(
                        listLayout_center,
                        viewModel.errorMessage,
                        2000
                )
            }
            CreateInstallationCentersState.CreateInstallationCenterSuccess -> {
                resetForm()
                AlertUtil.showActionAlertDialog(this, "", "New installation center has been added successfully !",
                        false, getString(R.string.btn_ok)) { dialog: DialogInterface?, which: Int ->
                    val intent = Intent()
                    intent.putExtra("result", "success")
                    setResult(RESULT_OK, intent);
                    finish()
                }
            }
        }
    }

    private fun updateCenterTypeSpinner(centerTypeList: ArrayList<InstallationCenterTypeRes>) {

        val adapter = CenterTypeDropdownAdapter(this, centerTypeList)
        spInstallationCentersType.adapter = adapter
        spInstallationCentersType.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                            parent: AdapterView<*>, view: View, position: Int, id: Long) {

                        selectedCenterTypeId = centerTypeList[position].commercial_location_type_id!!

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
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                        selectedSiteId = siteList[position].site_id!!

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
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                        selectedCustomerId = customerList[position].customer_id!!.toInt()
                        viewModel.onGetRegion(selectedCustomerId)
                        viewModel.onGetUser(selectedCustomerId)

                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                    }
                }
    }

    private fun updateUserSpinner(userList: ArrayList<UserDataRes>) {

        val adapter = UserDropdownAdapter(this, userList)
        spUsers_createCenter.adapter = adapter
        spUsers_createCenter.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                        selectedUserId = userList[position].user_id!!.toInt()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
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

    private fun addCenter() {
        viewModel.onCreateCenter(
                et_intallationName.text.toString(),
                selectedCenterTypeId,
                selectedSiteId,
                selectedRegionId,
                selectedCustomerId,
                selectedUserId,
                et_note_center.text.toString()
        )
    }

}