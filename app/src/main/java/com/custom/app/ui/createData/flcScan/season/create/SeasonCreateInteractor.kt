package com.custom.app.ui.createData.flcScan.season.create

import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import com.custom.app.util.Constants
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class SeasonCreateInteractor {

    interface CreateSeasonListener {

        fun onSeasonSuccess(body: ResponseBody)
        fun onSeasonFailure(msg: String)

        fun onCommoditySuccess(body: ArrayList<CommodityRes>)
        fun onCommodityFailure(msg: String)

        fun onSeasonNameEmpty()
    }

    fun addSeason(season_name: String, season_equation: String, customer_id: Int, commodity_id: Int, date_from: Long, date_To: Long, listener: CreateSeasonListener) {
        when {
            season_name.isEmpty() -> {
                listener.onSeasonNameEmpty()
            }
            else -> {

                val options = JsonObject()
                options.addProperty("season_name", season_name)
                options.addProperty("customer_id", customer_id)
                options.addProperty("commodity_id", commodity_id)
                options.addProperty("equation", season_equation)
                options.addProperty("from_date", date_from)
                options.addProperty("to_date", date_To)

                createSeason(listener, options)
            }
        }
    }

    private fun createSeason(listener: CreateSeasonListener, options: JsonObject) {

        val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
        val call = apiService.createSeason(Constants.TOKEN,options)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                listener.onSeasonFailure(t!!.message.toString())
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    listener.onSeasonSuccess(response.body()!!)
                } else {
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    if (response.code() == 401) {
                        listener.onSeasonFailure(jObjError.getString("error-message"))
                    } else {
                        listener.onSeasonFailure(jObjError.getString("error-message"))
                    }
                }
            }
        })
    }

    fun getCommodity(listener: CreateSeasonListener, customer_id: Int) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
            val call = apiService.getCommodity(Constants.TOKEN, "")
            call.enqueue(object : Callback<ArrayList<CommodityRes>> {
                override fun onResponse(
                        call: Call<ArrayList<CommodityRes>>,
                        response: Response<ArrayList<CommodityRes>>) {
                    when {
                        response.isSuccessful -> {
                            listener.onCommoditySuccess(response.body()!!)
                        }
                        else -> {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            when {
                                response.code() == 401 -> {
                                    listener.onCommodityFailure(jObjError.getString("error-message"))
                                }
                                else -> {
                                    listener.onCommodityFailure(jObjError.getString("error-message"))
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<CommodityRes>>, t: Throwable) {
                    listener.onCommodityFailure(t.message.toString())
                }
            })
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}