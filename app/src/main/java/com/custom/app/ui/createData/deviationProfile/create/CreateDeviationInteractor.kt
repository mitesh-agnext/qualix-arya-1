package com.custom.app.ui.createData.deviationProfile.create

import android.content.Context
import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import com.custom.app.util.Constants
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateDeviationInteractor(private val context: Context) {

    interface onCreateDeviationListener {

        fun onDeviationSuccess(body: ResponseBody)
        fun onDeviationFailure()

    }

    fun addDeviation(profile_id: Int, customer_id: Int, blMin: String, blMax: String, tempMin: String, tempMax: String, moistMin: String, moistMax: String, level1Hour: String,
                     level1Minute: String, level1Email: String, level2Hour: String, level2Minute: String, level2Email: String, level3Hour: String, level3Minute: String,
                     level3Email: String, level4Hour: String, level4Minute: String, level4Email: String, level5Hour: String, level5Minute: String, level5Email: String,
                     listener: onCreateDeviationListener) {

        val options = JsonObject()
        val rule = JsonArray()
        val jsonRule = JsonObject()
        val deviationRule = JsonArray()
        val jsonDeviationRule = JsonObject()

//        jsonRule.addProperty("mainOperator", mainOperator)
//        jsonRule.addProperty("bracketAtStart", "(")
//        jsonRule.addProperty("BL", blMin)
//        jsonRule.addProperty("operator", operator)
//        jsonRule.addProperty("Temp", blMax)
//        jsonRule.addProperty("bracketAtEnd", ")")
//        rule.add(jsonRule)
//
//        jsonDeviationRule.addProperty("level_id", level_id)
//        jsonDeviationRule.addProperty("level_type", level_type)
//        jsonDeviationRule.addProperty("frequency", frequency)
//        jsonDeviationRule.addProperty("to_emails", to_emails)
//        deviationRule.add(jsonDeviationRule)

        options.addProperty("profile_name", profile_id)
        options.addProperty("customer_id", customer_id)
        options.add("rule", rule)
        options.add("deviation_levels", deviationRule)

        createDeviation(listener, options)

    }

    private fun createDeviation(listener: onCreateDeviationListener, options: JsonObject) {

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