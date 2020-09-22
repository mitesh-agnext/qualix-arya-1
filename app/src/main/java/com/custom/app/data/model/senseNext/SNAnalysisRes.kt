package com.custom.app.data.model.senseNext

import com.google.gson.annotations.SerializedName

class SNAnalysisRes {
    @SerializedName("temp")
    var temp: String? = null
    @SerializedName("date")
    var date: String? = null
    @SerializedName("time")
    var time: String? = null
    @SerializedName("device_id")
    var device_id: String? = null
}