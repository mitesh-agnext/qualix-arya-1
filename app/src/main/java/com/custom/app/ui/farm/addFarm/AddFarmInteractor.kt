package com.custom.app.ui.farm.addFarm

import android.content.Context
import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import com.custom.app.ui.farm.ResAddFarm
import com.custom.app.ui.farm.ResCropVariety
import com.custom.app.ui.farm.ResCrops
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddFarmInteractor(private val context: Context) {

    private val apiService = ApiClient.getClient().create(ApiInterface::class.java)

    /**Get AllC rop*/
    fun getAllCrop(token: String, mCallBack: AddFarmCallback) {
        val call = apiService.getCrops(token)
        call.enqueue(object : Callback<ResCrops> {
            override fun onFailure(call: Call<ResCrops>, t: Throwable) {
                mCallBack.getCropFailureCallback()
            }

            override fun onResponse(call: Call<ResCrops>, response: Response<ResCrops>) {
                if (response.isSuccessful) {
                    mCallBack.getCropSuccessCallback(response)
                } else {
                    if (response.code() == 401) {
                        mCallBack.tokenExpire()
                    } else {
                        mCallBack.getCropFailureCallback()
                    }
                }
            }
        })
    }

    /**Get Crop Variety*/
    fun getCropVariety(token: String, cropID: String, mCallBack: AddFarmCallback) {

        val call = apiService.getCropVariety(token, cropID)
        call.enqueue(object : Callback<ResCropVariety> {
            override fun onFailure(call: Call<ResCropVariety>, t: Throwable) {
                mCallBack.getCropVarietyFailure()
            }

            override fun onResponse(
                    call: Call<ResCropVariety>,
                    response: Response<ResCropVariety>
            ) {
                if (response.isSuccessful) {
                    mCallBack.getCropVarietySuccess(response)
                } else {
                    if (response.code() == 401) {
                        mCallBack.tokenExpire()
                    } else {
                        mCallBack.getCropVarietyFailure()
                    }
                }
            }
        })
    }

    /**Add Farm*/
    fun addFarm(token: String, data: HashMap<String, Any>, mCallBack: AddFarmCallback) {
//        val call = apiService.addFarm(token, data)
        val call = apiService.addPlot(data)
        call.enqueue(object : Callback<ResAddFarm> {
            override fun onFailure(call: Call<ResAddFarm>, t: Throwable) {
                mCallBack.addFarmFailureCallback()
            }

            override fun onResponse(call: Call<ResAddFarm>, response: Response<ResAddFarm>) {
                when (response.code()) {
                    201 -> {
                        mCallBack.addFarmSuccessCallback(response)
                    }
                    401 -> {
                        mCallBack.tokenExpire()
                    }
                    else -> {
                        mCallBack.addFarmFailureCallback()
                    }
                }
//                if (response.isSuccessful) {
//                    mCallBack.addFarmSuccessCallback(response)
//                } else {
//                    if (response.code() == 401) {
//                        mCallBack.tokenExpire()
//                    } else {
//                        mCallBack.addFarmFailureCallback()
//                    }
//                }
            }
        })
    }

    interface AddFarmCallback {
        //Crop
        fun getCropSuccessCallback(response: Response<ResCrops>)

        fun getCropFailureCallback()

        //CropVariety
        fun getCropVarietySuccess(response: Response<ResCropVariety>)

        fun getCropVarietyFailure()


        //Add farm
        fun addFarmSuccessCallback(response: Response<ResAddFarm>)

        fun addFarmFailureCallback()

        //Token
        fun tokenExpire()
    }
}