package com.custom.app.ui.device.list

import com.google.gson.annotations.SerializedName

class DevicesData{

    @SerializedName("serial_number")
    var serial_number:String?=null
    @SerializedName("device_type")
    var device_type:String?=null
    @SerializedName("installation_center_name")
    var installation_center_name:String?=null
    @SerializedName("customer_id")
    var customer_id:String?=null
    @SerializedName("user_uuid")
    var user_uuid:String?=null
    @SerializedName("device_id")
    var device_id:Int?=null
    @SerializedName("device_type_id")
    var device_type_id:String?=null
    @SerializedName("city_code")
    var city_code:String?=null
    @SerializedName("state_code")
    var state_code:String?=null
    @SerializedName("country_code")
    var country_code:String?=null
    @SerializedName("installation_center_type_id")
    var installation_center_type_id:String?=null
    @SerializedName("installation_center_type_name")
    var installation_center_type_name:String?=null
    @SerializedName("device_group_id")
    var device_group_id:String?=null
    @SerializedName("device_sub_type_id")
    var device_sub_type_id:String?=null
    @SerializedName("hw_revision")
    var hw_revision:String?=null
    @SerializedName("fw_revision")
    var fw_revision:String?=null
    @SerializedName("start_of_life")
    var start_of_life:Long?=null
    @SerializedName("end_of_life")
    var end_of_life:Long?=null
    @SerializedName("start_of_service")
    var start_of_service:Long?=null
    @SerializedName("end_of_service")
    var end_of_service:Long?=null
    @SerializedName("sensor_profile_id")
    var sensor_profile_id:String?=null
    @SerializedName("device_group_desc")
    var device_group_desc:String?=null
    @SerializedName("device_sub_type_desc")
    var device_sub_type_desc:String?=null
    @SerializedName("sensor_profile_desc")
    var sensor_profile_desc:String?=null
    @SerializedName("status_id")
    var status_id:String?=null
    @SerializedName("status_desc")
    var status_desc:String?=null
    @SerializedName("vendor_name")
    var vendor_name:String?=null
}