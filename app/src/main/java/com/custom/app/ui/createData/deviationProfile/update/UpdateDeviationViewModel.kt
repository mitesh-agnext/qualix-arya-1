package com.custom.app.ui.createData.deviationProfile.update

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.createData.deviationProfile.create.CreateDeviationInteractor
import com.custom.app.ui.createData.profile.list.ProfileListInteractor
import com.custom.app.ui.createData.profile.list.ProfileListRes
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerListListener
import com.custom.app.ui.customer.list.CustomerRes
import okhttp3.ResponseBody

class CreateDeviationViewModel(val updateDeviationInteractor: UpdateDeviationInteractor, val customerInteractor: CustomerInteractor,
                               val profileListInteractor: ProfileListInteractor) : ViewModel(),
        CreateDeviationInteractor.onCreateDeviationListener, CustomerListListener, ProfileListInteractor.ListProfileInteractorCallback {

    private val _createDeviationState: MutableLiveData<ScreenState<UpdateDeviationState>> = MutableLiveData()
    val createDeviationState: LiveData<ScreenState<UpdateDeviationState>>
        get() = _createDeviationState

    private val _createDeviationResponse: MutableLiveData<ResponseBody> = MutableLiveData()
    val createDeviationResponse: LiveData<ResponseBody>
        get() = _createDeviationResponse

    private val _getCustomerList: MutableLiveData<ArrayList<CustomerRes>> = MutableLiveData()
    val getCustomerList: LiveData<ArrayList<CustomerRes>>
        get() = _getCustomerList

    private val _getProfileResponse: MutableLiveData<ArrayList<ProfileListRes>> = MutableLiveData()
    val getProfileResponse: LiveData<ArrayList<ProfileListRes>>
        get() = _getProfileResponse

    var errorMessage: String = "Error"

    fun onGetCustomer() {
        _createDeviationState.value = ScreenState.Loading
        customerInteractor.list(this)
    }

    fun onGetProfile(customerId: Int) {
        _createDeviationState.value = ScreenState.Loading
        profileListInteractor.allProfile(customerId, this)
    }

    fun onUpdateDeviation(profile_name: String, customer_id: Int, mainOperator: String, BLMIN: String, operator: String, BLMax: String, level_id: String, level_type: String,
                          frequency: String, to_emails: String, listener: UpdateDeviationInteractor.onUpdateDeviationListener) {
        _createDeviationState.value = ScreenState.Loading
        updateDeviationInteractor.updateDeviation(profile_name, customer_id, mainOperator, BLMIN, operator, BLMax, level_id, level_type, frequency, to_emails, listener)
    }

    override fun onCustomerListSuccess(body: ArrayList<CustomerRes>) {
        _getCustomerList.value = body
        _createDeviationState.value = ScreenState.Render(UpdateDeviationState.GetCustomerSuccess)
    }

    override fun onCustomerListFailure(msg: String) {
        errorMessage = msg
        _createDeviationState.value = ScreenState.Render(UpdateDeviationState.GetCustomerError)
    }

    override fun allProfileApiSuccess(body: ArrayList<ProfileListRes>) {
        _getProfileResponse.value = body
        _createDeviationState.value = ScreenState.Render(UpdateDeviationState.GetProfileSuccess)
    }

    override fun allProfileApiError(msg: String) {
        errorMessage = msg
        _createDeviationState.value = ScreenState.Render(UpdateDeviationState.GetProfileError)
    }

    override fun onTokenExpire() {}

    override fun deleteProfileSuccess(deletedPostion: Int) {}
    override fun deleteProfileFailure(msg: String) {}
    override fun onDeviationSuccess(body: ResponseBody) {}
    override fun onDeviationFailure() {}

}

class CreateDeviationViewModelFactory(
        val createColdstoreInteractor: UpdateDeviationInteractor, val customerInteractor: CustomerInteractor,
        val profileListInteractor: ProfileListInteractor) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CreateDeviationViewModel(createColdstoreInteractor, customerInteractor, profileListInteractor) as T
    }
}