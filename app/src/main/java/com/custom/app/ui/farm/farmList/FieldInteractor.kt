package com.agnext.qualixfarmer.farmList.farmList


import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import com.custom.app.ui.farm.farmList.FarmRes
import com.custom.app.ui.farm.farmList.ResAllFarms
import com.custom.app.ui.farm.farmList.ResBasic
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FieldInteractor {

    private val apiService = ApiClient.getVmsClient().create(ApiInterface::class.java)

    //Api hit to get list of all farms for farmer
    fun getAllFarm(token: String, mCallBack: AllFarmCallBack) {
        val call = apiService.getPlots("1")
        call.enqueue(object : Callback<ArrayList<FarmRes>> {
            override fun onFailure(call: Call<ArrayList<FarmRes>>, t: Throwable) {
                mCallBack.fieldListFailure()
            }

            override fun onResponse(
                call: Call<ArrayList<FarmRes>>,
                response: Response<ArrayList<FarmRes>>
            ) {
                mCallBack.fieldListSuccess(response)
            }
        })
    }

    //Delete farm
    fun deleteFarm(token: String, farmID: String,mCallBack: AllFarmCallBack) {
        val call = apiService.deleteFarm(token,farmID)
        call.enqueue(object :Callback<ResBasic>{
            override fun onFailure(call: Call<ResBasic>, t: Throwable) {
                mCallBack.farmDeleteFailure()
            }

            override fun onResponse(call: Call<ResBasic>, response: Response<ResBasic>) {
                mCallBack.farmDeleteSuccess()

            }
        })
    }

}


interface AllFarmCallBack {
    fun fieldListSuccess(responseData: Response<ArrayList<FarmRes>>)
    fun fieldListFailure()

    fun farmDeleteSuccess()
    fun farmDeleteFailure()

}

data class FieldData(
    val fieldId: String,
    val fieldName: String,
    val farmerName: String,
    val cropSeason: String,
    val crop: String,
    val cropType: String,
    val startDate: String,
    val endDate: String,
    val addres: String,
    val District: String,
    val area: String
)