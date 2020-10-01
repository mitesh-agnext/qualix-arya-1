package com.custom.app.ui.sample

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.base.app.ui.base.BaseFragment
import com.core.app.util.AlertUtil
import com.core.app.util.Util
import com.custom.app.CustomApp
import com.custom.app.R
import com.custom.app.data.model.farmer.upload.FarmerItem
import com.custom.app.data.model.section.LocationItem
import com.custom.app.databinding.FragmentSampleBinding
import com.custom.app.ui.home.HomeActivity
import com.custom.app.ui.scan.select.SelectScanFragment
import com.custom.app.util.Constants.KEY_SCAN_ID
import com.custom.app.util.Constants.SELECT_SCAN_FRAGMENT
import com.custom.app.util.Utils
import com.specx.device.util.Constants.KEY_DEVICE_ID
import com.specx.device.util.Constants.KEY_DEVICE_NAME
import com.specx.scan.data.model.analysis.AnalysisItem
import com.specx.scan.data.model.commodity.CommodityItem
import com.specx.scan.data.model.sample.SampleItem
import com.specx.scan.data.model.variety.VarietyItem
import com.specx.scan.util.Constants.KEY_FARMER
import com.user.app.data.UserManager
import org.parceler.Parcels
import javax.inject.Inject

class SampleFragment : BaseFragment(), SampleView {

    @Inject
    lateinit var userManager: UserManager

    @Inject
    lateinit var presenter: SamplePresenter

    @Inject
    lateinit var sampleInteractor: SampleInteractor

    private var deviceId = 0
    private var scanId: String? = null
    private var deviceName: String? = null
    private var batchId: String = ""
    private var callback: Callback? = null
    private var analysis: AnalysisItem? = null
    private lateinit var farmer: FarmerItem
    private lateinit var location: LocationItem
    private lateinit var commodity: CommodityItem
    private lateinit var controller: SampleController
    private var locations: List<LocationItem>? = null
    private lateinit var commodities: List<CommodityItem>
    private var varieties: List<VarietyItem>? = null
    private val timestamp = Util.getDatetime()
    private val sample = SampleItem("1", 1.0)
    private var scanDetail: ScanDetailRes? = null
    private lateinit var viewModel: DataFragmentViewModel
    private var _binding: FragmentSampleBinding? = null
    private val binding get() = _binding!!

