package com.custom.app.ui.senseNext.list

import com.custom.app.data.model.senseNext.SNDeviceRes
import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import com.custom.app.ui.createData.profile.list.ProfileListRes
import com.custom.app.ui.createData.profileType.list.ProfileTypeListRes
import com.custom.app.ui.createData.region.site.create.RegionRes
import com.custom.app.util.Constants
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class SNDeviceInteractor(val apiService: ApiInterface) {

    fun list(mCallback: SNDeviceListListener, data: HashMap<String, String>) {
        val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
        val call = apiService.getSNColdStore(Constants.TOKEN, data)
        call.enqueue(object : Callback<ArrayList<SNDeviceRes>> {
            override fun onFailure(call: Call<ArrayList<SNDeviceRes>>, t: Throwable) {
                mCallback.onError("Failed to fetch list")
            }

            override fun onResponse(call: Call<ArrayList<SNDeviceRes>>, response: Response<ArrayList<SNDeviceRes>>) {
                when (response.code()) {
                    200 -> {
                        mCallback.onSNDeviceListSuccess(response.body()!!)
                    }
                    400 -> {
                        try {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            mCallback.onError(jObjError.getString("error-message"))
                        } catch (e: Exception) {
                            mCallback.onError(e.message.toString())
                        }
                    }
                    401 -> {
                        mCallback.onTokenExpire()
                    }
                }
            }
        })
    }

    fun allRegion(customerId: Int, listener: SNDeviceListListener) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)

            val call = apiService.getRegion(Constants.TOKEN, customerId.toString())
            call.enqueue(object : Callback<ArrayList<RegionRes>> {
                override fun onResponse(
                        call: Call<ArrayList<RegionRes>>,
                        response: Response<ArrayList<RegionRes>>) {

                    when {
                        response.isSuccessful -> listener.onAllRegionSuccess(response.body()!!)
                        else -> {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            when {
                                else -> listener.onAllRegionFailure(jObjError.getString("error-message"))
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<RegionRes>>, t: Throwable) {
                    listener.onAllRegionFailure(t.message.toString())
                }
            })

        } catch (e: Exception) {
            Timber.e(e);
        }
    }

    fun allProfileType(customerId: Int, listener: SNDeviceListListener) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)

            val call = apiService.getProfileType(Constants.TOKEN, customerId)
            call.enqueue(object : Callback<ArrayList<ProfileTypeListRes>> {
                override fun onResponse(
                        call: Call<ArrayList<ProfileTypeListRes>>,
                        response: Response<ArrayList<ProfileTypeListRes>>) {

                    when {
                        response.isSuccessful -> listener.onAllProfileTypeSuccess(response.body()!!)
                        else -> {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            when {
                                else -> listener.onAllProfileTypeFailure(jObjError.getString("error-message"))
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<ProfileTypeListRes>>, t: Throwable) {
                    listener.onAllProfileTypeFailure(t.message.toString())
                }
            })

        } catch (e: Exception) {
            Timber.e(e);
        }
    }

    fun allProfile(customerId: Int, listener: SNDeviceListListener) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
            val call = apiService.getProfile(Constants.TOKEN, customerId)
            call.enqueue(object : Callback<ArrayList<ProfileListRes>> {
                override fun onResponse(
                        call: Call<ArrayList<ProfileListRes>>,
                        response: Response<ArrayList<ProfileListRes>>) {
                    when {
                        response.isSuccessful -> listener.onAllProfileSuccess(response.body()!!)
                        else -> {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            when {
                                else -> listener.onAllProfileFailure(jObjError.getString("error-message"))
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<ProfileListRes>>, t: Throwable) {
                    listener.onAllProfileFailure(t.message.toString())
                }
            })

        } catch (e: Exception) {
            Timber.e(e);
        }
    }
}

interface SNDeviceListListener {

    fun onAllRegionSuccess(body: ArrayList<RegionRes>)
    fun onAllRegionFailure(toString: String)
    fun onAllProfileTypeSuccess(body: ArrayList<ProfileTypeListRes>)
    fun onAllProfileTypeFailure(toString: String)
    fun onAllProfileSuccess(body: ArrayList<ProfileListRes>)
    fun onAllProfileFailure(toString: String)
    fun onSNDeviceListSuccess(devices: ArrayList<SNDeviceRes>)
    fun onSNDeviceDeleteUserSuccess()
    fun onError(msg: String?)
    fun onTokenExpire()
}