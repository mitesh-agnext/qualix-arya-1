package com.custom.app.ui.createData.analytics.analysis.payments

import com.google.gson.annotations.SerializedName

class PaymentListRes {

    @SerializedName("farmer_name")
    var farmer_name: String? = null
    @SerializedName("farmer_Id")
    var farmer_Id: Int? = null
    @SerializedName("payment_date")
    var payment_date: Long? = null
    @SerializedName("company_id")
    var company_id: Int? = null
    @SerializedName("company_name")
    var company_name: String? = null
    @SerializedName("commodity_id")
    var commodity_id: Int? = null
    @SerializedName("commodity_name")
    var commodity_name: String? = null
    @SerializedName("payment_amount")
    var payment_amount: Double? = null

}