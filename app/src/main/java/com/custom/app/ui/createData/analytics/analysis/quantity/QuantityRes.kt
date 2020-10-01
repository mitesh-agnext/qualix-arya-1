package com.custom.app.ui.createData.analytics.analysis.quantity

import com.google.gson.annotations.SerializedName

class QuantityRes {

    @SerializedName("unit")
    var unit: String? = null
    @SerializedName("average_quantity")
    var average_quantity: Double? = null
    @SerializedName("min_quantity")
    var min_quantity: String? = null
    @SerializedName("max_quantity")
    var max_quantity: Double? = null
    @SerializedName("total_quantity")
    var total_quantity: Double? = null


}