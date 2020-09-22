package com.custom.app.ui.device.add

import com.google.gson.annotations.SerializedName

class DeviceTypeRes{

    @SerializedName("device_type_id")
    var device_type_id:Int?=null
    @SerializedName("device_type_desc")
    var device_type_desc:String?=null
    @SerializedName("device_type_code")
    var device_type_code:String?=null

}