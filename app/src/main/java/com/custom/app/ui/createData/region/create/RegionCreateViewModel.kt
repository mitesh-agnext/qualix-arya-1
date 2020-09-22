package com.custom.app.ui.createData.region.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.createData.region.CreateRegionListener
import com.custom.app.ui.createData.region.RegionSiteInteractor
import com.custom.app.ui.customer.list.CustomerRes
import okhttp3.ResponseBody
import java.util.*

class RegionCreateViewModel(val createRegionInteractor: RegionSiteInteractor) : ViewModel(),
        CreateRegionListener {

    private val _regionCreateState: MutableLiveData<ScreenState<RegionCreateState>> = MutableLiveData()
    val regionCreateState: LiveData<ScreenState<RegionCreateState>>
        get() = _regionCreateState

    private val _regionCreateResponse: MutableLiveData<ResponseBody> = MutableLiveData()
    val regionCreateResponse: LiveData<ResponseBody>
        get() = _regionCreateResponse

    private val _getCustomerList: MutableLiveData<ArrayList<CustomerRes>> = MutableLiveData()
    val getCustomerList: LiveData<ArrayList<CustomerRes>>
        get() = _getCustomerList

    var errorMessage: String? = "Error"

    fun onGetCustomer() {
        _regionCreateState.value = ScreenState.Loading
        createRegionInteractor.getCustomer(this)
    }

    fun onCreateRegion(region_name: String, customer_id: Int) {
        _regionCreateState.value = ScreenState.Loading
        createRegionInteractor.addRegion(region_name,customer_id,this)
    }

    override fun onRegionSuccess(body: ResponseBody) {
        _regionCreateResponse.value = body
        _regionCreateState.value = ScreenState.Render(RegionCreateState.RegionCreateSuccess)
    }

    override fun onRegionFailure(msg : String) {
        errorMessage = msg
        _regionCreateState.value = ScreenState.Render(RegionCreateState.RegionCreateFailure)
    }

    override fun onGetCustomerSuccess(body: ArrayList<CustomerRes>) {
        _getCustomerList.value = body
        _regionCreateState.value = ScreenState.Render(RegionCreateState.GetCustomerSuccess)
    }

    override fun onGetCustomerFailure(msg: String) {
        errorMessage = msg
        _regionCreateState.value = ScreenState.Render(RegionCreateState.GetCustomerFailure)
    }

    override fun onGetCustomerEmpty(msg: String) {
        errorMessage = msg
        _regionCreateState.value = ScreenState.Render(RegionCreateState.GetCustomerFailure)
    }

    override fun onRegionNameEmpty() {
        _regionCreateState.value = ScreenState.Render(RegionCreateState.RegionNameEmpty)
    }
}

class CreateRegionViewModelFactory(
        private val regionInteractor: RegionSiteInteractor) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RegionCreateViewModel(regionInteractor) as T
    }
}