package com.custom.app.ui.createData.coldstore.updateColdstore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.core.app.util.RxUtils
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.createData.coldstore.coldstoreList.ColdstoreRes
import com.custom.app.ui.createData.coldstore.createColdstore.ColdstoreInteractor
import com.custom.app.ui.createData.coldstore.createColdstore.CreateColdstoreListener
import com.custom.app.ui.createData.coldstore.createColdstore.UpdateCenterListener
import com.custom.app.ui.createData.foodType.list.FoodTypeListInteractor
import com.custom.app.ui.createData.foodType.list.FoodTypeListRes
import com.custom.app.ui.createData.profile.list.ProfileListInteractor
import com.custom.app.ui.createData.profile.list.ProfileListRes
import com.custom.app.ui.createData.profileType.list.ProfileTypeListInteractor
import com.custom.app.ui.createData.profileType.list.ProfileTypeListRes
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

class UpdateCenterViewModel(val coldstoreInteractor: ColdstoreInteractor,
                            val customerInteractor: CustomerInteractor,
                            val profileListInteractor: ProfileListInteractor,
                            val profileTypeListInteractor: ProfileTypeListInteractor,
                            val foodTypeListInteractor: FoodTypeListInteractor,
                            val regionSiteInteractor: RegionSiteInteractor) : ViewModel(),

        UpdateCenterListener,
        CreateColdstoreListener,
        CustomerListListener,
        ProfileListInteractor.ListProfileInteractorCallback,
        ProfileTypeListInteractor.ListProfileTypeInteractorCallback,
        FoodTypeListInteractor.ListFoodTypeInteractorCallback,
        ListRegionInteractorCallback,
        ListSiteInteractorCallback {

    private var disposable: Disposable? = null

    private val _updateCenterState: MutableLiveData<ScreenState<UpdateColdstoreState>> = MutableLiveData()
    val updateCenterState: LiveData<ScreenState<UpdateColdstoreState>>
        get() = _updateCenterState

    private val _updateDeviceResponse: MutableLiveData<ResponseBody> = MutableLiveData()
    val updateDeviceResponse: LiveData<ResponseBody>
        get() = _updateDeviceResponse

    private val _getCustomerList: MutableLiveData<ArrayList<CustomerRes>> = MutableLiveData()
    val getCustomerList: LiveData<ArrayList<CustomerRes>>
        get() = _getCustomerList

    private val _getUserList: MutableLiveData<ArrayList<UserDataRes>> = MutableLiveData()
    val getUserList: LiveData<ArrayList<UserDataRes>>
        get() = _getUserList

    private val _getCenterTypeResponse: MutableLiveData<ArrayList<ColdstoreRes>> = MutableLiveData()
    val getCenterTypeResponse: LiveData<ArrayList<ColdstoreRes>>
        get() = _getCenterTypeResponse

    private val _getProfileResponse: MutableLiveData<ArrayList<ProfileListRes>> = MutableLiveData()
    val getProfileResponse: LiveData<ArrayList<ProfileListRes>>
        get() = _getProfileResponse

    private val _getProfileTypeResponse: MutableLiveData<ArrayList<ProfileTypeListRes>> = MutableLiveData()
    val getProfileTypeResponse: LiveData<ArrayList<ProfileTypeListRes>>
        get() = _getProfileTypeResponse

    private val _getFoodTypeResponse: MutableLiveData<ArrayList<FoodTypeListRes>> = MutableLiveData()
    val getFoodTypeResponse: LiveData<ArrayList<FoodTypeListRes>>
        get() = _getFoodTypeResponse

    private val _getRegionResponse: MutableLiveData<ArrayList<RegionRes>> = MutableLiveData()
    val getRegionResponse: LiveData<ArrayList<RegionRes>>
        get() = _getRegionResponse

    private val _getSiteResponse: MutableLiveData<ArrayList<SiteListRes>> = MutableLiveData()
    val getSiteResponse: LiveData<ArrayList<SiteListRes>>
        get() = _getSiteResponse

    var singleCenter: ColdstoreRes = ColdstoreRes()

    var errorMessage: String = "Error"

    fun onGetCustomer() {
        _updateCenterState.value = ScreenState.Loading
        customerInteractor.list(this)
    }

    fun onGetUser(customerId: Int) {
        _updateCenterState.value = ScreenState.Loading
        coldstoreInteractor.getUser(this, customerId)
    }

    fun onGetProfile(customerId: Int) {
        _updateCenterState.value = ScreenState.Loading
        profileListInteractor.allProfile(customerId, this)
    }

    fun onGetProfileType(customerId: Int) {
        _updateCenterState.value = ScreenState.Loading
        profileTypeListInteractor.allProfileType(customerId, this)
    }

    fun onGetFoodType(customerId: Int) {
        _updateCenterState.value = ScreenState.Loading
        foodTypeListInteractor.allFoodType(customerId, this)
    }

    fun onGetRegion(customerId: Int) {
        _updateCenterState.value = ScreenState.Loading
        regionSiteInteractor.allRegion(this, customerId)
    }

    fun onGetSite(keyword: String, regionId: Int) {
        _updateCenterState.value = ScreenState.Loading
        regionSiteInteractor.allSite(this, keyword, regionId)
    }

    fun onUpdateCenter(coldstoreId: Int, coldstoreName: String, customer_id: Int, profileId: Int, profileTypeId: Int,
                       foodTypeId: Int, siteId: Int, regionId: Int, userId: Int, note: String) {
        _updateCenterState.value = ScreenState.Loading
        coldstoreInteractor.updateCenter(coldstoreId, coldstoreName, customer_id, profileId, profileTypeId, foodTypeId, siteId, regionId,
                userId, note, this)
    }

    override fun onUpdateCenterSuccess(body: ResponseBody) {
        _updateCenterState.value = ScreenState.Render(UpdateColdstoreState.UpdateColdstoreSuccess)
    }

    override fun onUpdateCenterFailure() {
        _updateCenterState.value = ScreenState.Render(UpdateColdstoreState.UpdateColdstoreFailure)
    }

    override fun onColdstoreSuccess(body: ResponseBody) {}

    override fun onColdstoreFailure() {}

    override fun onCenterNameEmpty() {
        _updateCenterState.value = ScreenState.Render(UpdateColdstoreState.ColdstoreNameEmpty)
    }

    override fun onGetUserSuccess(body: ArrayList<UserDataRes>) {
        _getUserList.value = body
        _updateCenterState.value = ScreenState.Render(UpdateColdstoreState.GetUserSuccess)
    }

    override fun onGetUserFailure(msg: String?) {
        _updateCenterState.value = ScreenState.Render(UpdateColdstoreState.GetUserError)
    }

    override fun onCustomerListSuccess(body: ArrayList<CustomerRes>) {
        _getCustomerList.value = body
        _updateCenterState.value = ScreenState.Render(UpdateColdstoreState.GetCustomerSuccess)
    }

    override fun onCustomerListFailure(msg: String) {
        errorMessage = msg
        _updateCenterState.value = ScreenState.Render(UpdateColdstoreState.GetCustomerError)
    }

    override fun allProfileApiSuccess(body: ArrayList<ProfileListRes>) {
        _getProfileResponse.value = body
        _updateCenterState.value = ScreenState.Render(UpdateColdstoreState.GetProfileSuccess)
    }

    override fun allProfileApiError(msg: String) {
        errorMessage = msg
        _updateCenterState.value = ScreenState.Render(UpdateColdstoreState.GetProfileError)
    }

    override fun allProfileTypeApiSuccess(body: ArrayList<ProfileTypeListRes>) {
        _getProfileTypeResponse.value = body
        _updateCenterState.value = ScreenState.Render(UpdateColdstoreState.GetProfileTypeSuccess)
    }

    override fun allProfileTypeApiError(msg: String) {
        errorMessage = msg
        _updateCenterState.value = ScreenState.Render(UpdateColdstoreState.GetProfileTypeError)
    }

    override fun allFoodTypeApiSuccess(body: ArrayList<FoodTypeListRes>) {
        _getFoodTypeResponse.value = body
        _updateCenterState.value = ScreenState.Render(UpdateColdstoreState.GetFoodTypeSuccess)
    }

    override fun allFoodTypeApiError(msg: String) {
        errorMessage = msg
        _updateCenterState.value = ScreenState.Render(UpdateColdstoreState.GetFoodTypeError)
    }

    override fun onGetRegionSuccess(body: ArrayList<RegionRes>) {
        _getRegionResponse.value = body
        _updateCenterState.value = ScreenState.Render(UpdateColdstoreState.GetRegionSuccess)
    }

    override fun onGetRegionFailure(msg: String) {
        errorMessage = msg
        _updateCenterState.value = ScreenState.Render(UpdateColdstoreState.GetRegionError)

    }

    override fun onGetRegionEmpty(msg: String) {
        errorMessage = msg
        _updateCenterState.value = ScreenState.Render(UpdateColdstoreState.GetRegionError)
    }

    override fun onGetSiteSuccess(body: ArrayList<SiteListRes>) {
        _getSiteResponse.value = body
        _updateCenterState.value = ScreenState.Render(UpdateColdstoreState.GetSiteSuccess)
    }

    override fun onGetSiteError(msg: String) {
        errorMessage = msg
        _updateCenterState.value = ScreenState.Render(UpdateColdstoreState.GetSiteError)
    }

    override fun onGetSiteEmpty(msg: String) {
        errorMessage = msg
        _updateCenterState.value = ScreenState.Render(UpdateColdstoreState.GetSiteError)
    }

    override fun onTokenExpire() {
    }

    override fun deleteProfileSuccess(deletedPostion: Int) {}
    override fun deleteProfileFailure(msg: String) {}
    override fun deleteProfileTypeSuccess(deletedPostion: Int) {}
    override fun deleteProfileTypeFailure(msg: String) {}
    override fun deleteFoodTypeSuccess(deletedPostion: Int) {}
    override fun deleteFoodTypeFailure(msg: String) {}
    override fun deleteSiteSuccess(deletedPostion: Int) {}
    override fun deleteSiteFailure(msg: String) {}
    override fun deleteRegionSuccess(deletedPostion: Int) {}
    override fun deleteRegionFailure(msg: String) {}

    fun destroy() {
        RxUtils.dispose(disposable)
    }
}

class UpdateCenterViewModelFactory(
        val coldstoreInteractor: ColdstoreInteractor,
        val customerInteractor: CustomerInteractor,
        val profileListInteractor: ProfileListInteractor,
        val profileTypeListInteractor: ProfileTypeListInteractor,
        val foodTypeListInteractor: FoodTypeListInteractor,
        val regionListInteractor: RegionSiteInteractor) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return UpdateCenterViewModel(coldstoreInteractor,
                customerInteractor, profileListInteractor, profileTypeListInteractor,
                foodTypeListInteractor, regionListInteractor) as T
    }
}