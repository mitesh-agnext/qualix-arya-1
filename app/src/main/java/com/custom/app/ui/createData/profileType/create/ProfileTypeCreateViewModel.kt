package com.custom.app.ui.createData.profileType.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerListListener
import com.custom.app.ui.customer.list.CustomerRes
import okhttp3.ResponseBody

class ProfileTypeCreateViewModel(val profileTypeCreateInteractor: ProfileTypeCreateInteractor, val customerInteractor: CustomerInteractor) : ViewModel(),
        ProfileTypeCreateInteractor.CreateProfileTypeListener, CustomerListListener {

    private val _profileTypeCreateState: MutableLiveData<ScreenState<ProfileTypeCreateState>> = MutableLiveData()
    val profileTypeCreateState: LiveData<ScreenState<ProfileTypeCreateState>>
        get() = _profileTypeCreateState

    private val _profileTypeCreateResponse: MutableLiveData<ResponseBody> = MutableLiveData()
    val profileTypeCreateResponse: LiveData<ResponseBody>
        get() = _profileTypeCreateResponse

    private val _getCustomerList: MutableLiveData<ArrayList<CustomerRes>> = MutableLiveData()
    val getCustomerList: LiveData<ArrayList<CustomerRes>>
        get() = _getCustomerList

    var errorMessage = "Error"

    fun onGetCustomer() {
        _profileTypeCreateState.value = ScreenState.Loading
        customerInteractor.list(this)
    }

    fun onCreateProfileType(profile_name: String, customer_id: Int) {
        _profileTypeCreateState.value = ScreenState.Loading
        profileTypeCreateInteractor.addProfileType(profile_name, customer_id, this)
    }

    override fun onProfileTypeSuccess(body: ResponseBody) {
        _profileTypeCreateResponse.value = body
        _profileTypeCreateState.value = ScreenState.Render(ProfileTypeCreateState.ProfileTypeCreateSuccess)
    }

    override fun onProfileTypeFailure() {
        _profileTypeCreateState.value = ScreenState.Render(ProfileTypeCreateState.ProfileTypeCreateFailure)
    }

    override fun onProfileTypeNameEmpty() {
        _profileTypeCreateState.value = ScreenState.Render(ProfileTypeCreateState.ProfileTypeNameEmpty)
    }

    override fun onCustomerListSuccess(response: ArrayList<CustomerRes>) {
        _getCustomerList.value = response
        _profileTypeCreateState.value = ScreenState.Render(ProfileTypeCreateState.GetCustomerSuccess)
    }

    override fun onCustomerListFailure(msg: String) {
        errorMessage = msg
        _profileTypeCreateState.value = ScreenState.Render(ProfileTypeCreateState.GetCustomerFailure)
    }

    override fun onTokenExpire() {}
}

class CreateProfileTypeViewModelFactory(
        private val profileTypeInteractor: ProfileTypeCreateInteractor, val customerInteractor: CustomerInteractor) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProfileTypeCreateViewModel(profileTypeInteractor, customerInteractor) as T
    }
}