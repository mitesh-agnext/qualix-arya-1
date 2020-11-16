package com.custom.app.ui.sampleBLE

import android.hardware.Sensor
import com.custom.app.data.model.bleScan.CommodityResponse
import com.custom.app.data.model.bleScan.LocationResponse
import com.custom.app.data.model.bleScan.response.CommodityVarietyResponse
import com.custom.app.data.model.bleScan.response.SensorData
import com.custom.app.data.model.bleScan.response.SensorDataRepository
import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import com.custom.app.ui.sampleBleResult.SampleBleResultInteractor
import com.user.app.data.UserManager
import okhttp3.ResponseBody
import org.json.JSONObject
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
        call.enqueue(object : Callback<CommodityVarietyResponse> {
            override fun onResponse(call: Call<CommodityVarietyResponse>, response: Response<CommodityVarietyResponse>) {
                when (response.code()) {
                    200 -> {
                        listener.onCommoditySuccess(response.body()!!)
                    }
                    else -> {
                        listener.onCommodityFailure()
                    }
                }
            }

            override fun onFailure(call: Call<CommodityVarietyResponse>, t: Throwable) {
                listener.onCommodityFailure()
            }
        })
    }

    fun postBleScan(sensorData: SensorData, sensorDataRepository: SensorDataRepository) {

        val request = HashMap<String, String>()
        request["sample_id"] = sensorData.sampleId
        request["client_id"] = sensorData.clientId.toString()
        request["commodity_name"] = sensorData.commodityName
        request["moisture"] = sensorData.moisture
        request["temperature"] = sensorData.temperature
        request["truck_number"] = sensorData.truckNumber

        val call = apiService.postBleScan("Bearer " + userManager.token, request)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                when (response.code()) {
                    201 -> {
                        sensorData.isSynchronized = 1
                        Thread {
                            sensorDataRepository.updateData(sensorData)
                        }.start()
//                        listener.onPostScanSuccess()
                    }
                    else -> {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        if(jObjError.getString("error-code").equals("12063")){
                            sensorData.isSynchronized = 1
                            Thread {
                                sensorDataRepository.updateData(sensorData)
                            }.start()
                        }
//                        listener.onPostScanFailure(jObjError.getString("error-message"))
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                var str = t.message.toString()
//                listener.onPostScanFailure(str)
            }
        })

    }


    interface OnSampleBleInteractorListener {
        fun onLocationSuccess(locationList: ArrayList<LocationResponse>)
        fun onLocationFailure()
        fun onCommoditySuccess(commodityVarietyResponse: CommodityVarietyResponse)
        fun onCommodityFailure()
    }

}