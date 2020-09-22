package com.custom.app.data.model.country

import com.google.gson.annotations.SerializedName

class CountryRes {

    @SerializedName("country_id")
    var country_id: Int? = null
    @SerializedName("country_name")
    var country_name: String? = null

}