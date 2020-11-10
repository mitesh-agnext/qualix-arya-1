package com.custom.app.data.model.bleScan

import com.google.gson.annotations.SerializedName

class LocationResponse
{
    @SerializedName("code")
    var code: String? = null
    @SerializedName("allocated")
    var allocated: String? = null
    @SerializedName("installation_center_id")
    var installationCenterId: String? = null
    @SerializedName("inst_center_name")
    var instCenterName: String? = null
    @SerializedName("state_id")
    var stateId: String? = null
    @SerializedName("state_name")
    var stateName: String? = null
    @SerializedName("warehouse_name")
    var warehouse_name: String? = null
}