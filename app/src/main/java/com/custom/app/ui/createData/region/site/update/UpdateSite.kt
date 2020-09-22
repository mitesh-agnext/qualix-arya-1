package com.custom.app.ui.createData.region.site.update

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
import com.custom.app.data.model.country.CityRes
import com.custom.app.data.model.country.CountryRes
import com.custom.app.data.model.country.StateRes
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.createData.adapters.CustomerDropdownAdapter
import com.custom.app.ui.createData.adapters.UserDropdownAdapter
import com.custom.app.ui.createData.region.RegionSiteInteractor
import com.custom.app.ui.createData.region.site.create.RegionRes
import com.custom.app.ui.createData.region.site.create.adapters.CityDropdownAdapter
import com.custom.app.ui.createData.region.site.create.adapters.CountryDropdownAdapter
import com.custom.app.ui.createData.region.site.create.adapters.RegionDropdownAdapter
import com.custom.app.ui.createData.region.site.create.adapters.StateDropdownAdapter
import com.custom.app.ui.createData.region.site.list.SiteListRes
import com.custom.app.ui.customer.list.CustomerRes
import com.custom.app.ui.user.list.UserDataRes
import com.custom.app.ui.user.list.UserInteractor
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_create_intl_centers.progressAddDevices
import kotlinx.android.synthetic.main.activity_create_profile_type.*
import kotlinx.android.synthetic.main.activity_site.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.util.*
import javax.inject.Inject

class UpdateSite : BaseActivity() {

    @Inject
    lateinit var userInteractor: UserInteractor

    @Inject
    lateinit var regionSiteInteractor: RegionSiteInteractor
    private lateinit var viewModel: UpdateSiteViewModel
    private var selectedregionId: Int = 0
    private var selectedCustomerId: Int = 0
    private var selectedCountryId: Int = 0
    private var selectedStateId: Int = 0
    private var selectedCityId: Int = 0
    private var selectedUserId: Int = 0
    private var geoLocation: String? = "1,1"

    private lateinit var regionList: ArrayList<RegionRes>
    private lateinit var countryList: ArrayList<CountryRes>
    private lateinit var stateList: ArrayList<StateRes>
    private lateinit var cityList: ArrayList<CityRes>
    private lateinit var customerList: ArrayList<CustomerRes>
    private lateinit var userList: ArrayList<UserDataRes>

