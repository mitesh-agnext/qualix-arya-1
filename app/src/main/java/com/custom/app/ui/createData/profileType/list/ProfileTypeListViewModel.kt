package com.custom.app.ui.createData.profileType.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerListListener
import com.custom.app.ui.customer.list.CustomerRes

class ProfileTypeListViewModel(val profileTypeListInteractor: ProfileTypeListInteractor, val customerInteractor: CustomerInteractor) : ViewModel(),
        ProfileTypeListInteractor.ListProfileTypeInteractorCallback, CustomerListListener {

    private val _ProfileType_listState: MutableLiveData<ScreenState<ProfileTypeListState>> = MutableLiveData()
    val profileTypeListState: LiveData<ScreenState<ProfileTypeListState>>
        get() = _ProfileType_listState

    private val _profileTypeList: MutableLiveData<ArrayList<ProfileTypeListRes>> = MutableLiveData()
    val profileTypeList: LiveData<ArrayList<ProfileTypeListRes>>
        get() = _profileTypeList

    private val _customerList: MutableLiveData<ArrayList<CustomerRes>> = MutableLiveData()
    val customerList: LiveData<ArrayList<CustomerRes>>
        get() = _customerList

    private val _deletedPos: MutableLiveData<Int> = MutableLiveData()
    val deletedPos: LiveData<Int>
        get() = _deletedPos

    var errorMessage: String = "Error"

    fun onGetCustomerList() {
        _ProfileType_listState.value = ScreenState.Loading
        customerInteractor.list(this)
    }

    fun onGetProfileTypeList(customer_id: Int) {
        _ProfileType_listState.value = ScreenState.Loading
        profileTypeListInteractor.allProfileType(customer_id, this)
    }

    fun onDeleteProfileType(profileTypeId: Int, position: Int) {
        _ProfileType_listState.value = ScreenState.Loading
        profileTypeListInteractor.deleteProfileType(this, profileTypeId, position)
    }

    override fun allProfileTypeApiSuccess(body: ArrayList<ProfileTypeListRes>) {
        _profileTypeList.value = body
        _ProfileType_listState.value = ScreenState.Render(ProfileTypeListState.ProfileTypeListSuccess)
    }

    override fun allProfileTypeApiError(msg: String) {
        errorMessage = msg
        _ProfileType_listState.value = ScreenState.Render(ProfileTypeListState.ProfileTypeListFailure)
    }

    override fun deleteProfileTypeSuccess(deletedPostion: Int) {
        _deletedPos.value = deletedPostion
        _ProfileType_listState.value = ScreenState.Render(ProfileTypeListState.DeleteProfileTypeSuccess)
    }

    override fun deleteProfileTypeFailure(msg: String) {
        errorMessage = msg
        _ProfileType_listState.value = ScreenState.Render(ProfileTypeListState.DeleteProfileTypeFailure)
    }

    override fun onCustomerListSuccess(response: ArrayList<CustomerRes>) {
        _customerList.value = response
        _ProfileType_listState.value = ScreenState.Render(ProfileTypeListState.GetCustomerSuccess)
    }

    override fun onCustomerListFailure(msg: String) {
        errorMessage = msg
        _ProfileType_listState.value = ScreenState.Render(ProfileTypeListState.GetCustomerFailure)
    }

    override fun onTokenExpire() {}
}

class ProfileTypeListViewModelFactory(
        private val profileTypeListInteractor: ProfileTypeListInteractor, val customerInteractor: CustomerInteractor) :
        ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProfileTypeListViewModel(profileTypeListInteractor, customerInteractor) as T
    }
}