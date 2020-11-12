package com.custom.app.ui.sampleBleResult

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.base.app.ui.base.BaseActivity
import com.core.app.util.AlertUtil
import com.custom.app.CustomApp
import com.custom.app.R
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.home.HomeActivity
import com.custom.app.util.Constants
import com.custom.app.util.Utils.Companion.startActivityWithLoad
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_sample_ble.*
import kotlinx.android.synthetic.main.fragment_sample_ble_result.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class SampleBleActivity : BaseActivity(), View.OnClickListener {

    private var deviceId: String? = null
    private var scanId: String? = null
    private lateinit var viewModel: SampleBleResultVM
    private var testObject: ArrayList<BleResult> = ArrayList()
    var sampleId: String? = null
    var quantity: String? = null
    var truckNumber: String? = null
    var randomPIN: Int? = null
    @Inject
    lateinit var interactor: SampleBleResultInteractor

    companion object {
        val KEY_SCAN_ID: String = "KEY_SCAN_ID"
        val KEY_DEVICE_ID: String = "KEY_DEVICE_ID"
        val KEY_BLE_RESULT: String = "KEY_BLE_RESULT"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_sample_ble_result)

        val selectObject = intent.getStringExtra("selectObject")
        val gson = Gson()
        if (selectObject != null) {
            val type = object : TypeToken<ArrayList<BleResult>>() {}.type
            testObject = gson.fromJson(selectObject, type)
        }
        scanId = intent.getStringExtra(KEY_SCAN_ID)
        deviceId = intent.getStringExtra(KEY_DEVICE_ID)
        sampleId = intent.getStringExtra("SampleId")
        truckNumber = intent.getStringExtra("TruckNumber")
        quantity = intent.getStringExtra("Quantity")
        viewModel = ViewModelProvider(this, SampleBleResultVM.SampleBleResultVMFactory(interactor))[SampleBleResultVM::class.java]
        viewModel.sampleBleResultState.observe(::getLifecycle, ::updateUI)

        bnDone.setOnClickListener(this)
        bnShare.setOnClickListener(this)
        setData()
        stepsView.setStepTitles(Arrays.asList("Scan", "Data", "Results"))
        stepsView.visibility = View.VISIBLE
        setStep(0)
        setStep(1)
        setStep(2)
    }

    private fun updateUI(screenState: ScreenState<SampleBleResultState>?) {
        when (screenState) {
            ScreenState.Loading -> progress.visibility = View.VISIBLE
            is ScreenState.Render -> setViewState(screenState.renderState)
        }
    }

    fun setData() {
//        val bleresult = BleResult("Weight", quantity.toString());
        testObject.removeAt(1)
        testObject.removeAt(2)
//        testObject.add(1, bleresult)
        rvResult.adapter = SampleBleAdapter(this, testObject)
        rvResult.layoutManager = LinearLayoutManager(this)
        tvToken.text = "Token: ${generateToken()}"
    }

    private fun setViewState(state: SampleBleResultState) {
        when (state) {
            SampleBleResultState.loading -> {
                setProgress(true)
            }
            SampleBleResultState.postScanSuccess -> {
                setProgress(false)

                AlertUtil.showActionAlertDialog(this, "", "Data posted successfully !",
                        false, getString(R.string.btn_ok)) { dialog: DialogInterface?, which: Int ->
                    val intent = Intent(this, HomeActivity::class.java)
                    val bundle = Bundle()
                    bundle.putString(Constants.FLOW, Constants.NAV_SPLASH)
                    intent.putExtras(bundle)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()

//                    startActivityWithLoad(this, HomeActivity::class.java, bundle, true)
                }
            }
            SampleBleResultState.postScanFailure -> {
                setProgress(false)
                AlertUtil.showToast(this, viewModel.message)
                finish()
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

    fun setStep(step: Int) {
        if (stepsView != null) {
            if (step == 0) {
                stepsView.resetView()
            } else {
                stepsView.markCurrentAsDone()
                stepsView.selectStep(step)
            }
        }
    }

    fun generateToken(): String {
        randomPIN = (Math.random() * 9000).toInt() + 1000
        return randomPIN.toString()
    }

    override fun onClick(view: View?) {
        when (view) {
            bnDone -> {
                val request = HashMap<String, String>()
                request["sample_id"] = sampleId!!
                request["client_id"] = userManager.customerId
                request["commodity_name"] = testObject[0].Value
                request["moisture"] = testObject[1].Value
                request["token"] = generateToken()
                request["truck_number"] = truckNumber.toString()
                viewModel.postBleScan(request)
            }
            bnShare -> {
            }
        }
    }
}