package com.custom.app.ui.scan.list.history

import android.app.AlertDialog
import android.content.Intent
import android.content.Intent.ACTION_EDIT
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.core.util.Pair
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.base.app.ui.base.BaseActivity
import com.core.app.util.ActivityUtil
import com.core.app.util.AlertUtil
import com.custom.app.CustomApp
import com.custom.app.R
import com.custom.app.data.model.scanhistory.ScanData
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.scan.list.detail.ScanDetailActivity
import com.custom.app.util.Constants
import com.custom.app.util.Utils
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.specx.device.util.Constants.KEY_DEVICE_ID
import com.specx.device.util.Constants.KEY_DEVICE_NAME
import kotlinx.android.synthetic.main.activity_scan_history.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.layout_progress.*
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.collections.ArrayList

class ScanHistoryActivity : BaseActivity(), ListCallBack, AdapterView.OnItemSelectedListener, View.OnClickListener {

    @Inject
    lateinit var interactor: ScanHistoryInteractor

    @Inject
    lateinit var customerInteractor: CustomerInteractor

    private lateinit var viewModel: ScanHistoryVM
    private val customerName = ArrayList<String>()
    private val commodityName = ArrayList<String>()
    private val instCenterName = ArrayList<String>()
    private val regionName = ArrayList<String>()
    private val deviceTypeName = ArrayList<String>()
    private var filterData: MutableMap<String, String> = HashMap()
    var data: MutableMap<String, String> = HashMap()
    private lateinit var tvStartDate: TextView
    private lateinit var tvEndDate: TextView
    lateinit var spCommodity: Spinner
    lateinit var spInsCenter: Spinner
//    lateinit var spRegion: Spinner
    lateinit var spDeviceType: Spinner
    lateinit var alertDialog: AlertDialog
    lateinit var tvApply: TextView
    lateinit var tvCancel: TextView
    private var commodityPos = 0
    private var insCenterPos = 0
    private var regionPos = 0
    private var deviceTypePos = 0
    var linearLayoutManager: LinearLayoutManager? = null
    private val PAGE_START = 0
    private var isLoading = false
    private var isLastPage = false
    private val TOTAL_PAGES = 3
    private var currentPage: Int = PAGE_START
    var notificationdeviceId: Int? = null
    var notificationdeviceName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_history)
        initView()
    }

    fun initView() {
        toolbar.title = getString(R.string.scan_history)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        fbFilter.setOnClickListener(this)
        viewModel = ViewModelProvider(this, ScanHistoryViewModelFactory(interactor, customerInteractor))[ScanHistoryVM::class.java]
        viewModel.scanHistoryState.observe(::getLifecycle, ::setViewState)
//        if (userManager.customerType == "SERVICE_PROVIDER") {
//            viewModel.getCustomerList()
//        } else {
//            data["customer_id"] = userManager.customerId
//            viewModel.getScanHistory(data)
//        }
//        setProgress(true)
//        viewModel.getScanHistory(data)

        linearLayoutManager = LinearLayoutManager(this)
        rvScanHistory.setLayoutManager(linearLayoutManager)
    }

    private fun setViewState(state: ScanHistoryState) {
        when (state) {
            Loading -> {
                setProgress(true)
            }
            CustomerList -> {
                setProgress(false)
                customerName.clear()
                if (viewModel.customerList.size > 0) {
                    for (i in 0 until viewModel.customerList.size) {
                        customerName.add(viewModel.customerList[i].name.toString())
                    }
                }
            }
            CustomerError -> {
                setProgress(false)
                AlertUtil.showToast(this, "Error")
            }
            is List -> {
                setProgress(false)
                if (state.scanHistory.size > 0) {
                    tvNoData.visibility = View.GONE
                    rvScanHistory.visibility = View.VISIBLE
                    val scanHistoryAdapter = ScanHistoryAdapter(this, state.scanHistory, this, userManager.customerType)
                    rvScanHistory.adapter = scanHistoryAdapter
                } else {
                    tvNoData.visibility = View.VISIBLE
                    rvScanHistory.visibility = View.GONE
                }
            }
            is Error -> {
                setProgress(false)
                AlertUtil.showToast(this, state.message!!)
                tvNoData.visibility = View.VISIBLE
                rvScanHistory.visibility = View.GONE
            }
            is CommodityList -> {
                setProgress(false)
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
                setProgress(false)
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
                setProgress(false)
                regionName.clear()
                if (viewModel.regionList.size > 0) {
                    regionName.add(getString(R.string.select_region))
                    for (i in 0 until viewModel.regionList.size) {
                        regionName.add(viewModel.regionList[i].region_name.toString())
                    }
                } else {
                    regionName.add("No Data")
                }
//                Utils.setSpinnerAdapter(this, regionName, spRegion)
            }
            is DeviceTypeSuccess -> {
                setProgress(false)
                deviceTypeName.clear()
                if (viewModel.deviceTypeList.size > 0) {
                    deviceTypeName.add(getString(R.string.select_device))
                    for (i in 0 until viewModel.deviceTypeList.size) {
                        deviceTypeName.add(viewModel.deviceTypeList[i].device_type_desc.toString())
                    }
                } else {
                    deviceTypeName.add("No Data")
                }
                Utils.setSpinnerAdapter(this, deviceTypeName, spDeviceType)
            }
            is Token -> {
                setProgress(false)
                AlertUtil.showToast(this, getString(R.string.token_expire))
                userManager.clearData()
                Utils.tokenExpire(this)
            }
            is ApprovalSuccess -> {
                setProgress(false)
                onResume()
            }
            is ApprovalFailure -> {
                setProgress(false)
                onResume()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        setProgress(true)
        viewModel.getScanHistory(data)
    }

    private fun setProgress(isLoading: Boolean) {
        if (isLoading) {
            progress.visibility = View.VISIBLE
        } else {
            progress.visibility = View.GONE
        }
    }

    override fun onItemClick(pos: Int) {
        val intent = Intent(this, ScanDetailActivity::class.java)
        val gson = Gson()
        val type = object : TypeToken<ScanData>() {}.type
        val json = gson.toJson(viewModel.scanList[pos], type)
        intent.putExtra(Constants.FLOW, Constants.NAV_SCAN_HISTORY_ACTIVITY)
        intent.putExtra("selectObject", json)
        intent.putExtra("selectObject", json)
//        intent.putExtra("customerType", userManager.customerType)
        ActivityUtil.startActivity(this, intent, false)
    }

    override fun editItem(pos: Int) {
        val bundle = Bundle()
        bundle.putString(Constants.FLOW, Constants.NAV_SCAN_HISTORY_ACTIVITY)
        bundle.putString(Constants.KEY_SCAN_ID, viewModel.scanList[pos].scanId)
        bundle.putString(KEY_DEVICE_ID, viewModel.scanList[pos].deviceId.toString())
        bundle.putString(KEY_DEVICE_NAME, viewModel.scanList[pos].deviceName)
        setResult(RESULT_OK, Intent(ACTION_EDIT).putExtras(bundle))
        finish()
    }

    override fun onRejectClick(pos: Int) {
        setProgress(true)
        showCustomDialog(viewModel.scanList[pos].scanId!!.toInt(),viewModel.scanList[pos].deviceId!!.toInt(), viewModel.scanList[pos].deviceName.toString(), "2")
//        viewModel.setApproval(viewModel.scanList[pos].scanId!!.toInt(), 2)
    }

    override fun onApproveClick(pos: Int) {
        setProgress(true)
        showCustomDialog(viewModel.scanList[pos].scanId!!.toInt(),viewModel.scanList[pos].deviceId!!.toInt(), viewModel.scanList[pos].deviceName.toString(), "1")
//        viewModel.setApproval()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    override fun onItemSelected(spinner: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
        when (spinner) {
/*
            spCustomer -> {
                allFilterApis(viewModel.customerList[pos].customer_id!!)
                data["customer_id"] = viewModel.customerList[pos].customer_id.toString()
                filterData["customer_id"] = viewModel.customerList[pos].customer_id.toString()
                viewModel.getScanHistory(data)
            }
*/
            spCommodity -> {
                if (pos > 0) {
                    filterData["commodity_id"] = viewModel.commodityList[pos - 1].commodity_id.toString()
                    commodityPos = pos
                } else if (pos == 0) {
                    filterData.remove("commodity_id")
                }
            }
            spInsCenter -> {
                if (pos > 0) {
                    filterData["inst_center_id"] = viewModel.installationCenterList[pos - 1].installation_center_id.toString()
                    insCenterPos = pos
                } else if (pos == 0) {
                    filterData.remove("inst_center_id")
                }
            }
//            spRegion -> {
//                if (pos > 0) {
//                    filterData["region_id"] = viewModel.regionList[pos - 1].region_id.toString()
//                    regionPos = pos
//                } else if (pos == 0) {
//                    filterData.remove("region_id")
//                }
//            }
            spDeviceType -> {
                if (pos > 0) {
                    filterData["device_type_id"] = viewModel.deviceTypeList[pos - 1].device_type_id.toString()
                    deviceTypePos = pos
                } else if (pos == 0) {
                    filterData.remove("profile_type_id")
                }
            }
        }
    }

    override fun onClick(view: View?) {
        when (view) {
            fbFilter -> {
                filterDialog()
            }
            tvStartDate -> {
                dateRange()
            }
            tvEndDate -> {
                dateRange()
            }
            tvApply -> {
                alertDialog.dismiss()
                progress.visibility = View.VISIBLE
                viewModel.getScanHistory(filterData)
            }
            tvCancel -> {
                alertDialog.dismiss()
            }
        }
    }

    private fun allFilterApis(customerId: Int) {
        viewModel.getCommodityListVm(customerId)
        viewModel.getInstallationCentersVM(customerId)
//        viewModel.getRegionVM(customerId)
        viewModel.getDeviceTypeVM()
    }

    private fun filterDialog() {
        //Api hit for filter data
        allFilterApis(userManager.customerId.toInt())
        //Create Filter Dialog
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.layout_filter, null)
        dialogBuilder.setView(dialogView)
        tvStartDate = dialogView.findViewById(R.id.tvStartDate)
        tvEndDate = dialogView.findViewById(R.id.tvEndDate)
        spCommodity = dialogView.findViewById(R.id.spCommodity)
        spInsCenter = dialogView.findViewById(R.id.spInsCenter)
//        spRegion = dialogView.findViewById(R.id.spRegion)
        spDeviceType = dialogView.findViewById(R.id.spDeviceType)
        tvApply = dialogView.findViewById(R.id.tvApply)
        tvCancel = dialogView.findViewById(R.id.tvCancel)
        spCommodity.onItemSelectedListener = this
        spInsCenter.onItemSelectedListener = this
//        spRegion.onItemSelectedListener = this
        spDeviceType.onItemSelectedListener = this
        tvStartDate.setOnClickListener(this)
        tvEndDate.setOnClickListener(this)
        tvApply.setOnClickListener(this)
        tvCancel.setOnClickListener(this)
        alertDialog = dialogBuilder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
        val handler = Handler()
        handler.postDelayed({
            setFilterData()
        }, 1000)
    }

    private fun setFilterData() {
        //Setting default values
        if (filterData["date_from"] != null)
            tvStartDate.text = Utils.timeStampDate(filterData["date_from"]!!.toLong())
        if (filterData["date_to"] != null)
            tvEndDate.text = Utils.timeStampDate(filterData["date_to"]!!.toLong())
        if (commodityPos != 0 && commodityName.size >= commodityPos)
            spCommodity.setSelection(commodityPos)
        if (insCenterPos != 0 && instCenterName.size >= insCenterPos)
            spInsCenter.setSelection(insCenterPos)
//        if (regionPos != 0 && regionName.size >= regionPos)
//            spRegion.setSelection(regionPos)
        if (deviceTypePos != 0 && deviceTypeName.size >= deviceTypePos)
            spDeviceType.setSelection(deviceTypePos)
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
        val datePicker = builder.build()
        datePicker.addOnPositiveButtonClickListener { selection: Pair<Long?, Long?> ->
            val from = LocalDate.ofEpochDay(TimeUnit.MILLISECONDS.toDays(selection.first!!))
                    .atTime(LocalTime.MIDNIGHT)
            val to = LocalDate.ofEpochDay(TimeUnit.MILLISECONDS.toDays(selection.second!!))
                    .atTime(LocalTime.MAX)
            val fromMillis = from.toInstant(ZoneOffset.ofHoursMinutes(5, 30)).toEpochMilli()
            val toMillis = to.toInstant(ZoneOffset.ofHoursMinutes(5, 30)).toEpochMilli()
            tvStartDate.text = from.format(dateFormatter)
            tvEndDate.text = to.format(dateFormatter)
            filterData["date_from"] = fromMillis.toString()
            filterData["date_to"] = toMillis.toString()
        }
        datePicker.show(supportFragmentManager, datePicker.toString())
    }

    private fun showCustomDialog(scanId: Int, deviceId: Int, deviceName: String, scanStatus: String) {
        val viewGroup: ViewGroup = findViewById(android.R.id.content)
        val dialogView: View = LayoutInflater.from(this).inflate(R.layout.approve_rejectdialog, viewGroup, false)
        notificationdeviceId = deviceId
        notificationdeviceName = deviceName
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setView(dialogView)
        val alertDialog: androidx.appcompat.app.AlertDialog = builder.create()
        val tvYes = dialogView.findViewById<TextView>(R.id.btn_yes)
        val tvNo = dialogView.findViewById<TextView>(R.id.btn_no)
        val tvScanStatus = dialogView.findViewById<TextView>(R.id.tvScanStatus)
        val tvScanId = dialogView.findViewById<TextView>(R.id.tvScanId)
        val et_message = dialogView.findViewById<EditText>(R.id.et_message)
        tvScanId.text = "Scan Id: "+scanId.toString()
        if (scanStatus.equals("1")){
            tvScanStatus.text = "Are you sure, you want to approve the scan?"
        }
        else if (scanStatus.equals("2")){
            tvScanStatus.text = "Are you sure, you want to reject the scan?"
        }

        tvYes.setOnClickListener {
            viewModel.setApproval(scanId, scanStatus.toInt(),et_message.text.toString())
            alertDialog.dismiss()
        }
        tvNo.setOnClickListener {
            setProgress(false)
            alertDialog.dismiss()
        }
        alertDialog.show()
    }
}

interface ListCallBack {
    fun onItemClick(pos: Int)
    fun editItem(pos: Int)
    fun onRejectClick(pos: Int)
    fun onApproveClick(pos: Int)
}