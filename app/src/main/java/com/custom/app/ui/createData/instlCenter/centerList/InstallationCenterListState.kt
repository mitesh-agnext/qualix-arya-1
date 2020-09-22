package com.custom.app.ui.createData.instlCenter.centerList

enum class InstallationCenterListState {

    ListCenterSuccess,
    ListCenterFailure,

    CenterDeleteSuccess,
    CenterDeleteFailure,

    GetDevicesHasConnectionFailure,
    GetDevicesTokenExpired,

    GetCustomerListFailure,
    GetCustomerListSuccess
}