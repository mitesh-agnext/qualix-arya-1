package com.custom.app.ui.createData.coldstore.coldstoreList

import com.google.gson.annotations.SerializedName

class ColdstoreRes {

    @SerializedName("cold_store__id")
    var cold_store_id: Int? = null

    @SerializedName("inst_center_name")
    var inst_center_name: String? = null

    @SerializedName("profile_name")
    var profile_name: String? = null

    @SerializedName("profile_type_name")
    var profile_type_name: String? = null

    @SerializedName("profile_food_type_name")
    var profile_food_type_name: String? = null

    @SerializedName("notes")
    var notes: String? = null

    @SerializedName("customer_id")
    var customer_id: Int? = null

    @SerializedName("user_id")
    var user_id: Int? = null

    @SerializedName("site_name")
    var site_name: String? = null

    @SerializedName("region_id")
    var region_id: Int? = null


}