package com.custom.app.ui.dashboard

import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import com.user.app.data.UserManager
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class DashboardInteractor(val userManager: UserManager) {

    fun centerData(listener: CenterDetailInteractorCallback, query: Map<String, String>) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
            val call = apiService.centerDetails("Bearer ${userManager.token}", query)
            call.enqueue(object : Callback<CenterData> {
                override fun onResponse(
                        call: Call<CenterData>, response: Response<CenterData>) {

                    when {
                        response.isSuccessful -> listener.centerDetailSucess(response.body()!!)
                        else -> {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            when {
                                else -> listener.centerDetailError(jObjError.getString("error-message"))
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<CenterData>, t: Throwable) {
                    listener.centerDetailError(t.message.toString())
                }
            })

        } catch (e: Exception) {
            Timber.e(e);
        }
    }
}

interface CenterDetailInteractorCallback {

    fun centerDetailSucess(body: CenterData)
    fun centerDetailError(msg: String)
}