package com.custom.app.ui.createData.flcScan.season.list

import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import com.custom.app.util.Constants
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class SeasonListInteractor {

    interface ListSeasonInteractorCallback {
        fun allSeasonApiSuccess(body: ArrayList<SeasonRes>)
        fun allSeasonApiError(msg: String)
        fun deleteSeasonSuccess(deletedPostion: Int)
        fun deleteSeasonFailure(msg: String)
    }

    fun allSeason(listener: ListSeasonInteractorCallback, customerId: Int, key_search: String) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)

            val call = apiService.getSeason(Constants.TOKEN, customerId, key_search)
            call.enqueue(object : Callback<ArrayList<SeasonRes>> {
                override fun onResponse(
                        call: Call<ArrayList<SeasonRes>>,
                        response: Response<ArrayList<SeasonRes>>) {

                    when {
                        response.isSuccessful -> listener.allSeasonApiSuccess(response.body()!!)
                        else -> {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            when {
                                else -> listener.allSeasonApiError(jObjError.getString("error-message"))
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<SeasonRes>>, t: Throwable) {
                    listener.allSeasonApiError(t.message.toString())
                }
            })

        } catch (e: Exception) {
            Timber.e(e);
        }
    }

    fun deleteSeason(listener: ListSeasonInteractorCallback, regionId: Int, deletedPostion: Int) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)

            val call = apiService.deleteSeason(Constants.TOKEN, regionId)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>) {

                    when {
                        response.isSuccessful -> listener.deleteSeasonSuccess(deletedPostion)
                        else -> {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            when {
                                else -> listener.deleteSeasonFailure(jObjError.getString("error-message"))
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    listener.deleteSeasonFailure(t.message.toString())
                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}