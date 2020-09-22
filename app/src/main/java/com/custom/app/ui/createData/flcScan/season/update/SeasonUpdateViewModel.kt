package com.custom.app.ui.createData.flcScan.season.update

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.createData.flcScan.season.create.CommodityRes
import com.custom.app.ui.createData.flcScan.season.create.SeasonCreateInteractor
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerListListener
import com.custom.app.ui.customer.list.CustomerRes
import okhttp3.ResponseBody

class SeasonUpdateViewModel(val createSeasonInteractor: SeasonCreateInteractor, val seasonUpdateInteractor: SeasonUpdateInteractor, val customerInteractor: CustomerInteractor) : ViewModel(),
        SeasonCreateInteractor.CreateSeasonListener, SeasonUpdateInteractor.UpdateSeasonListener, CustomerListListener {

    private val _seasonUpdateState: MutableLiveData<ScreenState<SeasonUpdateState>> = MutableLiveData()
    val seasonUpdateState: LiveData<ScreenState<SeasonUpdateState>>
        get() = _seasonUpdateState

    private val _seasonUpdateResponse: MutableLiveData<ResponseBody> = MutableLiveData()
    val seasonUpdateResponse: LiveData<ResponseBody>
        get() = _seasonUpdateResponse

    private val _getCustomerList: MutableLiveData<ArrayList<CustomerRes>> = MutableLiveData()
    val getCustomerList: LiveData<ArrayList<CustomerRes>>
        get() = _getCustomerList

    private val _getCommodityList: MutableLiveData<ArrayList<CommodityRes>> = MutableLiveData()
    val getCommodityList: LiveData<ArrayList<CommodityRes>>
        get() = _getCommodityList

    var errorMessage: String = "Error"

    fun onGetCustomer() {
        _seasonUpdateState.value = ScreenState.Loading
        customerInteractor.list(this)
    }

    fun onGetCommodity(customer_id: Int) {
        _seasonUpdateState.value = ScreenState.Loading
        createSeasonInteractor.getCommodity(this, customer_id)
    }

    fun onUpdateSeason(season_name: String, season_equation: String, season_id: Int, commodity_id: Int, date_from: Long, date_to: Long) {
        _seasonUpdateState.value = ScreenState.Loading
        seasonUpdateInteractor.updateSeason(season_name, season_equation, season_id, commodity_id, date_from, date_to, this)
    }

    override fun onSeasonSuccess(body: ResponseBody) {

    }

    override fun onSeasonFailure(msg: String) {
    }

    override fun onCommoditySuccess(body: ArrayList<CommodityRes>) {
        _getCommodityList.value = body
        _seasonUpdateState.value = ScreenState.Render(SeasonUpdateState.GetCommoditySuccess)
    }

    override fun onCommodityFailure(msg: String) {
        errorMessage = msg
        _seasonUpdateState.value = ScreenState.Render(SeasonUpdateState.GetCommodityFailure)
    }

    override fun onSeasonUpdateSuccess(body: ResponseBody) {
        _seasonUpdateResponse.value = body
        _seasonUpdateState.value = ScreenState.Render(SeasonUpdateState.SeasonUpdateSuccess)
    }

    override fun onSeasonUpdateFailure(msg: String) {
        errorMessage = msg
        _seasonUpdateState.value = ScreenState.Render(SeasonUpdateState.SeasonUpdateFailure)
    }

    override fun onSeasonNameEmpty() {
        _seasonUpdateState.value = ScreenState.Render(SeasonUpdateState.SeasonNameEmpty)
    }

    override fun onCustomerListSuccess(body: ArrayList<CustomerRes>) {
        _getCustomerList.value = body
        _seasonUpdateState.value = ScreenState.Render(SeasonUpdateState.GetCustomerSuccess)
    }

    override fun onCustomerListFailure(msg: String) {
        errorMessage = msg
        _seasonUpdateState.value = ScreenState.Render(SeasonUpdateState.GetCustomerFailure)
    }

    override fun onTokenExpire() {
    }
}

class UpdateSeasonViewModelFactory(
        private val seasonInteractor: SeasonCreateInteractor, val seasonUpdateInteractor: SeasonUpdateInteractor, private val customerInteractor: CustomerInteractor) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SeasonUpdateViewModel(seasonInteractor, seasonUpdateInteractor, customerInteractor) as T
    }
}