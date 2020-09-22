package com.custom.app.ui.createData.analytics.analysis.numberOfFarmers

import com.google.gson.annotations.SerializedName

class NumberOfFarmerRes {

    @SerializedName("farmer_id")
    var farmer_id: Int? = null
    @SerializedName("phone_number")
    var phone_number: String? = null
    @SerializedName("area")
    var area: String? = null
    @SerializedName("inst_center_id")
    var inst_center_id: Int? = null
    @SerializedName("avg_colection")
    var avg_colection: String? = null

}