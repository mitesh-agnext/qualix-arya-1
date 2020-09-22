package com.custom.app.ui.createData.region.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.createData.region.ListRegionInteractorCallback
import com.custom.app.ui.createData.region.RegionSiteInteractor
import com.custom.app.ui.createData.region.site.create.RegionRes
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerListListener
import com.custom.app.ui.customer.list.CustomerRes

class RegionListViewModel(val regionListInteractor: RegionSiteInteractor, val customerInteractor: CustomerInteractor) : ViewModel(),
        ListRegionInteractorCallback, CustomerListListener {

    private val _Region_listState: MutableLiveData<ScreenState<RegionListState>> = MutableLiveData()
    val regionListState: LiveData<ScreenState<RegionListState>>
        get() = _Region_listState

    private val _regionList: MutableLiveData<ArrayList<RegionRes>> = MutableLiveData()
    val regionList: LiveData<ArrayList<RegionRes>>
        get() = _regionList

    private val _customerList: MutableLiveData<ArrayList<CustomerRes>> = MutableLiveData()
    val customerList: LiveData<ArrayList<CustomerRes>>
        get() = _customerList

    private val _deletedPos: MutableLiveData<Int> = MutableLiveData()
    val deletedPos: LiveData<Int>
        get() = _deletedPos

    var errorMassage: String = "Error"

    fun onGetRegionList(customerId: Int) {
        _Region_listState.value = ScreenState.Loading
        regionListInteractor.allRegion(this, customerId)
    }

    fun onDeleteRegion(regionId: Int, position: Int) {
        _Region_listState.value = ScreenState.Loading
        regionListInteractor.deleteRegion(this, regionId, position)
    }

    fun onGetCustomerList() {
        _Region_listState.value = ScreenState.Loading
        customerInteractor.list(this)
    }

    override fun onGetRegionSuccess(body: ArrayList<RegionRes>) {
        _regionList.value = body
        _Region_listState.value = ScreenState.Render(RegionListState.RegionListSuccess)
    }

    override fun onGetRegionFailure(msg: String) {
        errorMassage = msg
        _Region_listState.value = ScreenState.Render(RegionListState.DeleteRegionFailure)
    }

    override fun onGetRegionEmpty(msg: String) {
        errorMassage = msg
        _Region_listState.value = ScreenState.Render(RegionListState.DeleteRegionFailure)
    }

    override fun deleteRegionSuccess(deletedPostion: Int) {
        _deletedPos.value = deletedPostion
        _Region_listState.value = ScreenState.Render(RegionListState.DeleteRegionSuccess)
    }

    override fun deleteRegionFailure(msg: String) {
        errorMassage = msg
        _Region_listState.value = ScreenState.Render(RegionListState.DeleteRegionFailure)
    }

    override fun onCustomerListSuccess(body: ArrayList<CustomerRes>) {
        _customerList.value = body
        _Region_listState.value = ScreenState.Render(RegionListState.GetCustomerListSuccess)
    }

    override fun onCustomerListFailure(msg: String) {
        errorMassage = msg
        _Region_listState.value = ScreenState.Render(RegionListState.GetCustomerListFailure)
    }

    override fun onTokenExpire() {}
}

class RegionListViewModelFactory(
        private val regionsRegionListInteractor: RegionSiteInteractor, val customerInteractor: CustomerInteractor) :
        ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RegionListViewModel(regionsRegionListInteractor, customerInteractor) as T
    }
}