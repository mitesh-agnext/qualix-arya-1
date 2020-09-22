package com.custom.app.ui.device.add

import com.google.gson.annotations.SerializedName

class DeviceGroupRes{

    @SerializedName("device_group_id")
    var device_group_id:Int?=null
    @SerializedName("device_group_desc")
    var device_group_desc:String?=null
    @SerializedName("device_group_code")
    var device_group_code:String?=null

}