package com.custom.app.ui.createData.coldstore.updateColdstore

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
import com.custom.app.ui.createData.coldstore.coldstoreList.ColdstoreRes
import com.custom.app.ui.createData.coldstore.createColdstore.ColdstoreInteractor
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
import com.custom.app.ui.user.list.UserInteractor
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_add_device.progressAddDevices
import kotlinx.android.synthetic.main.activity_create_coldstore.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.util.*
import javax.inject.Inject

class UpdateColdstore : BaseActivity() {

    @Inject
    lateinit var userInteractor: UserInteractor

    @Inject
    lateinit var customerInteractor: CustomerInteractor

    @Inject
    lateinit var coldstoreInteractor: ColdstoreInteractor

    private lateinit var viewModel: UpdateCenterViewModel
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
    var centerId: Int? = null
    var testObject: ColdstoreRes? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_coldstore)

        val selectObject = intent.getStringExtra("selectObject")
        val gson = Gson()
        if (selectObject != null) {
            val type = object : TypeToken<ColdstoreRes>() {}.type
            testObject = gson.fromJson(selectObject, type)
        }

        viewModel = ViewModelProvider(this,
                UpdateCenterViewModelFactory(
                        coldstoreInteractor, customerInteractor, ProfileListInteractor(),
                        ProfileTypeListInteractor(), FoodTypeListInteractor(), RegionSiteInteractor(userManager)))[UpdateCenterViewModel::class.java]

        viewModel.updateCenterState.observe(::getLifecycle, ::updateUI)
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
        toolbar.title = getString(R.string.update_coldstore)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        btCreateColdstore.text = "Update coldstore"
        btCreateColdstore.setOnClickListener {
            updateCenter()
        }
    }

    private fun updateUI(screenState: ScreenState<UpdateColdstoreState>?) {
        when (screenState) {
            ScreenState.Loading -> progressAddDevices.visibility = View.VISIBLE
            is ScreenState.Render -> processLoginState(screenState.renderState)
        }
    }

    private fun processLoginState(renderState: UpdateColdstoreState) {
        progressAddDevices.visibility = View.GONE
        when (renderState) {
            UpdateColdstoreState.UpdateColdstoreFailure -> {
                AlertUtil.showToast(this, "Unable to update coldstore")
            }
            UpdateColdstoreState.UpdateColdstoreSuccess -> {
                resetForm()
                val intent = Intent()
                intent.putExtra("result", "success")
                setResult(RESULT_OK, intent);
                finish()
                AlertUtil.showToast(this, "Coldstore has been updated successfully !")
            }
            UpdateColdstoreState.ColdstoreNameEmpty -> {
                AlertUtil.showToast(this, "Please enter coldstore name")
            }
            UpdateColdstoreState.GetProfileError -> {
                AlertUtil.showToast(this, "Please select profile")
            }
            UpdateColdstoreState.GetProfileSuccess -> {
                profileList = viewModel.getProfileResponse.value!!
                updateProfileSpinner(profileList)
            }
            UpdateColdstoreState.GetProfileTypeError -> {
                AlertUtil.showToast(this, "Please select profile type")
            }
            UpdateColdstoreState.GetProfileTypeSuccess -> {
                profileTypeList = viewModel.getProfileTypeResponse.value!!
                updateProfileTypeSpinner(profileTypeList)
            }
            UpdateColdstoreState.GetFoodTypeError -> {
                AlertUtil.showToast(this, "Please select food type")
            }
            UpdateColdstoreState.GetFoodTypeSuccess -> {
                foodTypeList = viewModel.getFoodTypeResponse.value!!
                updateFoodTypeSpinner(foodTypeList)
            }
            UpdateColdstoreState.GetRegionError -> {
                AlertUtil.showToast(this, "Please select region")
            }
            UpdateColdstoreState.GetRegionSuccess -> {
                regionList = viewModel.getRegionResponse.value!!
                updateRegionSpinner(regionList)
            }
            UpdateColdstoreState.GetSiteError -> {
                AlertUtil.showToast(this, "Please select site")
            }
            UpdateColdstoreState.GetSiteSuccess -> {
                siteList = viewModel.getSiteResponse.value!!
                updateSiteSpinner(siteList)
            }
            UpdateColdstoreState.GetCustomerError -> {
                AlertUtil.showToast(this, "Unable to get customers")
            }
            UpdateColdstoreState.GetCustomerSuccess -> {
                customerList = viewModel.getCustomerList.value!!
                updateCustomerSpinner(customerList)
            }
            UpdateColdstoreState.GetUserError -> {
                AlertUtil.showToast(this, "Unable to get users")
            }
            UpdateColdstoreState.GetUserSuccess -> {
                userList = viewModel.getUserList.value!!
                updateUserSpinner(userList)
            }
        }
    }

    private fun updateProfileSpinner(profileList: ArrayList<ProfileListRes>) {

        val adapter = ProfileDropdownAdapter(this, profileList)
        spProfile_coldstore.adapter = adapter
        spProfile_coldstore.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View,
                            position: Int,
                            id: Long) {

                        if (profileList.isNotEmpty()) {
                            for (i in 0 until profileList.size) {
                                if (profileList[i].profile_name == testObject!!.profile_name) {
                                    spProfile_coldstore.setSelection(i)
                                }
                            }
                        }
                        selectedProfileId = profileList[position].profile_id!!
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
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
                        if (profileTypeList.isNotEmpty()) {
                            for (i in 0 until profileTypeList.size) {
                                if (profileTypeList[i].profile_type_name == testObject!!.profile_type_name) {
                                    spProfileType_coldstore.setSelection(i)
                                }
                            }
                        }
                        selectedProfileTypeId = profileTypeList[position].profile_type_id!!

                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
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
                        if (foodTypeList.isNotEmpty()) {
                            for (i in 0 until foodTypeList.size) {
                                if (foodTypeList[i].food_type_name == testObject!!.profile_food_type_name) {
                                    spFoodType_coldstore.setSelection(i)
                                }
                            }
                        }
                        selectedFoodTypeId = foodTypeList[position].food_type_id!!

                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
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
                        if (siteList.isNotEmpty()) {
                            for (i in 0 until siteList.size) {
                                if (siteList[i].site_name == testObject!!.site_name) {
                                    spSite_coldstore.setSelection(i)
                                }
                            }
                        }
                        selectedSiteId = siteList[position].site_id!!
                        updateView(viewModel.singleCenter)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
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
                        if (customerList.isNotEmpty()) {
                            for (i in 0 until customerList.size) {
                                if (customerList[i].customer_id == testObject!!.customer_id) {
                                    spCustomers_createCenter.setSelection(i)
                                }
                            }
                        }
                        selectedCustomerId = customerList[position].customer_id!!.toInt()
                        viewModel.onGetProfile(selectedCustomerId)
                        viewModel.onGetRegion(selectedCustomerId)
                        viewModel.onGetFoodType(selectedCustomerId)
                        viewModel.onGetProfileType(selectedCustomerId)
                        viewModel.onGetUser(selectedCustomerId)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
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
                        if (userList.isNotEmpty()) {
                            for (i in 0 until userList.size) {
                                if (userList[i].user_id == testObject!!.user_id) {
                                    spUsers_createCenter.setSelection(i)
                                }
                            }
                        }
                        selectedUserId = userList[position].user_id!!.toInt()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }
    }

    private fun updateRegionSpinner(regionList: ArrayList<RegionRes>) {

        val adapter = RegionDropdownAdapter(this, regionList)
        spRegion_coldstore.adapter = adapter
        spRegion_coldstore.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                        if (regionList.isNotEmpty()) {
                            for (i in 0 until regionList.size) {
                                if (regionList[i].region_id == testObject!!.region_id) {
                                    spRegion_coldstore.setSelection(i)
                                }
                            }
                        }
                        selectedRegionId = regionList[position].region_id!!.toInt()
                        viewModel.onGetSite("", selectedRegionId)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }
    }

    private fun resetForm() {
        et_intallationName.setText("")
    }

    private fun updateView(singleDevice: ColdstoreRes) {
        et_intallationName.setText(testObject!!.inst_center_name)
        et_note_coldstore.setText(testObject!!.notes)
        centerId = testObject!!.cold_store_id
    }

    private fun updateCenter() {
        viewModel.onUpdateCenter(centerId!!, et_intallationName.text.toString(), selectedCustomerId, selectedProfileId, selectedProfileTypeId,
                selectedFoodTypeId, selectedSiteId, selectedRegionId, selectedUserId, et_note_coldstore.text.toString()
        )
    }
}