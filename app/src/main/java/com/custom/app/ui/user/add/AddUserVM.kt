package com.custom.app.ui.user.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.data.model.country.CityRes
import com.custom.app.data.model.country.CountryRes
import com.custom.app.data.model.country.StateRes
import com.custom.app.data.model.role.RoleRes
import com.custom.app.ui.address.AddressInteractor
import com.custom.app.ui.address.AddressListener
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.customer.list.CustomerRes
import com.custom.app.ui.user.list.AddUserListener
import com.custom.app.ui.user.list.UserInteractor
import com.custom.app.ui.user.list.UserRoleListener
import retrofit2.Response

class AddUserVM(val userInteractor: UserInteractor,
                val addressInteractor: AddressInteractor) : ViewModel(),
        AddUserListener, UserRoleListener, AddressListener {

    private val _addUserState: MutableLiveData<ScreenState<AddUserState>> = MutableLiveData()
    val addUserState: LiveData<ScreenState<AddUserState>>
        get() = _addUserState

    private val _userRole: MutableLiveData<ArrayList<RoleRes>> = MutableLiveData()
    val userRole: LiveData<ArrayList<RoleRes>>
        get() = _userRole

    var countryArray = ArrayList<CountryRes>()
    var stateArray = ArrayList<StateRes>()
    var cityArray = ArrayList<CityRes>()
    var errorMsg: String = "Error"

    fun addUserVM(requestData: HashMap<String, Any>) {
        _addUserState.value = ScreenState.Loading
        userInteractor.addCustomer(requestData, this)
    }

    fun addUserSingleVM(requestData: HashMap<String, Any>) {
        _addUserState.value = ScreenState.Loading
        userInteractor.add(requestData, this)
    }

    fun userRole() {
        _addUserState.value = ScreenState.Loading
        userInteractor.role(this)
    }

    fun addressCountry() {
        _addUserState.value = ScreenState.Loading
        addressInteractor.getCountry(this)
    }

    fun addressState(countryId: String) {
        _addUserState.value = ScreenState.Loading
        addressInteractor.getState(countryId, this)
    }

    fun addressCity(stateId: String) {
        _addUserState.value = ScreenState.Loading
        addressInteractor.getCity(stateId, this)
    }

    /* Backward Flow */
    override fun onUserRoleSuccess(response: Response<ArrayList<RoleRes>>) {
        _userRole.value = response.body()
        _addUserState.value = ScreenState.Render(AddUserState.UserRoleSuccess)
    }

    override fun onUserRoleFailure(msg: String) {
        errorMsg = msg
        _addUserState.value = ScreenState.Render(AddUserState.UserRoleFailure)
    }

    override fun onAddUserSuccess(response: Response<CustomerRes>) {
        _addUserState.value = ScreenState.Render(AddUserState.AddUserSuccess)
    }

    override fun onAddUserFailure(msg: String) {
        errorMsg = msg
        _addUserState.value = ScreenState.Render(AddUserState.AddUserFailure)
    }

    override fun onAddUserSingleSuccess() {
        _addUserState.value = ScreenState.Render(AddUserState.AddUserSingleSuccess)
    }

    override fun onTokenExpire() {
        _addUserState.value = ScreenState.Render(AddUserState.TokenExpire)
    }

    override fun onCountrySuccess(response: ArrayList<CountryRes>?) {
        countryArray.clear()
        countryArray.addAll(response!!)
        _addUserState.value = ScreenState.Render(AddUserState.CountrySuccess)
    }

    override fun onCountryFailure(msg: String) {
        errorMsg = msg
        _addUserState.value = ScreenState.Render(AddUserState.CountryFailure)
    }

    override fun onStateSuccess(response: ArrayList<StateRes>) {
        stateArray.clear()
        stateArray.addAll(response!!)
        _addUserState.value = ScreenState.Render(AddUserState.StateSuccess)
    }

    override fun onStateFailure(msg: String) {
        errorMsg = msg
        _addUserState.value = ScreenState.Render(AddUserState.StateFailure)
    }

    override fun onCitySuccess(response: ArrayList<CityRes>) {
        cityArray.clear()
        cityArray.addAll(response)
        _addUserState.value = ScreenState.Render(AddUserState.CitySuccess)
    }

    override fun onCityFailure(msg: String) {
        errorMsg = msg
        _addUserState.value = ScreenState.Render(AddUserState.CityFailure)
    }
}

class AddUserViewModelFactory(private val userInteractor: UserInteractor, private val addressInteractor: AddressInteractor) :
        ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AddUserVM(userInteractor, addressInteractor) as T
    }
}