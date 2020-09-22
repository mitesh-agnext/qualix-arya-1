package com.custom.app.ui.createData.region.site.update

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
import com.custom.app.ui.createData.region.UpdateSiteListener
import com.custom.app.ui.createData.region.site.create.RegionRes
import com.custom.app.ui.createData.region.site.list.SiteListRes
import com.custom.app.ui.customer.list.CustomerRes
import com.custom.app.ui.user.list.UserDataRes
import io.reactivex.disposables.Disposable
import okhttp3.ResponseBody

class UpdateSiteViewModel(val createSiteInteractor: RegionSiteInteractor) : ViewModel(),
        CreateSiteListener, UpdateSiteListener {

    private var disposable: Disposable? = null

    private val _updateSiteState: MutableLiveData<ScreenState<UpdateSIteState>> = MutableLiveData()
    val updateSiteState: LiveData<ScreenState<UpdateSIteState>>
        get() = _updateSiteState

    private val _updateSiteResponse: MutableLiveData<ResponseBody> = MutableLiveData()
    val updateSiteResponse: LiveData<ResponseBody>
        get() = _updateSiteResponse

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

    var singleSite: SiteListRes = SiteListRes()

    var errorMessage: String? = "Error"

    fun onGetCustomer() {
        _updateSiteState.value = ScreenState.Loading
        createSiteInteractor.getCustomer(this)
    }

    fun onGetRegion(customerId: Int) {
        _updateSiteState.value = ScreenState.Loading
        createSiteInteractor.getRegion(this, customerId)
    }

    fun onGetCountry() {
        _updateSiteState.value = ScreenState.Loading
        createSiteInteractor.getCountry(this)
    }

    fun onGetState(country_id: Int) {
        _updateSiteState.value = ScreenState.Loading
        createSiteInteractor.getState(this, country_id)
    }

    fun onGetCity(state_id: Int) {
        _updateSiteState.value = ScreenState.Loading
        createSiteInteractor.getCity(this, state_id)
    }

    fun onGetUser(customerId: Int) {
        _updateSiteState.value = ScreenState.Loading
        createSiteInteractor.getUser(this,customerId)
    }

    fun onUpdateSite(siteId: Int, region_id: Int, countryId: Int, stateId: Int, cityId: Int, site_name: String, userId: Int,
                     geoLocation: String, customer_id: Int) {
        _updateSiteState.value = ScreenState.Loading
        createSiteInteractor.updateSite(siteId, region_id, countryId, stateId, cityId, site_name, userId, geoLocation, customer_id, this)
    }

    override fun onCreateSiteSuccess(body: ResponseBody) {}

    override fun onCreateSiteFailure(msg: String) {}

    override fun onGetUserSuccess(body: ArrayList<UserDataRes>) {
        _getUserList.value = body
        _updateSiteState.value = ScreenState.Render(UpdateSIteState.GetUserSuccess)
    }

    override fun onGetUserFailure(msg: String?) {
        errorMessage = msg
        _updateSiteState.value = ScreenState.Render(UpdateSIteState.GetUserFailure)
    }

    override fun onGetUserApiEmpty(msg: String) {
        errorMessage = msg
        _updateSiteState.value = ScreenState.Render(UpdateSIteState.GetUserFailure)
    }

    override fun onGetCustomerSuccess(body: ArrayList<CustomerRes>) {
        _getCustomerList.value = body
        _updateSiteState.value = ScreenState.Render(UpdateSIteState.GetCustomerSuccess)
    }

    override fun onGetCustomerFailure(msg: String) {
        errorMessage = msg
        _updateSiteState.value = ScreenState.Render(UpdateSIteState.GetCustomerFailure)
    }

    override fun onGetCustomerEmpty(msg: String) {
        errorMessage = msg
        _updateSiteState.value = ScreenState.Render(UpdateSIteState.GetCustomerFailure)
    }

    override fun onGetRegionSuccess(body: ArrayList<RegionRes>) {
        _getRegionList.value = body
        _updateSiteState.value = ScreenState.Render(UpdateSIteState.GetRegionSuccess)
    }

    override fun onGetRegionFailure(msg: String) {
        errorMessage = msg
        _updateSiteState.value = ScreenState.Render(UpdateSIteState.GetRegionFailure)
    }

    override fun onGetRegionEmpty(msg: String) {
        errorMessage = msg
        _updateSiteState.value = ScreenState.Render(UpdateSIteState.GetRegionFailure)
    }

    override fun onGetCountrySuccess(body: ArrayList<CountryRes>) {
        _getCountryList.value = body
        _updateSiteState.value = ScreenState.Render(UpdateSIteState.GetCountrySuccess)
    }

    override fun onGetCountryFailure(msg: String) {
        errorMessage = msg
        _updateSiteState.value = ScreenState.Render(UpdateSIteState.GetCountryFailure)
    }

    override fun onGetCountryEmpty(msg: String) {
        errorMessage = msg
        _updateSiteState.value = ScreenState.Render(UpdateSIteState.GetCountryFailure)
    }

    override fun onGetStateSuccess(body: ArrayList<StateRes>) {
        _getStateList.value = body
        _updateSiteState.value = ScreenState.Render(UpdateSIteState.GetStateSuccess)
    }

    override fun onGetStateFailure(msg: String) {
        errorMessage = msg
        _updateSiteState.value = ScreenState.Render(UpdateSIteState.GetStateFailure)
    }

    override fun onGetStateEmpty(msg: String) {
        errorMessage = msg
        _updateSiteState.value = ScreenState.Render(UpdateSIteState.GetStateFailure)
    }

    override fun onGetCitySuccess(body: ArrayList<CityRes>) {
        _getCityList.value = body
        _updateSiteState.value = ScreenState.Render(UpdateSIteState.GetCitySuccess)
    }

    override fun onGetCityFailure(msg: String) {
        errorMessage = msg
        _updateSiteState.value = ScreenState.Render(UpdateSIteState.GetCityFailure)
    }

    override fun onGetCityEmpty(msg: String) {
        errorMessage = msg
        _updateSiteState.value = ScreenState.Render(UpdateSIteState.GetCityFailure)

    }

    override fun onSiteNameEmpty() {
        _updateSiteState.value = ScreenState.Render(UpdateSIteState.SiteNameEmpty)
    }

    override fun onUpdateSiteSuccess(body: ResponseBody) {
        _updateSiteResponse.value = body
        _updateSiteState.value = ScreenState.Render(UpdateSIteState.UpdateSiteSuccess)
    }

    override fun onUpdateSiteFailure(msg: String) {
        errorMessage = msg
        _updateSiteState.value = ScreenState.Render(UpdateSIteState.UpdateSiteFailure)
    }

    fun destroy() {
        RxUtils.dispose(disposable)
    }
}

class UpdateSiteViewModelFactory(
        val siteInteractor: RegionSiteInteractor) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return UpdateSiteViewModel(siteInteractor) as T
    }
}