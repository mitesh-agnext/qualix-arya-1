package com.custom.app.ui.createData.analytics.analysis.quality

import com.google.gson.annotations.SerializedName

class QualityRangeRes {

    @SerializedName("quality_rules")
    var quality_rules: ArrayList<QualityRules>? = ArrayList()

}

class QualityRules{

    @SerializedName("analysis_code")
    var analysis_code: String? = null
    @SerializedName("rules")
    var rules: ArrayList<Rules>? = ArrayList()
}

class Rules{
    @SerializedName("max_val")
    var max_val: Double? = null
    @SerializedName("min_val")
    var min_val: Double? = null
    @SerializedName("commodity_id")
    var commodity_id: Int? = null
    @SerializedName("grade")
    var grade: String? = null
}