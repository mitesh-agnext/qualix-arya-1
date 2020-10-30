package com.custom.app.data.model.scan

import com.google.gson.annotations.SerializedName

class UpdateScanRequest {
    @SerializedName("analysisName")
    var analysisName: String? = null

    @SerializedName("labResultValue")
    var labResultValue: String? = null
}