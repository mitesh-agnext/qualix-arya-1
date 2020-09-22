package com.custom.app.ui.createData.region.site.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.createData.region.ListRegionInteractorCallback
import com.custom.app.ui.createData.region.ListSiteInteractorCallback
import com.custom.app.ui.createData.region.RegionSiteInteractor
import com.custom.app.ui.createData.region.site.create.RegionRes
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerListListener
import com.custom.app.ui.customer.list.CustomerRes

class SiteListViewModel(val siteListInteractor: RegionSiteInteractor, val customerInteractor: CustomerInteractor) : ViewModel(),
        ListSiteInteractorCallback, ListRegionInteractorCallback, CustomerListListener {

    private val _Site_listState: MutableLiveData<ScreenState<SiteListState>> = MutableLiveData()
    val siteListState: LiveData<ScreenState<SiteListState>>
        get() = _Site_listState

    private val _siteList: MutableLiveData<ArrayList<SiteListRes>> = MutableLiveData()
    val siteList: LiveData<ArrayList<SiteListRes>>
        get() = _siteList

    private val _regionList: MutableLiveData<ArrayList<RegionRes>> = MutableLiveData()
    val regionList: LiveData<ArrayList<RegionRes>>
        get() = _regionList

    private val _customerList: MutableLiveData<ArrayList<CustomerRes>> = MutableLiveData()
    val customerList: LiveData<ArrayList<CustomerRes>>
        get() = _customerList

    private val _deletedPos: MutableLiveData<Int> = MutableLiveData()
    val deletedPos: LiveData<Int>
        get() = _deletedPos

    var errorMessage: String = "Error"

    fun onGetSiteList(searchString: String, regionId: Int) {
        _Site_listState.value = ScreenState.Loading
        siteListInteractor.allSite(this, searchString, regionId)
    }

    fun onGetRegionList(customerId: Int) {
        _Site_listState.value = ScreenState.Loading
        siteListInteractor.allRegion(this, customerId)
    }

    fun onGetCustomerList() {
        _Site_listState.value = ScreenState.Loading
        customerInteractor.list(this)
    }

    fun onDeleteSite(siteId: Int, position: Int) {
        _Site_listState.value = ScreenState.Loading
        siteListInteractor.deleteSite(this, siteId, position)
    }

    override fun onGetSiteSuccess(body: ArrayList<SiteListRes>) {
        _siteList.value = body
        _Site_listState.value = ScreenState.Render(SiteListState.SiteListSuccess)
    }

    override fun onGetSiteError(msg: String) {
        errorMessage = msg
        _Site_listState.value = ScreenState.Render(SiteListState.SiteListFailure)
    }

    override fun onGetSiteEmpty(msg: String) {
        errorMessage = msg
        _Site_listState.value = ScreenState.Render(SiteListState.SiteListFailure)
    }

    override fun deleteSiteSuccess(deletedPostion: Int) {
        _deletedPos.value = deletedPostion
        _Site_listState.value = ScreenState.Render(SiteListState.DeleteSiteSuccess)
    }

    override fun deleteSiteFailure(msg: String) {
        errorMessage = msg
        _Site_listState.value = ScreenState.Render(SiteListState.DeleteSiteFailure)
    }

    override fun onGetRegionSuccess(body: ArrayList<RegionRes>) {
        _regionList.value = body
        _Site_listState.value = ScreenState.Render(SiteListState.RegionListSuccess)
    }

    override fun onGetRegionFailure(msg: String) {
        errorMessage = msg
        _Site_listState.value = ScreenState.Render(SiteListState.RegionListFailure)
    }

    override fun onGetRegionEmpty(msg: String) {
        errorMessage = msg
        _Site_listState.value = ScreenState.Render(SiteListState.RegionListFailure)
    }

    override fun deleteRegionSuccess(deletedPostion: Int) {}

    override fun deleteRegionFailure(msg: String) {}

    override fun onCustomerListSuccess(body: ArrayList<CustomerRes>) {
        _customerList.value = body
        _Site_listState.value = ScreenState.Render(SiteListState.GetCustomerListSuccess)
    }

    override fun onCustomerListFailure(msg: String) {
        errorMessage = msg
        _Site_listState.value = ScreenState.Render(SiteListState.GetCustomerListFailure)
    }

    override fun onTokenExpire() {}
}

class SitesListViewModelFactory(
        private val siteListInteractor: RegionSiteInteractor, val customerInteractor: CustomerInteractor) :
        ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SiteListViewModel(siteListInteractor, customerInteractor) as T
    }
}