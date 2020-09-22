package com.custom.app.ui.createData.instlCenter.updateCenter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.core.app.util.RxUtils
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.createData.instlCenter.CreateInstallationCenterListener
import com.custom.app.ui.createData.instlCenter.InstallationCenterInteractor
import com.custom.app.ui.createData.instlCenter.UpdateCenterListener
import com.custom.app.ui.createData.instlCenter.createInstallationCenter.InstallationCenterTypeRes
import com.custom.app.ui.createData.region.ListRegionInteractorCallback
import com.custom.app.ui.createData.region.ListSiteInteractorCallback
import com.custom.app.ui.createData.region.RegionSiteInteractor
import com.custom.app.ui.createData.region.site.create.RegionRes
import com.custom.app.ui.createData.region.site.list.SiteListRes
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerListListener
import com.custom.app.ui.customer.list.CustomerRes
import com.custom.app.ui.device.assign.InstallationCenterRes
import com.custom.app.ui.user.list.UserDataRes
import io.reactivex.disposables.Disposable
import okhttp3.ResponseBody

class UpdateCenterViewModel(val createIntlCentersInteractor: InstallationCenterInteractor,
                            val customerInteractor: CustomerInteractor,
                            val regionSiteInteractor: RegionSiteInteractor) : ViewModel(),
        UpdateCenterListener, CreateInstallationCenterListener,
        CustomerListListener, ListRegionInteractorCallback,
        ListSiteInteractorCallback {

    private var disposable: Disposable? = null

    private val _updateCenterState: MutableLiveData<ScreenState<UpdateCenterState>> = MutableLiveData()
    val updateCenterState: LiveData<ScreenState<UpdateCenterState>>
        get() = _updateCenterState

    private val _updateDeviceResponse: MutableLiveData<ResponseBody> = MutableLiveData()

    private val _getCustomerList: MutableLiveData<ArrayList<CustomerRes>> = MutableLiveData()
    val getCustomerList: LiveData<ArrayList<CustomerRes>>
        get() = _getCustomerList

    private val _getUserList: MutableLiveData<ArrayList<UserDataRes>> = MutableLiveData()
    val getUserList: LiveData<ArrayList<UserDataRes>>
        get() = _getUserList

    private val _getCenterTypeResponse: MutableLiveData<ArrayList<InstallationCenterTypeRes>> = MutableLiveData()
    val getCenterTypeResponse: LiveData<ArrayList<InstallationCenterTypeRes>>
        get() = _getCenterTypeResponse

    private val _getRegionResponse: MutableLiveData<ArrayList<RegionRes>> = MutableLiveData()
    val getRegionResponse: LiveData<ArrayList<RegionRes>>
        get() = _getRegionResponse

    private val _getSiteResponse: MutableLiveData<ArrayList<SiteListRes>> = MutableLiveData()
    val getSiteResponse: LiveData<ArrayList<SiteListRes>>
        get() = _getSiteResponse

    var singleCenter: InstallationCenterRes = InstallationCenterRes()

    var errorMessage: String? = "Error"

    fun onGetCustomer() {
        _updateCenterState.value = ScreenState.Loading
        customerInteractor.list(this)
    }

    fun onGetUser(customerId: Int) {
        _updateCenterState.value = ScreenState.Loading
        createIntlCentersInteractor.getUser(this, customerId)
    }

    fun onGetCenterType(regionId: Int) {
        _updateCenterState.value = ScreenState.Loading
        createIntlCentersInteractor.getInstallationCenterType(this)
    }

    fun onGetRegion(customerId: Int) {
        _updateCenterState.value = ScreenState.Loading
        regionSiteInteractor.allRegion(this, customerId)
    }

    fun onGetSite(keyword: String, regionId: Int) {
        _updateCenterState.value = ScreenState.Loading
        regionSiteInteractor.allSite(this, keyword, regionId)
    }

    fun onUpdateCenter(coldstoreId: Int, coldstoreName: String, center_type_id: Int, customer_id: Int, siteId: Int, regionId: Int, userId: Int, note: String) {
        _updateCenterState.value = ScreenState.Loading
        createIntlCentersInteractor.updateCenter(coldstoreId, coldstoreName, center_type_id, customer_id, siteId, regionId, userId, note, this)
    }

    override fun onUpdateCenterSuccess(body: ResponseBody) {
        _updateCenterState.value = ScreenState.Render(UpdateCenterState.UpdateInstallationCenterSuccess)
    }

    override fun onUpdateCenterFailure(msg: String) {
        errorMessage = msg
        _updateCenterState.value = ScreenState.Render(UpdateCenterState.UpdateInstallationCenterFailure)
    }

    override fun onInstallationCenterSuccess(body: ResponseBody) {}

    override fun onInstallationCenterFailure(msg: String) {}

    override fun onCenterNameEmpty() {
        _updateCenterState.value = ScreenState.Render(UpdateCenterState.InstallationCenterNameEmpty)
    }

    override fun onGetInstallationCenterTypeSuccess(body: ArrayList<InstallationCenterTypeRes>) {
        _getCenterTypeResponse.value = body
        _updateCenterState.value = ScreenState.Render(UpdateCenterState.GetCommercialTypeSuccess)
    }

    override fun onGetInstallationCenterTypeError(msg: String) {
        errorMessage = msg
        _updateCenterState.value = ScreenState.Render(UpdateCenterState.GetCommercialTypeError)
    }

    override fun onGetInstallationCenterTypeEmpty(msg: String) {
        errorMessage = msg
        _updateCenterState.value = ScreenState.Render(UpdateCenterState.GetCommercialTypeError)
    }

    override fun onGetUserSuccess(body: ArrayList<UserDataRes>) {
        _getUserList.value = body
        _updateCenterState.value = ScreenState.Render(UpdateCenterState.GetUserSuccess)
    }

    override fun onGetUserFailure(msg: String?) {
        errorMessage = msg
        _updateCenterState.value = ScreenState.Render(UpdateCenterState.GetUserError)
    }

    override fun onGetUserEmpty(msg: String?) {
        errorMessage = msg
        _updateCenterState.value = ScreenState.Render(UpdateCenterState.GetUserError)

    }

    override fun onCustomerListSuccess(body: ArrayList<CustomerRes>) {
        _getCustomerList.value = body
        _updateCenterState.value = ScreenState.Render(UpdateCenterState.GetCustomerSuccess)
    }

    override fun onCustomerListFailure(msg: String) {
        errorMessage = msg
        _updateCenterState.value = ScreenState.Render(UpdateCenterState.GetCustomerError)
    }

    override fun onGetRegionSuccess(body: ArrayList<RegionRes>) {
        _getRegionResponse.value = body
        _updateCenterState.value = ScreenState.Render(UpdateCenterState.GetRegionSuccess)
    }

    override fun onGetRegionFailure(msg: String) {
        errorMessage = msg
        _updateCenterState.value = ScreenState.Render(UpdateCenterState.GetRegionError)
    }

    override fun onGetRegionEmpty(msg: String) {
        errorMessage = msg
        _updateCenterState.value = ScreenState.Render(UpdateCenterState.GetRegionError)
    }

    override fun onGetSiteSuccess(body: ArrayList<SiteListRes>) {
        _getSiteResponse.value = body
        _updateCenterState.value = ScreenState.Render(UpdateCenterState.GetSiteSuccess)
    }

    override fun onGetSiteError(msg: String) {
        errorMessage = msg
        _updateCenterState.value = ScreenState.Render(UpdateCenterState.GetSiteError)
    }

    override fun onGetSiteEmpty(msg: String) {
        errorMessage = msg
        _updateCenterState.value = ScreenState.Render(UpdateCenterState.GetSiteError)
    }

    override fun onTokenExpire() {}

    override fun deleteSiteSuccess(deletedPostion: Int) {}
    override fun deleteSiteFailure(msg: String) {}
    override fun deleteRegionSuccess(deletedPostion: Int) {}
    override fun deleteRegionFailure(msg: String) {}

    fun destroy() {
        RxUtils.dispose(disposable)
    }
}

class UpdateCenterViewModelFactory(
        val createIntlCentersInteractor: InstallationCenterInteractor,
        val customerInteractor: CustomerInteractor, val regionSiteInteractor: RegionSiteInteractor) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return UpdateCenterViewModel(createIntlCentersInteractor, customerInteractor, regionSiteInteractor) as T
    }
}