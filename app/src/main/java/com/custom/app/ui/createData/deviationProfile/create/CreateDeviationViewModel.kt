package com.custom.app.ui.createData.deviationProfile.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.createData.profile.list.ProfileListInteractor
import com.custom.app.ui.createData.profile.list.ProfileListRes
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerListListener
import com.custom.app.ui.customer.list.CustomerRes
import okhttp3.ResponseBody

class CreateDeviationViewModel(val createDeviationInteractor: CreateDeviationInteractor, val customerInteractor: CustomerInteractor,
                               val profileListInteractor: ProfileListInteractor) : ViewModel(),
        CreateDeviationInteractor.onCreateDeviationListener, CustomerListListener, ProfileListInteractor.ListProfileInteractorCallback {

    private val _createDeviationState: MutableLiveData<ScreenState<CreateDeviationState>> = MutableLiveData()
    val createDeviationState: LiveData<ScreenState<CreateDeviationState>>
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

    fun onCreateDeviation(profile_id: Int, customer_id: Int, blMin: String, blMax: String, tempMin: String, tempMax: String, moistMin: String, moistMax: String,
                          level1Hour: String, level1Minute: String, level1Email: String, level2Hour: String, level2Minute: String, level2Email: String, level3Hour: String,
                          level3Minute: String, level3Email: String, level4Hour: String, level4Minute: String, level4Email: String, level5Hour: String, level5Minute: String,
                          level5Email: String) {
        _createDeviationState.value = ScreenState.Loading
        createDeviationInteractor.addDeviation(profile_id, customer_id, blMin, blMax, tempMin, tempMax, moistMin, moistMax, level1Hour, level1Minute, level1Email, level2Hour,
                level2Minute, level2Email, level3Hour, level3Minute, level3Email, level4Hour, level4Minute, level4Email, level5Hour, level5Minute, level5Email, this)
    }

    override fun onCustomerListSuccess(body: ArrayList<CustomerRes>) {
        _getCustomerList.value = body
        _createDeviationState.value = ScreenState.Render(CreateDeviationState.GetCustomerSuccess)
    }

    override fun onCustomerListFailure(msg: String) {
        errorMessage = msg
        _createDeviationState.value = ScreenState.Render(CreateDeviationState.GetCustomerError)
    }

    override fun allProfileApiSuccess(body: ArrayList<ProfileListRes>) {
        _getProfileResponse.value = body
        _createDeviationState.value = ScreenState.Render(CreateDeviationState.GetProfileSuccess)
    }

    override fun allProfileApiError(msg: String) {
        errorMessage = msg
        _createDeviationState.value = ScreenState.Render(CreateDeviationState.GetProfileError)
    }

    override fun onTokenExpire() {}

    override fun deleteProfileSuccess(deletedPostion: Int) {}
    override fun deleteProfileFailure(msg: String) {}
    override fun onDeviationSuccess(body: ResponseBody) {}
    override fun onDeviationFailure() {}

}

class CreateDeviationViewModelFactory(
        val createColdstoreInteractor: CreateDeviationInteractor, val customerInteractor: CustomerInteractor,
        val profileListInteractor: ProfileListInteractor) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CreateDeviationViewModel(createColdstoreInteractor, customerInteractor, profileListInteractor) as T
    }
}