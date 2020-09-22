package com.custom.app.data.model.role

import com.google.gson.annotations.SerializedName

class RoleRes
{
    @SerializedName("role_code")
    var role_code: String? = null
    @SerializedName("role_desc")
    var role_desc: String? = null
    @SerializedName("permissions")
    var permissions: String? = null
}