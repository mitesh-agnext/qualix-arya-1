package com.custom.app.ui.createData.flcScan

import com.google.gson.annotations.SerializedName

class FlcResultRes {

    @SerializedName("Time Taken(seconds)")
    var time_taken: String? = null
    @SerializedName("Fine_Count")
    var fine_count: String? = null
    @SerializedName("Coarse_Count")
    var coarse_count: String? = null
    @SerializedName("1Bud_Count")
    var OneBud_Count: Int? = null
    @SerializedName("1Leaf_Count")
    var OneLeaf_Count: Int? = null
    @SerializedName("2Leaf_Count")
    var TwoLeaf_Count: Int? = null
    @SerializedName("3Leaf_Count")
    var ThreeLeaf_Count: Int? = null
    @SerializedName("1LeafBud_Count")
    var OneLeafBud_Count: Int? = null
    @SerializedName("2LeafBud_Count")
    var TwoLeafBud_Count: Int? = null
    @SerializedName("3LeafBud_Count")
    var ThreeLeafBud_Count: Int? = null
    @SerializedName("1Banjhi_Count")
    var OneBanjhi_Count: Int? = null
    @SerializedName("1LeafBanjhi_Count")
    var OneLeafBanjhi_Count: Int? = null
    @SerializedName("2LeafBanjhi_Count")
    var TwoLeafBanjhi_Count: Int? = null
    @SerializedName("3LeafBanjhi_Count")
    var ThreeLeafBanjhi_Count: Int? = null
    @SerializedName("Total_Bunches")
    var Total_Bunches: Int? = null


}