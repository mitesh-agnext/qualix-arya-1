package com.custom.app.ui.customer.edit

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
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerState
import com.custom.app.ui.customer.list.EditCustomerListener

class EditCustomerVM(val customerInteractor: CustomerInteractor, val addressInteractor: AddressInteractor) : ViewModel(), EditCustomerListener, AddressListener {

    private val _customerStates: MutableLiveData<ScreenState<CustomerState>> = MutableLiveData()
    val customerStates: LiveData<ScreenState<CustomerState>>
        get() = _customerStates

    var countryArray = ArrayList<CountryRes>()
    var stateArray = ArrayList<StateRes>()
    var cityArray = ArrayList<CityRes>()
    var errorMassage = "error"

    /**Forward Flow*/
    fun updateCustomerVm(customer_id: String, requestData: HashMap<String, Any>) {
        _customerStates.value = ScreenState.Loading
        customerInteractor.edit(customer_id, requestData, this)
    }

    fun addressCountry() {
        _customerStates.value = ScreenState.Loading
        addressInteractor.getCountry(this)
    }

    fun addressState(countryId: String) {
        _customerStates.value = ScreenState.Loading
        addressInteractor.getState(countryId, this)
    }

    fun addressCity(stateId: String) {
        _customerStates.value = ScreenState.Loading
        addressInteractor.getCity(stateId, this)
    }

    /**Backward Flow*/
    override fun onEditCustomerSuccess() {
        _customerStates.value = ScreenState.Render(CustomerState.EditCustomerSuccess)
    }

    override fun onEditCustomerFailure(msg: String) {
        errorMassage = msg
        _customerStates.value = ScreenState.Render(CustomerState.EditCustomerFailure)
    }

    override fun onTokenExpire() {
        _customerStates.value = ScreenState.Render(CustomerState.TokenExpire)
    }

    override fun onCountrySuccess(response: ArrayList<CountryRes>?) {
        countryArray.clear()
        countryArray.addAll(response!!)
        _customerStates.value = ScreenState.Render(CustomerState.CountrySuccess)
    }

    override fun onCountryFailure(msg: String) {
        errorMassage = msg
        _customerStates.value = ScreenState.Render(CustomerState.CountryFailure)
    }

    override fun onStateSuccess(response: ArrayList<StateRes>) {
        stateArray.clear()
        stateArray.addAll(response!!)
        _customerStates.value = ScreenState.Render(CustomerState.StateSuccess)
    }

    override fun onStateFailure(msg: String) {
        errorMassage = msg
        _customerStates.value = ScreenState.Render(CustomerState.StateFailure)
    }

    override fun onCitySuccess(response: ArrayList<CityRes>) {
        cityArray.clear()
        cityArray.addAll(response!!)
        _customerStates.value = ScreenState.Render(CustomerState.CitySuccess)
    }

    override fun onCityFailure(msg: String) {
        errorMassage = msg
        _customerStates.value = ScreenState.Render(CustomerState.CityFailure)
    }
}

/**ViewModel Factory Method*/
class EditCustomerVMFactory(private val customerInteractor: CustomerInteractor, private val addressInteractor: AddressInteractor) :
        ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EditCustomerVM(customerInteractor, addressInteractor) as T
    }
}