package com.custom.app.ui.createData.analytics.analysis.quantity

import com.google.gson.annotations.SerializedName

class CollectionOverTimeRes {

    @SerializedName("total_collection")
    var weight: Double? = null
    @SerializedName("scan_date")
    var scan_date: String? = null

    @SerializedName("date_done")
    var date_done: Long? = null

}