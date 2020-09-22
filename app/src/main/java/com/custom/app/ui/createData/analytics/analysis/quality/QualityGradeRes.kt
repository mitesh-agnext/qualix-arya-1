package com.custom.app.ui.createData.analytics.analysis.quality

import com.google.gson.annotations.SerializedName

class QualityGradeRes {

    @SerializedName("grade")
    var grade: String? = null
    @SerializedName("scanCount")
    var scanCount: Int? = null

}