    companion object {
        @JvmStatic
        fun newInstance(scanId: String?, deviceId: Int, deviceName: String, farmer: FarmerItem): SampleFragment {
            val fragment = SampleFragment()
            val args = Bundle()
            args.putString(KEY_SCAN_ID, scanId)
            args.putInt(KEY_DEVICE_ID, deviceId)
            args.putString(KEY_DEVICE_NAME, deviceName)
            args.putParcelable(KEY_FARMER, Parcels.wrap(farmer))
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        (requireActivity().application as CustomApp).homeComponent.inject(this)
        super.onAttach(context)
        callback = context as Callback
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSampleBinding.inflate(inflater, container, false)
        val rootView = binding.root

        val layoutManager = LinearLayoutManager(context())
        binding.includeRecyclerview.recyclerView.layoutManager = layoutManager
        binding.includeRecyclerview.recyclerView.setEmptyView(binding.includeEmpty.root)
        binding.includeRecyclerview.recyclerView.setHasFixedSize(true)
        setStep(1)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.view = this

        if (arguments != null) {
            scanId = requireArguments().getString(KEY_SCAN_ID)
            deviceId = requireArguments().getInt(KEY_DEVICE_ID)
            deviceName = requireArguments().getString(KEY_DEVICE_NAME)
            farmer = Parcels.unwrap(requireArguments().getParcelable(KEY_FARMER))

            if (activity != null) {
                (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                val toolbar = (activity as HomeActivity?)!!.binding.includeAppbar.includeToolbar.toolbar
                (toolbar.findViewById<View>(R.id.title) as TextView).text = deviceName
            }

            controller = SampleController(this, farmer, sample)
            binding.includeRecyclerview.recyclerView.adapter = controller.adapter
            sample.scanId = scanId

            generateBatchId()

            binding.swipeLayout.setOnRefreshListener { fetchData() }

            if (!TextUtils.isEmpty(batchId)) {
                presenter.fetchAnalyses(batchId)
            }
        }

        viewModel = ViewModelProvider(this, DataFragmentViewModelFactory(sampleInteractor))[DataFragmentViewModel::class.java]
        viewModel.sampleStateLiveData.observe(::getLifecycle, ::setViewState)

        fetchData()
    }

    private fun fetchData() {
        if (deviceId != 1 && deviceId != 4) {
            if (!TextUtils.isEmpty(scanId)) {
                viewModel.fetchScanDetail(scanId!!, batchId, deviceId)
            } else {
                viewModel.fetchLocations()
            }
        } else {
            viewModel.fetchScanDetail(scanId!!, batchId, deviceId)
            viewModel.fetchLocations()
        }
    }

    private fun setViewState(state: SampleState) {
        when (state) {
            is Loading -> showProgressBar()
            is Locations -> {
                locations = viewModel.locations.value
                controller.setLocations(locations)
                presenter.fetchCommodities()
            }
            is ScanDetail -> {
                hideProgressBar()
                scanDetail = viewModel.scanDetail.value

                if (farmer.code == "X") {
                    controller.setFarmerDetail(scanDetail?.farmer_detail)
                }

                sample.id = scanDetail?.sample_id
                sample.lotId = scanDetail?.lot_id
                sample.quantity = scanDetail?.weight
                sample.quantityUnit = scanDetail?.quantity_unit
                sample.truckNumber = scanDetail?.truck_number

                if (deviceId == 1 || deviceId == 4) {
                    controller.setSample(sample)
                    controller.setCommodity(scanDetail?.commodity_name)
                    presenter.fetchAnalyses(batchId)
                } else {
                    if (!TextUtils.isEmpty(scanDetail?.batch_id)) {
                        updateBatchId(scanDetail?.batch_id!!)
                        presenter.fetchAnalyses(batchId)
                        controller.setScanDetail(scanDetail)
                    }
                }
                controller.showProceedButton(true)
                binding.includeEmpty.root.visibility = View.GONE
                binding.includeRecyclerview.recyclerView.visibility = View.VISIBLE
            }
            is ShowResult -> {
                hideProgressBar()
                callback?.showResultScreen(batchId, deviceId)
            }
            is Error -> {
                hideProgressBar()
                showMessage(state.message!!)
            }
            is TokenExpire -> {
                AlertUtil.showToast(activity, getString(R.string.token_expire))
                Utils.tokenExpire(requireActivity())
            }
        }
    }

    private fun setStep(step: Int) {
        val fragment = parentFragmentManager.findFragmentByTag(SELECT_SCAN_FRAGMENT)
        if (fragment != null) {
            (fragment as SelectScanFragment).setStep(step)
        }
    }

    private fun generateBatchId() {
        if (TextUtils.isEmpty(scanId) || deviceId == 1 || deviceId == 4) {
            val batchId: String = String.format("%s_%s", userManager.userId, timestamp)
            updateBatchId(batchId)
        }
    }

    private fun updateBatchId(batchId: String) {
        this.batchId = batchId;
        controller.setBatchId(batchId)
    }

    override fun showEmptyView() {
        controller.showProceedButton(false)
        binding.includeRecyclerview.recyclerView.visibility = View.GONE
        binding.includeEmpty.root.visibility = View.VISIBLE
    }

    override fun showProgressBar() {
        binding.swipeLayout.isRefreshing = true
        binding.includeEmpty.root.visibility = View.GONE
    }

    override fun hideProgressBar() {
        binding.swipeLayout.isRefreshing = false
    }

    override fun showMessage(msg: String) {
        AlertUtil.showSnackBar(binding.swipeLayout, msg)
    }

    override fun showAnalysis(analysis: AnalysisItem) {
        this.analysis = analysis
        if (analysis.isDone) {
            controller.setProceedTitle(getString(R.string.btn_scan_results))
        } else {
            controller.setProceedTitle(getString(R.string.btn_proceed))
        }
    }

    override fun onLocationSelected(location: LocationItem) {
        this.location = location
    }

    override fun showCommodities(commodities: List<CommodityItem>) {
        this.commodities = commodities
        controller.setCommodities(commodities)
        controller.showProceedButton(true)
        binding.includeEmpty.root.visibility = View.GONE
        binding.includeRecyclerview.recyclerView.visibility = View.VISIBLE
    }

    override fun onCommoditySelected(commodity: CommodityItem) {
        this.commodity = commodity
        hideVarieties()
        presenter.fetchVarieties(commodity)
    }

    override fun showVarieties(varieties: List<VarietyItem>?) {
        this.varieties = varieties
        controller.setVarieties(varieties)
        binding.includeEmpty.root.visibility = View.GONE
        binding.includeRecyclerview.recyclerView.visibility = View.VISIBLE
    }

    override fun onVarietySelected(variety: VarietyItem) {
        sample.variety = variety
    }

    override fun hideVarieties() {
        varieties = null
        showVarieties(varieties)
    }

    override fun onProceed() {
        if (!TextUtils.isEmpty(sample.lotId)) {
            if (!TextUtils.isEmpty(sample.id)) {
                if (sample.quantity != null && sample.quantity > 0) {
                    if (deviceId != 2 || analysis!!.isDone) {
                        if (deviceId == 1 || deviceId == 4) {
                            viewModel.updateScanDetail(batchId, farmer, sample, scanDetail!!)
                        } else {
                            callback?.showResultScreen(batchId, deviceId)
                        }
                    } else {
                        if (!TextUtils.isEmpty(userManager.savedDevice)) {
//                        if (analyticsProcuredValid(commodity)) {
                            callback?.showScanDialog(farmer.code, batchId, sample, commodity)
//                        } else {
//                            showMessage("Scans count expired!")
//                        }
                        } else {
                            callback?.showSelectDeviceDialog()
                        }
                    }
                } else {
                    showMessage(getString(R.string.empty_quantity))
                }
            } else {
                showMessage(getString(R.string.empty_sample_id))
            }
        } else {
            showMessage(getString(R.string.empty_lot_id))
        }
    }

    override fun onQuantityUnitSelected() {
        controller.requestModelBuild()
    }

    override fun onBatchIdClicked() {
        Util.copyToClipboard(context(), "Batch ID", batchId)
        AlertUtil.showToast(context(), getString(R.string.batch_copied_msg))
    }

    override fun onSampleIdChanged() {
        generateBatchId()
        presenter.updateAnalyses(batchId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.destroy()
        _binding = null
        setStep(0)
    }

    override fun onDetach() {
        callback = null
        super.onDetach()
    }

    interface Callback {
        fun showSelectDeviceDialog()
        fun showResultScreen(batchId: String, deviceId: Int)
        fun showScanDialog(farmerCode: String, batchId: String, sample: SampleItem, commodity: CommodityItem)
    }
}