package com.custom.app.data.model.bleScan

import com.google.gson.annotations.SerializedName

class CommodityResponse
{
    @SerializedName("commodity_id")
    var commodityId: String? = null
    @SerializedName("commodity_code")
    var commodityCode: String? = null
    @SerializedName("commodity_name")
    var commodityName: String? = null
    @SerializedName("commodity_category_id")
    var commodityCategoryId: String? = null
    @SerializedName("commodity_category_name")
    var commodityCategoryName: String? = null
    @SerializedName("count")
    var count: String? = null
}