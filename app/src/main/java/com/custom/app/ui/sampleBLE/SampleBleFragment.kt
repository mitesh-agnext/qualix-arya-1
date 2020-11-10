package com.custom.app.ui.sampleBLE

import android.bluetooth.BluetoothAdapter
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.base.app.ui.base.BaseFragment
import com.custom.app.R
import com.custom.app.data.model.farmer.upload.FarmerItem
import com.custom.app.ui.sampleBleResult.BleResult
import com.custom.app.ui.sampleBleResult.SampleBleResultFragment
import com.custom.app.ui.scan.select.SelectScanFragment
import com.custom.app.util.Constants
import kotlinx.android.synthetic.main.fragment_sample_ble.*
import org.parceler.Parcels
import javax.inject.Inject

class SampleBleFragment : BaseFragment(), View.OnClickListener, AdapterView.OnItemSelectedListener {
    private var deviceId = 0
    private var scanId: String? = null
    private var deviceName: String? = null
    private lateinit var farmer: FarmerItem
    private lateinit var viewModel: SampleBleViewModel

    @Inject
    lateinit var interactor: SampleBleInteractor

    companion object {
        @JvmStatic
        fun newInstance(scanId: String?, deviceId: Int, deviceName: String, farmer: FarmerItem): SampleBleFragment {
            val fragment = SampleBleFragment()
            val args = Bundle()
            args.putString(Constants.KEY_SCAN_ID, scanId)
            args.putInt(com.specx.device.util.Constants.KEY_DEVICE_ID, deviceId)
            args.putString(com.specx.device.util.Constants.KEY_DEVICE_NAME, deviceName)
            args.putParcelable(com.specx.scan.util.Constants.KEY_FARMER, Parcels.wrap(farmer))
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_sample_ble, container, false)
        setStep(1)

        viewModel = ViewModelProvider(this,
                SampleBleViewModel.SampleBleViewModelFactory(interactor))[SampleBleViewModel::class.java]
        viewModel.sampleBleStateState.observe(::getLifecycle, ::setViewState)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            scanId = requireArguments().getString(Constants.KEY_SCAN_ID)
            deviceId = requireArguments().getInt(com.specx.device.util.Constants.KEY_DEVICE_ID)
            deviceName = requireArguments().getString(com.specx.device.util.Constants.KEY_DEVICE_NAME)
            farmer = Parcels.unwrap(requireArguments().getParcelable(com.specx.scan.util.Constants.KEY_FARMER))
        }
        bnProceed.setOnClickListener(this)
        spLocation.onItemSelectedListener = this
        spCommodity.onItemSelectedListener = this
        viewModel.getLocation()
        viewModel.getCommodity()
        permissionCheck()
    }

    private fun setViewState(state: SampleBleState) {
        when (state) {
            SampleBleState.loading -> progress.visibility = View.VISIBLE
            SampleBleState.locationSuccess -> {
                progress.visibility = View.GONE
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, viewModel.locationArray)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spLocation.adapter = adapter
            }
            SampleBleState.locationFailure -> {
                progress.visibility = View.GONE
                showMessage("Error to get Locations")
            }
            SampleBleState.commoditySuccess->{
                progress.visibility = View.GONE
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, viewModel.commodityArray)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spCommodity.adapter = adapter
            }
            SampleBleState.commodityFailure->{
                progress.visibility = View.GONE
                showMessage("Error to get Commodity")
            }
            SampleBleState.tokenExpired -> {
            }
        }
    }

    private fun permissionCheck() {
        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        } else if (!mBluetoothAdapter.isEnabled) {
            // Bluetooth is not enabled :)
        } else {
            // Bluetooth is enabled
        }
    }

    // Function to check and request permission
    fun checkPermission(permission: String, requestCode: Int) {

        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(requireContext(), permission) === PackageManager.PERMISSION_DENIED) {
            ActivityCompat
                    .requestPermissions(
                            requireActivity(), arrayOf(permission),
                            requestCode)
        } else {
            Toast.makeText(requireContext(),
                    "Permission already granted",
                    Toast.LENGTH_SHORT)
                    .show()
        }
    }

    private fun setStep(step: Int) {
        val fragment = parentFragmentManager.findFragmentByTag(Constants.SELECT_SCAN_FRAGMENT)
        if (fragment != null) {
            (fragment as SelectScanFragment).setStep(step)
        }
    }

    override fun showMessage(msg: String?) {
        super.showMessage(msg)
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun showProgressBar() {
        super.showProgressBar()
        progress.visibility = View.VISIBLE
    }

    override fun onClick(view: View?) {
        when (view) {
            bnProceed -> {
                showProgressBar()
                showMessage("Connecting to BLE device")
                val bleResult1 = BleResult("Commodity", "Rice")
                val bleResult2 = BleResult("Weight", etQuantity.text.toString())
                val bleResult3 = BleResult("Mositure", "24")
                val bleResult4 = BleResult("Temperature", "12")
                val resultList = ArrayList<BleResult>()
                resultList.add(bleResult1)
                resultList.add(bleResult2)
                resultList.add(bleResult3)
                resultList.add(bleResult4)
                Handler(Looper.getMainLooper()).postDelayed({

                    fragmentTransition(R.id.layout_content,
                            SampleBleResultFragment.newInstance("ScanID", deviceId, resultList), Constants.SAMPLE_BLE_RESULT)
                }, 3000)
            }
        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, spinner: View?, pos: Int, p3: Long) {
        when(spinner)
        {
            spLocation->{
                tvLocId.text=viewModel.locationArray[pos].code
//                when (pos) {
//                    0 -> {
//                    }
//                }
            }
            spCommodity->{
                when (pos) {
                    0 -> {
                    }
                }
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }


}