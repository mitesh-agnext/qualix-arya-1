package com.custom.app.ui.device.update

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.device.add.DeviceGroupRes
import com.custom.app.ui.device.add.DeviceSubTypeRes
import com.custom.app.ui.device.add.DeviceTypeRes
import com.custom.app.ui.device.add.SensorProfileRes
import com.custom.app.ui.device.list.AddDeviceListener
import com.custom.app.ui.device.list.DeviceInteractor
import com.custom.app.ui.device.list.DevicesData
import com.custom.app.ui.device.list.UpdateDevicesListener
import okhttp3.ResponseBody

class UpdateDevicesViewModel(val deviceInteractor: DeviceInteractor) : ViewModel(),
        UpdateDevicesListener, AddDeviceListener {

    private val _updateDevicesState: MutableLiveData<ScreenState<UpdateDevicesState>> = MutableLiveData()
    val updateDevicesState: LiveData<ScreenState<UpdateDevicesState>>
        get() = _updateDevicesState

    private val _updateDeviceResponse: MutableLiveData<ResponseBody> = MutableLiveData()
    val updateDeviceResponse: LiveData<ResponseBody>
        get() = _updateDeviceResponse

    private val _getSensorProfileResponse: MutableLiveData<ArrayList<SensorProfileRes>> = MutableLiveData()
    val getSensorProfileResponse: LiveData<ArrayList<SensorProfileRes>>
        get() = _getSensorProfileResponse

    private val _getDeviceTypeResponse: MutableLiveData<ArrayList<DeviceTypeRes>> = MutableLiveData()
    val getDeviceTypeResponse: LiveData<ArrayList<DeviceTypeRes>>
        get() = _getDeviceTypeResponse

    private val _getDeviceSubTypeResponse: MutableLiveData<ArrayList<DeviceSubTypeRes>> = MutableLiveData()
    val getDeviceSubTypeResponse: LiveData<ArrayList<DeviceSubTypeRes>>
        get() = _getDeviceSubTypeResponse

    private val _getDeviceGroupResponse: MutableLiveData<ArrayList<DeviceGroupRes>> = MutableLiveData()
    val getDeviceGroupResponse: LiveData<ArrayList<DeviceGroupRes>>
        get() = _getDeviceGroupResponse

    var singleDevice: DevicesData = DevicesData()

    var errorMessage: String = "Error"

    fun onSensorProfileGet() {
        _updateDevicesState.value = ScreenState.Loading
        deviceInteractor.getSensorProfile(this)
    }

    fun onDeviceTypeGet() {
        _updateDevicesState.value = ScreenState.Loading
        deviceInteractor.getDevicesType(this)
    }

    fun onDeviceSubTypeGet() {
        _updateDevicesState.value = ScreenState.Loading
        deviceInteractor.getDevicesSubType(this)
    }

    fun onDeviceGroupGet() {
        _updateDevicesState.value = ScreenState.Loading
        deviceInteractor.getDevicesTypeGroup(this)
    }

    fun submitUpdateDevice(
            serial_nummber: String, device_type_id: Int, hw_revision: String, fw_revision: String, start_of_life: String,
            end_of_life: String, sensor_profile_id: Int, device_sub_type_id: Int, device_group_id: Int, vendor: String,
            device_Id: Int, context: Context) {

        _updateDevicesState.value = ScreenState.Loading
        deviceInteractor.updateDevices(serial_nummber, device_type_id, hw_revision, fw_revision, start_of_life, end_of_life,
                sensor_profile_id, device_sub_type_id, device_group_id, vendor, device_Id, this, context)
    }

    override fun onAddDevicesSuccess(body: ResponseBody) {
    }

    override fun onAddDevicesFailure(msg: String) {
    }

    override fun onUpdateDevicesSuccess(body: ResponseBody) {
        _updateDeviceResponse.value = body
        _updateDevicesState.value = ScreenState.Render(UpdateDevicesState.UpdateDevicesSuccess)
    }

    override fun onUpdateDevicesFailure(msg: String) {
        _updateDevicesState.value = ScreenState.Render(UpdateDevicesState.UpdateDevicesFailure)
    }

    override fun onSerialNumberEmpty() {
        _updateDevicesState.value = ScreenState.Render(UpdateDevicesState.SerialNumberEmpty)
    }

    override fun onVendorEmpty() {
        _updateDevicesState.value = ScreenState.Render(UpdateDevicesState.VendorEmpty)
    }

    override fun onStartLifeEmpty() {
        _updateDevicesState.value = ScreenState.Render(UpdateDevicesState.StartLifeEmpty)
    }

    override fun onEndLifeEmpty() {
        _updateDevicesState.value = ScreenState.Render(UpdateDevicesState.EndLifeEmpty)
    }

    override fun onGetSensorProfileSuccess(body: ArrayList<SensorProfileRes>) {
        _getSensorProfileResponse.value = body
        _updateDevicesState.value = ScreenState.Render(UpdateDevicesState.GetSensorProfileSuccess)
    }

    override fun onGetSensorProfileError(msg: String) {

        _updateDevicesState.value = ScreenState.Render(UpdateDevicesState.GetSensorProfileError)
    }

    override fun onGetSensorProfileEmpty(msg: String) {
        TODO("Not yet implemented")
    }


    override fun onGetDeviceTypeSuccess(body: ArrayList<DeviceTypeRes>) {
        _getDeviceTypeResponse.value = body
        _updateDevicesState.value = ScreenState.Render(UpdateDevicesState.GetDeviceTypeSuccess)
    }

    override fun onGetDeviceTypeError(msg: String) {
        errorMessage = msg
        _updateDevicesState.value = ScreenState.Render(UpdateDevicesState.GetDeviceTypeError)
    }

    override fun onGetDeviceTypeEmpty(msg: String) {
        errorMessage = msg
        _updateDevicesState.value = ScreenState.Render(UpdateDevicesState.GetDeviceTypeError)
    }

    override fun onGetDeviceTypeGroupSuccess(body: ArrayList<DeviceGroupRes>) {
        _getDeviceGroupResponse.value = body
        _updateDevicesState.value = ScreenState.Render(UpdateDevicesState.GetDeviceTypeGroupSuccess)
    }

    override fun onGetDeviceTypeGroupError(msg: String) {
        errorMessage = msg
        _updateDevicesState.value = ScreenState.Render(UpdateDevicesState.GetDeviceTypeGroupError)
    }

    override fun onGetDeviceTypeGroupEmpty(msg: String) {
        errorMessage = msg
        _updateDevicesState.value = ScreenState.Render(UpdateDevicesState.GetDeviceTypeGroupError)
    }

    override fun onGetDeviceSubTypeSuccess(body: ArrayList<DeviceSubTypeRes>) {
        _getDeviceSubTypeResponse.value = body
        _updateDevicesState.value = ScreenState.Render(UpdateDevicesState.GetDeviceSubTypeSuccess)
    }

    override fun onGetDeviceSubTypeError(msg: String) {
        errorMessage = msg
        _updateDevicesState.value = ScreenState.Render(UpdateDevicesState.GetDeviceSubTypeError)
    }

    override fun onGetDeviceSubTypeEmpty(msg: String) {
        errorMessage = msg
        _updateDevicesState.value = ScreenState.Render(UpdateDevicesState.GetDeviceSubTypeError)
    }

}

class UpdateDeviceViewModelFactory(
        private val deviceInteractor: DeviceInteractor) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return UpdateDevicesViewModel(deviceInteractor) as T
    }
}