package com.custom.app.ui.scan.list.detail

import com.custom.app.data.model.scanhistory.ScanData
import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import com.custom.app.ui.sample.ScanDetailRes
import com.custom.app.util.Constants
import com.google.gson.JsonObject
import com.user.app.data.UserManager
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScanDetailInteractor(val userManager: UserManager, val apiService: ApiInterface) {

    fun approveReject(scanId: Int, status: Int, message: String, listener: ScanDetailListener) {
        val options = JsonObject()
        options.addProperty("approval", status)
        options.addProperty("scan_id", scanId)
        options.addProperty("message", message)
        approveRejectPost(listener, options)
    }

    private fun approveRejectPost(listener: ScanDetailListener, options: JsonObject) {
        val apiService = ApiClient.getScmClient().create(ApiInterface::class.java)
        val call = apiService.approveReject("Bearer ${userManager.token}", options)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                listener.approvalFailure(t!!.message.toString())
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    listener.approvalSuccess(response.body()!!)
                } else {
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    if (response.code() == 401) {
                        listener.tokenExpire()
                    } else {
                        listener.approvalFailure(jObjError.getString("error-message"))
                    }
                }
            }
        })
    }


    fun fetchScanDetail(scanId: String, listener: ScanDetailListener) {
        val apiService = ApiClient.getScmClient().create(ApiInterface::class.java)

        val call = apiService.getScanDetail2("Bearer ${userManager.token}", scanId)
        call.enqueue(object : Callback<ScanData> {
            override fun onResponse(call: Call<ScanData>, response: Response<ScanData>) {

                when {
                    response.isSuccessful -> {
                        if (response.code() == 204) {
                            listener.fetchScanDetailFailure("No record found")
                        } else {
                            listener.fetchScanDetailSuccess(response.body()!!)
                        }
                    }
                    else -> {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        when {
                            else -> listener.fetchScanDetailFailure(jObjError.getString("error-message"))
                        }
                    }
                }
            }
            //ScanDetailRes
            override fun onFailure(call: Call<ScanData>, t: Throwable) {
                listener.fetchScanDetailFailure(t.message.toString())
            }
        })
    }
}

interface ScanDetailListener {
    fun approvalSuccess(body: ResponseBody)
    fun approvalFailure(string: String)
    fun fetchScanDetailSuccess(response:ScanData)
    fun fetchScanDetailFailure(error: String)
    fun tokenExpire()
}