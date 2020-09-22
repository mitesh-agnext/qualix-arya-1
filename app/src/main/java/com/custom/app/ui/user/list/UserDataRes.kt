package com.custom.app.ui.user.list

import com.google.gson.annotations.SerializedName

class UserDataRes {

    @SerializedName("user_id")
    var user_id: Int? = null
    @SerializedName("created_by")
    var created_by: String? = null
    @SerializedName("contact_number")
    var contact_number: String? = null
    @SerializedName("email")
    var email: String? = null
    @SerializedName("first_name")
    var first_name: String? = null
    @SerializedName("last_name")
    var last_name: String? = null
    @SerializedName("customer_id")
    var customer_id: String? = null
    @SerializedName("address")
    var address: ArrayList<ResAddress>? = null
    @SerializedName("roles_list")
    var roles_list: ArrayList<ResRole>? = null

}

class ResAddress {

    @SerializedName("city")
    var city: String? = null
    @SerializedName("country")
    var country: String? = null
    @SerializedName("state")
    var state: String? = null
    @SerializedName("address_id")
    var address_id: String? = null
    @SerializedName("address1")
    var address1: String? = null
    @SerializedName("address_line2")
    var address_line2: String? = null
    @SerializedName("pincode")
    var pincode: String? = null
    @SerializedName("address_type_id")
    var address_type_id: String? = null

}

class ResRole {

    @SerializedName("role_code")
    var role_code: String? = null
    @SerializedName("role_desc")
    var role_desc: String? = null
    @SerializedName("permissions")
    var permissions: ArrayList<ResPermission>? = null

}

class ResPermission {

    @SerializedName("permission_code")
    var permission_code: String? = null
    @SerializedName("permission_desc")
    var permission_desc: String? = null

}