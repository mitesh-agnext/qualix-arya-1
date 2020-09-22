package com.custom.app.ui.createData.flcScan.season.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerListListener
import com.custom.app.ui.customer.list.CustomerRes

class SeasonListViewModel(val regionListInteractor: SeasonListInteractor, val customerInteractor: CustomerInteractor) : ViewModel(),
        SeasonListInteractor.ListSeasonInteractorCallback, CustomerListListener {

    private val _Season_listState: MutableLiveData<ScreenState<SeasonListState>> = MutableLiveData()
    val regionListState: LiveData<ScreenState<SeasonListState>>
        get() = _Season_listState

    private val _regionList: MutableLiveData<ArrayList<SeasonRes>> = MutableLiveData()
    val regionList: LiveData<ArrayList<SeasonRes>>
        get() = _regionList

    private val _customerList: MutableLiveData<ArrayList<CustomerRes>> = MutableLiveData()
    val customerList: LiveData<ArrayList<CustomerRes>>
        get() = _customerList

    private val _deletedPos: MutableLiveData<Int> = MutableLiveData()
    val deletedPos: LiveData<Int>
        get() = _deletedPos

    var errorMassage: String = "Error"

    fun onGetSeasonList(customerId: Int, key_search: String) {
        _Season_listState.value = ScreenState.Loading
        regionListInteractor.allSeason(this, customerId, key_search)
    }

    fun onDeleteSeason(regionId: Int, position: Int) {
        _Season_listState.value = ScreenState.Loading
        regionListInteractor.deleteSeason(this, regionId, position)
    }

    fun onGetCustomerList() {
        _Season_listState.value = ScreenState.Loading
        customerInteractor.list(this)
    }

    override fun allSeasonApiSuccess(body: ArrayList<SeasonRes>) {
        _regionList.value = body
        _Season_listState.value = ScreenState.Render(SeasonListState.SeasonListSuccess)
    }

    override fun allSeasonApiError(msg: String) {
        errorMassage = msg
        _Season_listState.value = ScreenState.Render(SeasonListState.DeleteSeasonFailure)
    }

    override fun deleteSeasonSuccess(deletedPostion: Int) {
        _deletedPos.value = deletedPostion
        _Season_listState.value = ScreenState.Render(SeasonListState.DeleteSeasonSuccess)
    }

    override fun deleteSeasonFailure(msg: String) {
        errorMassage = msg
        _Season_listState.value = ScreenState.Render(SeasonListState.DeleteSeasonFailure)
    }

    override fun onCustomerListSuccess(body: ArrayList<CustomerRes>) {
        _customerList.value = body
        _Season_listState.value = ScreenState.Render(SeasonListState.GetCustomerListSuccess)
    }

    override fun onCustomerListFailure(msg: String) {
        errorMassage = msg
        _Season_listState.value = ScreenState.Render(SeasonListState.GetCustomerListFailure)
    }

    override fun onTokenExpire() {}
}

class SeasonListViewModelFactory(
        private val regionsSeasonListInteractor: SeasonListInteractor, val customerInteractor: CustomerInteractor) :
        ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SeasonListViewModel(regionsSeasonListInteractor, customerInteractor) as T
    }
}