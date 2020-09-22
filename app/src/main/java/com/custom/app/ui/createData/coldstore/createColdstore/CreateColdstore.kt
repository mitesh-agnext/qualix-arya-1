package com.custom.app.ui.createData.coldstore.createColdstore

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
import com.custom.app.ui.createData.adapters.*
import com.custom.app.ui.createData.foodType.list.FoodTypeListInteractor
import com.custom.app.ui.createData.foodType.list.FoodTypeListRes
import com.custom.app.ui.createData.profile.list.ProfileListInteractor
import com.custom.app.ui.createData.profile.list.ProfileListRes
import com.custom.app.ui.createData.profileType.list.ProfileTypeListInteractor
import com.custom.app.ui.createData.profileType.list.ProfileTypeListRes
import com.custom.app.ui.createData.region.RegionSiteInteractor
import com.custom.app.ui.createData.region.site.create.RegionRes
import com.custom.app.ui.createData.region.site.create.adapters.RegionDropdownAdapter
import com.custom.app.ui.createData.region.site.list.SiteListRes
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerRes
import com.custom.app.ui.user.list.UserDataRes
import kotlinx.android.synthetic.main.activity_create_coldstore.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.util.*
import javax.inject.Inject

class CreateColdstore : BaseActivity() {

    @Inject
    lateinit var customerInteractor: CustomerInteractor

    @Inject
    lateinit var coldstoreInteractor: ColdstoreInteractor

    private lateinit var viewModel: CreateCenterViewModel
    private var selectedUserId: Int = 0
    private var selectedCustomerId: Int = 0
    private var selectedRegionId: Int = 0
    private var selectedProfileId: Int = 0
    private var selectedProfileTypeId: Int = 0
    private var selectedFoodTypeId: Int = 0
    private var selectedSiteId: Int = 0

