package com.custom.app.data.model.count.order

import com.google.gson.annotations.SerializedName

class PurchaseOrderRes {

    @SerializedName("total_po_count")
    var total_po_count: Int? = null
    @SerializedName("current_month_po_count")
    var current_month_po_count: Int? = null
    @SerializedName("last_month_po_count")
    var last_month_po_count: Int? = null
    @SerializedName("device_type_po_count")
    var device_type_po_count: HashMap<String, Int>? = null
    @SerializedName("customer_po_count")
    var customer_po_count: HashMap<String, Int>? = null

}