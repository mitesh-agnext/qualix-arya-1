package com.custom.app.data.model.count.device

import com.custom.app.ui.home.DeviceItem
import com.google.gson.annotations.SerializedName

class TotalDeviceRes {

    @SerializedName("total_device_count")
    var total_device_count: Int? = null
    @SerializedName("device_type_count")
    var device_type_count: List<DeviceItem>? = null
    @SerializedName("highest_device_count")
    var highest_device_count: HashMap<String, Int>? = null
    @SerializedName("lowest_device_count")
    var lowest_device_count: HashMap<String, Int>? = null

}