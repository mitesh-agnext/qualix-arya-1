package com.custom.app.ui.createData.deviationProfile.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerListListener
import com.custom.app.ui.customer.list.CustomerRes

class DeviationListViewModel(val deviationListInteractor: DeviationListInteractor,
                             val customerInteractor: CustomerInteractor) : ViewModel(),
        DeviationListInteractor.ListDeviationInteractorCallback, CustomerListListener {

    private val _Deviation_listState: MutableLiveData<ScreenState<DeviationListState>> = MutableLiveData()
    val deviationListState: LiveData<ScreenState<DeviationListState>>
        get() = _Deviation_listState

    private val _deviationList: MutableLiveData<ArrayList<DeviationListRes>> = MutableLiveData()
    val deviationList: LiveData<ArrayList<DeviationListRes>>
        get() = _deviationList

    private val _customerList: MutableLiveData<ArrayList<CustomerRes>> = MutableLiveData()
    val customerList: LiveData<ArrayList<CustomerRes>>
        get() = _customerList

    private val _deletedPos: MutableLiveData<Int> = MutableLiveData()
    val deletedPos: LiveData<Int>
        get() = _deletedPos

    var errorMessage: String = "Error"

    fun onGetCustomerList() {
        _Deviation_listState.value = ScreenState.Loading
        customerInteractor.list(this)
    }

    fun onGetDeviationList(customer_id: Int) {
        _Deviation_listState.value = ScreenState.Loading
        deviationListInteractor.allDeviation(customer_id, this)
    }

    fun onDeleteDeviation(deviationId: Int, position: Int) {
        _Deviation_listState.value = ScreenState.Loading
        deviationListInteractor.deleteDeviation(this, deviationId, position)
    }

    override fun allDeviationApiSuccess(body: ArrayList<DeviationListRes>) {
        _deviationList.value = body
        _Deviation_listState.value = ScreenState.Render(DeviationListState.DeviationListSuccess)
    }

    override fun allDeviationApiError(msg: String) {
        errorMessage = msg
        _Deviation_listState.value = ScreenState.Render(DeviationListState.DeviationListFailure)
    }

    override fun deleteDeviationSuccess(deletedPostion: Int) {
        _deletedPos.value = deletedPostion
        _Deviation_listState.value = ScreenState.Render(DeviationListState.DeleteDeviationSuccess)
    }

    override fun deleteDeviationFailure(msg: String) {
        errorMessage = msg
        _Deviation_listState.value = ScreenState.Render(DeviationListState.DeleteDeviationFailure)
    }

    override fun onCustomerListSuccess(response: ArrayList<CustomerRes>) {
        _customerList.value = response
        _Deviation_listState.value = ScreenState.Render(DeviationListState.GetCustomerSuccess)
    }

    override fun onCustomerListFailure(msg: String) {
        errorMessage = msg
        _Deviation_listState.value = ScreenState.Render(DeviationListState.GetCustomerFailure)
    }

    override fun onTokenExpire() {}
}

class DeviationListViewModelFactory(
        private val deviationListInteractor: DeviationListInteractor, val customerInteractor: CustomerInteractor) :
        ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DeviationListViewModel(deviationListInteractor, customerInteractor) as T
    }
}