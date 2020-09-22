package com.custom.app.ui.createData.analytics.analysis.numberOfFarmers

import com.google.gson.annotations.SerializedName

class FarmerDataRes {

    @SerializedName("max_inst_center_id")
    var max_inst_center_id: Int? = null
    @SerializedName("max_farmer_id")
    var max_farmer_id: Int? = null
    @SerializedName("min_farmer_id")
    var min_farmer_id: Int? = null
    @SerializedName("min_inst_center_id")
    var min_inst_center_id: Int? = null
    @SerializedName("max_quantity")
    var max_quantity: Double? = null
    @SerializedName("min_quantity")
    var min_quantity: Double? = null
    @SerializedName("increment_percentage ")
    var increment: String? = null
    @SerializedName("decrement_percentage ")
    var decrement: String? = null
    @SerializedName("increment_graph_data")
    var increment_graph_data: Array<Increment_graph_data>? = null
    @SerializedName("decrement_graph_data")
    var decrement_graph_data: Array<Decrement_graph_data>? = null
}
class Increment_graph_data {

    @SerializedName("scan_date")
    var scan_date: String? = null
    @SerializedName("increment_graph_total_weight")
    var increment_graph_total_weight: Double? = null

}

class Decrement_graph_data {

    @SerializedName("scan_date")
    var scan_date: String? = null
    @SerializedName("decrement_graph_total_weight")
    var decrement_graph_total_weight: Double? = null

}