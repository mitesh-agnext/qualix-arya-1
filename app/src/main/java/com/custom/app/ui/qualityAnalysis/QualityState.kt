package com.custom.app.ui.qualityAnalysis

enum class QualityState{
    scansListSuccess,
    noScanListSuccess,
    scansListFailure,

    avgScanDataSuccess,
    avgScanDataFaliure,

    monthFlcDataSuccess,
    monthFlcDataFailure,

    tokenExpired
}