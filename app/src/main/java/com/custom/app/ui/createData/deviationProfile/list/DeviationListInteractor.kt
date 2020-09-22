package com.custom.app.ui.createData.deviationProfile.list

import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import com.custom.app.util.Constants
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class DeviationListInteractor {

    interface ListDeviationInteractorCallback {
        fun allDeviationApiSuccess(body: ArrayList<DeviationListRes>)
        fun allDeviationApiError(msg: String)
        fun deleteDeviationSuccess(deletedPostion: Int)
        fun deleteDeviationFailure(msg: String)
    }

    fun allDeviation(customerId: Int, listener: ListDeviationInteractorCallback) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)

            val call = apiService.getDeviation(Constants.TOKEN, customerId)
            call.enqueue(object : Callback<ArrayList<DeviationListRes>> {
                override fun onResponse(
                        call: Call<ArrayList<DeviationListRes>>,
                        response: Response<ArrayList<DeviationListRes>>) {

                    when {
                        response.isSuccessful -> listener.allDeviationApiSuccess(response.body()!!)
                        else -> {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            when {
                                else -> listener.allDeviationApiError(jObjError.getString("error-message"))
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<DeviationListRes>>, t: Throwable) {
                    listener.allDeviationApiError(t.message.toString())
                }
            })

        } catch (e: Exception) {
            Timber.e(e);
        }
    }

    fun deleteDeviation(listener: ListDeviationInteractorCallback, foodTypeId: Int, deletedPostion: Int) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)

            val call = apiService.deleteDeviation(Constants.TOKEN, foodTypeId)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>) {

                    when {
                        response.isSuccessful -> listener.deleteDeviationSuccess(deletedPostion)
                        else -> {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            when {
                                else -> listener.deleteDeviationFailure(jObjError.getString("error-message"))
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    listener.deleteDeviationFailure(t.message.toString())
                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}