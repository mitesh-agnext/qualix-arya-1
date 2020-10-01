package com.custom.app.ui.home

import com.custom.app.data.model.count.customer.TotalCustomerRes
import com.custom.app.data.model.count.device.TotalDeviceRes
import com.custom.app.data.model.count.order.PurchaseOrderRes
import com.custom.app.data.model.count.user.TotalUserRes
import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import com.custom.app.network.RestService
import com.custom.app.util.Constants
import com.google.gson.JsonObject
import com.user.app.data.UserManager
import io.reactivex.Single
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class HomeInteractor(val userManager: UserManager, val restService: RestService) {

    fun getTotalDevices(): Single<TotalDeviceRes> {
        return restService.totalDevices()
                .map(HomeParser::device)
    }

    fun getUnassignedDevices(): Single<TotalDeviceRes> {
        return restService.unassignedDevices()
                .map(HomeParser::device)
    }

    fun getTotalCustomers(): Single<TotalCustomerRes> {
        return restService.totalCustomers()
                .map(HomeParser::customer)
    }

    fun getTotalUsers(): Single<TotalUserRes> {
        return restService.totalUsers()
                .map(HomeParser::user)
    }

    fun getPurchaseOrders(): Single<PurchaseOrderRes> {
        return restService.purchaseOrders()
                .map(HomeParser::order)
    }

    fun allSubscribedDevices(listener: DeviceCallback) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
            val call = apiService.getSubscribedDevices("Bearer ${userManager.token}")

            call.enqueue(object : Callback<SubscribedDeviceRes> {
                override fun onResponse(call: Call<SubscribedDeviceRes>, response: Response<SubscribedDeviceRes>) {
                    when {
                        response.isSuccessful -> {
                            if (response.code()==204){
                                listener.allDevicesApiError("No record found")
                            }
                            else {
                                listener.allDevicesApiSuccess(response.body()!!)
                            }
                        }
                        else -> {
                            when {
                                else -> {
                                    val jObjError = JSONObject(response.errorBody()!!.string())
                                    listener.allDevicesApiError(jObjError.getString("error-message"))
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<SubscribedDeviceRes>, t: Throwable) {
                    listener.allDevicesApiError(t.message.toString())
                }
            })

        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    fun approveReject(scanId: Int, status: Int, listener: approval) {
        val options = JsonObject()
        options.addProperty("approval", status)
        options.addProperty("scan_id", scanId)

        approveRejectPost(listener, options)
    }

    private fun approveRejectPost(listener: approval, options: JsonObject) {

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
                        listener.approvalFailure(jObjError.getString("error-message"))
                    } else {
                        listener.approvalFailure(jObjError.getString("error-message"))
                    }
                }
            }
        })
    }

    interface DeviceCallback {

        fun allDevicesApiSuccess(body: SubscribedDeviceRes)
        fun allDevicesApiError(msg: String)

    }
    interface approval {
        fun approvalSuccess(body: ResponseBody)
        fun approvalFailure(string: String)
    }
}