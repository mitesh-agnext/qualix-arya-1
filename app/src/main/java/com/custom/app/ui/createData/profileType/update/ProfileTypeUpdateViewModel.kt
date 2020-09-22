package com.custom.app.ui.createData.profileType.update

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.createData.profileType.create.ProfileTypeCreateInteractor
import com.custom.app.ui.createData.profileType.list.ProfileTypeListRes
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerListListener
import com.custom.app.ui.customer.list.CustomerRes
import com.custom.app.ui.updateData.profileType.update.ProfileTypeUpdateInteractor
import okhttp3.ResponseBody

class ProfileTypeCreateViewModel(val profileTypeUpdateInteractor: ProfileTypeUpdateInteractor,
                                 val profileTypeCreateInteractor: ProfileTypeCreateInteractor,
                                 val customerInteractor: CustomerInteractor) : ViewModel(),
        ProfileTypeUpdateInteractor.UpdateProfileTypeListener,
        ProfileTypeCreateInteractor.CreateProfileTypeListener, CustomerListListener {

    private val _profileTypeUpdateState: MutableLiveData<ScreenState<ProfileTypeUpdateState>> = MutableLiveData()
    val profileTypeUpdateState: LiveData<ScreenState<ProfileTypeUpdateState>>
        get() = _profileTypeUpdateState

    private val _profileTypeUpdateResponse: MutableLiveData<ResponseBody> = MutableLiveData()
    val profileTypeUpdateResponse: LiveData<ResponseBody>
        get() = _profileTypeUpdateResponse

    private val _getCustomerList: MutableLiveData<ArrayList<CustomerRes>> = MutableLiveData()
    val getCustomerList: LiveData<ArrayList<CustomerRes>>
        get() = _getCustomerList

    var singleProfileType: ProfileTypeListRes = ProfileTypeListRes()
    var errorMessage: String = "Error"

    fun onGetCustomer() {
        _profileTypeUpdateState.value = ScreenState.Loading
        customerInteractor.list(this)
    }

    fun onUpdateProfileType(profile_type_name: String, profile_type_id: Int, customer_id: Int) {
        _profileTypeUpdateState.value = ScreenState.Loading
        profileTypeUpdateInteractor.updateProfileType(profile_type_name, profile_type_id, customer_id, this)
    }

    override fun onProfileTypeUpdateSuccess(body: ResponseBody) {
        _profileTypeUpdateResponse.value = body
        _profileTypeUpdateState.value = ScreenState.Render(ProfileTypeUpdateState.ProfileTypeUpdateSuccess)
    }

    override fun onProfileTypeUpdateFailure() {
        _profileTypeUpdateState.value = ScreenState.Render(ProfileTypeUpdateState.ProfileTypeUpdateFailure)
    }

    override fun onProfileTypeSuccess(body: ResponseBody) {}

    override fun onProfileTypeFailure() {}

    override fun onProfileTypeNameEmpty() {
        _profileTypeUpdateState.value = ScreenState.Render(ProfileTypeUpdateState.ProfileTypeNameEmpty)
    }

    override fun onCustomerListSuccess(response: ArrayList<CustomerRes>) {
        _getCustomerList.value = response
        _profileTypeUpdateState.value = ScreenState.Render(ProfileTypeUpdateState.GetCustomerSuccess)
    }

    override fun onCustomerListFailure(msg: String) {
        errorMessage = msg
        _profileTypeUpdateState.value = ScreenState.Render(ProfileTypeUpdateState.GetCustomerFailure)
    }

    override fun onTokenExpire() {}
}

class UpdateProfileTypeViewModelFactory(
        val profileTypeUpdateInteractor: ProfileTypeUpdateInteractor, private val profileTypecreateInteractor: ProfileTypeCreateInteractor,
        val customerInteractor: CustomerInteractor) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProfileTypeCreateViewModel(profileTypeUpdateInteractor, profileTypecreateInteractor, customerInteractor) as T
    }
}