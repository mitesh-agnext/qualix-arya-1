package com.custom.app.ui.device.add

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.device.list.AddDeviceListener
import com.custom.app.ui.device.list.DeviceInteractor
import okhttp3.ResponseBody

class AddDevicesViewModel(val deviceInteractor: DeviceInteractor) : ViewModel(), AddDeviceListener {

    private val _addDeviceState: MutableLiveData<ScreenState<AddDeviceState>> = MutableLiveData()
    val addDeviceState: LiveData<ScreenState<AddDeviceState>>
        get() = _addDeviceState

    private val _addDeviceResponse: MutableLiveData<ResponseBody> = MutableLiveData()
    val addDeviceResponse: LiveData<ResponseBody>
        get() = _addDeviceResponse

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

    var errorMessage: String = "Error"

    fun onSensorProfileGet() {
        _addDeviceState.value = ScreenState.Loading
        deviceInteractor.getSensorProfile(this)
    }

    fun onDeviceTypeGet() {
        _addDeviceState.value = ScreenState.Loading
        deviceInteractor.getDevicesType(this)
    }

    fun onDeviceSubTypeGet() {
        _addDeviceState.value = ScreenState.Loading
        deviceInteractor.getDevicesSubType(this)
    }

    fun onDeviceGroupGet() {
        _addDeviceState.value = ScreenState.Loading
        deviceInteractor.getDevicesTypeGroup(this)
    }

    fun submitAddDevice(
            serial_nummber: String, device_type_id: Int, hw_revision: String, fw_revision: String, start_of_life: String,
            end_of_life: String, sensor_profile_id: Int, device_sub_type_id: Int, device_group_id: Int, vendor: String, context: Context) {
        _addDeviceState.value = ScreenState.Loading

        deviceInteractor.addDevices(serial_nummber, device_type_id, hw_revision, fw_revision, start_of_life, end_of_life,
                sensor_profile_id, device_sub_type_id, device_group_id, vendor, this, context)
    }

    override fun onAddDevicesSuccess(body: ResponseBody) {
        _addDeviceResponse.value = body
        _addDeviceState.value = ScreenState.Render(AddDeviceState.AddDevicesSuccess)
    }

    override fun onAddDevicesFailure(msg: String) {
        errorMessage = msg
        _addDeviceState.value = ScreenState.Render(AddDeviceState.AddDevicesFailure)
    }

    override fun onSerialNumberEmpty() {
        _addDeviceState.value = ScreenState.Render(AddDeviceState.SerialNumberEmpty)
    }

    override fun onVendorEmpty() {
        _addDeviceState.value = ScreenState.Render(AddDeviceState.VendorEmpty)
    }

    override fun onStartLifeEmpty() {
        _addDeviceState.value = ScreenState.Render(AddDeviceState.StartLifeEmpty)
    }

    override fun onEndLifeEmpty() {
        _addDeviceState.value = ScreenState.Render(AddDeviceState.EndLifeEmpty)
    }

    override fun onGetSensorProfileSuccess(body: ArrayList<SensorProfileRes>) {
        _getSensorProfileResponse.value = body
        _addDeviceState.value = ScreenState.Render(AddDeviceState.GetSensorProfileSuccess)
    }

    override fun onGetSensorProfileEmpty(msg: String) {
        errorMessage = msg
        _addDeviceState.value = ScreenState.Render(AddDeviceState.GetSensorProfileError)
    }

    override fun onGetSensorProfileError(msg: String) {
        errorMessage = msg
        _addDeviceState.value = ScreenState.Render(AddDeviceState.GetSensorProfileError)
    }

    override fun onGetDeviceTypeSuccess(body: ArrayList<DeviceTypeRes>) {
        _getDeviceTypeResponse.value = body
        _addDeviceState.value = ScreenState.Render(AddDeviceState.GetDeviceTypeSuccess)
    }

    override fun onGetDeviceTypeEmpty(msg: String) {
        errorMessage = msg
        _addDeviceState.value = ScreenState.Render(AddDeviceState.GetDeviceTypeError)
    }

    override fun onGetDeviceTypeError(msg: String) {
        errorMessage = msg
        _addDeviceState.value = ScreenState.Render(AddDeviceState.GetDeviceTypeError)
    }

    override fun onGetDeviceTypeGroupSuccess(body: ArrayList<DeviceGroupRes>) {
        _getDeviceGroupResponse.value = body
        _addDeviceState.value = ScreenState.Render(AddDeviceState.GetDeviceTypeGroupSuccess)
    }

    override fun onGetDeviceTypeGroupEmpty(msg: String) {
        errorMessage = msg
        _addDeviceState.value = ScreenState.Render(AddDeviceState.GetDeviceTypeGroupError)
    }

    override fun onGetDeviceTypeGroupError(msg: String) {
        errorMessage = msg
        _addDeviceState.value = ScreenState.Render(AddDeviceState.GetDeviceTypeGroupError)
    }

    override fun onGetDeviceSubTypeSuccess(body: ArrayList<DeviceSubTypeRes>) {
        _getDeviceSubTypeResponse.value = body
        _addDeviceState.value = ScreenState.Render(AddDeviceState.GetDeviceSubTypeSuccess)
    }

    override fun onGetDeviceSubTypeEmpty(msg: String) {
        errorMessage = msg
        _addDeviceState.value = ScreenState.Render(AddDeviceState.GetDeviceSubTypeError)
    }

    override fun onGetDeviceSubTypeError(msg: String) {
        errorMessage = msg
        _addDeviceState.value = ScreenState.Render(AddDeviceState.GetDeviceSubTypeError)
    }
}

class AddDeviceViewModelFactory(private val deviceInteractor: DeviceInteractor) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AddDevicesViewModel(deviceInteractor) as T
    }
}