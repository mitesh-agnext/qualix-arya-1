package com.custom.app.ui.senseNext.devicesAnalysis

import com.custom.app.data.model.senseNext.SNAnalysisRes
import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import com.custom.app.util.Constants
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SNAnalysisInteractor {
//    "IOTDEV102"
    fun getAnalysis(deviceId: String, mCallback: OnSNAnalysisListener) {
        val apiService = ApiClient.getScmClient().create(ApiInterface::class.java)
        val call = apiService.getSNAnalysis(Constants.TOKEN,deviceId)
        call.enqueue(object : Callback<ArrayList<SNAnalysisRes>> {
            override fun onFailure(call: Call<ArrayList<SNAnalysisRes>>, t: Throwable) {
                mCallback.onAnalysisFailure("Failed to get Analysis")
            }
            override fun onResponse(call: Call<ArrayList<SNAnalysisRes>>, response: Response<ArrayList<SNAnalysisRes>>) {
                when (response.code()) {
                    200 -> {
                        if (response.body()!!.size > 0)
                            mCallback.onAnalysisSuccess(response.body()!!)
                    }
                    400 -> {
                        try {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            mCallback.onAnalysisFailure(jObjError.getString("error-message"))
                        } catch (e: Exception) {
                            mCallback.onAnalysisFailure(e.message.toString())
                        }
                    }
                    401 -> {
                        mCallback.onToken()
                    }
                    else->{
                        mCallback.onAnalysisFailure("Error")
                    }
                }
            }
        })
    }
}

interface OnSNAnalysisListener {
    fun onAnalysisSuccess(body: ArrayList<SNAnalysisRes>)
    fun onAnalysisFailure(s: String)
    fun onToken()

}
