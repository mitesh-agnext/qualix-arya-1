package com.custom.app.ui.createData.analytics.analysis.quality

import com.google.gson.annotations.SerializedName

class QualityOverTimeRes {

    @SerializedName("quality_avg")
    var quality_avg: Double? = null
    @SerializedName("scan_date")
    var scan_date: String? = null
    @SerializedName("scan_count")
    var scan_count: Int? = null
    @SerializedName("date_done")
    var date_done: Long? = null

}