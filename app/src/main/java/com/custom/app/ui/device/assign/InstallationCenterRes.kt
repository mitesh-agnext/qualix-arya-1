package com.custom.app.ui.device.assign

import com.google.gson.annotations.SerializedName

class InstallationCenterRes {

    @SerializedName("installation_center_id")
    var installation_center_id: Int? = null

    @SerializedName("inst_center_name")
    var inst_center_name: String? = null

    @SerializedName("profile_name")
    var profile_name: String? = null

    @SerializedName("profile_type_name")
    var profile_type_name: String? = null

    @SerializedName("profile_food_type_name")
    var profile_food_type_name: String? = null

    @SerializedName("notes")
    var notes: String? = null

    @SerializedName("commercial_location_type_desc")
    var commercial_location_type_desc: String? = null

    @SerializedName("customer_id")
    var customer_id: Int? = null

    @SerializedName("user_id")
    var user_id: Int? = null

    @SerializedName("site_name")
    var site_name: String? = null

    @SerializedName("region_id")
    var region_id: Int? = null

}