package com.custom.app.ui.device.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.ui.base.ScreenState

class DeviceListViewModel(val deviceInteractor: DeviceInteractor) : ViewModel(), ListDeviceInteractorCallback {

    private val _Device_listState: MutableLiveData<ScreenState<DeviceListState>> = MutableLiveData()
    val deviceListState: LiveData<ScreenState<DeviceListState>>
        get() = _Device_listState

    private val _deviceList: MutableLiveData<ArrayList<DevicesData>> = MutableLiveData()
    val deviceList: LiveData<ArrayList<DevicesData>>
        get() = _deviceList

    private val _deletedPos: MutableLiveData<Int> = MutableLiveData()
    val deletedPos: LiveData<Int>
        get() = _deletedPos

    var errorMessage: String = "Error"

    fun onGetDeviceList(searchString: String, listStatus: String) {
        _Device_listState.value = ScreenState.Loading
        deviceInteractor.allDevices(this, searchString, listStatus)
    }

    fun onDeleteDevice(deviceId: String, position: Int) {
        _Device_listState.value = ScreenState.Loading
        deviceInteractor.deleteDevices(this, deviceId, position)
    }

    override fun allDevicesApiSuccess(body: ArrayList<DevicesData>) {
        _deviceList.value = body
        _Device_listState.value = ScreenState.Render(DeviceListState.ListDevicesSuccess)
    }

    override fun allDevicesApiError(msg: String) {
        errorMessage = msg
        _Device_listState.value = ScreenState.Render(DeviceListState.ListDevicesFailure)
    }

    override fun allDevicesApiEmpty(msg: String) {
        errorMessage = msg
        _Device_listState.value = ScreenState.Render(DeviceListState.ListDevicesFailure)
    }

    override fun deleteDeviceSuccess(deletedPostion: Int) {
        _deletedPos.value = deletedPostion
        _Device_listState.value = ScreenState.Render(DeviceListState.DeleteDevicesSuccess)
    }

    override fun deleteDeviceFailure(msg: String) {
        errorMessage = msg
        _Device_listState.value = ScreenState.Render(DeviceListState.DeleteDevicesFailure)    }

}

class DevicesListViewModelFactory(private val devicesInteractor: DeviceInteractor) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DeviceListViewModel(devicesInteractor) as T
    }
}