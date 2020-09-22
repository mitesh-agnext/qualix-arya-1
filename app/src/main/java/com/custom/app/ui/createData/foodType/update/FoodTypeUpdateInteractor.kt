package com.custom.app.ui.updateData.foodType.update

import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import com.custom.app.util.Constants
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FoodTypeUpdateInteractor {

    interface UpdateFoodTypeListener {

        fun onFoodTypeUpdateSuccess(body: ResponseBody)
        fun onFoodTypeUpdateFailure()
        fun onFoodTypeNameEmpty()
    }

    fun updateFoodType(food_type_name: String, food_type_id: Int, customer_id: Int, listener: UpdateFoodTypeListener) {
        val options = JsonObject()
        options.addProperty("profile_food_type_id", food_type_id)
        options.addProperty("profile_food_type_name", food_type_name)
        options.addProperty("customer_id", customer_id)

        updateFoodType(food_type_id, listener, options)
    }

    private fun updateFoodType(food_type_id: Int, listener: UpdateFoodTypeListener, options: JsonObject) {

        val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
        val call = apiService.updateFoodType(Constants.TOKEN,food_type_id, options)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                listener.onFoodTypeUpdateFailure()
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    listener.onFoodTypeUpdateSuccess(response.body()!!)
                } else {
                    if (response.code() == 401) {
                        listener.onFoodTypeUpdateFailure()
                    } else {
                        listener.onFoodTypeUpdateFailure()
                    }
                }
            }
        })
    }
}