    private lateinit var profileList: ArrayList<ProfileListRes>
    private lateinit var profileTypeList: ArrayList<ProfileTypeListRes>
    private lateinit var foodTypeList: ArrayList<FoodTypeListRes>
    private lateinit var siteList: ArrayList<SiteListRes>
    private lateinit var customerList: ArrayList<CustomerRes>
    private lateinit var userList: ArrayList<UserDataRes>
    private lateinit var regionList: ArrayList<RegionRes>

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_coldstore)

        viewModel = ViewModelProvider(this, CreateCenterViewModelFactory(coldstoreInteractor, customerInteractor, ProfileListInteractor(), ProfileTypeListInteractor(),
                FoodTypeListInteractor(), RegionSiteInteractor(userManager)))[CreateCenterViewModel::class.java]

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
            viewModel.onGetProfile(selectedCustomerId)
            viewModel.onGetProfileType(selectedCustomerId)
            viewModel.onGetFoodType(selectedCustomerId)
            viewModel.onGetRegion(selectedCustomerId)
            viewModel.onGetUser(selectedCustomerId)
        }
    }

    fun initView() {
        toolbar.title = getString(R.string.create_coldstore)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        btCreateColdstore.setOnClickListener {
            addCenter()
        }
    }

    private fun updateUI(screenState: ScreenState<CreateColdstoreState>?) {
        when (screenState) {
            ScreenState.Loading -> progressAddDevices.visibility = View.VISIBLE
            is ScreenState.Render -> processLoginState(screenState.renderState)
        }
    }

    private fun processLoginState(renderState: CreateColdstoreState) {
        progressAddDevices.visibility = View.GONE
        when (renderState) {
            CreateColdstoreState.ColdstoreNameEmpty -> {
                AlertUtil.showSnackBar(coldstoreLayout, "Please enter coldstore name", 2000)
            }
            CreateColdstoreState.GetProfileError -> {
                AlertUtil.showSnackBar(coldstoreLayout, "Please select profile", 2000)
            }
            CreateColdstoreState.GetProfileSuccess -> {
                profileList = viewModel.getProfileResponse.value!!
                updateProfileSpinner(profileList)
            }
            CreateColdstoreState.GetProfileTypeError -> {
                AlertUtil.showSnackBar(coldstoreLayout, "Please select profile type", 2000)
            }
            CreateColdstoreState.GetProfileTypeSuccess -> {
                profileTypeList = viewModel.getProfileTypeResponse.value!!
                updateProfileTypeSpinner(profileTypeList)
            }
            CreateColdstoreState.GetFoodTypeError -> {
                AlertUtil.showSnackBar(coldstoreLayout, "Please select food type", 2000)
            }
            CreateColdstoreState.GetFoodTypeSuccess -> {
                foodTypeList = viewModel.getFoodTypeResponse.value!!
                updateFoodTypeSpinner(foodTypeList)
            }
            CreateColdstoreState.GetRegionError -> {
                AlertUtil.showSnackBar(coldstoreLayout, "Please select region", 2000)
            }
            CreateColdstoreState.GetRegionSuccess -> {
                regionList = viewModel.getRegionResponse.value!!
                updateRegionSpinner(regionList)
            }
            CreateColdstoreState.GetSiteError -> {
                AlertUtil.showSnackBar(coldstoreLayout, "Please select site", 2000)
            }
            CreateColdstoreState.GetSiteSuccess -> {
                siteList = viewModel.getSiteResponse.value!!
                updateSiteSpinner(siteList)
            }
            CreateColdstoreState.GetCustomerError -> {
                AlertUtil.showSnackBar(coldstoreLayout, "Unable to get customers", 2000)
            }
            CreateColdstoreState.GetCustomerSuccess -> {
                customerList = viewModel.getCustomerList.value!!
                updateCustomerSpinner(customerList)
            }
            CreateColdstoreState.GetUserError -> {
                AlertUtil.showSnackBar(coldstoreLayout, "Unable to get users", 2000)
            }
            CreateColdstoreState.GetUserSuccess -> {
                userList = viewModel.getUserList.value!!
                updateUserSpinner(userList)
            }
            CreateColdstoreState.CreateColdstoreFailure -> {
                AlertUtil.showSnackBar(coldstoreLayout, "Unable to add coldstore", 2000)
            }
            CreateColdstoreState.CreateColdstoreSuccess -> {
                resetForm()
                AlertUtil.showActionAlertDialog(this, "", "New coldstore has been added successfully !",
                        false, getString(R.string.btn_ok)) { dialog: DialogInterface?, which: Int ->
                    val intent = Intent()
                    intent.putExtra("result", "success")
                    setResult(RESULT_OK, intent);
                    finish()
                }
            }
        }
    }

    private fun updateProfileSpinner(profileList: ArrayList<ProfileListRes>) {

        val adapter = ProfileDropdownAdapter(this, profileList)
        spProfile_coldstore.adapter = adapter
        spProfile_coldstore.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                            parent: AdapterView<*>, view: View, position: Int, id: Long) {

                        selectedProfileId = profileList[position].profile_id!!

                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Another interface callback
                    }
                }
    }

    private fun updateProfileTypeSpinner(profileTypeList: ArrayList<ProfileTypeListRes>) {

        val adapter = ProfileTypeDropdownAdapter(this, profileTypeList)
        spProfileType_coldstore.adapter = adapter
        spProfileType_coldstore.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View,
                            position: Int,
                            id: Long) {

                        selectedProfileTypeId = profileTypeList[position].profile_type_id!!

                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Another interface callback
                    }
                }
    }

    private fun updateFoodTypeSpinner(foodTypeList: ArrayList<FoodTypeListRes>) {

        val adapter = FoodTypeDropdownAdapter(this, foodTypeList)
        spFoodType_coldstore.adapter = adapter
        spFoodType_coldstore.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View,
                            position: Int,
                            id: Long) {

                        selectedFoodTypeId = foodTypeList[position].food_type_id!!

                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Another interface callback
                    }
                }
    }

    private fun updateSiteSpinner(siteList: ArrayList<SiteListRes>) {

        val adapter = SiteDropdownAdapter(this, siteList)
        spSite_coldstore.adapter = adapter
        spSite_coldstore.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View,
                            position: Int,
                            id: Long) {

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
                    override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View,
                            position: Int,
                            id: Long) {

                        selectedCustomerId = customerList[position].customer_id!!.toInt()

                        viewModel.onGetProfile(selectedCustomerId)
                        viewModel.onGetProfileType(selectedCustomerId)
                        viewModel.onGetFoodType(selectedCustomerId)
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
        spRegion_coldstore.adapter = adapter
        spRegion_coldstore.onItemSelectedListener =
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
                selectedProfileId,
                selectedProfileTypeId,
                selectedFoodTypeId,
                selectedSiteId,
                selectedRegionId,
                selectedCustomerId,
                selectedUserId,
                et_note_coldstore.text.toString()
        )
    }
}