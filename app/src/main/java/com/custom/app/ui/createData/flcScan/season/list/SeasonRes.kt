package com.custom.app.ui.createData.flcScan.season.list

import com.google.gson.annotations.SerializedName

class SeasonRes{

    @SerializedName("season_id")
    var season_id:Int?=null
    @SerializedName("season_name")
    var season_name:String?=null
    @SerializedName("equation")
    var season_equation:String?=null
    @SerializedName("customer_id")
    var customer_id:Int?=null
    @SerializedName("commodity_id")
    var commodity_id:Int?=null
    @SerializedName("commodity_name")
    var commodity_name:String?=null
    @SerializedName("from_date")
    var from_date:Long?=null
    @SerializedName("to_date")
    var to_date:Long?=null
}