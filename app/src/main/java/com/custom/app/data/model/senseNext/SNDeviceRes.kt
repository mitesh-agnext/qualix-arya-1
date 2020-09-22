package com.custom.app.data.model.senseNext

import com.google.gson.annotations.SerializedName

class SNDeviceRes {

    @SerializedName("cold_store__id")
    var coldStoreId: String? = null
    @SerializedName("created_on")
    var createdOn: String? = null
    @SerializedName("inst_center_name")
    var instCenterName: String? = null
    @SerializedName("profile_id")
    var profileId: String? = null
    @SerializedName("profile_type_id")
    var profileTypeId: String? = null
    @SerializedName("profile_food_type_id")
    var profileFoodTypeId: String? = null
    @SerializedName("profile_name")
    var profileName: String? = null
    @SerializedName("profile_type_name")
    var profileTypeName: String? = null
    @SerializedName("profile_food_type_name")
    var profileFoodTypeName: String? = null
    @SerializedName("battery_level")
    var batteryLevel: String? = null
    @SerializedName("temp")
    var temp: String? = null
    @SerializedName("date")
    var date: String? = null
    @SerializedName("time")
    var time: String? = null
    @SerializedName("device_id")
    var deviceId: String? = null
    @SerializedName("escalation_level")
    var escalationLevel: String? = null
    @SerializedName("online")
    var online: Boolean? = null

}