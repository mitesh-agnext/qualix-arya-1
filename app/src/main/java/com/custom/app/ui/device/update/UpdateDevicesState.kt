package com.custom.app.ui.device.update

enum class UpdateDevicesState {
    UpdateDevicesSuccess,
    UpdateDevicesFailure,
    SerialNumberEmpty,
    VendorEmpty,
    StartLifeEmpty,
    EndLifeEmpty,
    hasConnectionError,

    GetSensorProfileSuccess,
    GetSensorProfileError,

    GetDeviceTypeSuccess,
    GetDeviceTypeError,

    GetDeviceTypeGroupSuccess,
    GetDeviceTypeGroupError,

    GetDeviceSubTypeSuccess,
    GetDeviceSubTypeError

}