package com.custom.app.ui.sampleBleResult

import com.custom.app.data.model.bleScan.BleScanResponse
import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import com.user.app.data.UserManager
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SampleBleResultInteractor (val userManager: UserManager)
{
    val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
    fun postBleScan( request: HashMap<String, String>,listener: OnSampleBleResultListener) {
        val call = apiService.postBleScan("Bearer " + userManager.token,request)
        call.enqueue(object:Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                when (response.code()) {
                    201 -> {
                        listener.onPostScanSuccess()
                    }
                    else -> {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        listener.onPostScanFailure(jObjError.getString("error-message"))
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                var str = t.message.toString()
                listener.onPostScanFailure(str)
            }
        })

    }

    interface OnSampleBleResultListener {
        fun onPostScanSuccess()
        fun onPostScanFailure(msg:String)
    }

}