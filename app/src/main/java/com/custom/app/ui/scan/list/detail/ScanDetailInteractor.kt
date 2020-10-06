package com.custom.app.ui.scan.list.detail

import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import com.custom.app.util.Constants
import com.google.gson.JsonObject
import com.user.app.data.UserManager
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScanDetailInteractor(val userManager: UserManager, val apiService: ApiInterface) {

    fun approveReject(scanId: Int, status: Int, listener: ScanDetailListener) {
        val options = JsonObject()
        options.addProperty("approval", status)
        options.addProperty("scan_id", scanId)
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
}

interface ScanDetailListener {
    fun approvalSuccess(body: ResponseBody)
    fun approvalFailure(string: String)
    fun tokenExpire()
}