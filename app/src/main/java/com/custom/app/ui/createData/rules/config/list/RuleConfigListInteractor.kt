package com.custom.app.ui.createData.rules.config.list

import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import com.custom.app.util.Constants
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class RuleConfigListInteractor {

    interface ListRuleConfigInteractorCallback {
        fun allRuleConfigApiSuccess(body: ArrayList<RuleConfigRes>)
        fun allRuleConfigApiError()
        fun deleteRuleConfigSuccess(deletedPostion: Int)
        fun deleteRuleConfigFailure()
    }

    fun allRuleConfig(listener: ListRuleConfigInteractorCallback, customerId: Int) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
            val call = apiService.getRuleConfig(Constants.TOKEN,customerId)
            call.enqueue(object : Callback<ArrayList<RuleConfigRes>> {
                override fun onResponse(
                        call: Call<ArrayList<RuleConfigRes>>,
                        response: Response<ArrayList<RuleConfigRes>>) {

                    when {
                        response.isSuccessful -> listener.allRuleConfigApiSuccess(response.body()!!)
                        else -> {
                            when {
                                else -> listener.allRuleConfigApiError()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<RuleConfigRes>>, t: Throwable) {
                    listener.allRuleConfigApiError()
                }
            })

        } catch (e: Exception) {
            Timber.e(e);
        }
    }

    fun deleteRuleConfig(listener: ListRuleConfigInteractorCallback, regionId: Int, deletedPostion: Int) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
            val call = apiService.deleteRuleConfig(Constants.TOKEN,regionId)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    when {
                        response.isSuccessful -> listener.deleteRuleConfigSuccess(deletedPostion)
                        else -> {
                            when {
                                else -> listener.deleteRuleConfigFailure()
                            }
                        }
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    listener.deleteRuleConfigFailure()
                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}