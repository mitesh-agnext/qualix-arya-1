package com.custom.app.data.model.country

import com.google.gson.annotations.SerializedName

class CityRes {

    @SerializedName("city_id")
    var city_id: Int? = null
    @SerializedName("city_name")
    var city_name: String? = null

}