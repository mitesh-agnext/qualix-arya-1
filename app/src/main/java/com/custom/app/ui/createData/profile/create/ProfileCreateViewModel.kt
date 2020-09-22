package com.custom.app.ui.createData.profile.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerListListener
import com.custom.app.ui.customer.list.CustomerRes
import okhttp3.ResponseBody

class ProfileCreateViewModel(val profileCreateInteractor: ProfileCreateInteractor, val customerInteractor: CustomerInteractor) : ViewModel(),
        ProfileCreateInteractor.CreateProfileListener, CustomerListListener {

    private val _profileCreateState: MutableLiveData<ScreenState<ProfileCreateState>> = MutableLiveData()
    val profileCreateState: LiveData<ScreenState<ProfileCreateState>>
        get() = _profileCreateState

    private val _profileCreateResponse: MutableLiveData<ResponseBody> = MutableLiveData()
    val profileCreateResponse: LiveData<ResponseBody>
        get() = _profileCreateResponse

    private val _getCustomerList: MutableLiveData<ArrayList<CustomerRes>> = MutableLiveData()
    val getCustomerList: LiveData<ArrayList<CustomerRes>>
        get() = _getCustomerList

    var errorMessage: String = "Error"
    fun onGetCustomer() {
        _profileCreateState.value = ScreenState.Loading
        customerInteractor.list(this)
    }

    fun onCreateProfile(profile_name: String, customer_id: Int, maxTemp: String, minTemp: String) {
        _profileCreateState.value = ScreenState.Loading
        profileCreateInteractor.addProfile(profile_name,customer_id,maxTemp,minTemp,this)
    }

    override fun onProfileSuccess(body: ResponseBody) {
        _profileCreateResponse.value = body
        _profileCreateState.value = ScreenState.Render(ProfileCreateState.ProfileCreateSuccess)
    }

    override fun onProfileFailure() {
        _profileCreateState.value = ScreenState.Render(ProfileCreateState.ProfileCreateFailure)
    }

    override fun onProfileNameEmpty() {
        _profileCreateState.value = ScreenState.Render(ProfileCreateState.ProfileNameEmpty)
    }

    override fun onMaxTempEmpty() {
        _profileCreateState.value = ScreenState.Render(ProfileCreateState.MaxTempEmpty)
    }

    override fun onMinTempEmpty() {
        _profileCreateState.value = ScreenState.Render(ProfileCreateState.MinTempEmpty)
    }

    override fun onCustomerListSuccess(response: ArrayList<CustomerRes>) {
    _getCustomerList.value = response
    _profileCreateState.value = ScreenState.Render(ProfileCreateState.GetCustomerSuccess)
    }

    override fun onCustomerListFailure(msg: String) {
        errorMessage = msg
        _profileCreateState.value = ScreenState.Render(ProfileCreateState.GetCustomerFailure)
    }

    override fun onTokenExpire() {
    }
}

class CreateProfileViewModelFactory(
        private val profileInteractor: ProfileCreateInteractor, val customerInteractor: CustomerInteractor) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProfileCreateViewModel(profileInteractor, customerInteractor) as T
    }
}