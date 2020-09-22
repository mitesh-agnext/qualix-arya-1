package com.custom.app.ui.home

import com.google.gson.annotations.SerializedName

class SubscribedDeviceRes {

    @SerializedName("device_type_details")
    var devices: ArrayList<DeviceItem>? = null

}

class DeviceItem {

    @SerializedName("device_type_id")
    var device_id: Int? = null
    @SerializedName("device_type_desc")
    var device_name: String? = null
    @SerializedName("device_count")
    var device_count: String? = null

}