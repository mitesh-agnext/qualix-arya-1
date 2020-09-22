package com.custom.app.ui.createData.region.site.create

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
import com.custom.app.ui.createData.region.site.create.adapters.CityDropdownAdapter
import com.custom.app.ui.createData.region.site.create.adapters.CountryDropdownAdapter
import com.custom.app.ui.createData.region.site.create.adapters.RegionDropdownAdapter
import com.custom.app.ui.createData.region.site.create.adapters.StateDropdownAdapter
import com.custom.app.ui.customer.list.CustomerRes
import com.custom.app.ui.user.list.UserDataRes
import com.custom.app.ui.user.list.UserInteractor
import kotlinx.android.synthetic.main.activity_site.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.util.*
import javax.inject.Inject

class CreateSite : BaseActivity() {

    @Inject
    lateinit var userInteractor: UserInteractor

    @Inject
    lateinit var regionSiteInteractor: RegionSiteInteractor

    private lateinit var viewModel: CreateSiteViewModel
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

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_site)

        viewModel = ViewModelProvider(this, CreateSiteViewModelFactory(regionSiteInteractor))[CreateSiteViewModel::class.java]

        viewModel.createSiteState.observe(::getLifecycle, ::updateUI)
        initView()

        if (userManager.customerType.equals("SERVICE_PROVIDER")) {
            titleCustomers_site.visibility = View.VISIBLE
            spLayoutCustomer_site.visibility = View.VISIBLE
            viewModel.onGetCustomer()
        } else {
            titleCustomers_site.visibility = View.GONE
            spLayoutCustomer_site.visibility = View.GONE
            selectedCustomerId = userManager.customerId.toInt()
            viewModel.onGetRegion(selectedCustomerId)
            viewModel.onGetUser(selectedCustomerId)
        }

        viewModel.onGetCountry()
    }

    fun initView() {
        toolbar.title = getString(R.string.create_site)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        btCreateSite.text = "Create site"
        btCreateSite.setOnClickListener {
            addSite()
        }
    }

    private fun updateUI(screenState: ScreenState<CreateSIteState>?) {
        when (screenState) {
            ScreenState.Loading -> progressAddDevices.visibility = View.VISIBLE
            is ScreenState.Render -> processLoginState(screenState.renderState)
        }
    }

    private fun processLoginState(renderState: CreateSIteState) {
        progressAddDevices.visibility = View.GONE
        when (renderState) {
            CreateSIteState.GetRegionFailure -> {
                AlertUtil.showSnackBar(listLayout_site, viewModel.errorMessage, 2000)
            }
            CreateSIteState.SiteNameEmpty -> {
                AlertUtil.showSnackBar(listLayout_site, viewModel.errorMessage, 2000)
            }
            CreateSIteState.GetRegionSuccess -> {
                regionList = viewModel.getRegionList.value!!
                updateRegionSpinner(regionList)
            }
            CreateSIteState.GetCustomerSuccess -> {
                customerList = viewModel.getCustomerList.value!!
                updateCustomerSpinner(customerList)
            }
            CreateSIteState.GetCustomerFailure -> {
                AlertUtil.showSnackBar(listLayout_site, viewModel.errorMessage, 2000)
            }
            CreateSIteState.GetCountrySuccess -> {
                countryList = viewModel.getCountryList.value!!
                updateCountrySpinner(countryList)
            }
            CreateSIteState.GetCountryFailure -> {
                AlertUtil.showSnackBar(listLayout_site, viewModel.errorMessage, 2000)
            }
            CreateSIteState.GetStateSuccess -> {
                stateList = viewModel.getStateList.value!!
                updateStateSpinner(stateList)
            }
            CreateSIteState.GetStateFailure -> {
                AlertUtil.showSnackBar(listLayout_site, viewModel.errorMessage, 2000)
            }
            CreateSIteState.GetCitySuccess -> {
                cityList = viewModel.getCityList.value!!
                updateCitySpinner(cityList)
            }
            CreateSIteState.GetCityFailure -> {
                AlertUtil.showSnackBar(listLayout_site, viewModel.errorMessage, 2000)
            }
            CreateSIteState.CreateSiteFailure -> {
                AlertUtil.showSnackBar(listLayout_site, viewModel.errorMessage, 2000)
            }
            CreateSIteState.CreateSiteSuccess -> {
                resetForm()
                AlertUtil.showActionAlertDialog(this, "", "New site has been created !",
                        false, getString(R.string.btn_ok)) { dialog: DialogInterface?, which: Int ->
                    val intent = Intent()
                    intent.putExtra("result", "success")
                    setResult(RESULT_OK, intent);
                    finish()
                }
            }
            CreateSIteState.GetUserFailure -> {
                AlertUtil.showSnackBar(listLayout_site, viewModel.errorMessage, 2000
                )
            }
            CreateSIteState.GetUserSuccess -> {
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
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

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
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

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
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

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
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

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
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

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
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                        selectedUserId = userList[position].user_id!!.toInt()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Another interface callback
                    }
                }
    }

    private fun resetForm() {
        et_SiteName_site.setText("")
    }

    private fun addSite() {
        viewModel.onCreateSite(selectedCustomerId, selectedregionId, selectedCountryId, selectedStateId, selectedCityId, et_SiteName_site.text.toString(),
                selectedUserId, geoLocation!!
        )
    }
}