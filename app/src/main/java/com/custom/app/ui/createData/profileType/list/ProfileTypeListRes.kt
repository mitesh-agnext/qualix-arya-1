package com.custom.app.ui.createData.profileType.list

import com.google.gson.annotations.SerializedName

class ProfileTypeListRes {

    @SerializedName("profile_type_id")
    var profile_type_id: Int? = null
    @SerializedName("profile_type_name")
    var profile_type_name: String? = null
    @SerializedName("customer_id")
    var customer_id: Int? = null

}