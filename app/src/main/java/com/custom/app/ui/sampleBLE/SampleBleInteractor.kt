package com.custom.app.ui.sampleBLE

import com.custom.app.data.model.bleScan.CommodityResponse
import com.custom.app.data.model.bleScan.LocationResponse
import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import com.user.app.data.UserManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SampleBleInteractor(val userManager: UserManager) {
    val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
    fun getLocation(listener: OnSampleBleInteractorListener) {
        val call = apiService.locationList("Bearer ${userManager.token}")
        call.enqueue(object : Callback<ArrayList<LocationResponse>> {
            override fun onResponse(call: Call<ArrayList<LocationResponse>>, response: Response<ArrayList<LocationResponse>>) {
                when (response.code()) {
                    200 -> {
                        listener.onLocationSuccess(response.body()!!)
                    }
                    else -> {
                        listener.onLocationFailure()
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<LocationResponse>>, t: Throwable) {
                listener.onLocationFailure()
            }
        })
    }

    fun getCommodity(listener: OnSampleBleInteractorListener) {
        val call = apiService.getCommodityByCategory("Bearer ${userManager.token}", userManager.customerId)
        call.enqueue(object : Callback<ArrayList<CommodityResponse>> {
            override fun onResponse(call: Call<ArrayList<CommodityResponse>>, response: Response<ArrayList<CommodityResponse>>) {
                when (response.code()) {
                    200 -> {
                        listener.onCommoditySuccess(response.body()!!)
                    }
                    else -> {
                        listener.onCommodityFailure()
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<CommodityResponse>>, t: Throwable) {
                listener.onCommodityFailure()
            }
        })
    }
    interface OnSampleBleInteractorListener {
        fun onLocationSuccess(locationList: ArrayList<LocationResponse>)
        fun onLocationFailure()
        fun onCommoditySuccess(commodityList: ArrayList<CommodityResponse>)
        fun onCommodityFailure()
    }

}