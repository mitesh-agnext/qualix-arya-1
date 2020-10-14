package com.custom.app.ui.dashboard

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class CenterData : Parcelable{

    @SerializedName("total_quantity")
    var total_quantity:String?=null
    @SerializedName("quantity_unit")
    var quantity_unit:String?=null
    @SerializedName("collection")
    var collection:collections?=null
}

class collections{

    @SerializedName("commodities")
    var commodities:Array<commodities>?=null
    @SerializedName("commulative_daily_data")
    var cumulativedata: Map<String?, Float?>? = null

}

class commodities{
    @SerializedName("commodity_name")
    var commodity_name:String?=null
    @SerializedName("commodity_id")
    var commodity_id:Int?=null
    @SerializedName("total")
    var total:String?=null
    @SerializedName("unit")
    var unit:String?=null

}