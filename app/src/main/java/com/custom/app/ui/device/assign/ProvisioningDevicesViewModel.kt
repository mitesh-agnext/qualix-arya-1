package com.custom.app.ui.device.assign

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.createData.coldstore.coldstoreList.ColdstoreRes
import com.custom.app.ui.createData.coldstore.createColdstore.ColdstoreInteractor
import com.custom.app.ui.createData.coldstore.createColdstore.ListCenterInteractorCallback
import com.custom.app.ui.customer.list.CustomerRes
import com.custom.app.ui.device.list.DeviceInteractor
import com.custom.app.ui.device.list.ProvisioningDevicesListener
import com.custom.app.ui.user.list.UserDataRes
import okhttp3.ResponseBody

class ProvisioningDevicesViewModel(val deviceInteractor: DeviceInteractor, val coldstoreInteractor: ColdstoreInteractor) : ViewModel(),
        ProvisioningDevicesListener, ListCenterInteractorCallback {

    private val _provisioningDevicesState: MutableLiveData<ScreenState<ProvisioningDevicesState>> = MutableLiveData()
    val provisioningDevicesState: LiveData<ScreenState<ProvisioningDevicesState>>
        get() = _provisioningDevicesState

    private val _provisioningDeviceResponse: MutableLiveData<ResponseBody> = MutableLiveData()
    val provisioningDeviceResponse: LiveData<ResponseBody>
        get() = _provisioningDeviceResponse

    private val _getCustomerResponse: MutableLiveData<ArrayList<CustomerRes>> = MutableLiveData()
    val getCustomerResponse: LiveData<ArrayList<CustomerRes>>
        get() = _getCustomerResponse

    private val _getUserResponse: MutableLiveData<ArrayList<UserDataRes>> = MutableLiveData()
    val getUserResponse: LiveData<ArrayList<UserDataRes>>
        get() = _getUserResponse

    private val _getInstallationCenterResponse: MutableLiveData<ArrayList<InstallationCenterRes>> = MutableLiveData()
    val getInstallationCenterResponse: LiveData<ArrayList<InstallationCenterRes>>
        get() = _getInstallationCenterResponse

    private val _getDeviationProfile: MutableLiveData<ArrayList<DeviationProfileRes>> = MutableLiveData()
    val getDeviationProfile: LiveData<ArrayList<DeviationProfileRes>>
        get() = _getDeviationProfile

    private val _getColdstores: MutableLiveData<ArrayList<ColdstoreRes>> = MutableLiveData()
    val getColdstores: LiveData<ArrayList<ColdstoreRes>>
        get() = _getColdstores

    fun onGetCustomer() {
        _provisioningDevicesState.value = ScreenState.Loading
        deviceInteractor.getDeviceCustomers(this)
    }

    fun onGetUser(customer_id: String) {
        _provisioningDevicesState.value = ScreenState.Loading
        deviceInteractor.getDeviceUsers(this, customer_id)
    }

    fun onGetColdstore(customer_id: Int) {
        _provisioningDevicesState.value = ScreenState.Loading
        coldstoreInteractor.allCenter(this,"",customer_id)
    }

    fun onGetInstallationCenters(customer_id: Int) {
        _provisioningDevicesState.value = ScreenState.Loading
        deviceInteractor.getInstallationCenters(this, customer_id)
    }

    fun onGetDeviationProfile(customer_id: Int) {
        _provisioningDevicesState.value = ScreenState.Loading
        deviceInteractor.getDeviationProfiles(this, customer_id)
    }

    fun onDeviceProvisioning(hw_revision: String, fw_revision: String, start_of_service: String, end_of_service: String,
                             customer_id: String, installation_center_id: String, device_id: Int, coldstore_id: String, deviation_id: String, user_id: String, context: Context) {
        _provisioningDevicesState.value = ScreenState.Loading
        deviceInteractor.deviceProvisioning(hw_revision, fw_revision, start_of_service, end_of_service, customer_id,
                installation_center_id, device_id, coldstore_id,deviation_id, user_id, this)
    }

    var errorMessage = ""
    override fun onProvisioningDevicesSuccess(body: ResponseBody) {
        _provisioningDeviceResponse.value = body
        _provisioningDevicesState.value = ScreenState.Render(ProvisioningDevicesState.ProvisioningDevicesSuccess)
    }

    override fun onProvisioningDevicesFailure(msg: String) {
        errorMessage = msg
        _provisioningDevicesState.value = ScreenState.Render(ProvisioningDevicesState.ProvisioningDevicesFailure)
    }

    override fun onGetCustomerSuccess(body: ArrayList<CustomerRes>) {
        _getCustomerResponse.value = body
        _provisioningDevicesState.value = ScreenState.Render(ProvisioningDevicesState.GetCustomerSuccess)
    }

    override fun onGetCustomerFailure(msg: String) {
        errorMessage = msg
        _provisioningDevicesState.value = ScreenState.Render(ProvisioningDevicesState.GetCustomerFailure)
    }

    override fun onGetCustomerEmpty(msg: String) {
        errorMessage = msg
        _provisioningDevicesState.value = ScreenState.Render(ProvisioningDevicesState.GetCustomerFailure)
    }

    override fun onGetUserSuccess(body: ArrayList<UserDataRes>) {
        _getUserResponse.value = body
        _provisioningDevicesState.value = ScreenState.Render(ProvisioningDevicesState.GetUserSuccess)
    }

    override fun onGetUserFailure(msg: String) {
        errorMessage = msg
        _provisioningDevicesState.value = ScreenState.Render(ProvisioningDevicesState.GetUserFailure)
    }

    override fun onGetUserEmpty(msg: String) {
        errorMessage = msg
        _provisioningDevicesState.value = ScreenState.Render(ProvisioningDevicesState.GetUserFailure)
    }

    override fun onGetInstallationCentersSuccess(body: ArrayList<InstallationCenterRes>) {
        _getInstallationCenterResponse.value = body
        _provisioningDevicesState.value = ScreenState.Render(ProvisioningDevicesState.GetInstallationCentersSuccess)
    }

    override fun onGetInstallationCentersFailure(msg: String) {
        errorMessage = msg
        _provisioningDevicesState.value = ScreenState.Render(ProvisioningDevicesState.GetInstallationsCentersFailure)
    }

    override fun onGetInstallationCentersEmpty(msg: String) {
        errorMessage = msg
        _provisioningDevicesState.value = ScreenState.Render(ProvisioningDevicesState.GetInstallationsCentersFailure)
    }

    override fun onGetDeviationProfileSuccess(body: ArrayList<DeviationProfileRes>) {
        _getDeviationProfile.value = body
        _provisioningDevicesState.value = ScreenState.Render(ProvisioningDevicesState.GetDeviationProfileSuccess)
    }

    override fun onGetDeviationProfileFailure(msg: String) {
        errorMessage = msg
        _provisioningDevicesState.value = ScreenState.Render(ProvisioningDevicesState.GetDeviationProfileFailure)
    }

    override fun onGetDeviationProfileEmpty(msg: String) {
        errorMessage = msg
        _provisioningDevicesState.value = ScreenState.Render(ProvisioningDevicesState.GetDeviationProfileFailure)
    }

    override fun onStartServiceEmpty() {
        _provisioningDevicesState.value = ScreenState.Render(ProvisioningDevicesState.StartServiceEmpty)
    }

    override fun onEndServiceEmpty() {
        _provisioningDevicesState.value = ScreenState.Render(ProvisioningDevicesState.EndServiceEmpty)
    }

    override fun allCentersApiSuccess(body: ArrayList<ColdstoreRes>) {
        _getColdstores.value = body
        _provisioningDevicesState.value = ScreenState.Render(ProvisioningDevicesState.GetColdstoreSuccess)
    }

    override fun allCentersApiError(msg: String) {
        errorMessage = msg
        _provisioningDevicesState.value = ScreenState.Render(ProvisioningDevicesState.GetDeviationProfileFailure)
    }

    override fun deleteCenterSuccess(deletedPostion: Int) {}
    override fun deleteCenterFailure(msg: String) {}
}

class ProvisioningDeviceViewModelFactory(
        private val deviceInteractor: DeviceInteractor,val coldstoreInteractor: ColdstoreInteractor) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProvisioningDevicesViewModel(deviceInteractor, coldstoreInteractor) as T
    }
}