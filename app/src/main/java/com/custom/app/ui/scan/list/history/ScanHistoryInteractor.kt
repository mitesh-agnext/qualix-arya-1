package com.custom.app.ui.scan.list.history

import com.custom.app.data.model.scanhistory.ScanData
import com.custom.app.data.model.scanhistory.ScanHistoryResT
import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import com.custom.app.ui.createData.flcScan.season.create.CommodityRes
import com.custom.app.ui.createData.region.site.create.RegionRes
import com.custom.app.ui.device.add.DeviceTypeRes
import com.custom.app.ui.device.assign.InstallationCenterRes
import com.custom.app.util.Constants
import com.google.gson.JsonObject
import com.user.app.data.UserManager
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class ScanHistoryInteractor(val userManager: UserManager, val apiService: ApiInterface) {

    fun getScanHistoryIn(data: MutableMap<String, String>, mCallback: ScanHistoryListener) {
        val apiService = ApiClient.getScmClient().create(ApiInterface::class.java)
        val call = apiService.scanHistory("Bearer ${userManager.token}", data)
        call.enqueue(object : Callback<ScanHistoryResT> {
            override fun onFailure(call: Call<ScanHistoryResT>, t: Throwable) {
                mCallback.scanHistoryFailure("Error to get scan history")
            }

            override fun onResponse(call: Call<ScanHistoryResT>, response: Response<ScanHistoryResT>) {
                when (response.code()) {
                    200 -> {
                        mCallback.scanHistorySuccess(response.body()!!.scan_history_data)
                    }
                    204 -> {
                        mCallback.scanHistoryFailure("No record found")
                    }
                    401 -> {
                        mCallback.tokenExpire()
                    }
                    400 -> {
                        try {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            mCallback.scanHistoryFailure(jObjError.getString("error-message"))
                        } catch (e: Exception) {
                            mCallback.scanHistoryFailure(e.message.toString())
                        }
                    }
                    else -> {
                        mCallback.scanHistoryFailure("Error to get scan history")
                    }
                }
            }
        })
    }

    fun getCommodity(mCallback: ScanHistoryListener, customer_id: Int) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
            val call = apiService.getCommodity("Bearer ${userManager.token}", "")
            call.enqueue(object : Callback<ArrayList<CommodityRes>> {
                override fun onResponse(
                        call: Call<ArrayList<CommodityRes>>,
                        response: Response<ArrayList<CommodityRes>>) {
                    when (response.code()) {
                        200 -> {
                            mCallback.commoditySuccess(response.body()!!)
                        }
                        204 -> {
                            mCallback.commodityFailure("No record found")
                        }
                        400 -> {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                mCallback.commodityFailure(jObjError.getString("error-message"))
                            } catch (e: Exception) {
                                mCallback.commodityFailure(e.message.toString())
                            }
                        }
                        401 -> {
                            mCallback.tokenExpire()
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<CommodityRes>>, t: Throwable) {
                    mCallback.commodityFailure(t.message.toString())
                }
            })
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    fun getInstallationCenters(mCallback: ScanHistoryListener, customer_id: Int) {
        val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
        val call = apiService.getInstallationCenters("Bearer ${userManager.token}", "", "", "")
        call.enqueue(object : Callback<ArrayList<InstallationCenterRes>> {
            override fun onResponse(call: Call<ArrayList<InstallationCenterRes>>,
                                    response: Response<ArrayList<InstallationCenterRes>>) {
                when (response.code()) {
                    200 -> {
                        mCallback.installationCentersSuccess(response.body()!!)
                    }
                    204 -> {
                        mCallback.installationCentersFailure("No record found")
                    }
                    400 -> {
                        try {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            mCallback.installationCentersFailure(jObjError.getString("error-message"))
                        } catch (e: Exception) {
                            mCallback.installationCentersFailure(e.message.toString())
                        }
                    }
                    401 -> {
                        mCallback.tokenExpire()
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<InstallationCenterRes>>, t: Throwable) {
                mCallback.installationCentersFailure("Error")
            }
        })
    }

    fun getRegions(customerId: Int, mCallback: ScanHistoryListener) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
            val call = apiService.getRegion("Bearer ${userManager.token}", "")
            call.enqueue(object : Callback<ArrayList<RegionRes>> {
                override fun onResponse(
                        call: Call<ArrayList<RegionRes>>,
                        response: Response<ArrayList<RegionRes>>) {
                    when (response.code()) {
                        200 -> {
                            mCallback.regionSuccess(response.body()!!)
                        }
                        204 -> {
                            mCallback.regionFailure("No record found")
                        }
                        400 -> {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                mCallback.regionFailure(jObjError.getString("error-message"))
                            } catch (e: Exception) {
                                mCallback.regionFailure(e.message.toString())
                            }
                        }
                        401 -> {
                            mCallback.tokenExpire()
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<RegionRes>>, t: Throwable) {
                    mCallback.regionFailure(t.message.toString())
                }
            })
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    fun getDevicesType(mCallback: ScanHistoryListener) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
            val call = apiService.getDeviceType("Bearer ${userManager.token}")
            call.enqueue(object : Callback<ArrayList<DeviceTypeRes>> {
                override fun onResponse(
                        call: Call<ArrayList<DeviceTypeRes>>,
                        response: Response<ArrayList<DeviceTypeRes>>) {
                    when (response.code()) {
                        200 -> {
                            mCallback.deviceTypeSuccess(response.body()!!)
                        }
                        204 -> {
                            mCallback.deviceTypeFailure("No record found")
                        }
                        400 -> {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                mCallback.deviceTypeFailure(jObjError.getString("error-message"))
                            } catch (e: Exception) {
                                mCallback.deviceTypeFailure(e.message.toString())
                            }
                        }
                        401 -> {
                            mCallback.tokenExpire()
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<DeviceTypeRes>>, t: Throwable) {
                    mCallback.deviceTypeFailure(t.message.toString())
                }
            })
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    fun approveReject(scanId: Int, status: Int, note: String, listener: ScanHistoryListener) {
        val options = JsonObject()
        options.addProperty("approval", status)
        options.addProperty("scan_id", scanId)
        options.addProperty("message", note)
        approveRejectPost(listener, options)
    }

    private fun approveRejectPost(listener: ScanHistoryListener, options: JsonObject) {
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

interface ScanHistoryListener {
    fun scanHistorySuccess(body: ArrayList<ScanData>?)
    fun scanHistoryFailure(massage: String)
    fun commoditySuccess(body: ArrayList<CommodityRes>)
    fun commodityFailure(msg: String)
    fun installationCentersSuccess(body: ArrayList<InstallationCenterRes>)
    fun installationCentersFailure(string: String)
    fun regionSuccess(body: ArrayList<RegionRes>)
    fun regionFailure(string: String)
    fun approvalSuccess(body: ResponseBody)
    fun approvalFailure(string: String)
    fun deviceTypeSuccess(body: ArrayList<DeviceTypeRes>)
    fun deviceTypeFailure(string: String)
    fun tokenExpire()
}