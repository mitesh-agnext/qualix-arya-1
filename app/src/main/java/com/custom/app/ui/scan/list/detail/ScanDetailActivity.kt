package com.custom.app.ui.scan.list.detail

import android.os.Bundle
import android.text.TextUtils
import android.view.View
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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.specx.device.util.Constants.KEY_DEVICE_ID
import com.specx.device.util.Constants.KEY_DEVICE_NAME
import kotlinx.android.synthetic.main.activity_scan_detail.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ScanDetailActivity : BaseActivity(), View.OnClickListener {

    @Inject
    lateinit var interactor: ScanDetailInteractor

    lateinit var testObject: ScanData
    private lateinit var viewModel: ScanDetailVM

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_detail)
        initView()
    }

    fun initView() {
        toolbar.title = getString(R.string.scan_history)
        setSupportActionBar(toolbar)

        fbScan.setOnClickListener(this)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        viewModel = ViewModelProvider(this, ScanDetailViewModelFactory(interactor))[ScanDetailVM::class.java]
        viewModel.scanDetailState.observe(::getLifecycle, ::setViewState)
        setData()
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
                onRestart()
            }
            is ApprovalFailure -> {
                setProgress(false)
                onRestart()
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

    override fun onRestart() {
        super.onRestart()
        setData()
    }

    private fun setData() {
        //getting data from intent
        val selectObject = intent.getStringExtra("selectObject")
        val customerType = intent.getStringExtra("customerType")
        val gson = Gson()
        if (selectObject != null) {
            val type = object : TypeToken<ScanData>() {}.type
            testObject = gson.fromJson(selectObject, type)
        }

        if (testObject != null) {
            tvScore.text = testObject.qualityScore
            if (testObject.commodityId != null)
                tvCommodity.text = testObject.commodityName
            tvScanId.text = testObject.scanId
            if (testObject.totalCount != null) {
                lnTotalCount.visibility = View.VISIBLE
                tvTotalCount.text = testObject.totalCount
            } else
                lnTotalCount.visibility = View.GONE
            if (testObject.dateDone != null) {
                val itemLong = (testObject.dateDone!!.toLong() / 1000)
                val d = Date(itemLong * 1000L)
                val itemDateStr: String = SimpleDateFormat("dd-MMM HH:mm").format(d)
                tvDoneOn.text = itemDateStr
            }
            if (testObject.batchId != null)
                tvDoneBy.text = testObject.batchId
            scanDetailRV()

            if (testObject.deviceId == 2 || TextUtils.isEmpty(testObject.deviceName)) {
                fbScan.visibility = View.GONE
            }

            lnRejectScanSetail.visibility = View.VISIBLE
            view.visibility = View.VISIBLE
            lnApproveScanSetail.isClickable = true
            lnRejectScanSetail.isClickable = true

            if (testObject.approval == 0 || testObject.approval == null) {
                lnApproveScanSetail.isClickable = true
                lnRejectScanSetail.isClickable = true
                tv_approveScanSetail.setTextColor(resources.getColor(R.color.dark_green))
                tv_rejectScanSetail.setTextColor(resources.getColor(R.color.red))

            } else if (testObject.approval == 1) {
                lnApproveScanSetail.isClickable = false
                lnRejectScanSetail.isClickable = true
                tv_approveScanSetail.text = "Approved"
                tv_approveScanSetail.setTextColor(resources.getColor(R.color.dark_text_color))
                tv_rejectScanSetail.setTextColor(resources.getColor(R.color.red))

            } else if (testObject.approval == 2) {
                lnApproveScanSetail.isClickable = true
                lnRejectScanSetail.isClickable = false
                tv_approveScanSetail.setTextColor(resources.getColor(R.color.dark_green))
                tv_rejectScanSetail.setTextColor(resources.getColor(R.color.dark_text_color))
                tv_rejectScanSetail.text = "Rejected"
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
                bundle.putString(Constants.KEY_SCAN_ID, testObject.scanId)
                bundle.putString(KEY_DEVICE_ID, testObject.deviceId.toString())
                bundle.putString(KEY_DEVICE_NAME, testObject.deviceName)
                Utils.startActivityWithLoad(this, HomeActivity::class.java, bundle, true)
            }
            lnApproveScanSetail -> {
                setProgress(true)
                viewModel.setApproval(testObject.scanId!!.toInt(), 1)
            }
            lnRejectScanSetail -> {
                viewModel.setApproval(testObject.scanId!!.toInt(), 2)
            }
        }
    }
}