    var siteId: Int? = null
    var testObject: SiteListRes? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_site)

        val selectObject = intent.getStringExtra("selectObject")
        val gson = Gson()
        if (selectObject != null) {
            val type = object : TypeToken<SiteListRes>() {}.type
            testObject = gson.fromJson(selectObject, type)
        }

        viewModel = ViewModelProvider(this, UpdateSiteViewModelFactory(regionSiteInteractor))[UpdateSiteViewModel::class.java]

        viewModel.updateSiteState.observe(::getLifecycle, ::updateUI)
        initView()

        if (userManager.customerType.equals("SERVICE_PROVIDER")) {
            titleCustomers_profileType.visibility = View.VISIBLE
            spLayoutCustomer_profileType.visibility = View.VISIBLE
            viewModel.onGetCustomer()
        } else {
            titleCustomers_profileType.visibility = View.GONE
            spLayoutCustomer_profileType.visibility = View.GONE
            selectedCustomerId = userManager.customerId.toInt()
            viewModel.onGetRegion(selectedCustomerId)
            viewModel.onGetUser(selectedCustomerId)
        }

        viewModel.onGetCountry()

    }

    fun initView() {
        toolbar.title = getString(R.string.update_site)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        btCreateSite.text = "Update site"
        btCreateSite.setOnClickListener {
            updateSite()
        }
    }

    private fun updateUI(screenState: ScreenState<UpdateSIteState>?) {
        when (screenState) {
            ScreenState.Loading -> progressAddDevices.visibility = View.VISIBLE
            is ScreenState.Render -> processLoginState(screenState.renderState)
        }
    }

    private fun processLoginState(renderState: UpdateSIteState) {
        progressAddDevices.visibility = View.GONE
        when (renderState) {
            UpdateSIteState.GetRegionFailure -> {
                AlertUtil.showSnackBar(
                        listLayout_site,
                        viewModel.errorMessage,
                        2000
                )
            }
            UpdateSIteState.SiteNameEmpty -> {
                AlertUtil.showSnackBar(
                        listLayout_site,
                        viewModel.errorMessage,
                        2000
                )
            }
            UpdateSIteState.GetRegionSuccess -> {
                regionList = viewModel.getRegionList.value!!
                updateRegionSpinner(regionList)
            }
            UpdateSIteState.GetCustomerSuccess -> {
                customerList = viewModel.getCustomerList.value!!
                updateCustomerSpinner(customerList)
            }
            UpdateSIteState.GetCustomerFailure -> {
                AlertUtil.showSnackBar(
                        listLayout_site,
                        viewModel.errorMessage,
                        2000
                )
            }
            UpdateSIteState.GetCountrySuccess -> {
                countryList = viewModel.getCountryList.value!!
                updateCountrySpinner(countryList)
            }
            UpdateSIteState.GetCountryFailure -> {
                AlertUtil.showSnackBar(
                        listLayout_site,
                        viewModel.errorMessage,
                        2000
                )
            }
            UpdateSIteState.GetStateSuccess -> {
                stateList = viewModel.getStateList.value!!
                updateStateSpinner(stateList)
            }
            UpdateSIteState.GetStateFailure -> {
                AlertUtil.showSnackBar(
                        listLayout_site,
                        viewModel.errorMessage,
                        2000
                )
            }
            UpdateSIteState.GetCitySuccess -> {
                cityList = viewModel.getCityList.value!!
                updateCitySpinner(cityList)
            }
            UpdateSIteState.GetCityFailure -> {
                AlertUtil.showSnackBar(
                        listLayout_site,
                        viewModel.errorMessage,
                        2000
                )
            }
            UpdateSIteState.UpdateSiteFailure -> {
                AlertUtil.showSnackBar(
                        listLayout_site,
                        viewModel.errorMessage,
                        2000
                )
            }
            UpdateSIteState.UpdateSiteSuccess -> {
                resetForm()
                AlertUtil.showActionAlertDialog(this, "", "Site has been updated !",
                        false, getString(R.string.btn_ok)) { dialog: DialogInterface?, which: Int ->
                    val intent = Intent()
                    intent.putExtra("result", "success")
                    setResult(RESULT_OK, intent);
                    finish()
                }
            }
            UpdateSIteState.GetUserFailure -> {
                AlertUtil.showSnackBar(
                        listLayout_site,
                        viewModel.errorMessage,
                        2000
                )
            }
            UpdateSIteState.GetUserSuccess -> {
                userList = viewModel.getUserList.value!!
                updateUserSpinner(userList)
            }
        }
    }

    private fun updateCustomerSpinner(customerList: ArrayList<CustomerRes>) {

        val adapter = CustomerDropdownAdapter(this, customerList)
        spCustomers_site.adapter = adapter
        spCustomers_site.onItemSelectedListener =
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

    private fun updateRegionSpinner(regionList: ArrayList<RegionRes>) {

        val adapter = RegionDropdownAdapter(this, regionList)
        spRegion_site.adapter = adapter
        spRegion_site.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View,
                            position: Int,
                            id: Long) {

                        selectedregionId = regionList[position].region_id!!

                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Another interface callback
                    }
                }
    }

    private fun updateCountrySpinner(countryList: ArrayList<CountryRes>) {

        val adapter = CountryDropdownAdapter(this, countryList)
        spCountry_site.adapter = adapter
        spCountry_site.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View,
                            position: Int,
                            id: Long) {

                        selectedCountryId = countryList[position].country_id!!.toInt()
                        viewModel.onGetState(selectedCountryId)

                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Another interface callback
                    }
                }
    }

    private fun updateStateSpinner(stateList: ArrayList<StateRes>) {

        val adapter = StateDropdownAdapter(this, stateList)
        spState_site.adapter = adapter
        spState_site.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View,
                            position: Int,
                            id: Long) {

                        selectedStateId = stateList[position].state_id!!.toInt()
                        viewModel.onGetCity(selectedStateId)

                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Another interface callback
                    }
                }
    }

    private fun updateCitySpinner(cityList: ArrayList<CityRes>) {

        val adapter = CityDropdownAdapter(this, cityList)
        spCity_site.adapter = adapter
        spCity_site.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View,
                            position: Int,
                            id: Long) {

                        selectedCityId = cityList[position].city_id!!.toInt()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Another interface callback
                    }
                }
    }

    private fun updateUserSpinner(userList: ArrayList<UserDataRes>) {

        val adapter = UserDropdownAdapter(this, userList)
        spUser_site.adapter = adapter
        spUser_site.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View,
                            position: Int,
                            id: Long) {

                        selectedUserId = userList[position].user_id!!.toInt()
                        updateView(viewModel.singleSite)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Another interface callback
                    }
                }
    }

    private fun resetForm() {
        et_SiteName_site.setText("")
    }

    private fun updateView(singleSite: SiteListRes) {

        if (customerList.isNotEmpty()) {
            for (i in 0 until customerList.size) {
                spCustomers_site.setSelection(customerList[i].customer_id.toString().indexOf(testObject!!.customer_id.toString()))
            }
        }


        if (userList.isNotEmpty()) {
            for (i in 0 until userList.size) {
                spUser_site.setSelection(userList[i].user_id.toString().indexOf(testObject!!.user_id.toString()))
            }
        }

        if (regionList.isNotEmpty()) {
            for (i in 0 until regionList.size) {
                spRegion_site.setSelection(regionList[i].region_id.toString().indexOf(testObject!!.region_id.toString()))
            }
        }

        if (countryList.isNotEmpty()) {
            for (i in 0 until countryList.size) {
                spCountry_site.setSelection(countryList[i].country_id.toString().indexOf(testObject!!.country_id.toString()))
            }
        }

        if (stateList.isNotEmpty()) {
            for (i in 0 until stateList.size) {
                spState_site.setSelection(stateList[i].state_id.toString().indexOf(testObject!!.state_id.toString()))
            }
        }
        if (cityList.isNotEmpty()) {
            for (i in 0 until cityList.size) {
                spCity_site.setSelection(cityList[i].city_id.toString().indexOf(testObject!!.city_id.toString()))
            }
        }
        et_SiteName_site.setText(testObject!!.site_name)
        siteId = testObject!!.site_id

//        geoLocation = testObject!!.coordinates

    }

    private fun updateSite() {
        viewModel.onUpdateSite(
                siteId!!,
                selectedregionId,
                selectedCountryId,
                selectedStateId,
                selectedCityId,
                et_SiteName_site.text.toString(),
                selectedUserId,
                geoLocation!!,
                selectedCustomerId

        )
    }

}