package com.custom.app.data.model.country

import com.google.gson.annotations.SerializedName

class StateRes {

    @SerializedName("state_id")
    var state_id: Int? = null
    @SerializedName("state_name")
    var state_name: String? = null

}