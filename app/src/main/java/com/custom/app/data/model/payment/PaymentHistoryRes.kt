package com.custom.app.data.model.payment

import com.google.gson.annotations.SerializedName

class PaymentHistoryRes {
    @SerializedName("payment_sum")
    var paymentSum: String? = null
    @SerializedName("client_id")
    var clientId: String? = null
    @SerializedName("client_name")
    var clientName: String? = null
    @SerializedName("commodity_id")
    var commodityId: String? = null
    @SerializedName("commodity_name")
    var commodityName: String? = null
    @SerializedName("quality")
    var quality: String? = null
    @SerializedName("done_by")
    var doneBy: String? = null
    @SerializedName("done_on")
    var doneOn: String? = null

    @SerializedName("payment_detail")
    var paymentDetail: PayDetail? = null
}

class PayDetail {
    @SerializedName("batch_id")
    var batchId: String? = null
    @SerializedName("weight")
    var weight: String? = null
    @SerializedName("done_on")
    var doneOn: String? = null
    @SerializedName("quality")
    var quality: String? = null
}