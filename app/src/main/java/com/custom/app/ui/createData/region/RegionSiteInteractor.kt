package com.custom.app.ui.createData.region

import com.custom.app.data.model.country.CityRes
import com.custom.app.data.model.country.CountryRes
import com.custom.app.data.model.country.StateRes
import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import com.custom.app.ui.createData.region.site.create.RegionRes
import com.custom.app.ui.createData.region.site.list.SiteListRes
import com.custom.app.ui.customer.list.CustomerRes
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

class RegionSiteInteractor(val userManager: UserManager) {

    fun addRegion(region_name: String, customer_id: Int, listener: CreateRegionListener) {
        when {
            region_name.isEmpty() -> {
                listener.onRegionNameEmpty()
            }
            else -> {

                val options = JsonObject()
                options.addProperty("region_name", region_name)
                options.addProperty("customer_id", customer_id)

                createRegion(listener, options)
            }
        }
    }

    private fun createRegion(listener: CreateRegionListener, options: JsonObject) {

        val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
        val call = apiService.createRegion("Bearer ${userManager.token}", options)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                listener.onRegionFailure(t!!.message.toString())
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>) {

                if (response.isSuccessful) {
                    listener.onRegionSuccess(response.body()!!)

                } else {
                    val jObjError = JSONObject(response.errorBody()!!.string())

                    if (response.code() == 401) {
                        listener.onRegionFailure(jObjError.getString("error-message"))
                    }
                    else {
                        listener.onRegionFailure(jObjError.getString("error-message"))
                    }
                }
            }
        })
    }

    fun getCustomer(listener: CreateRegionListener) {
        try {
            val apiService = ApiClient.getClient().create(ApiInterface::class.java)
            val call = apiService.getCustomers("Bearer ${userManager.token}")
            call.enqueue(object : Callback<ArrayList<CustomerRes>> {
                override fun onResponse(
                        call: Call<ArrayList<CustomerRes>>,
                        response: Response<ArrayList<CustomerRes>>) {
                    when {
                        response.isSuccessful -> {

                            if (response.code()==204){
                                listener.onGetCustomerEmpty("No record found")
                            }
                            else {
                                listener.onGetCustomerSuccess(response.body()!!)
                            }
                        }
                        else -> {
                            val jObjError = JSONObject(response.errorBody()!!.string())

                            when {
                                response.code() == 401 -> listener.onGetCustomerFailure(jObjError.getString("error-message"))
                                else -> listener.onGetCustomerFailure(jObjError.getString("error-message"))
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<CustomerRes>>, t: Throwable) {
                    listener.onGetCustomerFailure(t.message.toString())
                }
            })
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    fun allRegion(listener: ListRegionInteractorCallback, customerId: Int) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)

            val call = apiService.getRegion("Bearer ${userManager.token}", customerId.toString())
            call.enqueue(object : Callback<ArrayList<RegionRes>> {
                override fun onResponse(
                        call: Call<ArrayList<RegionRes>>,
                        response: Response<ArrayList<RegionRes>>) {
                    when {
                        response.isSuccessful -> {

                            if (response.code()==204){
                                listener.onGetRegionEmpty("No record found")
                            }
                            else {
                                listener.onGetRegionSuccess(response.body()!!)
                            }
                        }
                        else -> {
                            val jObjError = JSONObject(response.errorBody()!!.string())

                            when {
                                else -> listener.onGetRegionFailure(jObjError.getString("error-message"))
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<RegionRes>>, t: Throwable) {
                    listener.onGetRegionFailure(t.message.toString())
                }
            })

        } catch (e: Exception) {
            Timber.e(e);
        }
    }

    fun deleteRegion(listener: ListRegionInteractorCallback, regionId: Int, deletedPostion: Int) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)

            val call = apiService.deleteRegion("Bearer ${userManager.token}", regionId.toString())
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                        call: Call<ResponseBody>, response: Response<ResponseBody>) {

                    when {
                        response.isSuccessful -> listener.deleteRegionSuccess(deletedPostion)
                        else -> {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            when {
                                else -> listener.deleteRegionFailure(jObjError.getString("error-message"))
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    listener.deleteRegionFailure(t.message.toString())
                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateRegion(region_id: Int, region_name: String, customer_id: Int, listener: UpdateRegionListener) {
        val options = JsonObject()
        options.addProperty("region_name", region_name)
        options.addProperty("region_id", region_id)
        options.addProperty("customer_id", customer_id)

        updateRegion(region_id, listener, options)
    }

    private fun updateRegion(regionId: Int, listener: UpdateRegionListener, options: JsonObject) {

        val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
        val call = apiService.updateRegion("Bearer ${userManager.token}", options, regionId)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                listener.onUpdateRegionFailure(t!!.message.toString())
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    listener.onUpdateRegionSuccess(response.body()!!)
                } else {
                    val jObjError = JSONObject(response.errorBody()!!.string())

                    if (response.code() == 401) {
                        listener.onUpdateRegionFailure(jObjError.getString("error-message"))
                    } else {
                        listener.onUpdateRegionFailure(jObjError.getString("error-message"))
                    }
                }
            }
        })
    }

    fun addSite(customerId: Int, region_id: Int, countryId: Int, stateId: Int, cityId: Int, site_name: String, userId: Int, geoLocation: String, listener: CreateSiteListener) {
        when {
            site_name.isEmpty() -> {
                listener.onSiteNameEmpty()
            }
            else -> {

                val options = JsonObject()
                options.addProperty("customer_id", customerId)
                options.addProperty("region_id", region_id)
                options.addProperty("country_id", countryId)
                options.addProperty("state_id", stateId)
                options.addProperty("city_id", cityId)
                options.addProperty("site_name", site_name)
                options.addProperty("user_id", userId)
                options.addProperty("geo_location", geoLocation)

                createSite(listener, options)
            }
        }
    }

    private fun createSite(listener: CreateSiteListener, options: JsonObject) {

        val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
        val call = apiService.createSite("Bearer ${userManager.token}", options)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                listener.onCreateSiteFailure(t!!.message.toString())
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    listener.onCreateSiteSuccess(response.body()!!)
                } else {
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    if (response.code() == 401) {
                        listener.onCreateSiteFailure(jObjError.getString("error-message"))
                    } else {
                        listener.onCreateSiteFailure(jObjError.getString("error-message"))
                    }
                }
            }
        })
    }

    fun getRegion(listener: CreateSiteListener, customerId: Int) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
            val call = apiService.getRegion("Bearer ${userManager.token}", customerId.toString())
            call.enqueue(object : Callback<ArrayList<RegionRes>> {
                override fun onResponse(
                        call: Call<ArrayList<RegionRes>>,
                        response: Response<ArrayList<RegionRes>>) {

                    when {
                        response.isSuccessful -> {

                            if (response.code()==204){
                                listener.onGetRegionEmpty("No record found")
                            }
                            else {
                                listener.onGetRegionSuccess(response.body()!!)
                            }

                        }
                        else -> {
                            val jObjError = JSONObject(response.errorBody()!!.string())

                            when {
                                response.code() == 401 -> listener.onGetRegionFailure(jObjError.getString("error-message"))
                                else -> listener.onGetRegionFailure(jObjError.getString("error-message"))
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<RegionRes>>, t: Throwable) {
                    listener.onGetRegionFailure(t.message.toString())
                }
            })
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    fun getCustomer(listener: CreateSiteListener) {
        try {
            val apiService = ApiClient.getClient().create(ApiInterface::class.java)
            val call = apiService.getCustomers("Bearer ${userManager.token}")
            call.enqueue(object : Callback<ArrayList<CustomerRes>> {
                override fun onResponse(
                        call: Call<ArrayList<CustomerRes>>,
                        response: Response<ArrayList<CustomerRes>>) {

                    when {
                        response.isSuccessful -> {
                            if (response.code()==204) {
                                listener.onGetCustomerEmpty("No record found")

                            }
                            else {
                                listener.onGetCustomerSuccess(response.body()!!)
                            }
                        }
                        else -> {
                            val jObjError = JSONObject(response.errorBody()!!.string())

                            when {
                                response.code() == 401 -> listener.onGetCustomerFailure(jObjError.getString("error-message"))
                                else -> listener.onGetCustomerFailure(jObjError.getString("error-message"))
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<CustomerRes>>, t: Throwable) {
                    listener.onGetCustomerFailure(t.message.toString())
                }
            })
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    fun getUser(listener: CreateSiteListener, customer_id: Int) {
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
                                listener.onGetUserApiEmpty("No record found")
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

    fun getCountry(listener: CreateSiteListener) {
        try {
            val apiService = ApiClient.getClient().create(ApiInterface::class.java)
            val call = apiService.getCountry("Bearer ${userManager.token}")
            call.enqueue(object : Callback<ArrayList<CountryRes>> {
                override fun onResponse(
                        call: Call<ArrayList<CountryRes>>,
                        response: Response<ArrayList<CountryRes>>) {

                    when {
                        response.isSuccessful -> {
                            if (response.code()==204){
                                listener.onGetCountryEmpty("No record found")
                            }
                            else {
                                listener.onGetCountrySuccess(response.body()!!)
                            }
                        }
                        else -> {
                            val jObjError = JSONObject(response.errorBody()!!.string())

                            when {
                                response.code() == 401 -> listener.onGetCountryFailure(jObjError.getString("error-message"))
                                else -> listener.onGetCountryFailure(jObjError.getString("error-message"))
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<CountryRes>>, t: Throwable) {
                    listener.onGetCountryFailure(t.message.toString())
                }
            })
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    fun getState(listener: CreateSiteListener, country_id: Int) {
        try {
            val apiService = ApiClient.getClient().create(ApiInterface::class.java)
            val call = apiService.getState("Bearer ${userManager.token}", country_id.toString())
            call.enqueue(object : Callback<ArrayList<StateRes>> {
                override fun onResponse(
                        call: Call<ArrayList<StateRes>>,
                        response: Response<ArrayList<StateRes>>) {

                    when {
                        response.isSuccessful -> {
                            if (response.code()==204) {
                                listener.onGetStateEmpty("No record found")
                            }else {
                                listener.onGetStateSuccess(response.body()!!)
                            }
                        }
                        else -> {
                            val jObjError = JSONObject(response.errorBody()!!.string())

                            when {
                                response.code() == 401 -> listener.onGetStateFailure(jObjError.getString("error-message"))
                                else -> listener.onGetStateFailure(jObjError.getString("error-message"))
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<StateRes>>, t: Throwable) {
                    listener.onGetStateFailure(t.message.toString())
                }
            })
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    fun getCity(listener: CreateSiteListener, state_id: Int) {
        try {
            val apiService = ApiClient.getClient().create(ApiInterface::class.java)
            val call = apiService.getCity("Bearer ${userManager.token}", state_id.toString())
            call.enqueue(object : Callback<ArrayList<CityRes>> {
                override fun onResponse(
                        call: Call<ArrayList<CityRes>>,
                        response: Response<ArrayList<CityRes>>) {


                    when {
                        response.isSuccessful -> {
                            if (response.code()==204){
                                listener.onGetCityFailure("No record found")
                            }
                            else {
                                listener.onGetCitySuccess(response.body()!!)
                            }

                        }
                        else -> {
                            val jObjError = JSONObject(response.errorBody()!!.string())

                            when {
                                response.code() == 401 -> listener.onGetCityFailure(jObjError.getString("error-message"))
                                else ->
                                    listener.onGetCityFailure(jObjError.getString("error-message"))
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<CityRes>>, t: Throwable) {
                    listener.onGetCityFailure(t.message.toString())
                }
            })
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    fun allSite(listener: ListSiteInteractorCallback, keyword: String, regionId: Int) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)

            val call = apiService.getSite("Bearer ${userManager.token}", keyword, regionId)
            call.enqueue(object : Callback<ArrayList<SiteListRes>> {
                override fun onResponse(
                        call: Call<ArrayList<SiteListRes>>,
                        response: Response<ArrayList<SiteListRes>>) {

                    when {
                        response.isSuccessful -> {
                            if (response.code()==204) {
                                listener.onGetSiteEmpty("No record found")
                            }else {
                                listener.onGetSiteSuccess(response.body()!!)
                            }
                        }
                        else -> {
                            val jObjError = JSONObject(response.errorBody()!!.string())

                            when {
                                else -> listener.onGetSiteError(jObjError.getString("error-message"))
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<SiteListRes>>, t: Throwable) {
                    listener.onGetSiteError(t.message.toString())
                }
            })

        } catch (e: Exception) {
            Timber.e(e);
        }
    }

    fun deleteSite(listener: ListSiteInteractorCallback, siteId: Int, deletedPostion: Int) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)

            val call = apiService.deleteSite("Bearer ${userManager.token}", siteId)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>) {

                    when {
                        response.isSuccessful -> listener.deleteSiteSuccess(deletedPostion)
                        else -> {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            when {
                                else -> listener.deleteSiteFailure(jObjError.getString("error-message"))
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    listener.deleteSiteFailure(t.message.toString())
                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateSite(siteId: Int, region_id: Int, countryId: Int, stateId: Int, cityId: Int, site_name: String, userId: Int, geoLocation: String, customer_id: Int, listener: UpdateSiteListener) {

        val options = JsonObject()
        options.addProperty("region_id", region_id)
        options.addProperty("country_id", countryId)
        options.addProperty("state_id", stateId)
        options.addProperty("city_id", cityId)
        options.addProperty("site_name", site_name)
        options.addProperty("user_id", userId)
        options.addProperty("coordinates", geoLocation)
        options.addProperty("customer_id", customer_id)

        updateSite(siteId, listener, options)

    }

    private fun updateSite(siteId: Int, listener: UpdateSiteListener, options: JsonObject) {

        val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
        val call = apiService.updateSite("Bearer ${userManager.token}", siteId, options)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                listener.onUpdateSiteFailure(t!!.message.toString())
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    listener.onUpdateSiteSuccess(response.body()!!)
                } else {
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    if (response.code() == 401) {
                        listener.onUpdateSiteFailure(jObjError.getString("error-message"))
                    } else {
                        listener.onUpdateSiteFailure(jObjError.getString("error-message"))
                    }
                }
            }
        })
    }

}

interface CreateRegionListener {

    fun onRegionSuccess(body: ResponseBody)
    fun onRegionFailure(msg: String)

    fun onGetCustomerSuccess(body: ArrayList<CustomerRes>)
    fun onGetCustomerFailure(msg: String)
    fun onGetCustomerEmpty(msg: String)


    fun onRegionNameEmpty()
}

interface ListRegionInteractorCallback {
    fun onGetRegionSuccess(body: ArrayList<RegionRes>)
    fun onGetRegionFailure(msg: String)
    fun onGetRegionEmpty(msg: String)

    fun deleteRegionSuccess(deletedPostion: Int)
    fun deleteRegionFailure(msg: String)
}

interface UpdateRegionListener {

    fun onUpdateRegionSuccess(body: ResponseBody)
    fun onUpdateRegionFailure(msg: String)

}

interface CreateSiteListener {

    fun onGetCustomerSuccess(body: ArrayList<CustomerRes>)
    fun onGetCustomerFailure(msg: String)
    fun onGetCustomerEmpty(msg: String)

    fun onGetRegionSuccess(body: ArrayList<RegionRes>)
    fun onGetRegionFailure(msg: String)
    fun onGetRegionEmpty(msg: String)

    fun onGetCountrySuccess(body: ArrayList<CountryRes>)
    fun onGetCountryFailure(msg: String)
    fun onGetCountryEmpty(msg: String)

    fun onGetStateSuccess(body: ArrayList<StateRes>)
    fun onGetStateFailure(msg: String)
    fun onGetStateEmpty(msg: String)

    fun onGetCitySuccess(body: ArrayList<CityRes>)
    fun onGetCityFailure(msg: String)
    fun onGetCityEmpty(msg: String)

    fun onSiteNameEmpty()

    fun onCreateSiteSuccess(body: ResponseBody)
    fun onCreateSiteFailure(msg: String)

    fun onGetUserSuccess(body: ArrayList<UserDataRes>)
    fun onGetUserFailure(msg: String?)
    fun onGetUserApiEmpty(msg: String)

}

interface ListSiteInteractorCallback {
    fun onGetSiteSuccess(body: ArrayList<SiteListRes>)
    fun onGetSiteError(msg: String)
    fun onGetSiteEmpty(msg: String)

    fun deleteSiteSuccess(deletedPostion: Int)
    fun deleteSiteFailure(msg: String)
}

interface UpdateSiteListener {

    fun onUpdateSiteSuccess(body: ResponseBody)
    fun onUpdateSiteFailure(msg: String)

}