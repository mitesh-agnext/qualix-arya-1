package com.custom.app.ui.createData.analytics.analysis.quantity

import com.google.gson.annotations.SerializedName

class CollectionCenterRegionRes {

    @SerializedName("center_collection")
    var center_collection: Float? = null
    @SerializedName("date_done")
    var date_done: Long? = null
    @SerializedName("scan_date")
    var scan_date: String? = null
    @SerializedName("region_collection")
    var region_collection: Float? = null

}