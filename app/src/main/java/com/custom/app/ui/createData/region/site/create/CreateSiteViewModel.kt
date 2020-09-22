package com.custom.app.ui.createData.region.site.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.core.app.util.RxUtils
import com.custom.app.data.model.country.CityRes
import com.custom.app.data.model.country.CountryRes
import com.custom.app.data.model.country.StateRes
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.createData.region.CreateSiteListener
import com.custom.app.ui.createData.region.RegionSiteInteractor
import com.custom.app.ui.customer.list.CustomerRes
import com.custom.app.ui.user.list.UserDataRes
import io.reactivex.disposables.Disposable
import okhttp3.ResponseBody

class CreateSiteViewModel(val createSiteInteractor: RegionSiteInteractor) : ViewModel(), CreateSiteListener {

    private var disposable: Disposable? = null

    private val _createSiteState: MutableLiveData<ScreenState<CreateSIteState>> = MutableLiveData()
    val createSiteState: LiveData<ScreenState<CreateSIteState>>
        get() = _createSiteState

    private val _createSiteResponse: MutableLiveData<ResponseBody> = MutableLiveData()

    private val _getCustomerList: MutableLiveData<ArrayList<CustomerRes>> = MutableLiveData()
    val getCustomerList: LiveData<ArrayList<CustomerRes>>
        get() = _getCustomerList

    private val _getRegionList: MutableLiveData<ArrayList<RegionRes>> = MutableLiveData()
    val getRegionList: LiveData<ArrayList<RegionRes>>
        get() = _getRegionList

    private val _getCountryList: MutableLiveData<ArrayList<CountryRes>> = MutableLiveData()
    val getCountryList: LiveData<ArrayList<CountryRes>>
        get() = _getCountryList

    private val _getStateList: MutableLiveData<ArrayList<StateRes>> = MutableLiveData()
    val getStateList: LiveData<ArrayList<StateRes>>
        get() = _getStateList

    private val _getCityList: MutableLiveData<ArrayList<CityRes>> = MutableLiveData()
    val getCityList: LiveData<ArrayList<CityRes>>
        get() = _getCityList

    private val _getUserList: MutableLiveData<ArrayList<UserDataRes>> = MutableLiveData()
    val getUserList: LiveData<ArrayList<UserDataRes>>
        get() = _getUserList

    var errorMessage: String? = "Error"

    fun onGetCustomer() {
        _createSiteState.value = ScreenState.Loading
        createSiteInteractor.getCustomer(this)
    }

    fun onGetRegion(customerId: Int) {
        _createSiteState.value = ScreenState.Loading
        createSiteInteractor.getRegion(this, customerId)
    }

    fun onGetCountry() {
        _createSiteState.value = ScreenState.Loading
        createSiteInteractor.getCountry(this)
    }

    fun onGetState(country_id: Int) {
        _createSiteState.value = ScreenState.Loading
        createSiteInteractor.getState(this, country_id)
    }

    fun onGetCity(state_id: Int) {
        _createSiteState.value = ScreenState.Loading
        createSiteInteractor.getCity(this, state_id)
    }

    fun onGetUser(customer_id: Int) {
        _createSiteState.value = ScreenState.Loading
        createSiteInteractor.getUser(this, customer_id)

    }

    fun onCreateSite(customerId: Int, region_id: Int, countryId: Int, stateId: Int, cityId: Int, site_name: String, userId: Int,
                     geoLocation: String) {
        _createSiteState.value = ScreenState.Loading
        createSiteInteractor.addSite(customerId, region_id, countryId, stateId, cityId, site_name, userId, geoLocation, this)
    }

    override fun onCreateSiteSuccess(body: ResponseBody) {
        _createSiteResponse.value = body
        _createSiteState.value = ScreenState.Render(CreateSIteState.CreateSiteSuccess)
    }

    override fun onCreateSiteFailure(msg: String) {
        errorMessage = msg
        _createSiteState.value = ScreenState.Render(CreateSIteState.CreateSiteFailure)
    }

    override fun onGetUserSuccess(body: ArrayList<UserDataRes>) {
        _getUserList.value = body
        _createSiteState.value = ScreenState.Render(CreateSIteState.GetUserSuccess)
    }

    override fun onGetUserFailure(msg: String?) {
        errorMessage = msg
        _createSiteState.value = ScreenState.Render(CreateSIteState.GetUserFailure)
    }

    override fun onGetUserApiEmpty(msg: String) {
        TODO("Not yet implemented")
    }

    override fun onGetCustomerSuccess(body: ArrayList<CustomerRes>) {
        _getCustomerList.value = body
        _createSiteState.value = ScreenState.Render(CreateSIteState.GetCustomerSuccess)
    }

    override fun onGetCustomerFailure(msg: String) {
        errorMessage = msg
        _createSiteState.value = ScreenState.Render(CreateSIteState.GetCustomerFailure)
    }

    override fun onGetCustomerEmpty(msg: String) {
        errorMessage = msg
        _createSiteState.value = ScreenState.Render(CreateSIteState.GetCustomerFailure)
    }

    override fun onGetRegionSuccess(body: ArrayList<RegionRes>) {
        _getRegionList.value = body
        _createSiteState.value = ScreenState.Render(CreateSIteState.GetRegionSuccess)
    }

    override fun onGetRegionFailure(msg: String) {
        errorMessage = msg
        _createSiteState.value = ScreenState.Render(CreateSIteState.GetRegionFailure)
    }

    override fun onGetRegionEmpty(msg: String) {
        errorMessage = msg
        _createSiteState.value = ScreenState.Render(CreateSIteState.GetRegionFailure)
    }

    override fun onGetCountrySuccess(body: ArrayList<CountryRes>) {
        _getCountryList.value = body
        _createSiteState.value = ScreenState.Render(CreateSIteState.GetCountrySuccess)
    }

    override fun onGetCountryFailure(msg: String) {
        errorMessage = msg
        _createSiteState.value = ScreenState.Render(CreateSIteState.GetCountryFailure)
    }

    override fun onGetCountryEmpty(msg: String) {
        errorMessage = msg
        _createSiteState.value = ScreenState.Render(CreateSIteState.GetCountryFailure)
    }

    override fun onGetStateSuccess(body: ArrayList<StateRes>) {
        _getStateList.value = body
        _createSiteState.value = ScreenState.Render(CreateSIteState.GetStateSuccess)
    }

    override fun onGetStateFailure(msg: String) {
        errorMessage = msg
        _createSiteState.value = ScreenState.Render(CreateSIteState.GetStateFailure)
    }

    override fun onGetStateEmpty(msg: String) {
        errorMessage = msg
        _createSiteState.value = ScreenState.Render(CreateSIteState.GetStateFailure)

    }

    override fun onGetCitySuccess(body: ArrayList<CityRes>) {
        _getCityList.value = body
        _createSiteState.value = ScreenState.Render(CreateSIteState.GetCitySuccess)
    }

    override fun onGetCityFailure(msg: String) {
        errorMessage = msg
        _createSiteState.value = ScreenState.Render(CreateSIteState.GetCityFailure)
    }

    override fun onGetCityEmpty(msg: String) {
        errorMessage = msg
        _createSiteState.value = ScreenState.Render(CreateSIteState.GetCityFailure)

    }

    override fun onSiteNameEmpty() {
        _createSiteState.value = ScreenState.Render(CreateSIteState.SiteNameEmpty)
    }

    fun destroy() {
        RxUtils.dispose(disposable)
    }
}

class CreateSiteViewModelFactory(val siteInteractor: RegionSiteInteractor) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CreateSiteViewModel(siteInteractor) as T
    }
}