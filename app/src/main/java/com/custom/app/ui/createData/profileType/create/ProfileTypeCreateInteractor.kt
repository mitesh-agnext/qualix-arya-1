package com.custom.app.ui.createData.profileType.create

import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import com.custom.app.util.Constants
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileTypeCreateInteractor {

    interface CreateProfileTypeListener {

        fun onProfileTypeSuccess(body: ResponseBody)
        fun onProfileTypeFailure()

        fun onProfileTypeNameEmpty()

    }

    fun addProfileType(profile_name: String, customer_id: Int, listener: CreateProfileTypeListener) {
        when {
            profile_name.isEmpty() -> {
                listener.onProfileTypeNameEmpty()
            }
            else -> {

                val options = JsonObject()
                options.addProperty("profile_type_name", profile_name)
                options.addProperty("customer_id", customer_id)

                createProfileType(listener, options)
            }
        }
    }

    private fun createProfileType(listener: CreateProfileTypeListener, options: JsonObject) {

        val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
        val call = apiService.createProfileType(Constants.TOKEN,options)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                listener.onProfileTypeFailure()
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    listener.onProfileTypeSuccess(response.body()!!)
                } else {
                    if (response.code() == 401) {
                        listener.onProfileTypeFailure()
                    } else {
                        listener.onProfileTypeFailure()
                    }
                }
            }
        })
    }

}