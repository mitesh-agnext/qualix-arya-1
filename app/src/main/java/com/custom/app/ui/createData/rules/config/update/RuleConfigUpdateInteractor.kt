package com.custom.app.ui.createData.rules.config.update

import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import com.custom.app.util.Constants
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RuleConfigUpdateInteractor {

    interface UpdateRuleConfigListener {

        fun onUpdateRuleConfigSuccess(body: ResponseBody)
        fun onUpdateRuleConfigFailure()

    }

    fun updateRuleConfig(rule_config_id: Int, rule_config_name: String, customer_id: Int, listener: UpdateRuleConfigListener) {
        val options = JsonObject()
        options.addProperty("rule_config_name", rule_config_name)
        options.addProperty("rule_config_id", rule_config_id)
        options.addProperty("customer_id", customer_id)

        updateRuleConfig(rule_config_id, listener, options)
    }

    private fun updateRuleConfig(regionId: Int, listener: UpdateRuleConfigListener, options: JsonObject) {

        val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
        val call = apiService.updateRuleConfig(Constants.TOKEN, options, regionId)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                listener.onUpdateRuleConfigFailure()
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    listener.onUpdateRuleConfigSuccess(response.body()!!)
                } else {
                    if (response.code() == 401) {
                        listener.onUpdateRuleConfigFailure()
                    } else {
                        listener.onUpdateRuleConfigFailure()
                    }
                }
            }
        })
    }
}