package com.custom.app.data.model.section

import com.google.gson.annotations.SerializedName

class GardenRes {

    @SerializedName("garden_id")
    var garden_id:String?=null
    @SerializedName("customer_id")
    var customer_id:String?=null
    @SerializedName("name")
    var name:String?=null
    @SerializedName("user_id")
    var user_id:String?=null
    @SerializedName("location_name")
    var location_name:String?=null
    @SerializedName("location_id")
    var location_id:String?=null

}