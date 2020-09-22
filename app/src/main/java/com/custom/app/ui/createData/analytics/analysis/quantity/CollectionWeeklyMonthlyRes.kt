package com.custom.app.ui.createData.analytics.analysis.quantity

import com.google.gson.annotations.SerializedName

class CollectionWeeklyMonthlyRes {

    @SerializedName("unit")
    var unit: String? = null
    @SerializedName("difference")
    var difference: Double? = null
    @SerializedName("difference_percentage")
    var difference_percentage: Double? = null
    @SerializedName("graph_data")
    var graph_data: ArrayList<CollectionOverTimeRes> = ArrayList()

}