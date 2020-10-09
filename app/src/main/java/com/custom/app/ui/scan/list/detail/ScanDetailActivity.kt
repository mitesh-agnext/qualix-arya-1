package com.custom.app.ui.scan.list.detail

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.base.app.ui.base.BaseActivity
import com.core.app.util.AlertUtil
import com.custom.app.CustomApp
import com.custom.app.R
import com.custom.app.data.model.scanhistory.ScanData
import com.custom.app.ui.home.HomeActivity
import com.custom.app.util.Constants
import com.custom.app.util.Utils
import com.custom.app.util.Utils.Companion.startActivityWithLoadNoBackStack
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.specx.device.util.Constants.KEY_DEVICE_ID
import com.specx.device.util.Constants.KEY_DEVICE_NAME
import kotlinx.android.synthetic.main.activity_scan_detail.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import javax.inject.Inject

class ScanDetailActivity : BaseActivity(), View.OnClickListener {

    @Inject
    lateinit var interactor: ScanDetailInteractor

    lateinit var testObject: ScanData
    private lateinit var viewModel: ScanDetailVM
    var scanId: String? = null
    var deviceId: String? = null
    var deviceName: String? = null
    var scanStatus: String? = null
    var notificationdeviceId: Int? = null
    var notificationdeviceName: String? = null
    var flow: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_detail)
        initView()
    }

    fun initView() {
        toolbar.title = "Scan Detail"
        setSupportActionBar(toolbar)

        fbScan.setOnClickListener(this)
        lnApproveScanDetail.setOnClickListener(this)
        lnRejectScanDetail.setOnClickListener(this)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        testObject = ScanData()
        viewModel = ViewModelProvider(this, ScanDetailViewModelFactory(interactor))[ScanDetailVM::class.java]
        viewModel.scanDetailState.observe(::getLifecycle, ::setViewState)

        flow = intent.getStringExtra(Constants.FLOW)
        if (flow == Constants.NAV_NOTIFICATION) {
            scanId = intent.getStringExtra(Constants.KEY_SCAN_ID)
            deviceId = intent.getStringExtra(KEY_DEVICE_ID)
            deviceName = intent.getStringExtra(KEY_DEVICE_NAME)
            scanStatus = intent.getStringExtra(Constants.KEY_SCAN_STATUS)
            tvScanId.text = scanId
            viewModel.getScanDetail(scanId!!)
        } else if (flow == Constants.NAV_SCAN_HISTORY_ACTIVITY) {
            //getting data from intent
            val selectObject = intent.getStringExtra("selectObject")
            val customerType = intent.getStringExtra("customerType")
            val gson = Gson()
            if (selectObject != null) {
                val type = object : TypeToken<ScanData>() {}.type
                testObject = gson.fromJson(selectObject, type)
            }
//            setData(testObject)
            scanId = testObject.scanId!!
            scanStatus = testObject.approval.toString()
            deviceName = testObject.deviceName
            deviceId = testObject.deviceId.toString()
            tvScanId.text = scanId
            viewModel.getScanDetail(testObject.scanId!!)
        }
    }

    private fun setViewState(state: ScanDetailState) {
        when (state) {
            Loading -> {
                setProgress(true)
            }
            is Token -> {
                setProgress(false)
                AlertUtil.showToast(this, getString(R.string.token_expire))
                userManager.clearData()
                Utils.tokenExpire(this)
            }
            is ApprovalSuccess -> {
                setProgress(false)
                setForward()
            }
            is ApprovalFailure -> {
                setProgress(false)
            }
            is FetchScanFailure -> {
                setProgress(false)
                Log.e("error", "error")
            }
            is FetchScanSuccess -> {
                setProgress(false)
                testObject = viewModel.scanDetail
                setData(testObject)
            }
        }
    }

    fun setForward() {
        val bundle = Bundle()
        if (flow == Constants.NAV_NOTIFICATION) {
            bundle.putString(Constants.FLOW, Constants.NAV_NOTIFICATION)
        } else {
            bundle.putString(Constants.FLOW, Constants.NAV_SCAN_HISTORY_ACTIVITY)
        }
        bundle.putString(Constants.KEY_SCAN_ID, scanId)
        bundle.putString(KEY_DEVICE_ID, deviceId.toString())
        bundle.putString(KEY_DEVICE_NAME, deviceName)
        Utils.startActivityWithLoad(this, HomeActivity::class.java, bundle, true)
    }

    private fun setProgress(isLoading: Boolean) {
        if (isLoading) {
            progress.visibility = View.VISIBLE
        } else {
            progress.visibility = View.GONE
        }
    }

    private fun setData(testObject: ScanData) {

        if (testObject != null) {
            tvScore.text = testObject.qualityScore
            if (testObject.commodityId != null)
                tvCommodity.text = testObject.commodityName
            if (testObject.totalCount != null) {
                lnTotalCount.visibility = View.VISIBLE
                tvTotalCount.text = testObject.totalCount
            } else
                lnTotalCount.visibility = View.GONE
//            if (testObject.dateDone != null) {
//                val itemLong = (testObject.dateDone!!.toLong() / 1000)
//                val d = Date(itemLong * 1000L)
//                val itemDateStr: String = SimpleDateFormat("dd-MMM HH:mm").format(d)
//                tvDoneOn.text = itemDateStr
//            }
            tvDoneOn.text = testObject.weight
            if (testObject.batchId != null)
                tvDoneBy.text = testObject.batchId
            if (testObject.analysisResults != null) {
                scanDetailRV()
            } else {

            }

            if (testObject.deviceId == 2 || TextUtils.isEmpty(testObject.deviceName)) {
                fbScan.visibility = View.GONE
            }

            lnRejectScanDetail.visibility = View.VISIBLE
            view.visibility = View.VISIBLE
            lnApproveScanDetail.isClickable = true
            lnRejectScanDetail.isClickable = true

            if (testObject.approval == 0 || testObject.approval == null) {
                lnApproveScanDetail.isClickable = true
                lnRejectScanDetail.isClickable = true
                tv_approveScanDetail.setTextColor(resources.getColor(R.color.dark_green))
                tv_rejectScanDetail.setTextColor(resources.getColor(R.color.red))

            } else if (testObject.approval == 1) {
                lnApproveScanDetail.isClickable = false
                lnRejectScanDetail.isClickable = true
                tv_approveScanDetail.text = "Approved"
                tv_approveScanDetail.setTextColor(resources.getColor(R.color.dark_text_color))
                tv_rejectScanDetail.setTextColor(resources.getColor(R.color.red))

            } else if (testObject.approval == 2) {
                lnApproveScanDetail.isClickable = true
                lnRejectScanDetail.isClickable = false
                tv_approveScanDetail.setTextColor(resources.getColor(R.color.dark_green))
                tv_rejectScanDetail.setTextColor(resources.getColor(R.color.dark_text_color))
                tv_rejectScanDetail.text = "Rejected"
            }
        }
    }

    private fun scanDetailRV() {
        rvScanInfo.layoutManager = LinearLayoutManager(this)
        rvScanInfo.adapter = ScanDetailAdapter(this, testObject.analysisResults!!)
    }

    override fun onClick(view: View?) {
        when (view) {
            fbScan -> {
                val bundle = Bundle()
                bundle.putString(Constants.FLOW, Constants.NAV_SCAN_HISTORY_ACTIVITY)
                bundle.putString(Constants.KEY_SCAN_ID, scanId)
                bundle.putString(KEY_DEVICE_ID, deviceId.toString())
                bundle.putString(KEY_DEVICE_NAME, deviceName)
                Utils.startActivityWithLoad(this, HomeActivity::class.java, bundle, true)
            }
            lnApproveScanDetail -> {
                setProgress(true)
                showCustomDialog(scanId!!.toInt(), deviceId!!, deviceName.toString(), "1")
            }
            lnRejectScanDetail -> {
                setProgress(true)
                showCustomDialog(scanId!!.toInt(), deviceId!!, deviceName.toString(), "2")
            }
        }
    }

    private fun showCustomDialog(scanId: Int, deviceId: String, deviceName: String, scanStatus: String) {
        val viewGroup: ViewGroup = findViewById(android.R.id.content)
        val dialogView: View = LayoutInflater.from(this).inflate(R.layout.approve_rejectdialog, viewGroup, false)
        notificationdeviceName = deviceName
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
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
            setProgress(false)
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        setProgress(false)
        if (flow == Constants.NAV_NOTIFICATION) {
            val bundle = Bundle()
            bundle.putString(Constants.FLOW, Constants.NAV_SPLASH)
            startActivityWithLoadNoBackStack(this, HomeActivity::class.java, bundle, true)
        } else {
            setProgress(false)
            finish()
        }
    }
}