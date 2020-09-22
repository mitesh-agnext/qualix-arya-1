package com.custom.app.ui.createData.region.update

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.createData.region.CreateRegionListener
import com.custom.app.ui.createData.region.RegionSiteInteractor
import com.custom.app.ui.createData.region.UpdateRegionListener
import com.custom.app.ui.createData.region.site.create.RegionRes
import com.custom.app.ui.customer.list.CustomerRes
import okhttp3.ResponseBody
import java.util.*

class RegionUpdateViewModel(val createRegionInteractor: RegionSiteInteractor) : ViewModel(),
        CreateRegionListener, UpdateRegionListener {

    private val _regionUpdateState: MutableLiveData<ScreenState<RegionUpdateState>> = MutableLiveData()
    val regionUpdateState: LiveData<ScreenState<RegionUpdateState>>
        get() = _regionUpdateState

    private val _regionUpdateResponse: MutableLiveData<ResponseBody> = MutableLiveData()
    val regionUpdateResponse: LiveData<ResponseBody>
        get() = _regionUpdateResponse

    private val _getCustomerList: MutableLiveData<ArrayList<CustomerRes>> = MutableLiveData()
    val getCustomerList: LiveData<ArrayList<CustomerRes>>
        get() = _getCustomerList

    var singleRegion: RegionRes = RegionRes()
    var errorMessage: String? = "Error"
    fun onGetCustomer() {
        _regionUpdateState.value = ScreenState.Loading
        createRegionInteractor.getCustomer(this)
    }

    fun onUpdateRegion(region_name: String, region_id: Int, customer_id: Int) {
        _regionUpdateState.value = ScreenState.Loading
        createRegionInteractor.updateRegion(region_id,region_name,customer_id, this)
    }

    override fun onRegionSuccess(body: ResponseBody) {
    }

    override fun onRegionFailure(msg: String) {

    }

    override fun onGetCustomerSuccess(body: ArrayList<CustomerRes>) {
        _getCustomerList.value = body
        _regionUpdateState.value = ScreenState.Render(RegionUpdateState.GetCustomerSuccess)
    }

    override fun onGetCustomerFailure(msg: String) {
        errorMessage = msg
        _regionUpdateState.value = ScreenState.Render(RegionUpdateState.GetCustomerFailure)
    }

    override fun onGetCustomerEmpty(msg: String) {
        errorMessage = msg
        _regionUpdateState.value = ScreenState.Render(RegionUpdateState.GetCustomerFailure)
    }

    override fun onRegionNameEmpty() {
        _regionUpdateState.value = ScreenState.Render(RegionUpdateState.RegionNameEmpty)
    }

    override fun onUpdateRegionSuccess(body: ResponseBody) {
        _regionUpdateResponse.value = body
        _regionUpdateState.value = ScreenState.Render(RegionUpdateState.RegionUpdateSuccess)

    }

    override fun onUpdateRegionFailure(msg: String) {
        errorMessage = msg
        _regionUpdateState.value = ScreenState.Render(RegionUpdateState.RegionUpdateFailure)
    }
}

class UpdateRegionViewModelFactory(
    private val createRegionInteractor: RegionSiteInteractor) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RegionUpdateViewModel(createRegionInteractor) as T

    }
}