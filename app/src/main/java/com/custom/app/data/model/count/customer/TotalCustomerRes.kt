package com.custom.app.data.model.count.customer

import com.google.gson.annotations.SerializedName

class TotalCustomerRes {

    @SerializedName("total_customer")
    var total_customer: Int? = null
    @SerializedName("total_partners")
    var total_partners: Int? = null
    @SerializedName("customers_under_partners")
    var customers_under_partners: Int? = null

}