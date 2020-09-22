package com.custom.app.ui.createData.coldstore.createColdstore

import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import com.custom.app.ui.createData.coldstore.coldstoreList.ColdstoreRes
import com.custom.app.ui.user.list.UserDataRes
import com.google.gson.JsonObject
import com.user.app.data.UserManager
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.*

class ColdstoreInteractor(val userManager: UserManager) {

    fun addCenter(installation_CenterName: String, profile_id: Int, profile_type_id: Int,
                  profile_food_type_id: Int, site_id: Int, region_id: Int, customerId: Int, userId: Int, note: String, listener: CreateColdstoreListener) {
        when {
            installation_CenterName.isEmpty() -> {
                listener.onCenterNameEmpty()
            }
            else -> {

                val options = JsonObject()
                options.addProperty("inst_center_name", installation_CenterName)
                options.addProperty("profile_id", profile_id)
                options.addProperty("profile_type_id", profile_type_id)
                options.addProperty("profile_food_type_id", profile_food_type_id)
                options.addProperty("customer_id", customerId)
                options.addProperty("notes", note)
                options.addProperty("site_id", site_id)
                options.addProperty("region_id", region_id)
                options.addProperty("user_id", userId)

                createColdstore(listener, options)
            }
        }
    }

    private fun createColdstore(listener: CreateColdstoreListener, options: JsonObject) {

        val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
        val call = apiService.createColdstore("Bearer ${userManager.token}", options)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                listener.onColdstoreFailure()
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    listener.onColdstoreSuccess(response.body()!!)
                } else {
                    if (response.code() == 401) {
                        listener.onColdstoreFailure()
                    } else {
                        listener.onColdstoreFailure()
                    }
                }
            }
        })
    }

    fun getUser(listener: CreateColdstoreListener, customer_id: Int) {
        try {
            val apiService = ApiClient.getClient().create(ApiInterface::class.java)
            val call = apiService.getUsers("Bearer ${userManager.token}", customer_id.toString())
            call.enqueue(object : Callback<java.util.ArrayList<UserDataRes>> {
                override fun onResponse(
                        call: Call<java.util.ArrayList<UserDataRes>>,
                        response: Response<ArrayList<UserDataRes>>) {

                    when {
                        response.isSuccessful -> {
                            listener.onGetUserSuccess(response.body()!!)
                        }
                        else -> {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            when {
                                response.code() == 401 -> listener.onGetUserFailure(jObjError.getString("error-message"))
                                else -> listener.onGetUserFailure(jObjError.getString("error-message"))
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<java.util.ArrayList<UserDataRes>>, t: Throwable) {
                    listener.onGetUserFailure(t.message.toString())
                }
            })
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    fun allCenter(listener: ListCenterInteractorCallback, keyword: String, customer_id : Int) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)

            val call = apiService.getColdstores("Bearer ${userManager.token}",keyword, customer_id)
            call.enqueue(object : Callback<ArrayList<ColdstoreRes>> {
                override fun onResponse(
                        call: Call<ArrayList<ColdstoreRes>>,
                        response: Response<ArrayList<ColdstoreRes>>) {

                    when {
                        response.isSuccessful -> listener.allCentersApiSuccess(response.body()!!)
                        else -> {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            when {
                                else -> listener.allCentersApiError(jObjError.getString("error-message"))
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<ColdstoreRes>>, t: Throwable) {
                    listener.allCentersApiError(t.message.toString())
                }
            })

        } catch (e: Exception) {
            Timber.e(e);
        }
    }

    fun deleteCenter(listener: ListCenterInteractorCallback, centerId: String, deletedPostion: Int) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)

            val call = apiService.deleteColdstore("Bearer ${userManager.token}",centerId)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>) {

                    when {
                        response.isSuccessful -> listener.deleteCenterSuccess(deletedPostion)
                        else -> {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            when {
                                else -> listener.deleteCenterFailure(jObjError.getString("error-message"))
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    listener.deleteCenterFailure(t.message.toString())
                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateCenter(cold_store_id: Int, installation_CenterName: String, customer_id: Int, profile_id: Int, profile_type_id: Int,
                     profile_food_type_id: Int, site_id: Int, region_id: Int, userId: Int, note: String, listener: UpdateCenterListener) {

        val options = JsonObject()

        options.addProperty("cold_store_id", cold_store_id)
        options.addProperty("inst_center_name", installation_CenterName)
        options.addProperty("region_id", region_id)
        options.addProperty("profile_id", profile_id)
        options.addProperty("profile_type_id", profile_type_id)
        options.addProperty("profile_food_type_id", profile_food_type_id)
        options.addProperty("notes", note)
        options.addProperty("site_id", site_id)
        options.addProperty("user_id", userId)
        options.addProperty("customer_id", customer_id)

        updateInstallationCenter(listener, options, cold_store_id)
    }

    private fun updateInstallationCenter(listener: UpdateCenterListener, options: JsonObject, centerId: Int) {

        val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
        val call = apiService.updateColdstore("Bearer ${userManager.token}",options, centerId)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                listener.onUpdateCenterFailure()
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    listener.onUpdateCenterSuccess(response.body()!!)
                } else {
                    if (response.code() == 401) {
                        listener.onUpdateCenterFailure()
                    } else {
                        listener.onUpdateCenterFailure()
                    }
                }
            }
        })
    }

}

interface CreateColdstoreListener {

    fun onColdstoreSuccess(body: ResponseBody)
    fun onColdstoreFailure()

    fun onCenterNameEmpty()

    fun onGetUserSuccess(body: ArrayList<UserDataRes>)
    fun onGetUserFailure(msg: String?)
}

interface ListCenterInteractorCallback {
    fun allCentersApiSuccess(body: ArrayList<ColdstoreRes>)
    fun allCentersApiError(msg: String)

    fun deleteCenterSuccess(deletedPostion: Int)
    fun deleteCenterFailure(msg: String)
}

interface UpdateCenterListener {

    fun onUpdateCenterSuccess(body: ResponseBody)
    fun onUpdateCenterFailure()
}
