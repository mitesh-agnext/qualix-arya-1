package com.custom.app.ui.createData.foodType.list

import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import com.custom.app.util.Constants
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class FoodTypeListInteractor {

    interface ListFoodTypeInteractorCallback {
        fun allFoodTypeApiSuccess(body: ArrayList<FoodTypeListRes>)
        fun allFoodTypeApiError(msg: String)
        fun deleteFoodTypeSuccess(deletedPostion: Int)
        fun deleteFoodTypeFailure(msg: String)
    }

    fun allFoodType(customerId: Int, listener: ListFoodTypeInteractorCallback) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)

            val call = apiService.getFoodType(Constants.TOKEN, customerId)
            call.enqueue(object : Callback<ArrayList<FoodTypeListRes>> {
                override fun onResponse(
                        call: Call<ArrayList<FoodTypeListRes>>,
                        response: Response<ArrayList<FoodTypeListRes>>) {

                    when {
                        response.isSuccessful -> listener.allFoodTypeApiSuccess(response.body()!!)
                        else -> {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            when {
                                else -> listener.allFoodTypeApiError(jObjError.getString("error-message"))
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<FoodTypeListRes>>, t: Throwable) {
                    listener.allFoodTypeApiError(t.message.toString())
                }
            })

        } catch (e: Exception) {
            Timber.e(e);
        }
    }

    fun deleteFoodType(listener: ListFoodTypeInteractorCallback, foodTypeId: Int, deletedPostion: Int) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)

            val call = apiService.deleteFoodType(Constants.TOKEN, foodTypeId)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>) {

                    when {
                        response.isSuccessful -> listener.deleteFoodTypeSuccess(deletedPostion)
                        else -> {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            when {
                                else -> listener.deleteFoodTypeFailure(jObjError.getString("error-message"))
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    listener.deleteFoodTypeFailure(t.message.toString())
                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}