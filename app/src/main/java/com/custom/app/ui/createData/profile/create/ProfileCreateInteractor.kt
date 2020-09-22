package com.custom.app.ui.createData.profile.create

import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import com.custom.app.util.Constants
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileCreateInteractor {

    interface CreateProfileListener {

        fun onProfileSuccess(body: ResponseBody)
        fun onProfileFailure()

        fun onProfileNameEmpty()
        fun onMaxTempEmpty()
        fun onMinTempEmpty()

    }

    fun addProfile(profile_name: String, customer_id: Int, maxTemp: String, minTemp: String, listener: CreateProfileListener) {
        when {
            profile_name.isEmpty() -> {
                listener.onProfileNameEmpty()
            }
            maxTemp.isEmpty() -> {
                listener.onMaxTempEmpty()
            }
            minTemp.isEmpty() -> {
                listener.onMinTempEmpty()
            }
            else -> {

                val options = JsonObject()
                options.addProperty("profile_name", profile_name)
                options.addProperty("customer_id", customer_id)
                options.addProperty("max_temp", maxTemp)
                options.addProperty("min_temp", minTemp)

                createProfile(listener, options)
            }
        }
    }

    private fun createProfile(listener: CreateProfileListener, options: JsonObject) {

        val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
        val call = apiService.createProfile(Constants.TOKEN, options)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                listener.onProfileFailure()
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    listener.onProfileSuccess(response.body()!!)
                } else {
                    if (response.code() == 401) {
                        listener.onProfileFailure()
                    } else {
                        listener.onProfileFailure()
                    }
                }
            }
        })
    }


}