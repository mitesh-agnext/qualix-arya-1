package com.custom.app.ui.createData.analytics.analysis.payments

import com.google.gson.annotations.SerializedName

class PaymentRes {

    @SerializedName("scan_date")
    var scan_date: String? = null
    @SerializedName("date_done")
    var date_done: Long? = null
    @SerializedName("total_payment")
    var total_payment: Double? = null
    @SerializedName("payment_count")
    var payment_count: String? = null

}