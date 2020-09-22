package com.custom.app.ui.updateData.profile.update

import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import com.custom.app.util.Constants
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileUpdateInteractor {

    interface UpdateProfileListener {

        fun onProfileUpdateSuccess(body: ResponseBody)
        fun onProfileUpdateFailure()

        fun onProfileNameEmpty()
        fun onMaxTempEmpty()
        fun onMinTempEmpty()
    }

    fun updateProfile(profile_name: String, profile_id: Int, customerId: Int, maxTemp: String, minTemp: String, listener: UpdateProfileListener) {
        val options = JsonObject()
        options.addProperty("profile_id", profile_id)
        options.addProperty("profile_name", profile_name)
        options.addProperty("max_temp", maxTemp)
        options.addProperty("min_temp", minTemp)
        options.addProperty("customer_id", customerId)


        updateProfile(profile_id, listener, options)
    }

    private fun updateProfile(profile_id: Int, listener: UpdateProfileListener, options: JsonObject) {

        val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
        val call = apiService.updateProfile(Constants.TOKEN, profile_id, options)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                listener.onProfileUpdateFailure()
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    listener.onProfileUpdateSuccess(response.body()!!)
                } else {
                    if (response.code() == 401) {
                        listener.onProfileUpdateFailure()
                    } else {
                        listener.onProfileUpdateFailure()
                    }
                }
            }
        })
    }
}