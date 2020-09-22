package com.custom.app.ui.createData.profile.update

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.createData.profile.create.ProfileCreateInteractor
import com.custom.app.ui.createData.profile.list.ProfileListRes
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerListListener
import com.custom.app.ui.customer.list.CustomerRes
import com.custom.app.ui.updateData.profile.update.ProfileUpdateInteractor
import okhttp3.ResponseBody

class ProfileUpdateViewModel(val profileUpdateInteractor: ProfileUpdateInteractor, val profileCreateInteractor: ProfileCreateInteractor,
                             val customerInteractor: CustomerInteractor) : ViewModel(),
        ProfileUpdateInteractor.UpdateProfileListener, ProfileCreateInteractor.CreateProfileListener, CustomerListListener {

    private val _profileUpdateState: MutableLiveData<ScreenState<ProfileUpdateState>> = MutableLiveData()
    val profileUpdateState: LiveData<ScreenState<ProfileUpdateState>>
        get() = _profileUpdateState

    private val _profileUpdateResponse: MutableLiveData<ResponseBody> = MutableLiveData()
    val profileUpdateResponse: LiveData<ResponseBody>
        get() = _profileUpdateResponse

    private val _getCustomerList: MutableLiveData<ArrayList<CustomerRes>> = MutableLiveData()
    val getCustomerList: LiveData<ArrayList<CustomerRes>>
        get() = _getCustomerList

    var singleProfile: ProfileListRes = ProfileListRes()
    var errorMessage: String = "Error"

    fun onGetCustomer() {
        _profileUpdateState.value = ScreenState.Loading
        customerInteractor.list(this)
    }

    fun onUpdateProfile(profile_name: String, profile_id: Int, customerId: Int, maxTemp: String, minTemp: String) {
        _profileUpdateState.value = ScreenState.Loading
        profileUpdateInteractor.updateProfile(profile_name, profile_id, customerId, maxTemp, minTemp, this)
    }

    override fun onProfileSuccess(body: ResponseBody) {}

    override fun onProfileFailure() {}

    override fun onProfileUpdateSuccess(body: ResponseBody) {
        _profileUpdateResponse.value = body
        _profileUpdateState.value = ScreenState.Render(ProfileUpdateState.ProfileUpdateSuccess)
    }

    override fun onProfileUpdateFailure() {
        _profileUpdateState.value = ScreenState.Render(ProfileUpdateState.ProfileUpdateFailure)
    }

    override fun onProfileNameEmpty() {
        _profileUpdateState.value = ScreenState.Render(ProfileUpdateState.ProfileNameEmpty)
    }

    override fun onMaxTempEmpty() {
        _profileUpdateState.value = ScreenState.Render(ProfileUpdateState.MaxTempEmpty)
    }

    override fun onMinTempEmpty() {
        _profileUpdateState.value = ScreenState.Render(ProfileUpdateState.MinTempEmpty)
    }

    override fun onCustomerListSuccess(response: ArrayList<CustomerRes>) {
        _getCustomerList.value = response
        _profileUpdateState.value = ScreenState.Render(ProfileUpdateState.GetCustomerSuccess)
    }

    override fun onCustomerListFailure(msg: String) {
        errorMessage = msg
        _profileUpdateState.value = ScreenState.Render(ProfileUpdateState.GetCustomerFailure)
    }

    override fun onTokenExpire() {
    }
}

class UpdateProfileViewModelFactory(
        val profileUpdateInteractor: ProfileUpdateInteractor, private val profilecreateInteractor: ProfileCreateInteractor,
        val customerInteractor: CustomerInteractor) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProfileUpdateViewModel(profileUpdateInteractor, profilecreateInteractor, customerInteractor) as T
    }
}