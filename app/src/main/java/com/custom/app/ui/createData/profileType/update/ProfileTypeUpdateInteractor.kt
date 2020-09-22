package com.custom.app.ui.updateData.profileType.update

import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import com.custom.app.util.Constants
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileTypeUpdateInteractor {

    interface UpdateProfileTypeListener {

        fun onProfileTypeUpdateSuccess(body: ResponseBody)
        fun onProfileTypeUpdateFailure()

        fun onProfileTypeNameEmpty()
    }

    fun updateProfileType(profile_type_name: String, profile_type_id: Int, customer_id: Int, listener: UpdateProfileTypeListener) {
        val options = JsonObject()
        options.addProperty("profile_type_id", profile_type_id)
        options.addProperty("profile_type_name", profile_type_name)
        options.addProperty("customer_id", customer_id)

        updateProfileType(profile_type_id, listener, options)
    }

    private fun updateProfileType(profile_type_id: Int, listener: UpdateProfileTypeListener, options: JsonObject) {

        val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
        val call = apiService.updateProfileType(Constants.TOKEN,profile_type_id, options)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                listener.onProfileTypeUpdateFailure()
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    listener.onProfileTypeUpdateSuccess(response.body()!!)
                } else {
                    if (response.code() == 401) {
                        listener.onProfileTypeUpdateFailure()
                    } else {
                        listener.onProfileTypeUpdateFailure()
                    }
                }
            }
        })
    }

}