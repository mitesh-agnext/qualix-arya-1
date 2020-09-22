package com.custom.app.ui.payment.list

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.core.util.Pair
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.base.app.ui.base.BaseActivity
import com.core.app.util.AlertUtil
import com.custom.app.CustomApp
import com.custom.app.R
import com.custom.app.data.model.payment.PaymentHistoryRes
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.payment.detail.PaymentDetailActivity
import com.custom.app.util.Utils
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_payment_history.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.layout_progress.*
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PaymentHistoryActivity : BaseActivity(), ListCallBack, AdapterView.OnItemSelectedListener, View.OnClickListener {

    @Inject
    lateinit var interactor: PaymentHistoryInteractor

    @Inject
    lateinit var customerInteractor: CustomerInteractor

    var expandIndicator: Boolean = true
    private lateinit var viewModel: PaymentHistoryVM
    private val customerName = ArrayList<String>()
    private val commodityName = ArrayList<String>()
    private val instCenterName = ArrayList<String>()
    private val regionName = ArrayList<String>()
    private var filterData: MutableMap<String, String> = HashMap()
    var data: MutableMap<String, String> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_history)
        initView()
    }

    fun initView() {
        toolbar.title = getString(R.string.payment_history)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        spCustomer.onItemSelectedListener = this
        spCommodity.onItemSelectedListener = this
        spInsCenter.onItemSelectedListener = this
        spRegion.onItemSelectedListener = this

        lnFilter.setOnClickListener(this)
        lnEndDate.setOnClickListener(this)
        lnStartDate.setOnClickListener(this)
        tvApply.setOnClickListener(this)
        tvCancel.setOnClickListener(this)

        viewModel = ViewModelProvider(this,
                PaymentHistoryViewModelFactory(interactor, customerInteractor))[PaymentHistoryVM::class.java]
        viewModel.paymentHistoryStateLiveData.observe(::getLifecycle, ::setViewState)

        if (userManager.customerType == "SERVICE_PROVIDER") {
            viewModel.getCustomerList()
        } else {
            tvCustomer.visibility = View.GONE
            spCustomer.visibility = View.GONE
            allFilterApis(userManager.customerId.toInt())
            data["customer_id"] = userManager.customerId
            viewModel.getPaymentHistory(data)
        }
    }

    private fun setViewState(state: PaymentHistoryState) {
        when (state) {
            Loading -> {
                setProgress(true)
            }
            is List -> {
                setProgress(false)
                if (viewModel.paymentList.size > 0) {
                    tvNoData.visibility = View.GONE
                    rvPaymentHistory.visibility = View.VISIBLE
                    hideFilter()
                    var scanHistoryAdapter = PaymentHistoryAdapter(this, viewModel.paymentList, this)
                    rvPaymentHistory.adapter = scanHistoryAdapter
                    rvPaymentHistory.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
                } else {
                    tvNoData.visibility = View.VISIBLE
                    rvPaymentHistory.visibility = View.GONE
                }
            }
            is Error -> {
                setProgress(false)
                AlertUtil.showToast(this, state.message!!)
            }

            CustomerList -> {
                progress.visibility = View.GONE
                customerName.clear()
                if (viewModel.customerList.size > 0) {
                    for (i in 0 until viewModel.customerList.size) {
                        customerName.add(viewModel.customerList[i].name.toString())
                    }
                    Utils.setSpinnerAdapter(this, customerName, spCustomer)
                }
            }
            CustomerError -> {
                setProgress(false)
                AlertUtil.showToast(this, "Error")
            }

            is CommodityList -> {
                progress.visibility = View.GONE
                commodityName.clear()
                if (viewModel.commodityList.size > 0) {
                    commodityName.add(getString(R.string.select_commodity))
                    for (i in 0 until viewModel.commodityList.size) {
                        commodityName.add(viewModel.commodityList[i].commodity_name.toString())
                    }
                } else {
                    commodityName.add("No Data")
                }
                Utils.setSpinnerAdapter(this, commodityName, spCommodity)
            }
            is CommodityError -> {
            }

            is InstallationCentersSuccess -> {
                progress.visibility = View.GONE
                instCenterName.clear()
                if (viewModel.installationCenterList.size > 0) {
                    instCenterName.add("Select Installation")
                    for (i in 0 until viewModel.installationCenterList.size) {
                        instCenterName.add(viewModel.installationCenterList[i].inst_center_name.toString())
                    }
                } else {
                    instCenterName.add("No Data")
                }
                Utils.setSpinnerAdapter(this, instCenterName, spInsCenter)
            }
            is RegionSuccess -> {
                progress.visibility = View.GONE
                regionName.clear()
                if (viewModel.regionList.size > 0) {
                    regionName.add(getString(R.string.select_region))
                    for (i in 0 until viewModel.regionList.size) {
                        regionName.add(viewModel.regionList[i].region_name.toString())
                    }
                } else {
                    regionName.add("No Data")
                }
                Utils.setSpinnerAdapter(this, regionName, spRegion)
            }
            is Token -> {
                AlertUtil.showToast(this, getString(R.string.token_expire))
                Utils.tokenExpire(this@PaymentHistoryActivity)
            }
        }
    }

    private fun setProgress(isLoading: Boolean) {
        if (isLoading) {
            progress.visibility = View.VISIBLE
        } else {
            progress.visibility = View.GONE
        }
    }

    override fun onItemClick(pos: Int) {
        var intent = Intent(this, PaymentDetailActivity::class.java)
        val gson = Gson()
        val type = object : TypeToken<PaymentHistoryRes>() {}.type
        val json = gson.toJson(viewModel.paymentList[pos], type)
        intent.putExtra("selectObject", json)
        startActivity(intent)
    }

    private fun allFilterApis(customerId: Int) {
        viewModel.getCommodityListVm(customerId)
        viewModel.getInstallationCentersVM(customerId)
        viewModel.getRegionVM(customerId)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onItemSelected(spinner: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
        when (spinner) {
            spCustomer -> {
                allFilterApis(viewModel.customerList[pos].customer_id!!)
                data["customer_id"] = viewModel.customerList[pos].customer_id.toString()
                filterData["customer_id"] = viewModel.customerList[pos].customer_id.toString()
                viewModel.getPaymentHistory(data)
            }
            spCommodity -> {
                if (pos > 0)
                    filterData["commodity_id"] = viewModel.commodityList[pos - 1].commodity_id.toString()
                else if(pos==0)
                {
                    filterData.remove("commodity_id")
                }
            }
            spInsCenter -> {
                if (pos > 0)
                    filterData["inst_center_id"] = viewModel.installationCenterList[pos - 1].installation_center_id.toString()
                else if(pos==0)
                {
                    filterData.remove("inst_center_id")
                }
            }
            spRegion -> {
                if (pos > 0)
                    filterData["region_id"] = viewModel.regionList[pos - 1].region_id.toString()
                else if(pos==0)
                {
                    filterData.remove("region_id")
                }
            }
        }
    }

    override fun onClick(view: View?) {
        when (view) {
            lnFilter -> {
                if (expandIndicator) {
                    expandIndicator = false
                    lnFilterBody.visibility = View.GONE
                    ivFilter.setBackgroundResource(R.drawable.ic_down)
                } else {
                    expandIndicator = true
                    lnFilterBody.visibility = View.VISIBLE
                    ivFilter.setBackgroundResource(R.drawable.ic_up)
                }
            }
            lnEndDate -> {
                dateRange()
            }
            lnStartDate -> {
                dateRange()
            }
            tvApply -> {
                hideFilter()
                viewModel.getPaymentHistory(filterData)
            }
            tvCancel -> {
                cancelClick()
                hideFilter()
                viewModel.getPaymentHistory(data)
            }
        }
    }

    private fun hideFilter() {
        expandIndicator = false
        lnFilterBody.visibility = View.GONE
        ivFilter.setBackgroundResource(R.drawable.ic_down)
    }

    fun cancelClick()
    {
        tvStartDate.text=getString(R.string.start_date)
        tvEndDate.text=getString(R.string.end_date)
        spCommodity.setSelection(0)
        spInsCenter.setSelection(0)
        spRegion.setSelection(0)
    }
    private fun dateRange() {
        val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        builder.setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
        val constraints = CalendarConstraints.Builder()
        val max = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli()
        constraints.setValidator(DateValidatorPointBackward.before(max))
        builder.setTheme(R.style.ThemeOverlay_MaterialComponents_MaterialCalendar)
        builder.setCalendarConstraints(constraints.build())
        var datePicker = builder.build()
        datePicker.addOnPositiveButtonClickListener(MaterialPickerOnPositiveButtonClickListener { selection: Pair<Long?, Long?> ->
            tvStartDate.text = LocalDate.ofEpochDay(TimeUnit.MILLISECONDS.toDays(selection.first!!)).format(dateFormatter)
            tvEndDate.text = LocalDate.ofEpochDay(TimeUnit.MILLISECONDS.toDays(selection.second!!)).format(dateFormatter)
            filterData["date_from"] = selection.first.toString()
            filterData["date_to"] = selection.second.toString()

        })
        datePicker.show(supportFragmentManager, datePicker.toString())

    }
}