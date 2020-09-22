package com.custom.app.ui.createData.profile.list

import com.google.gson.annotations.SerializedName

class ProfileListRes {

    @SerializedName("profile_id")
    var profile_id: Int? = null
    @SerializedName("profile_name")
    var profile_name: String? = null
    @SerializedName("max_temp")
    var max_temp: String? = null
    @SerializedName("min_temp")
    var min_temp: String? = null
    @SerializedName("customer_id")
    var customer_id: Int? = null

}