package com.custom.app.ui.createData.foodType.list

import com.google.gson.annotations.SerializedName

class FoodTypeListRes {

    @SerializedName("profile_food_type_id")
    var food_type_id: Int? = null
    @SerializedName("profile_food_type_name")
    var food_type_name: String? = null
    @SerializedName("customer_id")
    var customer_id: Int? = null

}