package com.custom.app.data.model.section

import com.google.gson.annotations.SerializedName

class LocationItem {

    @SerializedName("location_id")
    var location_id:String?=null
    @SerializedName("city_code")
    var city_code:String?=null
    @SerializedName("client_code")
    var client_code:String?=null
    @SerializedName("code")
    var code:String?=null
    @SerializedName("location")
    var location:String?=null
    @SerializedName("name")
    var name:String?=null
    @SerializedName("commercial_location_type_id")
    var commercial_location_type_id:String?=null
    @SerializedName("commercial_location_type_desc")
    var commercial_location_type_desc:String?=null

}