package com.custom.app.ui.createData.instlCenter

import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import com.custom.app.ui.createData.instlCenter.createInstallationCenter.InstallationCenterTypeRes
import com.custom.app.ui.device.assign.InstallationCenterRes
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

class InstallationCenterInteractor(val userManager: UserManager) {

    fun addCenter(installation_CenterName: String, installation_CenterTypeId: Int, site_id: Int, region_id: Int, customerId: Int, userId: Int, note: String,
                  listener: CreateInstallationCenterListener) {
        when {
            installation_CenterName.isEmpty() -> {
                listener.onCenterNameEmpty()
            }
            else -> {

                val options = JsonObject()
                options.addProperty("inst_center_name", installation_CenterName)
                options.addProperty("commercial_location_type_id", installation_CenterTypeId)
                options.addProperty("customer_id", customerId)
                options.addProperty("notes", note)
//                options.addProperty("site_id", site_id)
//                options.addProperty("region_id", region_id)
                options.addProperty("user_id", userId)

                createInstallationCenter(listener, options)
            }
        }
    }

    private fun createInstallationCenter(listener: CreateInstallationCenterListener, options: JsonObject) {

        val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
        val call = apiService.createInstallationCenter("Bearer ${userManager.token}", options)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                listener.onInstallationCenterFailure(t!!.message.toString())
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    listener.onInstallationCenterSuccess(response.body()!!)
                } else {
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    if (response.code() == 401) {
                        listener.onInstallationCenterFailure(jObjError.getString("error-message"))
                    } else {
                        listener.onInstallationCenterFailure(jObjError.getString("error-message"))
                    }
                }
            }
        })
    }

    fun getInstallationCenterType(listener: CreateInstallationCenterListener) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
            val call = apiService.getInstallationCenterType("Bearer ${userManager.token}")
            call.enqueue(object : Callback<ArrayList<InstallationCenterTypeRes>> {
                override fun onResponse(
                        call: Call<ArrayList<InstallationCenterTypeRes>>,
                        response: Response<ArrayList<InstallationCenterTypeRes>>) {

                    when {
                        response.isSuccessful -> {
                            if (response.code()==204){
                                listener.onGetInstallationCenterTypeError("No record found")
                            }
                            else {
                                listener.onGetInstallationCenterTypeSuccess(response.body()!!)
                            }
                        }
                        else -> {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            when {
                                response.code() == 401 -> {
                                    listener.onGetInstallationCenterTypeError(jObjError.getString("error-message"))
                                }
                                else -> {
                                    listener.onGetInstallationCenterTypeError(jObjError.getString("error-message"))
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<InstallationCenterTypeRes>>, t: Throwable) {
                    listener.onGetInstallationCenterTypeError(t.message.toString())
                }
            })
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    fun getUser(listener: CreateInstallationCenterListener, customer_id: Int) {
        try {
            val apiService = ApiClient.getClient().create(ApiInterface::class.java)
            val call = apiService.getUsers("Bearer ${userManager.token}", customer_id.toString())
            call.enqueue(object : Callback<ArrayList<UserDataRes>> {
                override fun onResponse(
                        call: Call<ArrayList<UserDataRes>>,
                        response: Response<ArrayList<UserDataRes>>) {

                    when {
                        response.isSuccessful -> {
                            if (response.code()==204){
                                listener.onGetUserEmpty("No record found")
                            }
                            else {
                                listener.onGetUserSuccess(response.body()!!)
                            }
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

                override fun onFailure(call: Call<ArrayList<UserDataRes>>, t: Throwable) {
                    listener.onGetUserFailure(t.message.toString())
                }
            })
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    fun allCenter(listener: ListCenterInteractorCallback, keyword: String, customer_id : Int, region_id : String) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)

            val call = apiService.getInstallationCenters("Bearer ${userManager.token}",keyword, customer_id.toString(), region_id)
            call.enqueue(object : Callback<ArrayList<InstallationCenterRes>> {
                override fun onResponse(
                        call: Call<ArrayList<InstallationCenterRes>>,
                        response: Response<ArrayList<InstallationCenterRes>>) {

                    when {
                        response.isSuccessful -> {
                            if (response.code()==204){
                                listener.allCentersApiEmpty("No record found")
                            }
                            else {
                                listener.allCentersApiSuccess(response.body()!!)
                            }
                        }
                        else -> {
                            val jObjError = JSONObject(response.errorBody()!!.string())

                            when {
                                else -> {
                                    listener.allCentersApiError(jObjError.getString("error-message"))
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<InstallationCenterRes>>, t: Throwable) {
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

            val call = apiService.deleteInstallationCenter("Bearer ${userManager.token}",centerId)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>) {

                    when {
                        response.isSuccessful -> listener.deleteCenterSuccess(deletedPostion)
                        else -> {
                            when {
                                else -> {
                                    val jObjError = JSONObject(response.errorBody()!!.string())
                                    listener.deleteCenterFailure(jObjError.getString("error-message"))
                                }
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

    fun updateCenter(cold_store_id: Int, installation_CenterName: String, center_type_id: Int, customer_id: Int, site_id: Int, region_id: Int, userId: Int, note: String, listener: UpdateCenterListener) {

        val options = JsonObject()

        options.addProperty("installation_center_id", cold_store_id)
        options.addProperty("inst_center_name", installation_CenterName)
        options.addProperty("commercial_location_type_id", center_type_id)
        options.addProperty("region_id", region_id)
        options.addProperty("notes", note)
        options.addProperty("site_id", site_id)
        options.addProperty("user_id", userId)
        options.addProperty("customer_id", customer_id)

        updateInstallationCenter(listener, options, cold_store_id)
    }

    private fun updateInstallationCenter(listener: UpdateCenterListener, options: JsonObject, centerId: Int) {

        val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
        val call = apiService.updateInstallationCenter("Bearer ${userManager.token}", options, centerId)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                listener.onUpdateCenterFailure(t!!.message.toString())
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    listener.onUpdateCenterSuccess(response.body()!!)
                } else {
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    if (response.code() == 401) {
                        listener.onUpdateCenterFailure(jObjError.getString("error-message"))
                    } else {
                        listener.onUpdateCenterFailure(jObjError.getString("error-message"))
                    }
                }
            }
        })
    }

}

interface CreateInstallationCenterListener {

    fun onInstallationCenterSuccess(body: ResponseBody)
    fun onInstallationCenterFailure(msg: String)

    fun onCenterNameEmpty()

    fun onGetInstallationCenterTypeSuccess(body: ArrayList<InstallationCenterTypeRes>)
    fun onGetInstallationCenterTypeError(msg: String)
    fun onGetInstallationCenterTypeEmpty(msg: String)

    fun onGetUserSuccess(body: ArrayList<UserDataRes>)
    fun onGetUserFailure(msg: String?)
    fun onGetUserEmpty(msg: String?)

}

interface ListCenterInteractorCallback {
    fun allCentersApiSuccess(body: ArrayList<InstallationCenterRes>)
    fun allCentersApiError(msg: String)
    fun allCentersApiEmpty(msg: String)

    fun deleteCenterSuccess(deletedPostion: Int)
    fun deleteCenterFailure(msg: String)
}

interface UpdateCenterListener {

    fun onUpdateCenterSuccess(body: ResponseBody)
    fun onUpdateCenterFailure(msg: String)
}