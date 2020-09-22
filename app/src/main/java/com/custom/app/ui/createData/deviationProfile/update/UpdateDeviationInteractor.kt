package com.custom.app.ui.createData.deviationProfile.update

import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import com.custom.app.util.Constants
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateDeviationInteractor {

    interface onUpdateDeviationListener {

        fun onDeviationSuccess(body: ResponseBody)
        fun onDeviationFailure()

    }

    fun updateDeviation(profile_name: String, customer_id: Int, mainOperator: String, BLMIN: String, operator: String, BLMax: String, level_id: String, level_type: String,
                     frequency: String, to_emails: String, listener: UpdateDeviationInteractor.onUpdateDeviationListener) {

        val options = JsonObject()
        val rule = JsonArray()
        val jsonRule = JsonObject()
        val deviationRule = JsonArray()
        val jsonDeviationRule = JsonObject()

//        jsonRule.addProperty("mainOperator", mainOperator)
//        jsonRule.addProperty("bracketAtStart", "(")
//        jsonRule.addProperty("BL", BLMIN)
//        jsonRule.addProperty("operator", operator)
//        jsonRule.addProperty("Temp", BLMax)
//        jsonRule.addProperty("bracketAtEnd", ")")
//        rule.add(jsonRule)
//
//        jsonDeviationRule.addProperty("level_id", level_id)
//        jsonDeviationRule.addProperty("level_type", level_type)
//        jsonDeviationRule.addProperty("frequency", frequency)
//        jsonDeviationRule.addProperty("to_emails", to_emails)
//        deviationRule.add(jsonDeviationRule)

        options.addProperty("profile_name", profile_name)
        options.addProperty("customer_id", customer_id)
        options.add("rule", rule)
        options.add("deviation_levels", deviationRule)

        createDeviation(listener, options)
    }

    private fun createDeviation(listener: onUpdateDeviationListener, options: JsonObject) {

        val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
        val call = apiService.createDeviation(Constants.TOKEN, options)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                listener.onDeviationFailure()
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    listener.onDeviationSuccess(response.body()!!)
                } else {
                    if (response.code() == 401) {
                        listener.onDeviationFailure()
                    } else {
                        listener.onDeviationFailure()
                    }
                }
            }
        })
    }
}