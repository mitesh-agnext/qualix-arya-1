package com.custom.app.ui.qualityAnalysis

import com.google.gson.annotations.SerializedName

class ResAvgScanData {

    @SerializedName("success")
    var success: String? = null
    @SerializedName("message")
    var message: String? = null
    @SerializedName("devMessage")
    var devMessage: String? = null

    @SerializedName("data")
    var data = ArrayList<AvgData>()
}

class AvgData
{
    @SerializedName("averageMonth")
    var averageMonth: String? = null
    @SerializedName("scanCountMonth")
    var scanCountMonth: String? = null
    @SerializedName("averageWeek")
    var averageWeek: String? = null
    @SerializedName("scanCountWeek")
    var scanCountWeek: String? = null
    @SerializedName("averageDay")
    var averageDay: String? = null
    @SerializedName("scanCountDay")
    var scanCountDay: String? = null


    @SerializedName("averageYear")
    var averageYear: String? = null
    @SerializedName("scanCountYear")
    var scanCountYear: String? = null
    @SerializedName("scanPeriodYear")
    var scanPeriodYear: String? = null


}
