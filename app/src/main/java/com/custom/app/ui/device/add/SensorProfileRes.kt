package com.custom.app.ui.device.add

import com.google.gson.annotations.SerializedName

class SensorProfileRes{

    @SerializedName("device_sensor_profile_id")
    var device_sensor_profile_id:Int?=null
    @SerializedName("device_sensor_profile_desc")
    var device_sensor_profile_desc:String?=null
    @SerializedName("device_sensor_profile_code")
    var device_sensor_profile_code:String?=null

}