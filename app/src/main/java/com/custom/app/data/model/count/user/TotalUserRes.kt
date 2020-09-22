package com.custom.app.data.model.count.user

import com.google.gson.annotations.SerializedName

class TotalUserRes {

    @SerializedName("total_users")
    var total_users: Int? = null
    @SerializedName("service_provider_users")
    var service_provider_users: Int? = null
    @SerializedName("total_customer_users")
    var total_customer_users: Int? = null
    @SerializedName("total_partners_users")
    var total_partners_users: Int? = null

}