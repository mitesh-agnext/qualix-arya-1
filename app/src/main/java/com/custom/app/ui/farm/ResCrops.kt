package com.custom.app.ui.farm

import com.google.gson.annotations.SerializedName

class ResCrops {

    @SerializedName("success")
    var success: String? = null
    @SerializedName("message")
    var message: String? = null
    @SerializedName("httpCode")
    var httpCode: String? = null
    @SerializedName("data")
    var cropData: ArrayList<CropData>? = null
}

class CropData
{
    @SerializedName("cropId")
    var cropId: String? = null
    @SerializedName("name")
    var name: String? = null
}
