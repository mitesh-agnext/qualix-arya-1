package com.custom.app.ui.createData.instlCenter.centerList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.createData.instlCenter.InstallationCenterInteractor
import com.custom.app.ui.createData.instlCenter.ListCenterInteractorCallback
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerListListener
import com.custom.app.ui.customer.list.CustomerRes
import com.custom.app.ui.device.assign.InstallationCenterRes

class InstallationCenterListViewModel(val centerListInteractor: InstallationCenterInteractor, val customerInteractor: CustomerInteractor) : ViewModel(),
        ListCenterInteractorCallback, CustomerListListener {

    private val _Center_listState: MutableLiveData<ScreenState<InstallationCenterListState>> = MutableLiveData()
    val center_listState: LiveData<ScreenState<InstallationCenterListState>>
        get() = _Center_listState

    private val _centerList: MutableLiveData<ArrayList<InstallationCenterRes>> = MutableLiveData()
    val centerList: LiveData<ArrayList<InstallationCenterRes>>
        get() = _centerList

    private val _customerList: MutableLiveData<ArrayList<CustomerRes>> = MutableLiveData()
    val customerList: LiveData<ArrayList<CustomerRes>>
        get() = _customerList

    private val _deletedPos: MutableLiveData<Int> = MutableLiveData()
    val deletedPos: LiveData<Int>
        get() = _deletedPos

    var errorMassage: String = "Error"

    fun onGetCenterList(searchString: String, customer_id: Int) {
        _Center_listState.value = ScreenState.Loading
        centerListInteractor.allCenter(this, searchString, customer_id,"")
    }

    fun onDeleteCenter(centerId: String, position: Int) {
        _Center_listState.value = ScreenState.Loading
        centerListInteractor.deleteCenter(this, centerId,position)
    }

    fun onGetCustomerList() {
        _Center_listState.value = ScreenState.Loading
        customerInteractor.list(this)
    }

    override fun allCentersApiSuccess(body: ArrayList<InstallationCenterRes>) {
    _centerList.value = body
    _Center_listState.value = ScreenState.Render(InstallationCenterListState.ListCenterSuccess)
    }

    override fun allCentersApiError(msg: String) {
        errorMassage = msg
        _Center_listState.value = ScreenState.Render(InstallationCenterListState.ListCenterFailure)
    }

    override fun allCentersApiEmpty(msg: String) {
        errorMassage = msg
        _Center_listState.value = ScreenState.Render(InstallationCenterListState.ListCenterFailure)
    }

    override fun deleteCenterSuccess(deletedPostion: Int) {
        _deletedPos.value = deletedPostion
        _Center_listState.value = ScreenState.Render(InstallationCenterListState.CenterDeleteSuccess)
    }

    override fun deleteCenterFailure(msg: String) {
        errorMassage = msg
        _Center_listState.value = ScreenState.Render(InstallationCenterListState.CenterDeleteFailure)
    }

    override fun onCustomerListSuccess(body: ArrayList<CustomerRes>) {
        _customerList.value = body
        _Center_listState.value = ScreenState.Render(InstallationCenterListState.GetCustomerListSuccess)
    }

    override fun onCustomerListFailure(msg: String) {
        errorMassage = msg
        _Center_listState.value = ScreenState.Render(InstallationCenterListState.GetCustomerListFailure)
    }

    override fun onTokenExpire() {
    }
}

class CenterListViewModelFactory(
        private val centerListInteractor: InstallationCenterInteractor, val customerInteractor: CustomerInteractor) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return InstallationCenterListViewModel(centerListInteractor, customerInteractor) as T
    }
}