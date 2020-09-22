package com.custom.app.ui.customer.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.data.model.country.CityRes
import com.custom.app.data.model.country.CountryRes
import com.custom.app.data.model.country.StateRes
import com.custom.app.ui.address.AddressInteractor
import com.custom.app.ui.address.AddressListener
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.createData.analytics.analyticsScreen.CategoryTabsRes
import com.custom.app.ui.customer.list.AddCustomerListener
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerRes
import com.custom.app.ui.customer.list.CustomerState

class CustomerViewModel(val customerInteractor: CustomerInteractor, val addressInteractor: AddressInteractor) : ViewModel(), AddCustomerListener, AddressListener {

    private val _addCustomerState: MutableLiveData<ScreenState<CustomerState>> = MutableLiveData()
    val addCustomerState: LiveData<ScreenState<CustomerState>>
        get() = _addCustomerState

    var categoryArray = ArrayList<CategoryTabsRes>()

    var partnerList = ArrayList<CustomerRes>()
    var countryArray = ArrayList<CountryRes>()
    var stateArray = ArrayList<StateRes>()
    var cityArray = ArrayList<CityRes>()
    var errorMsg = "error"

    /**Forward Flow*/
    fun getPartners() {
        _addCustomerState.value = ScreenState.Loading
        customerInteractor.getPartner(this)
    }

    fun addressCountry() {
        _addCustomerState.value = ScreenState.Loading
        addressInteractor.getCountry(this)
    }

    fun addressState(countryId: String) {
        _addCustomerState.value = ScreenState.Loading
        addressInteractor.getState(countryId, this)
    }

    fun addressCity(stateId: String) {
        _addCustomerState.value = ScreenState.Loading
        addressInteractor.getCity(stateId, this)
    }

    fun getCategory(customer_id: String) {
        _addCustomerState.value = ScreenState.Loading
        customerInteractor.allCategories(this,customer_id)
    }

    fun addCustomerVM(requestData: HashMap<String, Any>) {
        _addCustomerState.value = ScreenState.Loading
        customerInteractor.add(requestData, this)
    }

    override fun onGetPartnerSuccess(response: ArrayList<CustomerRes>) {
        partnerList.clear()
        val customerRes = CustomerRes()
        customerRes.name = "Select Partner"
        partnerList.add(customerRes)
        partnerList.addAll(response)
        _addCustomerState.value = ScreenState.Render(CustomerState.GetPartnerSuccess)
    }

    override fun onGetPartnerFailure(msg: String) {
        errorMsg = msg
        _addCustomerState.value = ScreenState.Render(CustomerState.GetPartnerFailure)
    }

    override fun onAddCustomerSuccess() {
        _addCustomerState.value = ScreenState.Render(CustomerState.AddCustomerSuccess)
    }

    override fun onAddCustomerFailure() {
        _addCustomerState.value = ScreenState.Render(CustomerState.AddCustomerFailure)
    }

    override fun allCategoryApiSuccess(body: ArrayList<CategoryTabsRes>) {
        categoryArray.addAll(body)
        _addCustomerState.value = ScreenState.Render(CustomerState.AllCategoryApiSuccess)
    }

    override fun allCategoryApiError(msg: String) {
        errorMsg = msg
        _addCustomerState.value = ScreenState.Render(CustomerState.AllCategoryApiError)
    }

    override fun onTokenExpire() {
        _addCustomerState.value = ScreenState.Render(CustomerState.TokenExpire)
    }

    override fun onCountrySuccess(response: ArrayList<CountryRes>?) {
        countryArray.clear()
        countryArray.addAll(response!!)
        _addCustomerState.value = ScreenState.Render(CustomerState.CountrySuccess)
    }

    override fun onCountryFailure(msg: String) {
        errorMsg = msg
        _addCustomerState.value = ScreenState.Render(CustomerState.CountryFailure)
    }

    override fun onStateSuccess(response: ArrayList<StateRes>) {
        stateArray.clear()
        stateArray.addAll(response!!)
        _addCustomerState.value = ScreenState.Render(CustomerState.StateSuccess)
    }

    override fun onStateFailure(msg: String) {
        errorMsg = msg
        _addCustomerState.value = ScreenState.Render(CustomerState.StateFailure)
    }

    override fun onCitySuccess(response: ArrayList<CityRes>) {
        cityArray.clear()
        cityArray.addAll(response!!)
        _addCustomerState.value = ScreenState.Render(CustomerState.CitySuccess)
    }

    override fun onCityFailure(msg: String) {
        errorMsg = msg
        _addCustomerState.value = ScreenState.Render(CustomerState.CityFailure)
    }
}

class AddCustomerViewModelFactory(private val customerInteractor: CustomerInteractor, private val addressInteractor: AddressInteractor) :
        ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CustomerViewModel(customerInteractor, addressInteractor) as T
    }
}