package com.custom.app.ui.qualityAnalysis

import com.google.gson.annotations.SerializedName

class ResUserScans{
    @SerializedName("success")
    var success:String?=null
    @SerializedName("message")
    var message:String?=null
    @SerializedName("devMessage")
    var devMessage:String?=null
    @SerializedName("data")
    var data:List<ScanDetail>?=null
}

class ScanDetail
{
    @SerializedName("id")
    var id:String?=null
    @SerializedName("dateDone")
    var dateDone:String?=null
    @SerializedName("ccId")
    var ccId:String?=null
    @SerializedName("timeCreatedOn")
    var timeCreatedOn:String?=null
    @SerializedName("oneLeafBud")
    var oneLeafBud:String?=null
    @SerializedName("twoLeafBud")
    var twoLeafBud:String?=null
    @SerializedName("threeLeafBud")
    var threeLeafBud:String?=null
    @SerializedName("oneLeafBanjhi")
    var oneLeafBanjhi:String?=null
    @SerializedName("twoLeafBanjhi")
    var twoLeafBanjhi:String?=null
    @SerializedName("oneBudCount")
    var oneBudCount:String?=null
    @SerializedName("oneLeafCount")
    var oneLeafCount:String?=null
    @SerializedName("twoLeafCount")
    var twoLeafCount:String?=null
    @SerializedName("threeLeafCount")
    var threeLeafCount:String?=null
    @SerializedName("oneBanjhiCount")
    var oneBanjhiCount:String?=null
    @SerializedName("qualityScore")
    var qualityScore:String?=null
    @SerializedName("totalRecords")
    var totalRecords:String?=null
    @SerializedName("ccName")
    var ccName:String?=null
    @SerializedName("username")
    var username:String?=null
    @SerializedName("sectionName")
    var sectionName:String?=null
    @SerializedName("totalWeight")
    var totalWeight:String?=null
    @SerializedName("areaCovered")
    var areaCovered:String?=null
    @SerializedName("seasonId")
    var seasonId:String?=null
}
