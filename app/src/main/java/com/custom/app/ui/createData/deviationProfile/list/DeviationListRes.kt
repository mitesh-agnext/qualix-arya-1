package com.custom.app.ui.createData.deviationProfile.list

import com.google.gson.annotations.SerializedName

class DeviationListRes {

    @SerializedName("deviation_id")
    var deviation_id: Int? = null
    @SerializedName("deviation")
    var deviation: String? = null
    @SerializedName("customer_id")
    var customer_id: Int? = null

}