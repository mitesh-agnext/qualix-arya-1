package com.custom.app.ui.dashboard

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.base.app.ui.base.BaseFragment
import com.core.app.util.AlertUtil
import com.custom.app.CustomApp
import com.custom.app.R
import com.custom.app.data.model.quantity.QuantityDetailRes
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.business.BusinessFragment
import com.custom.app.ui.collection.CollectionFragment
import com.custom.app.ui.quality.QualityMapFragment
import com.custom.app.ui.supplier.SupplierFragment
import com.custom.app.util.Constants
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.user.app.data.UserManager
import kotlinx.android.synthetic.main.fragment_dashboard.*
import org.parceler.Parcels
import javax.inject.Inject

class DashboardFragment : BaseFragment(), DashboardView, OnTabSelectedListener {

    private var detail: QuantityDetailRes? = null
    private var centerDetails: CenterData? = null
    private var categoryId: String? = null
    private var startDate: String? = null
    private var endDate: String? = null
    private var centerId: String? = null
    private var deviceType: String? = null
    private var deviceSerialNo: String? = null
    private var quantity: String? = null
    var tabLayout: TabLayout? = null
    private lateinit var viewModel: DashboardViewModel
    @Inject
    lateinit var userManager: UserManager
    @Inject
    lateinit var interactor: DashboardInteractor

    override fun onAttach(context: Context) {
        (requireActivity().application as CustomApp).homeComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_dashboard, container, false)
        tabLayout = rootView.findViewById(R.id.tab_layout)
        tabLayout!!.addOnTabSelectedListener(this)

        viewModel = ViewModelProvider(this, DashboardFragmentViewModelFactory(interactor))[DashboardViewModel::class.java]
        viewModel.dashboardFragmentState.observe(::getLifecycle, ::updateUI)

        return rootView

    }

    private fun updateUI(screenStateDevice: ScreenState<DashboardFragmentState>?) {
        when (screenStateDevice) {
            ScreenState.Loading -> {
//                progressListDevices.visibility = View.VISIBLE
            }
            is ScreenState.Render -> processLoginState(screenStateDevice.renderState)
        }
    }

    private fun processLoginState(renderStateDevice: DashboardFragmentState) {
//        progressListDevices.visibility = View.GONE

        when (renderStateDevice) {
            DashboardFragmentState.ListCenterFailure -> {
                AlertUtil.showSnackBar(mainLayout, "Unable to get data", 2000)
            }
            DashboardFragmentState.ListCenterSuccess -> {
                centerDetails = viewModel.centerList.value
                showBusinessScreen(centerDetails)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            detail = Parcels.unwrap<QuantityDetailRes>(requireArguments().getParcelable(Constants.KEY_QUANTITY_DETAIL))
            categoryId = requireArguments().getString(Constants.KEY_CATEGORY_ID)
            startDate = requireArguments().getString(Constants.KEY_START_DATE)
            endDate = requireArguments().getString(Constants.KEY_END_DATE)
            centerId = requireArguments().getString(Constants.KEY_CENTER_ID)
            deviceType = requireArguments().getString(Constants.KEY_DEVICE_TYPE)
            deviceSerialNo = requireArguments().getString(Constants.KEY_DEVICE_SERIAL_NO)
            quantity = requireArguments().getString(Constants.KEY_TOTAL_QUANTITY)

            if (centerId != null && deviceType != null){
                val map = HashMap<String,String>()
                map.put("date_from",startDate.toString())
                map.put("date_to",endDate.toString())
                map.put("commodity_category_id",categoryId.toString())
                map.put("customer_id",userManager.customerId.toString())
                map.put("device_serial_number", deviceSerialNo.toString())
                map.put("device_type",deviceType.toString())
                map.put("inst_center_id",centerId.toString())

                viewModel.onGetCenterData(map)
            }
            else {
                showBusinessScreen(centerDetails)
            }

        }
    }

    override fun onTabSelected(tab: TabLayout.Tab) {
        when (tab.position) {
            0 -> showBusinessScreen(centerDetails)
            1 -> showCollectionScreen()
//            3 -> showQualityMapScreen()
            2 -> showSupplierScreen()
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab) {}

    override fun onTabReselected(tab: TabLayout.Tab) {}

    private fun showBusinessScreen(centerDetails: CenterData?) {
        if (!TextUtils.isEmpty(centerId) && !TextUtils.isEmpty(deviceType)) {
            replaceFragment(R.id.layout_child, BusinessFragment.newInstance(detail, centerDetails, categoryId, startDate, endDate, centerId, deviceType, deviceSerialNo, quantity), Constants.BUSINESS_FRAGMENT)
        } else {
            replaceFragment(R.id.layout_child, BusinessFragment.newInstance(detail, centerDetails, categoryId, startDate, endDate), Constants.BUSINESS_FRAGMENT)
        }
    }

    private fun showCollectionScreen() {
        if (!TextUtils.isEmpty(centerId) && !TextUtils.isEmpty(deviceType)) {
            replaceFragment(R.id.layout_child, CollectionFragment
                    .newInstance(categoryId, startDate, endDate, centerId, deviceType, deviceSerialNo), Constants.COLLECTION_FRAGMENT)
        } else {
            replaceFragment(R.id.layout_child, CollectionFragment
                    .newInstance(categoryId, startDate, endDate), Constants.COLLECTION_FRAGMENT)
        }
    }

    private fun showQualityMapScreen() {
        if (!TextUtils.isEmpty(centerId) && !TextUtils.isEmpty(deviceType)) {
            replaceFragment(R.id.layout_child, QualityMapFragment
                    .newInstance(categoryId, startDate, endDate, centerId, deviceType, deviceSerialNo), Constants.QUALITY_MAP_FRAGMENT)
        } else {
            replaceFragment(R.id.layout_child, QualityMapFragment
                    .newInstance(categoryId, startDate, endDate), Constants.QUALITY_MAP_FRAGMENT)
        }
    }

    private fun showSupplierScreen() {
        if (!TextUtils.isEmpty(centerId) && !TextUtils.isEmpty(deviceType)) {
            replaceFragment(R.id.layout_child, SupplierFragment
                    .newInstance(categoryId, startDate, endDate, centerId, deviceType, deviceSerialNo), Constants.SUPPLIER_FRAGMENT)
        } else {
            replaceFragment(R.id.layout_child, SupplierFragment
                    .newInstance(categoryId, startDate, endDate), Constants.SUPPLIER_FRAGMENT)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(detail: QuantityDetailRes, categoryId: String?, startDate: String?, endDate: String?, vararg filter: String?): DashboardFragment {
            val fragment = DashboardFragment()
            val args = Bundle()
            args.putParcelable(Constants.KEY_QUANTITY_DETAIL, Parcels.wrap(detail))
            args.putString(Constants.KEY_CATEGORY_ID, categoryId)
            args.putString(Constants.KEY_START_DATE, startDate)
            args.putString(Constants.KEY_END_DATE, endDate)
            if (filter.size > 1) {
                args.putString(Constants.KEY_CENTER_ID, filter[0])
                args.putString(Constants.KEY_DEVICE_TYPE, filter[1])
                args.putString(Constants.KEY_DEVICE_SERIAL_NO, filter[2])
                args.putString(Constants.KEY_TOTAL_QUANTITY, filter[3])
            }
            fragment.arguments = args

            return fragment
        }
    }
}