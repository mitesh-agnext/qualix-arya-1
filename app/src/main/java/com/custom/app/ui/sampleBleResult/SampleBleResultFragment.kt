package com.custom.app.ui.sampleBleResult

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.base.app.ui.base.BaseFragment
import com.custom.app.CustomApp
import com.custom.app.R
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.sampleBLE.SampleBleState
import com.custom.app.ui.scan.select.SelectScanFragment
import com.custom.app.util.Constants
import kotlinx.android.synthetic.main.fragment_sample_ble.*
import kotlinx.android.synthetic.main.fragment_sample_ble_result.*
import org.parceler.Parcels
import javax.inject.Inject

class SampleBleResultFragment : BaseFragment(), View.OnClickListener {
    private var deviceId = 0
    private var scanId: String? = null
    private var resultList: ArrayList<BleResult>? = null
    private lateinit var viewModel: SampleBleResultVM
    @Inject
    lateinit var interactor: SampleBleResultInteractor


    companion object {
        val KEY_SCAN_ID: String = "KEY_SCAN_ID"
        val KEY_DEVICE_ID: String = "KEY_DEVICE_ID"
        val KEY_BLE_RESULT: String = "KEY_BLE_RESULT"

        @JvmStatic
        fun newInstance(scanId: String?, deviceId: Int, bleResult: ArrayList<BleResult>): SampleBleResultFragment {
            val fragment = SampleBleResultFragment()
            val args = Bundle()
            args.putString(KEY_SCAN_ID, scanId)
            args.putInt(KEY_DEVICE_ID, deviceId)
            args.putParcelable(KEY_BLE_RESULT, Parcels.wrap(bleResult))
            fragment.arguments = args
            return fragment
        }
    }
    override fun onAttach(context: Context) {
        (requireActivity().application as CustomApp).homeComponent.inject(this)
        super.onAttach(context)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_sample_ble_result, container, false)
        viewModel = ViewModelProvider(this,
                SampleBleResultVM.SampleBleResultVMFactory(interactor))[SampleBleResultVM::class.java]
        viewModel.sampleBleResultState.observe(::getLifecycle, ::updateUI)
        return view
    }
    private fun updateUI(screenState: ScreenState<SampleBleResultState>?) {
        when (screenState) {
            ScreenState.Loading -> progress.visibility = View.VISIBLE
            is ScreenState.Render -> setViewState(screenState.renderState)
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            scanId = requireArguments().getString(KEY_SCAN_ID)
            deviceId = requireArguments().getInt(KEY_DEVICE_ID)
            resultList = Parcels.unwrap(requireArguments().getParcelable(KEY_BLE_RESULT))
        }
        bnDone.setOnClickListener(this)
        bnShare.setOnClickListener(this)
        setData()
        setStep(2)
    }

    fun setData() {
        rvResult.adapter = SampleBleAdapter(requireContext(), resultList!!)
        rvResult.layoutManager = LinearLayoutManager(requireContext())
        tvToken.text="Token: ${generateToken()}"
    }

    private fun setViewState(state: SampleBleResultState) {
        when (state) {
            SampleBleResultState.loading -> {}
            SampleBleResultState.postScanSuccess->{}
            SampleBleResultState.postScanFailure->{}
        }}
            private fun setStep(step: Int) {
        val fragment = parentFragmentManager.findFragmentByTag(Constants.SELECT_SCAN_FRAGMENT)
        if (fragment != null) {
            (fragment as SelectScanFragment).setStep(step)
        }
    }

    fun generateToken(): String {
        val randomPIN = (Math.random() * 9000).toInt() + 1000
        return randomPIN.toString()
    }

    override fun onClick(view: View?) {
        when (view) {
            bnDone -> {
                val request= HashMap<String, Any>()
                request["sample_id"] = "12"
                request["client_id"] = "12"
                request["commodity_name"] = "12"
                request["moisture"] = "12"
                request["temperature"] = "12"

                viewModel.postBleScan(request)
            }
            bnShare -> {
            }
        }
    }

}