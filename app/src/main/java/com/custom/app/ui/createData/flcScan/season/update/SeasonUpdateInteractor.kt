package com.custom.app.ui.createData.flcScan.season.update

import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import com.custom.app.util.Constants
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SeasonUpdateInteractor {

    interface UpdateSeasonListener {

        fun onSeasonUpdateSuccess(body: ResponseBody)
        fun onSeasonUpdateFailure(msg: String)

        fun onSeasonNameEmpty()
    }

    fun updateSeason(season_name: String, season_equation: String, season_id: Int, commodity_id: Int, date_from: Long, date_To: Long, listener: UpdateSeasonListener) {
        when {
            season_name.isEmpty() -> {
                listener.onSeasonNameEmpty()
            }
            else -> {

                val options = JsonObject()
                options.addProperty("season_name", season_name)
                options.addProperty("commodity_id", commodity_id)
                options.addProperty("equation", season_equation)
                options.addProperty("from_date", date_from)
                options.addProperty("to_date", date_To)

                UpdateSeason(listener, options, season_id)
            }
        }
    }

    private fun UpdateSeason(listener: UpdateSeasonListener, options: JsonObject, season_id: Int) {

        val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
        val call = apiService.updateSeason(Constants.TOKEN, options, season_id)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                listener.onSeasonUpdateFailure(t!!.message.toString())
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    listener.onSeasonUpdateSuccess(response.body()!!)
                } else {
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    if (response.code() == 401) {
                        listener.onSeasonUpdateFailure(jObjError.getString("error-message"))
                    } else {
                        listener.onSeasonUpdateFailure(jObjError.getString("error-message"))
                    }
                }
            }
        })
    }
}