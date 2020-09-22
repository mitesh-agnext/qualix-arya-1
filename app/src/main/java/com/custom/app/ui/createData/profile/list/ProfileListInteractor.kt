package com.custom.app.ui.createData.profile.list

import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import com.custom.app.util.Constants
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class ProfileListInteractor {

    interface ListProfileInteractorCallback {
        fun allProfileApiSuccess(body: ArrayList<ProfileListRes>)
        fun allProfileApiError(msg: String)
        fun deleteProfileSuccess(deletedPostion: Int)
        fun deleteProfileFailure(msg: String)
    }

    fun allProfile(customerId: Int, listener: ListProfileInteractorCallback) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
            val call = apiService.getProfile(Constants.TOKEN, customerId)
            call.enqueue(object : Callback<ArrayList<ProfileListRes>> {
                override fun onResponse(
                        call: Call<ArrayList<ProfileListRes>>,
                        response: Response<ArrayList<ProfileListRes>>) {
                    when {
                        response.isSuccessful -> listener.allProfileApiSuccess(response.body()!!)
                        else -> {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            when {
                                else -> listener.allProfileApiError(jObjError.getString("error-message"))
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<ProfileListRes>>, t: Throwable) {
                    listener.allProfileApiError(t.message.toString())
                }
            })

        } catch (e: Exception) {
            Timber.e(e);
        }
    }

    fun deleteProfile(listener: ListProfileInteractorCallback, profileId: Int, deletedPostion: Int) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)

            val call = apiService.deleteProfile(Constants.TOKEN, profileId)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>) {

                    when {
                        response.isSuccessful -> listener.deleteProfileSuccess(deletedPostion)
                        else -> {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            when {
                                else -> listener.deleteProfileFailure(jObjError.getString("error-message"))
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    listener.deleteProfileFailure(t.message.toString())
                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}