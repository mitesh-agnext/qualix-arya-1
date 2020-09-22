package com.custom.app.ui.createData.coldstore.coldstoreList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.createData.coldstore.createColdstore.ColdstoreInteractor
import com.custom.app.ui.createData.coldstore.createColdstore.ListCenterInteractorCallback
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerListListener
import com.custom.app.ui.customer.list.CustomerRes

class ColdstoreListViewModel(val coldstoreInteractor: ColdstoreInteractor, val customerInteractor: CustomerInteractor) : ViewModel(),
        ListCenterInteractorCallback, CustomerListListener {

    private val _Center_listState: MutableLiveData<ScreenState<ColdstoreListState>> = MutableLiveData()
    val center_listState: LiveData<ScreenState<ColdstoreListState>>
        get() = _Center_listState

    private val _centerList: MutableLiveData<ArrayList<ColdstoreRes>> = MutableLiveData()
    val centerList: LiveData<ArrayList<ColdstoreRes>>
        get() = _centerList

    private val _customerList: MutableLiveData<ArrayList<CustomerRes>> = MutableLiveData()
    val customerList: LiveData<ArrayList<CustomerRes>>
        get() = _customerList

    private val _deletedPos: MutableLiveData<Int> = MutableLiveData()
    val deletedPos: LiveData<Int>
        get() = _deletedPos

    var errorMessage: String = "Error"

    fun onGetCenterList(searchString: String, customer_id: Int) {
        _Center_listState.value = ScreenState.Loading
        coldstoreInteractor.allCenter(this, searchString, customer_id)
    }

    fun onDeleteCenter(centerId: String, position: Int) {
        _Center_listState.value = ScreenState.Loading
        coldstoreInteractor.deleteCenter(this, centerId,position)
    }

    fun onGetCustomerList() {
        _Center_listState.value = ScreenState.Loading
        customerInteractor.list(this)
    }

    override fun allCentersApiSuccess(body: ArrayList<ColdstoreRes>) {
    _centerList.value = body
    _Center_listState.value = ScreenState.Render(ColdstoreListState.ListCenterSuccess)
    }

    override fun allCentersApiError(msg: String) {
        errorMessage = msg
        _Center_listState.value = ScreenState.Render(ColdstoreListState.ListCenterFailure)
    }

    override fun deleteCenterSuccess(deletedPostion: Int) {
        _deletedPos.value = deletedPostion
        _Center_listState.value = ScreenState.Render(ColdstoreListState.CenterDeleteSuccess)
    }

    override fun deleteCenterFailure(msg: String) {
        errorMessage = msg
        _Center_listState.value = ScreenState.Render(ColdstoreListState.CenterDeleteFailure)
    }

    override fun onCustomerListSuccess(body: ArrayList<CustomerRes>) {
        _customerList.value = body
        _Center_listState.value = ScreenState.Render(ColdstoreListState.GetCustomerListSuccess)
    }

    override fun onCustomerListFailure(msg: String) {
        errorMessage = msg
        _Center_listState.value = ScreenState.Render(ColdstoreListState.GetCustomerListFailure)
    }

    override fun onTokenExpire() {
    }
}

class CenterListViewModelFactory(
        private val coldstoreInteractor: ColdstoreInteractor, val customerInteractor: CustomerInteractor) :
        ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ColdstoreListViewModel(coldstoreInteractor, customerInteractor) as T
    }
}