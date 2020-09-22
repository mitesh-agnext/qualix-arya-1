package com.custom.app.ui.farm.updateFarm

import android.content.Context
import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import com.custom.app.ui.farm.ResParticularFarm
import retrofit2.Call
import retrofit2.Response

class UpdateFramInteractor(private val context: Context)
{
    private val apiService = ApiClient.getClient().create(ApiInterface::class.java)

    /**Api hit to get particular farm*/
    fun getParticularFarm(token:String,farmId:String,mCallback: UpdateFarmFinishedListener)
    {
        val call = apiService.getParticularFarm(token,farmId)
        call.enqueue(object: retrofit2.Callback<ResParticularFarm> {
            override fun onFailure(call: Call<ResParticularFarm>, t: Throwable) {
                mCallback.onGetParticularFarmFailure()
            }

            override fun onResponse(
                call: Call<ResParticularFarm>,
                response: Response<ResParticularFarm>
            ) {
                mCallback.onGetParticularFarmSuccess(response.body())

            }
        })
    }

    interface UpdateFarmFinishedListener {
        fun onGetParticularFarmSuccess(response: ResParticularFarm?)
        fun onGetParticularFarmFailure()
    }

}