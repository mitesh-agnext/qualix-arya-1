package com.custom.app.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.ui.base.ScreenState
import okhttp3.ResponseBody

class HomeViewModel(val homeInteractor: HomeInteractor) : ViewModel(), HomeInteractor.DeviceCallback, HomeInteractor.approval {

    private val _homeState: MutableLiveData<ScreenState<HomeDeviceState>> = MutableLiveData()
    val homeState: LiveData<ScreenState<HomeDeviceState>>
        get() = _homeState

    private val _homeList: MutableLiveData<SubscribedDeviceRes> = MutableLiveData()
    val homeList: LiveData<SubscribedDeviceRes>
        get() = _homeList

    var errorMessage: String = "Error"

    fun onGetDevices() {
        _homeState.value = ScreenState.Loading
        homeInteractor.allSubscribedDevices(this)
    }

    fun setApproval(scanId: Int, status: Int) {
        homeInteractor.approveReject(scanId, status, this)
    }

    override fun allDevicesApiSuccess(body: SubscribedDeviceRes) {
        _homeList.value = body
        _homeState.value = ScreenState.Render(HomeDeviceState.SubscribeDeviceSuccess)
    }

    override fun allDevicesApiError(msg: String) {
        errorMessage = msg
        _homeState.value = ScreenState.Render(HomeDeviceState.SubscribeDeviceFailure)
    }

    override fun approvalSuccess(body: ResponseBody) {
        _homeState.value = ScreenState.Render(HomeDeviceState.ApprovalSuccess)
    }

    override fun approvalFailure(msg: String) {
        errorMessage = msg
        _homeState.value = ScreenState.Render(HomeDeviceState.ApprovalFailure)
    }
}

class HomeDevicesViewModelFactory(val homeInteractor: HomeInteractor) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel(homeInteractor) as T
    }
}