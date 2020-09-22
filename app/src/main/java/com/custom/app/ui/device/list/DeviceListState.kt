package com.custom.app.ui.device.list

enum class DeviceListState {

    ListDevicesSuccess,
    ListDevicesFailure,

    DeviceDeleteSuccess,
    DeviceDeleteFailure,

    GetDevicesHasConnectionFailure,
    GetDevicesTokenExpired,

    NewTokenGeneratedSuccess,
    NewTokenGeneratedFailed,
    NewTokenGeneratedConnectionError,

    DeleteDevicesSuccess,
    DeleteDevicesFailure

}