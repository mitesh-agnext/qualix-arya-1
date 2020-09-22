package com.custom.app.ui.address

import com.custom.app.data.model.country.CityRes
import com.custom.app.data.model.country.CountryRes
import com.custom.app.data.model.country.StateRes
import com.custom.app.network.ApiInterface
import com.user.app.data.UserManager
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddressInteractor(val apiService: ApiInterface,val userManager: UserManager) {

    val countryErrorMsg = "Error to get countries"
    val stateErrorMsg = "Error to get states"
    val cityErrorMsg = "Error to get cities"

    fun getCountry(mCallback: AddressListener) {
        val call = apiService.getCountry("Bearer ${userManager.token}")
        call.enqueue(object : Callback<ArrayList<CountryRes>> {
            override fun onFailure(call: Call<ArrayList<CountryRes>>, t: Throwable) {
                mCallback.onCountryFailure(countryErrorMsg)
            }

            override fun onResponse(call: Call<ArrayList<CountryRes>>, response: Response<ArrayList<CountryRes>>) {
                when (response.code()) {
                    200 -> {
                        if (response.body()!!.size > 0)
                            mCallback.onCountrySuccess(response.body())
                        else
                            mCallback.onCountryFailure(countryErrorMsg)
                    }
                    401 -> {
                        mCallback.onTokenExpire()
                    }
                    400 -> {
                        try {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            mCallback.onCountryFailure(jObjError.getString("error-message"))
                        } catch (e: Exception) {
                            mCallback.onCountryFailure(e.message.toString())
                        }
                    }
                    else -> {
                        mCallback.onCountryFailure(countryErrorMsg)
                    }
                }
            }
        })
    }

    fun getState(countryID: String, mCallback: AddressListener) {
        val call = apiService.getState("Bearer ${userManager.token}", countryID)
        call.enqueue(object : Callback<ArrayList<StateRes>> {
            override fun onFailure(call: Call<ArrayList<StateRes>>, t: Throwable) {
                mCallback.onStateFailure(stateErrorMsg)
            }

            override fun onResponse(call: Call<ArrayList<StateRes>>, response: Response<ArrayList<StateRes>>) {
                when (response.code()) {
                    200 -> {
                        if (response.body()!!.size > 0)
                            mCallback.onStateSuccess(response.body()!!)
                        else
                            mCallback.onStateFailure(stateErrorMsg)
                    }
                    401 -> {
                        mCallback.onTokenExpire()
                    }
                    400 -> {
                        try {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            mCallback.onStateFailure(jObjError.getString("error-message"))
                        } catch (e: Exception) {
                            mCallback.onStateFailure(e.message.toString())
                        }
                    }
                    else -> {
                        mCallback.onStateFailure(stateErrorMsg)
                    }
                }
            }
        })
    }

    fun getCity(stateId: String, mCallback: AddressListener) {
        val call = apiService.getCity("Bearer ${userManager.token}", stateId)
        call.enqueue(object : Callback<ArrayList<CityRes>> {
            override fun onFailure(call: Call<ArrayList<CityRes>>, t: Throwable) {
                mCallback.onCityFailure(cityErrorMsg)
            }

            override fun onResponse(call: Call<ArrayList<CityRes>>, response: Response<ArrayList<CityRes>>) {
                when (response.code()) {
                    200 -> {
                        if (response.body()!!.size > 0)
                            mCallback.onCitySuccess(response.body()!!)
                        else
                            mCallback.onCityFailure(cityErrorMsg)
                    }
                    401 -> {
                        mCallback.onTokenExpire()
                    }
                    400 -> {
                        try {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            mCallback.onCityFailure(jObjError.getString("error-message"))
                        } catch (e: Exception) {
                            mCallback.onCityFailure(e.message.toString())
                        }
                    }
                    else -> {
                        mCallback.onCityFailure(cityErrorMsg)
                    }
                }
            }
        })
    }
}

interface AddressListener {

    fun onCountrySuccess(response: ArrayList<CountryRes>?)
    fun onCountryFailure(msg: String)
    fun onStateSuccess(body: ArrayList<StateRes>)
    fun onStateFailure(msg: String)
    fun onCitySuccess(body: ArrayList<CityRes>)
    fun onCityFailure(msg: String)
    fun onTokenExpire()

}