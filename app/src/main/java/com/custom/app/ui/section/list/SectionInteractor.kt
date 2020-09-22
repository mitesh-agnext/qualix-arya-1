package com.custom.app.ui.section.list

import com.custom.app.data.model.section.DivisionRes
import com.custom.app.data.model.section.GardenRes
import com.custom.app.data.model.section.LocationItem
import com.custom.app.data.model.section.SectionRes
import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import com.custom.app.util.Constants
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SectionInteractor(val apiServiceIAM: ApiInterface) {
    val locationError = "Error to get location"
    val gardenError = "Error to get garden"
    val divisionError = "Error to get division"
    val sectionError = "Error to get error"
    val addSectionError = "Error to add error"
    val addGardenError = "Error to add garden"
    val addDivisionError = "Error to add division"

    val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)

    fun getLocation(mCallback: SectionCallback) {
        val call = apiService.getLocations(Constants.TOKEN)
        call.enqueue(object : Callback<ArrayList<LocationItem>> {
            override fun onFailure(call: Call<ArrayList<LocationItem>>, t: Throwable) {
                mCallback.onLocationFailure(locationError)
            }

            override fun onResponse(call: Call<ArrayList<LocationItem>>, response: Response<ArrayList<LocationItem>>) {
                when (response.code()) {
                    200 -> {
                        mCallback.onLocationSuccess(response.body()!!)
                    }
                    401 -> {
                        mCallback.onTokenExpire()
                    }
                    else -> {
                        mCallback.onLocationFailure(locationError)
                    }
                }
            }
        })
    }

    fun getGarden(locationId: String, mCallback: SectionCallback) {
        val call = apiService.getGarden(Constants.TOKEN, locationId)
        call.enqueue(object : Callback<ArrayList<GardenRes>> {
            override fun onFailure(call: Call<ArrayList<GardenRes>>, t: Throwable) {
                mCallback.onGardenFailure(gardenError)
            }

            override fun onResponse(call: Call<ArrayList<GardenRes>>, response: Response<ArrayList<GardenRes>>) {
                when (response.code()) {
                    200 -> {
                        mCallback.onGardenSuccess(response.body()!!)
                    }
                    401 -> {
                        mCallback.onTokenExpire()
                    }
                    else -> {
                        mCallback.onGardenFailure(gardenError)
                    }
                }
            }
        })
    }

    fun getDivision(gardenID: String, mCallback: SectionCallback) {
        val call = apiService.getDivision(Constants.TOKEN, gardenID)
        call.enqueue(object : Callback<ArrayList<DivisionRes>> {
            override fun onFailure(call: Call<ArrayList<DivisionRes>>, t: Throwable) {
                mCallback.onDivisionFailure(divisionError)
            }

            override fun onResponse(call: Call<ArrayList<DivisionRes>>, response: Response<ArrayList<DivisionRes>>) {
                when (response.code()) {
                    200 -> {
                        mCallback.onDivisionSuccess(response.body()!!)
                    }
                    401 -> {
                        mCallback.onTokenExpire()
                    }
                    else -> {
                        mCallback.onDivisionFailure(divisionError)
                    }
                }
            }
        })
    }

    fun getSection(divisionId: String, mCallback: SectionCallback) {
        val call = apiService.getSection(Constants.TOKEN, divisionId)
        call.enqueue(object : Callback<ArrayList<SectionRes>> {
            override fun onFailure(call: Call<ArrayList<SectionRes>>, t: Throwable) {
                mCallback.onGetSectionsFailure(sectionError)
            }

            override fun onResponse(call: Call<ArrayList<SectionRes>>, response: Response<ArrayList<SectionRes>>) {
                when (response.code()) {
                    200 -> {
                        mCallback.onGetSectionsSuccess(response.body()!!)
                    }
                    401 -> {
                        mCallback.onTokenExpire()
                    }
                    else -> {
                        mCallback.onDivisionFailure(sectionError)
                    }
                }
            }
        })
    }

    fun addSection(requestData: HashMap<String, Any>, mCallback: SectionCallback) {
        val call = apiService.addSection(Constants.TOKEN, requestData)
        call.enqueue(object : Callback<SectionRes> {
            override fun onFailure(call: Call<SectionRes>, t: Throwable) {
                mCallback.onGetSectionsFailure(addSectionError)
            }

            override fun onResponse(call: Call<SectionRes>, response: Response<SectionRes>) {
                when (response.code()) {
                    201 -> {
                        mCallback.onAddSectionSuccess(response.body()!!)
                    }
                    400 -> {
                        try {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            mCallback.onGetSectionsFailure(jObjError.getString("error-message"))
                        } catch (e: Exception) {
                            mCallback.onGetSectionsFailure(e.message.toString())
                        }
                    }
                    400 -> {
                        try {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            mCallback.onGetSectionsFailure(jObjError.getString("error-message"))
                        } catch (e: Exception) {
                            mCallback.onGetSectionsFailure(e.message.toString())
                        }
                    }
                    401 -> {
                        mCallback.onTokenExpire()
                    }
                    else -> {
                        mCallback.onGetSectionsFailure(sectionError)
                    }
                }
            }
        })
    }

    fun updateSection(sectionId: String, requestData: HashMap<String, Any>, mCallback: SectionCallback) {
        val call = apiService.updateSection(Constants.TOKEN, sectionId, requestData)
        call.enqueue(object : Callback<SectionRes> {
            override fun onFailure(call: Call<SectionRes>, t: Throwable) {
                mCallback.onUpdateSectionFailure("Error to update")
            }

            override fun onResponse(call: Call<SectionRes>, response: Response<SectionRes>) {
                when (response.code()) {
                    200 -> {
                        mCallback.onUpdateSectionSuccess(response)
                    }
                    400 -> {
                        try {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            mCallback.onUpdateSectionFailure(jObjError.getString("error-message"))
                        } catch (e: Exception) {
                            mCallback.onUpdateSectionFailure(e.message.toString())
                        }
                    }
                    401 -> {
                        mCallback.onTokenExpire()
                    }
                }
            }
        })
    }

    fun deleteSection(sectionId: String, mCallback: SectionCallback) {
        val call = apiService.deleteSection(Constants.TOKEN, sectionId)
        call.enqueue(object : Callback<SectionRes> {
            override fun onFailure(call: Call<SectionRes>, t: Throwable) {
                mCallback.onDeleteSectionFailure("Error to delete")
            }

            override fun onResponse(call: Call<SectionRes>, response: Response<SectionRes>) {
                when (response.code()) {
                    204 -> {
                        mCallback.onDeleteSectionSuccess(response)
                    }
                    400 -> {
                        try {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            mCallback.onDeleteSectionFailure(jObjError.getString("error-message"))
                        } catch (e: Exception) {
                            mCallback.onDeleteSectionFailure(e.message.toString())
                        }
                    }
                    401 -> {
                        mCallback.onTokenExpire()
                    }
                }
            }

        })
    }

    fun addGarden(requestData: HashMap<String, Any>, mCallback: SectionCallback) {
        val call = apiService.addGarden(Constants.TOKEN, requestData)
        call.enqueue(object : Callback<GardenRes> {
            override fun onFailure(call: Call<GardenRes>, t: Throwable) {
                mCallback.onAddGardenFailure(addGardenError)
            }

            override fun onResponse(call: Call<GardenRes>, response: Response<GardenRes>) {
                when (response.code()) {
                    201 -> {
                        mCallback.onAddGardenSuccess(response)
                    }
                    400 -> {
                        try {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            mCallback.onAddGardenFailure(jObjError.getString("error-message"))
                        } catch (e: Exception) {
                            mCallback.onAddGardenFailure(e.message.toString())
                        }
                    }
                    401 -> {
                        mCallback.onTokenExpire()
                    }
                    else -> {
                        mCallback.onAddGardenFailure(addGardenError)
                    }
                }
            }
        })
    }

    fun addDivision(requestData: HashMap<String, Any>, mCallback: SectionCallback) {
        val call = apiService.addDivision(Constants.TOKEN, requestData)
        call.enqueue(object : Callback<DivisionRes> {
            override fun onFailure(call: Call<DivisionRes>, t: Throwable) {
                mCallback.onAddDivisionFailure(addDivisionError)
            }

            override fun onResponse(call: Call<DivisionRes>, response: Response<DivisionRes>) {
                when (response.code()) {
                    201 -> {
                        mCallback.onAddDivisionSuccess(response)
                    }
                    400 -> {
                        try {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            mCallback.onAddDivisionFailure(jObjError.getString("error-message"))
                        } catch (e: Exception) {
                            mCallback.onAddDivisionFailure(e.message.toString())
                        }
                    }
                    401 -> {
                        mCallback.onTokenExpire()
                    }
                    else -> {
                        mCallback.onAddDivisionFailure(addDivisionError)
                    }
                }
            }
        })
    }
}

interface SectionCallback {

    fun onLocationSuccess(body: ArrayList<LocationItem>)
    fun onLocationFailure(message: String)

    fun onGardenSuccess(body: ArrayList<GardenRes>)
    fun onGardenFailure(locationError: String)

    fun onDivisionSuccess(body: ArrayList<DivisionRes>)
    fun onDivisionFailure(gardenError: String)

    fun onGetSectionsSuccess(body: ArrayList<SectionRes>)
    fun onGetSectionsFailure(divisionError: String)

    fun onAddSectionSuccess(body: SectionRes)
    fun onAddSectionFailure(message: String)

    fun onUpdateSectionSuccess(body: Response<SectionRes>)
    fun onUpdateSectionFailure(message: String)

    fun onDeleteSectionSuccess(body: Response<SectionRes>)
    fun onDeleteSectionFailure(message: String)

    fun onAddGardenSuccess(response: Response<GardenRes>)
    fun onAddGardenFailure(message: String)

    fun onAddDivisionSuccess(response: Response<DivisionRes>)
    fun onAddDivisionFailure(message: String)

    fun onTokenExpire()

}

