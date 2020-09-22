package com.custom.app.ui.createData.flcScan

enum class FlcState {

    UploadImageSuccess,
    UploadImageFailure,

    SaveFLCSuccess,
    SaveFLCFailure,
    SaveFLCTokenExpire,

    FetchFLCResultSuccess,
    FetchFLCResultFailure,

    ClearDataSuccess,
    ClearDataFailure,

    FetchFLCLocationSuccess,
    FetchFLCLocationFailure,

    FetchFLCGardenSuccess,
    FetchFLCGardenFailure,

    FetchFLCDivisionSuccess,
    FetchFLCDivisionFailure,

    FetchFLCSectionSuccess,
    FetchFLCSectionFailure,

    FetchFLCSectionCodeSuccess,
    FetchFLCSectionCodeFailure,

    SectionIdEmpty,
    AgentCodeEmpty

}