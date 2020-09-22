package com.custom.app.ui.device.add

enum class AddDeviceState {

    AddDevicesSuccess,
    AddDevicesFailure,
    SerialNumberEmpty,
    VendorEmpty,
    StartLifeEmpty,
    EndLifeEmpty,

    GetSensorProfileSuccess,
    GetSensorProfileError,

    GetDeviceTypeSuccess,
    GetDeviceTypeError,

    GetDeviceTypeGroupSuccess,
    GetDeviceTypeGroupError,

    GetDeviceSubTypeSuccess,
    GetDeviceSubTypeError

}