package com.custom.app.ui.payment.list

import com.custom.app.data.model.payment.PaymentHistoryRes
import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import com.custom.app.ui.createData.flcScan.season.create.CommodityRes
import com.custom.app.ui.createData.region.site.create.RegionRes
import com.custom.app.ui.device.assign.InstallationCenterRes
import com.custom.app.util.Constants
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class PaymentHistoryInteractor(val apiService: ApiInterface) {

    fun getPaymentHistoryIn(data: MutableMap<String, String>, mCallback: PaymentHistoryListener) {
        val apiService = ApiClient.getVmsClient().create(ApiInterface::class.java)
        val call = apiService.getPaymentHistory(Constants.TOKEN, data)
        call.enqueue(object : Callback<ArrayList<PaymentHistoryRes>> {
            override fun onFailure(call: Call<ArrayList<PaymentHistoryRes>>, t: Throwable) {
                mCallback.paymentHistoryFailure("Error to get scan history")
            }

            override fun onResponse(call: Call<ArrayList<PaymentHistoryRes>>, response: Response<ArrayList<PaymentHistoryRes>>) {
                when (response.code()) {
                    200 -> {
                        mCallback.paymentHistorySuccess(response.body())
                    }
                    401 -> {
                        mCallback.tokenExpire()
                    }
                    400 -> {
                        try {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            mCallback.paymentHistoryFailure(jObjError.getString("error-message"))
                        } catch (e: Exception) {
                            mCallback.paymentHistoryFailure(e.message.toString())
                        }
                    }
                    else -> {
                        mCallback.paymentHistoryFailure("Error to get scan history")
                    }
                }
            }
        })
    }

    fun getCommodity(mCallback: PaymentHistoryListener, customer_id: Int) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
            val call = apiService.getCommodity(Constants.TOKEN, "")
            call.enqueue(object : Callback<ArrayList<CommodityRes>> {
                override fun onResponse(
                        call: Call<ArrayList<CommodityRes>>,
                        response: Response<ArrayList<CommodityRes>>) {
                    when (response.code()) {
                        200 -> {
                            mCallback.commoditySuccess(response.body()!!)
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

    fun getInstallationCenters(mCallback: PaymentHistoryListener, customer_id: Int) {

        val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
        val call = apiService.getInstallationCenters(Constants.TOKEN, "", customer_id.toString(), "")

        call.enqueue(object : Callback<ArrayList<InstallationCenterRes>> {
            override fun onResponse(call: Call<ArrayList<InstallationCenterRes>>,
                                    response: Response<ArrayList<InstallationCenterRes>>) {
                when (response.code()) {
                    200 -> {
                        mCallback.installationCentersSuccess(response.body()!!)
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

    fun getRegions(customerId: Int, mCallback: PaymentHistoryListener) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
            val call = apiService.getRegion(Constants.TOKEN, customerId.toString())
            call.enqueue(object : Callback<ArrayList<RegionRes>> {
                override fun onResponse(
                        call: Call<ArrayList<RegionRes>>,
                        response: Response<ArrayList<RegionRes>>) {
                    when (response.code()) {
                        200 -> {
                            mCallback.regionSuccess(response.body()!!)
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
}

interface PaymentHistoryListener {
    fun paymentHistorySuccess(body: ArrayList<PaymentHistoryRes>?)
    fun paymentHistoryFailure(massage: String)
    fun tokenExpire()
    fun commoditySuccess(body: ArrayList<CommodityRes>)
    fun commodityFailure(msg: String)

    fun installationCentersSuccess(body: ArrayList<InstallationCenterRes>)
    fun installationCentersFailure(string: String)

    fun regionSuccess(body: ArrayList<RegionRes>)
    fun regionFailure(string: String)
}