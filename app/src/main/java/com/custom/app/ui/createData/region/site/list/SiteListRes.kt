package com.custom.app.ui.createData.region.site.list

import com.google.gson.annotations.SerializedName

class SiteListRes {

    @SerializedName("site_name")
    var site_name: String? = null
    @SerializedName("site_id")
    var site_id: Int? = null
    @SerializedName("region_name")
    var region_name: String? = null
    @SerializedName("region_id")
    var region_id: Int? = null
    @SerializedName("customer_id")
    var customer_id: Int? = null
    @SerializedName("user_id")
    var user_id: Int? = null
    @SerializedName("country_id")
    var country_id: Int? = null
    @SerializedName("state_id")
    var state_id: Int? = null
    @SerializedName("city_id")
    var city_id: Int? = null
    @SerializedName("coordinates")
    var coordinates: String? = null


}