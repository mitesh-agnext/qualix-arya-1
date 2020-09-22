package com.custom.app.ui.createData.analytics.analysis.payments

import com.google.gson.annotations.SerializedName

class PaymentChartRes {

    @SerializedName("total_payment")
    var total_payment: Double? = null
    @SerializedName("inst_center_id")
    var inst_center_id: Int? = null

}