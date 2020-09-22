package com.custom.app.ui.farm.farmList

import com.google.gson.annotations.SerializedName

class FarmRes {
    @SerializedName("plot_id")
    var plot_id:String?=null
    @SerializedName("area")
    var area:String?=null
    @SerializedName("address")
    var address:String?=null
    @SerializedName("district")
    var district:String?=null
    @SerializedName("farmer_id")
    var farmer_id:String?=null
    @SerializedName("farmer_name")
    var farmer_name:String?=null
    @SerializedName("plot_name")
    var plot_name:String?=null
    @SerializedName("crop_id")
    var crop_id:String?=null
    @SerializedName("crop_verity_id")
    var crop_verity_id:String?=null
    @SerializedName("coordinates")
    var coordinates:ArrayList<Coordinate>?=null
}
class  Coordinate
{
    @SerializedName("latitude")
    var latitude:String?=null
    @SerializedName("longitude")
    var longitude:String?=null
}