package com.custom.app.ui.createData.analytics.analyticsScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.createData.flcScan.season.create.CommodityRes
import com.custom.app.ui.createData.instlCenter.createInstallationCenter.InstallationCenterTypeRes
import com.custom.app.ui.createData.region.site.create.RegionRes
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerListListener
import com.custom.app.ui.customer.list.CustomerRes
import com.custom.app.ui.device.assign.InstallationCenterRes

class AnalyticsViewModel(
        val customerInteractor: CustomerInteractor, val analyticsInteractor: AnalyticsInteractor) : ViewModel(),
        CustomerListListener, AnalyticsInteractorCallback {

    private val _Analytics_state: MutableLiveData<ScreenState<AnalyticsState>> = MutableLiveData()
    val analyticsListState: LiveData<ScreenState<AnalyticsState>>
        get() = _Analytics_state

    private val _regionList: MutableLiveData<ArrayList<RegionRes>> = MutableLiveData()
    val regionList: LiveData<ArrayList<RegionRes>>
        get() = _regionList

    private val _customerList: MutableLiveData<ArrayList<CustomerRes>> = MutableLiveData()
    val customerList: LiveData<ArrayList<CustomerRes>>
        get() = _customerList

    private val _centerTypeList: MutableLiveData<ArrayList<InstallationCenterTypeRes>> = MutableLiveData()
    val centerTypeList: LiveData<ArrayList<InstallationCenterTypeRes>>
        get() = _centerTypeList

    private val _centerList: MutableLiveData<ArrayList<InstallationCenterRes>> = MutableLiveData()
    val centerList: LiveData<ArrayList<InstallationCenterRes>>
        get() = _centerList

    private val _commodityList: MutableLiveData<ArrayList<CommodityRes>> = MutableLiveData()
    val commodityList: LiveData<ArrayList<CommodityRes>>
        get() = _commodityList

    private val _categoryList: MutableLiveData<ArrayList<CategoryTabsRes>> = MutableLiveData()
    val categoryList: LiveData<ArrayList<CategoryTabsRes>>
        get() = _categoryList

    private val _deletedPos: MutableLiveData<Int> = MutableLiveData()
    val deletedPos: LiveData<Int>
        get() = _deletedPos

    var errorMessage: String = "Error"

    fun onGetRegions(customerId: String) {
        _Analytics_state.value = ScreenState.Loading
        analyticsInteractor.allRegion(this, customerId)
    }

    fun onGetCenterType(regionId: String) {
        _Analytics_state.value = ScreenState.Loading
        analyticsInteractor.allCenterType(this, regionId)
    }

    fun onGetCenters(customerId: String, keyword: String, regionId: String) {
        _Analytics_state.value = ScreenState.Loading
        analyticsInteractor.allCenter(this, keyword, customerId, regionId)
    }

    fun onGetCustomerList() {
        _Analytics_state.value = ScreenState.Loading
        customerInteractor.list(this)
    }

    fun onGetCommodityList(categoryId: Int) {
        _Analytics_state.value = ScreenState.Loading
        analyticsInteractor.allCommodities(this, categoryId)
    }
    fun onGetCategoryList(customerId: String) {
        _Analytics_state.value = ScreenState.Loading
        analyticsInteractor.allCategories(this,customerId)
    }

    override fun onGetRegionSuccess(body: ArrayList<RegionRes>) {
        _regionList.value = body
        _Analytics_state.value = ScreenState.Render(AnalyticsState.RegionListSuccess)
    }

    override fun onGetRegionFailure(msg: String) {
        errorMessage = msg
        _Analytics_state.value = ScreenState.Render(AnalyticsState.RegionListFailure)
    }

    override fun onCustomerListSuccess(body: ArrayList<CustomerRes>) {
        _customerList.value = body
        _Analytics_state.value = ScreenState.Render(AnalyticsState.GetCustomerListSuccess)
    }

    override fun onCustomerListFailure(msg: String) {
        errorMessage = msg
        _Analytics_state.value = ScreenState.Render(AnalyticsState.GetCustomerListFailure)
    }

    override fun allCentersApiSuccess(body: ArrayList<InstallationCenterRes>) {
        _centerList.value = body
        _Analytics_state.value = ScreenState.Render(AnalyticsState.GetCenterListSuccess)
    }

    override fun allCentersApiError(msg: String) {
        errorMessage = msg
        _Analytics_state.value = ScreenState.Render(AnalyticsState.GetCenterListFailure)
    }

    override fun allCategoryApiSuccess(body: ArrayList<CategoryTabsRes>) {
        _categoryList.value = body
        _Analytics_state.value = ScreenState.Render(AnalyticsState.GetCategoryListSuccess)
    }

    override fun allCategoryApiError(msg: String) {
        errorMessage = msg
        _Analytics_state.value = ScreenState.Render(AnalyticsState.GetCategoryListFailure)
    }

    override fun allCommodityApiSuccess(body: ArrayList<CommodityRes>) {
        _commodityList.value = body
        _Analytics_state.value = ScreenState.Render(AnalyticsState.GetCommodityListSuccess)
    }

    override fun allCommodityApiError(msg: String) {
        errorMessage = msg
        _Analytics_state.value = ScreenState.Render(AnalyticsState.GetCommodityListFailure)
    }

    override fun allCCenterTypeSuccess(body: ArrayList<InstallationCenterTypeRes>) {
        _centerTypeList.value = body
        _Analytics_state.value = ScreenState.Render(AnalyticsState.GetCenterTypeListSuccess)
    }

    override fun allCCenterTypeError(msg: String) {
        errorMessage = msg
        _Analytics_state.value = ScreenState.Render(AnalyticsState.GetCenterTypeListFailure)
    }

    override fun onTokenExpire() {}
}

class AnalyticsViewModelFactory(
        val customerInteractor: CustomerInteractor, val analyticsInteractor: AnalyticsInteractor) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AnalyticsViewModel(customerInteractor, analyticsInteractor) as T
    }
}