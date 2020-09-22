package com.custom.app.ui.createData.analytics.analyticsScreen

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.base.app.ui.base.BaseActivity
import com.core.app.util.AlertUtil
import com.custom.app.CustomApp
import com.custom.app.R
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.createData.adapters.CenterDropdownAdapter
import com.custom.app.ui.createData.adapters.CenterTypeDropdownAdapter
import com.custom.app.ui.createData.adapters.CommodityDropdownAdapter
import com.custom.app.ui.createData.adapters.CustomerDropdownAdapter
import com.custom.app.ui.createData.flcScan.season.create.CommodityRes
import com.custom.app.ui.createData.instlCenter.createInstallationCenter.InstallationCenterTypeRes
import com.custom.app.ui.createData.region.site.create.RegionRes
import com.custom.app.ui.createData.region.site.create.adapters.RegionDropdownAdapter
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerRes
import com.custom.app.ui.device.assign.InstallationCenterRes
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import kotlinx.android.synthetic.main.activity_analysis_screen.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class AnalysisScreen : BaseActivity() {

    @Inject
    lateinit var customerInteractor: CustomerInteractor

    @Inject
    lateinit var interactor: AnalyticsInteractor

    private lateinit var viewModel: AnalyticsViewModel
    var viewPager: ViewPager? = null
    var tabs_main: TabLayout? = null
    var tabs_categories: TabLayout? = null

    var show_hide_status: Int? = null

    companion object {
        var selectedCustomerId: String = ""
        var selectedRegionId: String = ""
        var selectedCenterId: String = ""
        var selectedCenterTypeId: String = ""
        var selectedCommodityId: String = ""
        var epochFromDate: Long? = null
        var epochToDate: Long? = null
    }

    private var fromDate: String? = ""
    private var toDate: String? = ""

    private lateinit var centerTypeList: ArrayList<InstallationCenterTypeRes>
    private lateinit var centerList: ArrayList<InstallationCenterRes>
    private lateinit var customerList: ArrayList<CustomerRes>
    private lateinit var regionList: ArrayList<RegionRes>
    private lateinit var commodityList: ArrayList<CommodityRes>
    private lateinit var categoryList: ArrayList<CategoryTabsRes>

    var currentDay: String? = null
    var weekDay: String? = null
    val WEEK_DATA = "WEEK_DATA"

    fun getCurrentDate() {
        var calendar = Calendar.getInstance()
        val date = calendar.time
        val dateFormat = SimpleDateFormat("dd-MM-yyyy")

        currentDay = dateFormat.format(date)

        weekDay = getCalculatedDate(currentDay!!, "dd-MM-yyyy", -7)

        typeFilterData(WEEK_DATA)
    }

    fun getCalculatedDate(date: String, dateFormat: String, days: Int): String {
        val cal = Calendar.getInstance()
        val s = SimpleDateFormat(dateFormat)
        if (date.isNotEmpty()) {
            cal.time = s.parse(date)
        }
        cal.add(Calendar.DAY_OF_YEAR, days)
        return s.format(Date(cal.timeInMillis))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analysis_screen)
        toolbar.title = getString(R.string.analytics)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        show_hide_status = 0
        selectionLayout.visibility = View.VISIBLE

        show_hide_layout.setOnClickListener {
            if (show_hide_status == 0) {
                show_hide_layout.setImageDrawable(getDrawable(R.drawable.hide))
                show_hide_status = 1
                selectionLayout.visibility = View.VISIBLE
            } else if (show_hide_status == 1) {
                show_hide_layout.setImageDrawable(getDrawable(R.drawable.show))
                show_hide_status = 0
                selectionLayout.visibility = View.GONE
            }
        }

        viewPager = findViewById(R.id.viewPager)
        viewPager!!.offscreenPageLimit = 0
        tabs_main = findViewById(R.id.tabs_main)
        tabs_categories = findViewById(R.id.tabs_categories)
        tabs_main!!.setupWithViewPager(viewPager)

//        rvCategories = findViewById(R.id.rvCategories_tabs)

        tvDateFrom_analytics.setOnClickListener {
            datePickerDialog(tvDateFrom_analytics)
        }
        tvDateTo_analytics.setOnClickListener {
            datePickerDialog(tvDateTo_analytics)
        }

        btnSubmitFilter.setOnClickListener {

            if (selectedCenterId.equals("0")) {
                selectedCenterId == ""
            } else if (selectedRegionId.equals("0")) {
                selectedRegionId == ""
            } else if (selectedCenterTypeId.equals("0")) {
                selectedCenterTypeId == ""
            }

            val sampleFragmentPagerAdapter = SampleFragmentPagerAdapter(supportFragmentManager, applicationContext, selectedCustomerId, selectedCommodityId,
                    selectedRegionId, selectedCenterId, selectedCenterTypeId, epochToDate.toString(), epochFromDate.toString())
            viewPager!!.currentItem
            viewPager!!.adapter = sampleFragmentPagerAdapter
        }

        viewModel = ViewModelProvider(this, AnalyticsViewModelFactory(customerInteractor, interactor))[AnalyticsViewModel::class.java]
        viewModel.analyticsListState.observe(::getLifecycle, ::updateUI)

        if (userManager.customerType.equals("SERVICE_PROVIDER")) {
            customerLayout.visibility = View.VISIBLE
            viewModel.onGetCustomerList()
        } else {
            customerLayout.visibility = View.GONE
            selectedCustomerId = userManager.customerId
            viewModel.onGetCategoryList(selectedCustomerId)
        }

        getCurrentDate()

        tabs_categories!!.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {

                viewModel.onGetCommodityList(categoryList[tab.position].commodity_category_id!!)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {
                viewModel.onGetCommodityList(categoryList[tab.position].commodity_category_id!!)

            }
        })
    }

    private fun updateUI(screenState: ScreenState<AnalyticsState>?) {
        when (screenState) {
            ScreenState.Loading -> progressAnalytics.visibility = View.VISIBLE
            is ScreenState.Render -> processLoginState(screenState.renderState)
        }
    }

    private fun processLoginState(renderState: AnalyticsState) {
        progressAnalytics.visibility = View.GONE
        when (renderState) {
            AnalyticsState.GetCenterTypeListFailure -> {
                AlertUtil.showSnackBar(listLayout_analysis, viewModel.errorMessage, 2000)
            }
            AnalyticsState.GetCenterTypeListSuccess -> {
                centerTypeList = viewModel.centerTypeList.value!!
                updateCenterTypeSpinner(centerTypeList)
            }
            AnalyticsState.GetCenterListFailure -> {
                AlertUtil.showSnackBar(listLayout_analysis, viewModel.errorMessage, 2000)
            }
            AnalyticsState.GetCenterListSuccess -> {
                centerList = viewModel.centerList.value!!
                updateCenterSpinner(centerList)
            }
            AnalyticsState.RegionListFailure -> {
                AlertUtil.showSnackBar(listLayout_analysis, viewModel.errorMessage, 2000)
            }
            AnalyticsState.RegionListSuccess -> {
                regionList = viewModel.regionList.value!!
                updateRegionSpinner(regionList)
            }
            AnalyticsState.GetCustomerListFailure -> {
                AlertUtil.showSnackBar(listLayout_analysis, viewModel.errorMessage, 2000)
            }
            AnalyticsState.GetCustomerListSuccess -> {
                customerList = viewModel.customerList.value!!
                updateCustomerSpinner(customerList)
            }
            AnalyticsState.GetCommodityListFailure -> {
                AlertUtil.showSnackBar(listLayout_analysis, viewModel.errorMessage, 2000)
            }
            AnalyticsState.GetCommodityListSuccess -> {
                commodityList = viewModel.commodityList.value!!
                updateCommoditySpinner(commodityList)
            }
            AnalyticsState.GetCategoryListFailure -> {
                AlertUtil.showSnackBar(listLayout_analysis, viewModel.errorMessage, 2000)
            }
            AnalyticsState.GetCategoryListSuccess -> {
                categoryList = viewModel.categoryList.value!!
                setTabs(categoryList)

            }

        }
    }

    private fun updateCustomerSpinner(customerList: ArrayList<CustomerRes>) {

        val adapter = CustomerDropdownAdapter(this, customerList)
        spCustomer_analytics.adapter = adapter
        spCustomer_analytics.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                        selectedCustomerId = customerList[position].customer_id!!.toString()
                        viewModel.onGetRegions(selectedCustomerId)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                    }
                }
    }

    private fun updateRegionSpinner(regionList: ArrayList<RegionRes>) {

        val adapter = RegionDropdownAdapter(this, regionList)
        spRegion_analytics.adapter = adapter
        spRegion_analytics.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                        selectedRegionId = regionList[position].region_id!!.toString()

                        if (selectedRegionId.equals("0") || selectedRegionId == "0") {
                            selectedRegionId = ""
                        }

                        viewModel.onGetCenterType(selectedRegionId)
                        viewModel.onGetCenters(selectedCustomerId, "", selectedRegionId)

                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }
    }

    private fun updateCommoditySpinner(commodityList: ArrayList<CommodityRes>) {

        val adapter = CommodityDropdownAdapter(this, commodityList)
        spCommodity_analytics!!.adapter = adapter
        spCommodity_analytics!!.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                        selectedCommodityId = commodityList[position].commodity_id!!.toString()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }
    }

    private fun updateCenterTypeSpinner(centerTypeList: ArrayList<InstallationCenterTypeRes>) {

        val adapter = CenterTypeDropdownAdapter(this, centerTypeList)
        spCenters_type_analytics.adapter = adapter
        spCenters_type_analytics.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                        selectedCenterTypeId = centerTypeList[position].commercial_location_type_id!!.toString()

                        if (selectedCenterTypeId.equals("0") || selectedCenterTypeId == "0") {
                            selectedCenterTypeId = ""
                        }

                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Another interface callback
                    }
                }
    }

    private fun updateCenterSpinner(centerList: ArrayList<InstallationCenterRes>) {

        val adapter = CenterDropdownAdapter(this, centerList)
        spCenters_analytics.adapter = adapter
        spCenters_analytics.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                        selectedCenterId = centerList[position].installation_center_id!!.toString()
                        if (selectedCenterId.equals("0") || selectedCenterId == "0") {
                            selectedCenterId = ""
                        }

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

        val dpDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year: Int, monthOfYear: Int, dayOfMonth: Int ->

            val fmt = SimpleDateFormat("dd-MM-yyyy")
            val month = monthOfYear + 1
            val date = fmt.parse("$dayOfMonth-$month-$year")

            val fmtOut = SimpleDateFormat("dd-MM-yyyy")

            when (textView.id) {
                R.id.tvDateFrom_analytics -> {
                    epochFromDate = date.time
                    tvDateFrom_analytics.text = fmtOut.format(date)
                }
                R.id.tvDateTo_analytics -> {
                    epochToDate = date.time
                    tvDateTo_analytics.text = fmtOut.format(date)
                }
            }
        }, year, month, day)

        val datePicker = dpDialog.datePicker
        val calendar = Calendar.getInstance()
        datePicker.maxDate = calendar.timeInMillis

        dpDialog.show()
    }

    private fun typeFilterData(requestTime: String) {
        fromDate = currentDay!!
        toDate = currentDay!!
        val fmt = SimpleDateFormat("dd-MM-yyyy")
        val date = fmt.parse(currentDay)
        epochToDate = date.time

        when (requestTime) {
            WEEK_DATA -> {
                val fmt = SimpleDateFormat("dd-MM-yyyy")
                val date = fmt.parse(weekDay)
                epochFromDate = date.time
            }
        }

        tvDateFrom_analytics.text = weekDay
        tvDateTo_analytics.text = toDate
    }

    private fun setTabs(tabList: ArrayList<CategoryTabsRes>) {

        for (i in 0 until tabList.size) {
            val tabName = tabList[i].commodity_category_name
            tabs_categories!!.addTab(tabs_categories!!.newTab().setText(tabName))
            tabs_categories!!.getTabAt(i)!!.setIcon(R.drawable.tea)
        }

        viewModel.onGetRegions(selectedCustomerId)
        viewModel.onGetCommodityList(tabList[0].commodity_category_id!!)
    }

}