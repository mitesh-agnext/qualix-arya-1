package com.custom.app.ui.createData.profileType.list

import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import com.custom.app.util.Constants
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class ProfileTypeListInteractor {

    interface ListProfileTypeInteractorCallback {
        fun allProfileTypeApiSuccess(body: ArrayList<ProfileTypeListRes>)
        fun allProfileTypeApiError(msg: String)
        fun deleteProfileTypeSuccess(deletedPostion: Int)
        fun deleteProfileTypeFailure(msg: String)
    }

    fun allProfileType(customerId: Int, listener: ListProfileTypeInteractorCallback) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)

            val call = apiService.getProfileType(Constants.TOKEN,customerId)
            call.enqueue(object : Callback<ArrayList<ProfileTypeListRes>> {
                override fun onResponse(
                        call: Call<ArrayList<ProfileTypeListRes>>,
                        response: Response<ArrayList<ProfileTypeListRes>>) {

                    when {
                        response.isSuccessful -> listener.allProfileTypeApiSuccess(response.body()!!)
                        else -> {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            when {
                                else -> listener.allProfileTypeApiError(jObjError.getString("error-message"))
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<ProfileTypeListRes>>, t: Throwable) {
                    listener.allProfileTypeApiError(t.message.toString())
                }
            })

        } catch (e: Exception) {
            Timber.e(e);
        }
    }

    fun deleteProfileType(listener: ListProfileTypeInteractorCallback, profileTypeId: Int, deletedPostion: Int) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)

            val call = apiService.deleteProfileType(Constants.TOKEN,profileTypeId)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>) {

                    when {
                        response.isSuccessful -> listener.deleteProfileTypeSuccess(deletedPostion)
                        else -> {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            when {
                                else -> listener.deleteProfileTypeFailure(jObjError.getString("error-message"))
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    listener.deleteProfileTypeFailure(t.message.toString())
                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}