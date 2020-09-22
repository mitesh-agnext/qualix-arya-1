package com.custom.app.ui.createData.flcScan.season.create

import com.google.gson.annotations.SerializedName

class CommodityRes{

    @SerializedName("commodity_id")
    var commodity_id:Int?=null
    @SerializedName("commodity_code")
    var commodity_code:String?=null
    @SerializedName("commodity_name")
    var commodity_name:String?=null

}