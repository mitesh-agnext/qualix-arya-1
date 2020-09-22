package com.custom.app.ui.user.edit

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
import com.custom.app.ui.user.list.EditUserListener
import com.custom.app.ui.user.list.UserInteractor
import com.custom.app.ui.user.list.UserRoleListener
import retrofit2.Response

class EditUserVM(val userInteractor: UserInteractor, val addressInteractor: AddressInteractor) : ViewModel(), EditUserListener, UserRoleListener, AddressListener {

    private val _userStates: MutableLiveData<ScreenState<EditUserState>> = MutableLiveData()
    val userStates: LiveData<ScreenState<EditUserState>>
        get() = _userStates

    private val _userRole: MutableLiveData<ArrayList<RoleRes>> = MutableLiveData()
    val userRole: LiveData<ArrayList<RoleRes>>
        get() = _userRole

    var countryArray = ArrayList<CountryRes>()
    var stateArray = ArrayList<StateRes>()
    var cityArray = ArrayList<CityRes>()
    var errorMassage = "error"

    //Forward flow
    fun updateUser(user_id: String, requestData: HashMap<String, Any>) {
        _userStates.value = ScreenState.Loading
        userInteractor.edit(user_id, requestData, this)
    }

    fun userRole() {
        _userStates.value = ScreenState.Loading
        userInteractor.role(this)
    }

    fun addressCountry() {
        _userStates.value = ScreenState.Loading
        addressInteractor.getCountry(this)
    }

    fun addressState(countryId: String) {
        _userStates.value = ScreenState.Loading
        addressInteractor.getState(countryId, this)
    }

    fun addressCity(stateId: String) {
        _userStates.value = ScreenState.Loading
        addressInteractor.getCity(stateId, this)
    }

    //Backward flow
    override fun onEditUserSuccess() {
        _userStates.value = ScreenState.Render(EditUserState.EditUserSuccess)
    }

    override fun onEditUserFailure(msg: String) {
        errorMassage = msg
        _userStates.value = ScreenState.Render(EditUserState.EditUserFailure)
    }

    override fun onTokenExpire() {
        _userStates.value = ScreenState.Render(EditUserState.TokenExpire)
    }

    override fun onUserRoleSuccess(response: Response<ArrayList<RoleRes>>) {
        _userRole.value = response.body()
        _userStates.value = ScreenState.Render(EditUserState.UserRoleSuccess)
    }

    override fun onUserRoleFailure(msg: String) {
        errorMassage = msg
        _userStates.value = ScreenState.Render(EditUserState.UserRoleFailure)
    }

    override fun onCountrySuccess(response: ArrayList<CountryRes>?) {
        countryArray.clear()
        countryArray.addAll(response!!)
        _userStates.value = ScreenState.Render(EditUserState.CountrySuccess)
    }

    override fun onCountryFailure(msg: String) {
        errorMassage = msg
        _userStates.value = ScreenState.Render(EditUserState.CountryFailure)
    }

    override fun onStateSuccess(response: ArrayList<StateRes>) {
        stateArray.clear()
        stateArray.addAll(response!!)
        _userStates.value = ScreenState.Render(EditUserState.StateSuccess)
    }

    override fun onStateFailure(msg: String) {
        errorMassage = msg
        _userStates.value = ScreenState.Render(EditUserState.StateFailure)
    }

    override fun onCitySuccess(response: ArrayList<CityRes>) {
        cityArray.clear()
        cityArray.addAll(response!!)
        _userStates.value = ScreenState.Render(EditUserState.CitySuccess)
    }

    override fun onCityFailure(msg: String) {
        errorMassage = msg
        _userStates.value = ScreenState.Render(EditUserState.CityFailure)
    }
}

class EditUserVMFactory(private val userInteractor: UserInteractor, val addressInteractor: AddressInteractor) :
        ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EditUserVM(userInteractor, addressInteractor) as T
    }
}