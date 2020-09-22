package com.custom.app.ui.createData.flcScan.season.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerListListener
import com.custom.app.ui.customer.list.CustomerRes
import okhttp3.ResponseBody

class SeasonCreateViewModel(val createSeasonInteractor: SeasonCreateInteractor, val customerInteractor: CustomerInteractor) : ViewModel(),
        SeasonCreateInteractor.CreateSeasonListener, CustomerListListener {

    private val _seasonCreateState: MutableLiveData<ScreenState<SeasonCreateState>> = MutableLiveData()
    val seasonCreateState: LiveData<ScreenState<SeasonCreateState>>
        get() = _seasonCreateState

    private val _seasonCreateResponse: MutableLiveData<ResponseBody> = MutableLiveData()
    val seasonCreateResponse: LiveData<ResponseBody>
        get() = _seasonCreateResponse

    private val _getCustomerList: MutableLiveData<ArrayList<CustomerRes>> = MutableLiveData()
    val getCustomerList: LiveData<ArrayList<CustomerRes>>
        get() = _getCustomerList

    private val _getCommodityList: MutableLiveData<ArrayList<CommodityRes>> = MutableLiveData()
    val getCommodityList: LiveData<ArrayList<CommodityRes>>
        get() = _getCommodityList

    var errorMessage: String = "Error"

    fun onGetCustomer() {
        _seasonCreateState.value = ScreenState.Loading
        customerInteractor.list(this)
    }

    fun onGetCommodity(customer_id: Int) {
        _seasonCreateState.value = ScreenState.Loading
        createSeasonInteractor.getCommodity(this,customer_id)
    }

    fun onCreateSeason(season_name: String, season_equation: String,customer_id: Int, commodity_id: Int, date_from: Long, date_To: Long) {
        _seasonCreateState.value = ScreenState.Loading
        createSeasonInteractor.addSeason(season_name, season_equation, customer_id, commodity_id, date_from,date_To,this)
    }

    override fun onSeasonSuccess(body: ResponseBody) {
        _seasonCreateResponse.value = body
        _seasonCreateState.value = ScreenState.Render(SeasonCreateState.SeasonCreateSuccess)
    }

    override fun onSeasonFailure(msg: String) {
        errorMessage = msg
        _seasonCreateState.value = ScreenState.Render(SeasonCreateState.SeasonCreateFailure)
    }

    override fun onCommoditySuccess(body: ArrayList<CommodityRes>) {
        _getCommodityList.value = body
        _seasonCreateState.value = ScreenState.Render(SeasonCreateState.GetCommoditySuccess)
    }

    override fun onCommodityFailure(msg: String) {
        errorMessage = msg
        _seasonCreateState.value = ScreenState.Render(SeasonCreateState.GetCommodityFailure)
    }

    override fun onSeasonNameEmpty() {
        _seasonCreateState.value = ScreenState.Render(SeasonCreateState.SeasonNameEmpty)
    }

    override fun onCustomerListSuccess(body: ArrayList<CustomerRes>) {
        _getCustomerList.value = body
        _seasonCreateState.value = ScreenState.Render(SeasonCreateState.GetCustomerSuccess)
    }

    override fun onCustomerListFailure(msg: String) {
        errorMessage = msg
        _seasonCreateState.value = ScreenState.Render(SeasonCreateState.GetCustomerFailure)
    }

    override fun onTokenExpire() {
    }
}

class CreateSeasonViewModelFactory(
        private val seasonInteractor: SeasonCreateInteractor, private val customerInteractor: CustomerInteractor) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SeasonCreateViewModel(seasonInteractor, customerInteractor) as T
    }
}