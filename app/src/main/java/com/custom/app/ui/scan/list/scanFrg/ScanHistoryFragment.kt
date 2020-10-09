package com.custom.app.ui.scan.list.scanFrg

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Pair
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.base.app.ui.base.BaseFragment
import com.custom.app.CustomApp
import com.custom.app.R
import com.custom.app.data.model.scanhistory.ScanData
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.home.HomeActivity
import com.custom.app.ui.scan.list.detail.ScanDetailActivity
import com.custom.app.ui.scan.list.history.*
import com.custom.app.ui.scan.select.SelectScanFragment
import com.custom.app.util.Constants
import com.custom.app.util.Constants.*
import com.custom.app.util.Utils
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.specx.device.util.Constants.KEY_DEVICE_ID
import com.specx.device.util.Constants.KEY_DEVICE_NAME
import kotlinx.android.synthetic.main.fragment_scan_history.*
import kotlinx.android.synthetic.main.layout_progress.*
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ScanHistoryFragment : BaseFragment(), ListCallBack, View.OnClickListener, AdapterView.OnItemSelectedListener {
    private var regionPos = 0
    private var commodityPos = 0
    private var insCenterPos = 0
    private var deviceTypePos = 0
    private var param1: String? = null
    private var param2: String? = null
    private val regionName = ArrayList<String>()
    private val commodityName = ArrayList<String>()
    private val instCenterName = ArrayList<String>()
    private var data: MutableMap<String, String> = HashMap()
    private var filterData: MutableMap<String, String> = HashMap()
    private lateinit var viewModel: ScanHistoryVM
    private lateinit var tvStartDate: TextView
    private lateinit var tvEndDate: TextView
    private lateinit var spCommodity: Spinner
    private lateinit var spInsCenter: Spinner
    private lateinit var spRegion: Spinner
    private lateinit var spDeviceType: Spinner
    private lateinit var alertDialog: AlertDialog
    private lateinit var tvApply: TextView
    private lateinit var tvCancel: TextView
    private lateinit var deviceTypeLayoutFilter: LinearLayout
    var notificationdeviceId: Int? = null
    var notificationdeviceName: String? = null

    @Inject
    lateinit var interactor: ScanHistoryInteractor

    @Inject
    lateinit var customerInteractor: CustomerInteractor
    override fun onCreate(savedInstanceState: Bundle?) {
        (requireActivity().application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_scan_history, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(deviceId: Int, deviceName: String, customerType: String) =
                ScanHistoryFragment().apply {
                    arguments = Bundle().apply {
                        putInt(KEY_DEVICE_ID, deviceId)
                        putString(KEY_DEVICE_NAME, deviceName)
                        putString(KEY_CUSTOMER_TYPE, customerType)
                        KEY_CATEGORY_ID
                    }
                }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    fun initView() {
        fbFilter.setOnClickListener(this)
        viewModel = ViewModelProvider(this, ScanHistoryViewModelFactory(interactor, customerInteractor))[ScanHistoryVM::class.java]
        viewModel.scanHistoryState.observe(::getLifecycle, ::setViewState)
        showProgress(true)
        data["device_type_id"] = requireArguments().getInt(KEY_DEVICE_ID).toString()
        filterData["device_type_id"] = requireArguments().getInt(KEY_DEVICE_ID).toString()
        if (activity != null && arguments != null) {
            (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            val toolbar = (activity as HomeActivity?)!!.binding.includeAppbar.includeToolbar.toolbar
            val deviceName = requireArguments().getString(KEY_DEVICE_NAME)
            (toolbar.findViewById<View>(R.id.title) as TextView).text = deviceName
        }
//        viewModel.getScanHistory(data)
    }

    private fun setViewState(state: ScanHistoryState) {
        when (state) {
            ScanListSuccess -> {
                showProgress(false)
                if (viewModel.scanList.size > 0) {
                    tvNoData.visibility = View.GONE
                    rvScanHistory.visibility = View.VISIBLE
                    val scanHistoryAdapter = ScanHistoryAdapter(activity as Context, viewModel.scanList, this,
                            requireArguments().getInt(KEY_CUSTOMER_TYPE).toString())
                    rvScanHistory.adapter = scanHistoryAdapter
                    rvScanHistory.layoutManager = LinearLayoutManager(activity) as RecyclerView.LayoutManager?
                } else {
                    tvNoData.visibility = View.VISIBLE
                    rvScanHistory.visibility = View.GONE
                }
            }
            ScanListFailure -> {
                showProgress(false)
                tvNoData.visibility = View.VISIBLE
                rvScanHistory.visibility = View.GONE
            }
            is CommodityList -> {
                showProgress(false)
                commodityName.clear()
                if (viewModel.commodityList.size > 0) {
                    commodityName.add(getString(R.string.select_commodity))
                    for (i in 0 until viewModel.commodityList.size) {
                        commodityName.add(viewModel.commodityList[i].commodity_name.toString())
                    }
                } else {
                    commodityName.add("No Data")
                }
                Utils.setSpinnerAdapter(requireContext(), commodityName, spCommodity)
            }
            is CommodityError -> {
                showProgress(false)
            }
            is InstallationCentersSuccess -> {
                showProgress(false)
                instCenterName.clear()
                if (viewModel.installationCenterList.size > 0) {
                    instCenterName.add("Select Installation")
                    for (i in 0 until viewModel.installationCenterList.size) {
                        instCenterName.add(viewModel.installationCenterList[i].inst_center_name.toString())
                    }
                } else {
                    instCenterName.add("No Data")
                }
                Utils.setSpinnerAdapter(requireContext(), instCenterName, spInsCenter)
            }
            is RegionSuccess -> {
                showProgress(false)
                regionName.clear()
                if (viewModel.regionList.size > 0) {
                    regionName.add(getString(R.string.select_region))
                    for (i in 0 until viewModel.regionList.size) {
                        regionName.add(viewModel.regionList[i].region_name.toString())
                    }
                } else {
                    regionName.add("No Data")
                }
                Utils.setSpinnerAdapter(requireContext(), regionName, spRegion)
            }
            is ApprovalSuccess -> {
                showProgress(false)
                onResume()
            }
            is ApprovalFailure -> {
                showProgress(false)
                onResume()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getScanHistory(data)
    }

    private fun showProgress(progressStatus: Boolean) {
        if (progressStatus)
            progress.visibility = View.VISIBLE
        else
            progress.visibility = View.GONE
    }

    private fun allFilterApis(customerId: Int) {
        viewModel.getCommodityListVm(customerId)
        viewModel.getInstallationCentersVM(customerId)
        viewModel.getRegionVM(customerId)
    }

    private fun filterDialog() {
        allFilterApis(91)
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.layout_filter, null)
        dialogBuilder.setView(dialogView)
        tvStartDate = dialogView.findViewById(R.id.tvStartDate)
        tvEndDate = dialogView.findViewById(R.id.tvEndDate)
        spCommodity = dialogView.findViewById(R.id.spCommodity)
        spInsCenter = dialogView.findViewById(R.id.spInsCenter)
        spRegion = dialogView.findViewById(R.id.spRegion)
        spDeviceType = dialogView.findViewById(R.id.spDeviceType)
        tvApply = dialogView.findViewById(R.id.tvApply)
        tvCancel = dialogView.findViewById(R.id.tvCancel)
        deviceTypeLayoutFilter = dialogView.findViewById(R.id.deviceTypeLayoutFilter)
        deviceTypeLayoutFilter.visibility = View.GONE
        spCommodity.onItemSelectedListener = this
        spInsCenter.onItemSelectedListener = this
        spRegion.onItemSelectedListener = this
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
        if (filterData["date_from"] != null)
            tvStartDate.text = Utils.timeStampDate(filterData["date_from"]!!.toLong())
        if (filterData["date_to"] != null)
            tvEndDate.text = Utils.timeStampDate(filterData["date_to"]!!.toLong())
        if (commodityPos != 0 && commodityName.size >= commodityPos)
            spCommodity.setSelection(commodityPos)
        if (insCenterPos != 0 && instCenterName.size >= insCenterPos)
            spInsCenter.setSelection(insCenterPos)
        if (regionPos != 0 && regionName.size >= regionPos)
            spRegion.setSelection(regionPos)
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
        datePicker.show(requireActivity().supportFragmentManager, datePicker.toString())
    }

    override fun onItemClick(pos: Int) {
        val intent = Intent(context(), ScanDetailActivity::class.java)
        val gson = Gson()
        val type = object : TypeToken<ScanData>() {}.type
        val json = gson.toJson(viewModel.scanList[pos], type)
        intent.putExtra("selectObject", json)
        intent.putExtra(Constants.FLOW, Constants.NAV_SCAN_HISTORY_ACTIVITY)
        startActivity(intent)
    }

    override fun editItem(pos: Int) {
        val deviceId = viewModel.scanList[pos].deviceId
        val deviceName = viewModel.scanList[pos].deviceName
        if (deviceId != 2 && !TextUtils.isEmpty(deviceName)) {
            fragmentTransition(SelectScanFragment
                    .newInstance(viewModel.scanList[pos].scanId, deviceId!!, deviceName), SELECT_SCAN_FRAGMENT)
        }
    }

    override fun onRejectClick(pos: Int) {
//        viewModel.setApproval(viewModel.scanList[pos].scanId!!.toInt(), 2)
        showProgress(true)
        showCustomDialog(viewModel.scanList[pos].scanId!!.toInt(), viewModel.scanList[pos].deviceId!!.toInt(), viewModel.scanList[pos].deviceName.toString(), "2")
    }

    override fun onApproveClick(pos: Int) {
        showProgress(true)
        showCustomDialog(viewModel.scanList[pos].scanId!!.toInt(), viewModel.scanList[pos].deviceId!!.toInt(), viewModel.scanList[pos].deviceName.toString(), "1")
//        viewModel.setApproval(viewModel.scanList[pos].scanId!!.toInt(), 1)
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
                showProgress(true)
                viewModel.getScanHistory(filterData)
            }
            tvCancel -> {
                alertDialog.dismiss()
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}

    override fun onItemSelected(spinner: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
        when (spinner) {
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
            spRegion -> {
                if (pos > 0) {
                    filterData["region_id"] = viewModel.regionList[pos - 1].region_id.toString()
                    regionPos = pos
                } else if (pos == 0) {
                    filterData.remove("region_id")
                }
            }
        }
    }

    private fun showCustomDialog(scanId: Int, deviceId: Int, deviceName: String, scanStatus: String) {
        val viewGroup: ViewGroup = requireActivity().findViewById(android.R.id.content)
        val dialogView: View = LayoutInflater.from(requireActivity()).inflate(R.layout.approve_rejectdialog, viewGroup, false)
        notificationdeviceId = deviceId
        notificationdeviceName = deviceName
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireActivity())
        builder.setView(dialogView)
        val alertDialog: androidx.appcompat.app.AlertDialog = builder.create()
        val tvYes = dialogView.findViewById<TextView>(R.id.btn_yes)
        val tvNo = dialogView.findViewById<TextView>(R.id.btn_no)
        val tvScanStatus = dialogView.findViewById<TextView>(R.id.tvScanStatus)
        val tvScanId = dialogView.findViewById<TextView>(R.id.tvScanId)
        val et_message = dialogView.findViewById<EditText>(R.id.et_message)
        tvScanId.text = "Scan Id: " + scanId.toString()
        if (scanStatus.equals("1")) {
            tvScanStatus.text = "Are you sure, you want to approve the scan?"
        } else if (scanStatus.equals("2")) {
            tvScanStatus.text = "Are you sure, you want to reject the scan?"
        }

        tvYes.setOnClickListener {
            viewModel.setApproval(scanId, scanStatus.toInt(), et_message.text.toString())
            alertDialog.dismiss()
        }
        tvNo.setOnClickListener {
            showProgress(false)
            alertDialog.dismiss()
        }
        alertDialog.show()
    }
}