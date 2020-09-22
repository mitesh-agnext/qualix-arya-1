package com.custom.app.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.ui.base.ScreenState

class HomeViewModel(val homeInteractor: HomeInteractor) : ViewModel(), HomeInteractor.DeviceCallback {

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

    override fun allDevicesApiSuccess(body: SubscribedDeviceRes) {
        _homeList.value = body
        _homeState.value = ScreenState.Render(HomeDeviceState.SubscribeDeviceSuccess)
    }

    override fun allDevicesApiError(msg: String) {
        errorMessage = msg
        _homeState.value = ScreenState.Render(HomeDeviceState.SubscribeDeviceFailure)
    }
}

class HomeDevicesViewModelFactory(val homeInteractor: HomeInteractor) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel(homeInteractor) as T
    }
}