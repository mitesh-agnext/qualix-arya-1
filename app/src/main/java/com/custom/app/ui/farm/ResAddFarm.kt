package com.custom.app.ui.farm

import com.google.gson.annotations.SerializedName

class ResAddFarm
{
    @SerializedName("success")
    var success: String? = null
    @SerializedName("message")
    var message: String? = null
    @SerializedName("devMessage")
    var devMessage: String? = null

    @SerializedName("data")
    var data: ArrayList<ResData>? = null

    class ResData
    {
        @SerializedName("cropId")
        var cropId: String? = null
        @SerializedName("cropName")
        var cropName: String? = null
        @SerializedName("cropVerityId")
        var cropVerityId: String? = null
        @SerializedName("cropVerityName")
        var cropVerityName: String? = null
        @SerializedName("farmId")
        var farmId: String? = null
        @SerializedName("farmerId")
        var farmerId: String? = null
        @SerializedName("farmerName")
        var farmerName: String? = null
        @SerializedName("district")
        var district: String? = null
        @SerializedName("area")
        var area: String? = null
        @SerializedName("address")
        var address: String? = null
        @SerializedName("farmIndics")
        var farmIndics: ArrayList<ResLatLong>? = null

    }

    class ResLatLong
    {
        @SerializedName("farmIndicsId")
        var farmIndicsId: String? = null
        @SerializedName("lat")
        var lat: String? = null
        @SerializedName("lang")
        var lang: String? = null
    }
}