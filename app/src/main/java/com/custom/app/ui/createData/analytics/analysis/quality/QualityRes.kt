package com.custom.app.ui.createData.analytics.analysis.quality

import com.google.gson.annotations.SerializedName

class QualityRes {

    @SerializedName("avg_quality")
    var avg_quality: Double? = null
    @SerializedName("min_quality")
    var min_quality: String? = null
    @SerializedName("max_quality")
    var max_quality: Double? = null

}