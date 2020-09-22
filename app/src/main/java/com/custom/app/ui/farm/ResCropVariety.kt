package com.custom.app.ui.farm

import com.google.gson.annotations.SerializedName

class ResCropVariety
{

    @SerializedName("success")
    var success: String?=null
    @SerializedName("message")
    var message: String?=null
    @SerializedName("data")
    var cropVarietyData: ArrayList<CropVarietyData>?=null

    class CropVarietyData
    {

        @SerializedName("cropVerityId")
        var cropVerityId: String?=null
        @SerializedName("name")
        var name: String?=null
        @SerializedName("cropId")
        var cropId: String?=null
        @SerializedName("cropName")
        var cropName: String?=null
    }
}