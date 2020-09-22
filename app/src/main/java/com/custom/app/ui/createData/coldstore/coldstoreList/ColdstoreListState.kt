package com.custom.app.ui.createData.coldstore.coldstoreList

enum class ColdstoreListState {

    ListCenterSuccess,
    ListCenterFailure,

    CenterDeleteSuccess,
    CenterDeleteFailure,

    GetDevicesHasConnectionFailure,
    GetDevicesTokenExpired,

    GetCustomerListFailure,
    GetCustomerListSuccess
}