package com.custom.app.ui.createData.rules.config.create

import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import com.custom.app.ui.customer.list.CustomerRes
import com.custom.app.util.Constants
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.*

class RuleConfigCreateInteractor {

    interface CreateRuleConfigListener {

        fun onRuleConfigSuccess(body: ResponseBody)
        fun onRuleConfigFailure()

        fun onGetCustomerSuccess(body: ArrayList<CustomerRes>)
        fun onGetCustomerFailure()

        fun onRuleConfigNameEmpty()
    }

    fun addRuleConfig(ruleConfig_name: String, customer_id: Int, listener: CreateRuleConfigListener) {
        when {
            ruleConfig_name.isEmpty() -> {
                listener.onRuleConfigNameEmpty()
            }
            else -> {

                val options = JsonObject()
                options.addProperty("rule_config_name", ruleConfig_name)
                options.addProperty("customer_id", customer_id)

                createRuleConfig(listener, options)
            }
        }
    }

    private fun createRuleConfig(listener: CreateRuleConfigListener, options: JsonObject) {

        val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
        val call = apiService.createRuleConfig(Constants.TOKEN, options)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                listener.onRuleConfigFailure()
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    listener.onRuleConfigSuccess(response.body()!!)
                } else {
                    if (response.code() == 401) {
                        listener.onRuleConfigFailure()
                    } else {
                        listener.onRuleConfigFailure()
                    }
                }
            }
        })
    }

    fun getCustomer(listener: CreateRuleConfigListener) {
        try {
            val apiService = ApiClient.getClient().create(ApiInterface::class.java)
            val call = apiService.getCustomers(Constants.TOKEN)
            call.enqueue(object : Callback<ArrayList<CustomerRes>> {
                override fun onResponse(
                        call: Call<ArrayList<CustomerRes>>,
                        response: Response<ArrayList<CustomerRes>>) {

                    when {
                        response.isSuccessful -> {
                            listener.onGetCustomerSuccess(response.body()!!)
                        }
                        else -> {
                            when {
                                response.code() == 401 -> listener.onGetCustomerFailure()
                                else -> listener.onGetCustomerFailure()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<CustomerRes>>, t: Throwable) {
                }
            })
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}