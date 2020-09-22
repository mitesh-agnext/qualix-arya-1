package com.custom.app.ui.createData.foodType.create

import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import com.custom.app.util.Constants
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FoodTypeCreateInteractor {

    interface CreateFoodTypeListener {

        fun onFoodTypeSuccess(body: ResponseBody)
        fun onFoodTypeFailure()

        fun onFoodTypeNameEmpty()

    }

    fun addFoodType(food_name: String, customer_id: Int, listener: CreateFoodTypeListener) {
        when {
            food_name.isEmpty() -> {
                listener.onFoodTypeNameEmpty()
            }
            else -> {

                val options = JsonObject()
                options.addProperty("profile_food_type_name", food_name)
                options.addProperty("customer_id", customer_id)

                createFoodType(listener, options)
            }
        }
    }

    private fun createFoodType(listener: CreateFoodTypeListener, options: JsonObject) {

        val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
        val call = apiService.createFoodType(Constants.TOKEN,options)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                listener.onFoodTypeFailure()
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    listener.onFoodTypeSuccess(response.body()!!)
                } else {
                    if (response.code() == 401) {
                        listener.onFoodTypeFailure()
                    } else {
                        listener.onFoodTypeFailure()
                    }
                }
            }
        })
    }

}