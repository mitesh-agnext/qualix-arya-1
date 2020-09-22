package com.custom.app.ui.createData.instlCenter.createInstallationCenter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.core.app.util.RxUtils
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.createData.instlCenter.CreateInstallationCenterListener
import com.custom.app.ui.createData.instlCenter.InstallationCenterInteractor
import com.custom.app.ui.createData.region.ListRegionInteractorCallback
import com.custom.app.ui.createData.region.ListSiteInteractorCallback
import com.custom.app.ui.createData.region.RegionSiteInteractor
import com.custom.app.ui.createData.region.site.create.RegionRes
import com.custom.app.ui.createData.region.site.list.SiteListRes
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerListListener
import com.custom.app.ui.customer.list.CustomerRes
import com.custom.app.ui.user.list.UserDataRes
import io.reactivex.disposables.Disposable
import okhttp3.ResponseBody

class CreateCenterViewModel(val createInstallationCenterInteractor: InstallationCenterInteractor,
                            val customerInteractor: CustomerInteractor, val regionSiteInteractor: RegionSiteInteractor) : ViewModel(),

        CreateInstallationCenterListener, CustomerListListener, ListRegionInteractorCallback, ListSiteInteractorCallback {

    private var disposable: Disposable? = null

    private val _createCenterState: MutableLiveData<ScreenState<CreateInstallationCentersState>> = MutableLiveData()
    val createCenterState: LiveData<ScreenState<CreateInstallationCentersState>>
        get() = _createCenterState

    private val _createCenterResponse: MutableLiveData<ResponseBody> = MutableLiveData()

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

    var errorMessage: String? = "Error"

    fun onGetCustomer() {
        _createCenterState.value = ScreenState.Loading
        customerInteractor.list(this)
    }

    fun onGetUser(customerId: Int) {
        _createCenterState.value = ScreenState.Loading
        createInstallationCenterInteractor.getUser(this, customerId)
    }

    fun onGetCenterType() {
        _createCenterState.value = ScreenState.Loading
        createInstallationCenterInteractor.getInstallationCenterType(this)
    }

    fun onGetRegion(customerId: Int) {
        _createCenterState.value = ScreenState.Loading
        regionSiteInteractor.allRegion(this, customerId)
    }

    fun onGetSite(keyword: String, regionId: Int) {
        _createCenterState.value = ScreenState.Loading
        regionSiteInteractor.allSite(this, keyword, regionId)
    }

    fun onCreateCenter(installation_CenterName: String, installation_CenterTypeId: Int, siteId: Int, regionId: Int, customerId: Int, userId: Int, note: String) {
        _createCenterState.value = ScreenState.Loading
        createInstallationCenterInteractor.addCenter(installation_CenterName, installation_CenterTypeId, siteId, regionId, customerId, userId, note, this)
    }

    override fun onInstallationCenterSuccess(body: ResponseBody) {
        _createCenterResponse.value = body
        _createCenterState.value = ScreenState.Render(CreateInstallationCentersState.CreateInstallationCenterSuccess)
    }

    override fun onInstallationCenterFailure(msg: String) {
        errorMessage = msg
        _createCenterState.value = ScreenState.Render(CreateInstallationCentersState.CreateInstallationCenterFailure)
    }

    override fun onCenterNameEmpty() {
        _createCenterState.value = ScreenState.Render(CreateInstallationCentersState.InstallationCenterNameEmpty)
    }

    override fun onGetInstallationCenterTypeSuccess(body: ArrayList<InstallationCenterTypeRes>) {
        _getCenterTypeResponse.value = body
        _createCenterState.value = ScreenState.Render(CreateInstallationCentersState.GetCommercialTypeSuccess)
    }

    override fun onGetInstallationCenterTypeError(msg: String) {
        errorMessage = msg
        _createCenterState.value = ScreenState.Render(CreateInstallationCentersState.GetCommercialTypeError)
    }

    override fun onGetInstallationCenterTypeEmpty(msg: String) {
        errorMessage = msg
        _createCenterState.value = ScreenState.Render(CreateInstallationCentersState.GetCommercialTypeError)
    }

    override fun onGetUserSuccess(body: ArrayList<UserDataRes>) {
        _getUserList.value = body
        _createCenterState.value = ScreenState.Render(CreateInstallationCentersState.GetUserSuccess)
    }

    override fun onGetUserFailure(msg: String?) {
        errorMessage = msg
        _createCenterState.value = ScreenState.Render(CreateInstallationCentersState.GetUserError)
    }

    override fun onGetUserEmpty(msg: String?) {
        errorMessage = msg
        _createCenterState.value = ScreenState.Render(CreateInstallationCentersState.GetUserError)
    }

    override fun onCustomerListSuccess(body: ArrayList<CustomerRes>) {
        _getCustomerList.value = body
        _createCenterState.value = ScreenState.Render(CreateInstallationCentersState.GetCustomerSuccess)
    }

    override fun onCustomerListFailure(msg: String) {
        errorMessage = msg
        _createCenterState.value = ScreenState.Render(CreateInstallationCentersState.GetCustomerError)
    }

    override fun onGetRegionSuccess(body: ArrayList<RegionRes>) {
        _getRegionResponse.value = body
        _createCenterState.value = ScreenState.Render(CreateInstallationCentersState.GetRegionSuccess)
    }

    override fun onGetRegionFailure(msg: String) {
        errorMessage = msg
        _createCenterState.value = ScreenState.Render(CreateInstallationCentersState.GetRegionError)
    }

    override fun onGetRegionEmpty(msg: String) {
        errorMessage = msg
        _createCenterState.value = ScreenState.Render(CreateInstallationCentersState.GetRegionError)
    }

    override fun onGetSiteSuccess(body: ArrayList<SiteListRes>) {
        _getSiteResponse.value = body
        _createCenterState.value = ScreenState.Render(CreateInstallationCentersState.GetSiteSuccess)
    }

    override fun onGetSiteError(msg: String) {
        errorMessage = msg
        _createCenterState.value = ScreenState.Render(CreateInstallationCentersState.GetSiteError)
    }

    override fun onGetSiteEmpty(msg: String) {
        errorMessage = msg
        _createCenterState.value = ScreenState.Render(CreateInstallationCentersState.GetSiteError)
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

class CreateCenterViewModelFactory(
        val createInstallationCenterInteractor: InstallationCenterInteractor,
        val customerInteractor: CustomerInteractor, val regionSiteInteractor: RegionSiteInteractor) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CreateCenterViewModel(createInstallationCenterInteractor, customerInteractor, regionSiteInteractor) as T
    }
}