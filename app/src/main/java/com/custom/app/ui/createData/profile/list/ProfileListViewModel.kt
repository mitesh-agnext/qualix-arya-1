package com.custom.app.ui.createData.profile.list

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerListListener
import com.custom.app.ui.customer.list.CustomerRes

class ProfileListViewModel(val profileListInteractor: ProfileListInteractor, val customerInteractor: CustomerInteractor) : ViewModel(),
        ProfileListInteractor.ListProfileInteractorCallback, CustomerListListener {

    private val _Profile_listState: MutableLiveData<ScreenState<ProfileListState>> = MutableLiveData()
    val profileListState: LiveData<ScreenState<ProfileListState>>
        get() = _Profile_listState

    private val _profileList: MutableLiveData<ArrayList<ProfileListRes>> = MutableLiveData()
    val profileList: LiveData<ArrayList<ProfileListRes>>
        get() = _profileList

    private val _customerList: MutableLiveData<ArrayList<CustomerRes>> = MutableLiveData()
    val customerList: LiveData<ArrayList<CustomerRes>>
        get() = _customerList

    private val _deletedPos: MutableLiveData<Int> = MutableLiveData()
    val deletedPos: LiveData<Int>
        get() = _deletedPos

    var errorMessage: String = "Error"

    fun onGetCustomerList() {
        _Profile_listState.value = ScreenState.Loading
        customerInteractor.list(this)
    }

    fun onGetProfileList(customer_id: Int) {
        _Profile_listState.value = ScreenState.Loading
        profileListInteractor.allProfile(customer_id,this)
    }

    fun onDeleteProfile(context: Context, profileId: Int, position: Int) {
        _Profile_listState.value = ScreenState.Loading
        profileListInteractor.deleteProfile(this, profileId,position)
    }

    override fun allProfileApiSuccess(body: ArrayList<ProfileListRes>) {
        _profileList.value = body
        _Profile_listState.value = ScreenState.Render(ProfileListState.ProfileListSuccess)
    }

    override fun allProfileApiError(msg: String) {
        errorMessage = msg
        _Profile_listState.value = ScreenState.Render(ProfileListState.ProfileListFailure)
    }

    override fun deleteProfileSuccess(deletedPostion: Int) {
        _deletedPos.value = deletedPostion
        _Profile_listState.value = ScreenState.Render(ProfileListState.DeleteProfileSuccess)
    }

    override fun deleteProfileFailure(msg: String) {
        errorMessage = msg
        _Profile_listState.value = ScreenState.Render(ProfileListState.DeleteProfileFailure)
    }

    override fun onCustomerListSuccess(response: ArrayList<CustomerRes>) {
        _customerList.value = response
        _Profile_listState.value = ScreenState.Render(ProfileListState.GetCustomerSuccess)
    }

    override fun onCustomerListFailure(msg: String) {
        errorMessage = msg
        _Profile_listState.value = ScreenState.Render(ProfileListState.GetCustomerFailure)
    }

    override fun onTokenExpire() {}
}

class ProfilesListViewModelFactory(
        private val profilesProfileListInteractor: ProfileListInteractor, val customerInteractor: CustomerInteractor) :
        ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProfileListViewModel(profilesProfileListInteractor, customerInteractor) as T
    }
}