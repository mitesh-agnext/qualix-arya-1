package com.custom.app.ui.sampleBleResult

import com.custom.app.data.model.bleScan.BleScanResponse
import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import com.user.app.data.UserManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SampleBleResultInteractor (val userManager: UserManager)
{
    val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
    fun postBleScan( request: HashMap<String, Any>,listener: OnSampleBleResultListener) {
        val call = apiService.postBleScan("Bearer ${userManager.token}",request)
        call.enqueue(object:Callback<BleScanResponse>{
            override fun onResponse(call: Call<BleScanResponse>, response: Response<BleScanResponse>) {
                when (response.code()) {
                    201 -> {
                        listener.onPostScanSuccess()
                    }
                    else -> {
                        listener.onPostScanFailure()
                    }
                }
            }

            override fun onFailure(call: Call<BleScanResponse>, t: Throwable) {
                listener.onPostScanFailure()
            }
        })

    }

    interface OnSampleBleResultListener {
        fun onPostScanSuccess()
        fun onPostScanFailure()
    }

}