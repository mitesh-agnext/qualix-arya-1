package com.custom.app.ui.createData.flcScan

import android.os.Build
import android.os.Handler
import androidx.annotation.RequiresApi
import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import com.custom.app.util.Constants
import com.google.gson.GsonBuilder
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class FlcInteractor {

    interface FlcListener {

        fun onUploadImageSuccess(resResultFlc: ImageUploadResult?)
        fun onUploadImageFailure(msg: String)

        fun onFetchFLCResultSuccess(resFetchFlc: FlcResultRes?)
        fun onFetchFLCResultFailure(msg: String)

        fun onSaveFLCSuccess(resSaveFlc: ResponseBody?)
        fun onSaveFLCFailure(msg: String)
        fun onSaveFLCTokenExpire()

        fun onClearDataSuccess(resClearData: FlcCleanDirRes?)
        fun onClearDataFailure(msg: String)

        fun onFetchLocationSuccess(resFetchLocation: ArrayList<LocationList>?)
        fun onFetchLocationFailure(msg: String)

        fun onFetchGardenSuccess(resFetchGarden: ArrayList<GardenList>?)
        fun onFetchGardenFailure(msg: String)

        fun onFetchDivisionSuccess(resFetchDivision: ArrayList<DevisionList>?)
        fun onFetchDivisionFailure(msg: String)

        fun onFetchSectionSuccess(resFetchSection: ArrayList<SectionData>?)
        fun onFetchSectionFailure(msg: String)

        fun onFetchSectionCodeSuccess(resFetchSection: ArrayList<SectionData>?)
        fun onFetchSectionCodeFailure(msg: String)

        fun onSectionIdEmpty()
        fun onAgentCodeEmpty()

    }

    fun saveFlc(oneLeafBud: String, twoLeafBud: String, threeLeafBud: String, oneLeafBanjhi: String, twoLeafBanjhi: String, oneBanjhiCount: String,
                oneBudCount: String, oneLeafCount: String, twoLeafCount: String, threeLeafCount: String, qualityScore: Double, totalCount: Double,
                secId: String, agent_code: String, totalWeight: Double, areaCovered: Double, listener: FlcListener) {

        val option = HashMap<String?, String?>()

        option.put("oneLeafBud", oneLeafBud)
        option.put("twoLeafBud", twoLeafBud)
        option.put("threeLeafBud", threeLeafBud)
        option.put("oneLeafBanjhi", oneLeafBanjhi)
        option.put("twoLeafBanjhi", twoLeafBanjhi)
        option.put("oneBanjhiCount", oneBanjhiCount)
        option.put("oneBudCount", oneBudCount)
        option.put("oneLeafCount", oneLeafCount)
        option.put("twoLeafCount", twoLeafCount)
        option.put("threeLeafCount", threeLeafCount)
        option.put("totalCount", totalCount.toString())
        option.put("qualityScore", qualityScore.toString())
        option.put("sectionId", secId)
        option.put("totalWeight", totalWeight.toString())
        option.put("areaCovered", areaCovered.toString())
        option.put("agent_code", agent_code)

        addFLC(option, listener)

    }

    fun ClearData(secId: String, userId: String, listener: FlcListener) {

        try {

            val apiService = ApiClient.getClientFlc().create(ApiInterface::class.java)
            val call = apiService.cleanDir(userId, secId)

            call!!.enqueue(object : Callback<FlcCleanDirRes?> {

                override fun onFailure(call: Call<FlcCleanDirRes?>, t: Throwable) {
                    listener.onClearDataFailure(t.message.toString())

                }

                @RequiresApi(Build.VERSION_CODES.ECLAIR)
                override fun onResponse(
                        call: Call<FlcCleanDirRes?>,
                        response: Response<FlcCleanDirRes?>) {

                    if (response.isSuccessful) {
                        listener.onClearDataSuccess(response.body())

                    } else {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        listener.onClearDataFailure(jObjError.getString("error-message"))

                    }
                }
            })

        } catch (e: Exception) {

            e.printStackTrace()
            listener.onClearDataFailure(e.message.toString())

        }
    }

    fun FetchResult(secId: String, userId: String, listener: FlcListener) {

        Handler().postDelayed({
            try {

                val apiService = ApiClient.getClientFlc().create(ApiInterface::class.java)

                val call = apiService.getFLC(userId, secId)
                call!!.enqueue(object : Callback<FlcResultRes?> {

                    override fun onFailure(call: Call<FlcResultRes?>, t: Throwable) {
                        listener.onFetchFLCResultFailure(t.message.toString())
                    }

                    override fun onResponse(
                            call: Call<FlcResultRes?>, response: Response<FlcResultRes?>) {

                        if (response.isSuccessful) {

                            if (response.body() != null) {
                                listener.onFetchFLCResultSuccess(response.body())
                            }
                        } else {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            listener.onFetchFLCResultFailure(jObjError.getString("error-message"))
                        }
                    }
                })

            } catch (e: Exception) {
            }
        }, 250)

    }

    fun UploadImageServerNew(file: File, userId: String, secId: String, listener: FlcListener) {
        try {
            val builder = MultipartBody.Builder()
            builder.setType(MultipartBody.FORM)

            builder.addFormDataPart(
                    "image",
                    file.name,
                    RequestBody.create(MediaType.parse("multipart/form-data"), file)
            )

            builder.addFormDataPart("userId", userId)
            builder.addFormDataPart("sectionId", secId)

            val requestBody = builder.build()

            val apiService: ApiInterface = ApiClient.getClientFlc().create(ApiInterface::class.java)

            val call = apiService.uploadTeaImg(requestBody)

            call!!.enqueue(object : Callback<ImageUploadResult?> {
                @RequiresApi(Build.VERSION_CODES.ECLAIR)
                override fun onResponse(
                        call: Call<ImageUploadResult?>,
                        response: Response<ImageUploadResult?>) {

                    val gsonBuilder = GsonBuilder()
                    val gson = gsonBuilder.create()
                    val resSuccess = gson.toJson(response.body())
                    var resError = gson.toJson(response.errorBody())

                    if (response.isSuccessful) {
                        listener.onUploadImageSuccess(response.body())
                    }
                }

                override fun onFailure(call: Call<ImageUploadResult?>, t: Throwable) {
                    listener.onUploadImageFailure(t.message.toString())
                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
            listener.onUploadImageFailure(e.message.toString())
        }
    }

    fun addFLC(option: HashMap<String?, String?>, listener: FlcListener) {

        try {
            val apiService = ApiClient.getScmClient().create(ApiInterface::class.java)
            val call = apiService.addFlc(Constants.TOKEN, option)

            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    listener.onSaveFLCFailure(t.message.toString())
                }

                override fun onResponse(
                        call: Call<ResponseBody?>,
                        response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        listener.onSaveFLCSuccess(response.body())
                    } else {
                        if (response.code() == 401) {
                            listener.onSaveFLCTokenExpire()
                        } else {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            listener.onSaveFLCFailure(jObjError.getString("error-message"))
                        }

                    }
                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
            listener.onSaveFLCFailure(e.message.toString())
        }
    }

    fun FetchLocations(listener: FlcListener) {

        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
            val call = apiService.getAllFlcLocations(Constants.TOKEN)
            call.enqueue(object : Callback<ArrayList<LocationList>?> {

                override fun onFailure(call: Call<ArrayList<LocationList>?>, t: Throwable) {
                    listener.onFetchLocationFailure(t.message.toString())
                }

                override fun onResponse(
                        call: Call<ArrayList<LocationList>?>,
                        response: Response<ArrayList<LocationList>?>) {

                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            listener.onFetchLocationSuccess(response.body())
                        }
                    } else {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        listener.onFetchLocationFailure(jObjError.getString("error-message"))
                    }
                }
            })

        } catch (e: Exception) {
        }
    }

    fun FetchGarden(locationId: Int, listener: FlcListener) {

        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)

            val call = apiService.getAllFlcGardens(Constants.TOKEN, locationId)
            call.enqueue(object : Callback<ArrayList<GardenList>?> {

                override fun onFailure(call: Call<ArrayList<GardenList>?>, t: Throwable) {
                    listener.onFetchGardenFailure(t.message.toString())
                }

                override fun onResponse(
                        call: Call<ArrayList<GardenList>?>,
                        response: Response<ArrayList<GardenList>?>) {

                    if (response.isSuccessful) {

                        if (response.body() != null) {
                            listener.onFetchGardenSuccess(response.body())
                        }
                    } else {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        listener.onFetchGardenFailure(jObjError.getString("error-message"))
                    }
                }
            })

        } catch (e: Exception) {
        }
    }

    fun FetchDivision(gardenId: Int, listener: FlcListener) {

        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)

            val call = apiService.getAllFlcDivisions(Constants.TOKEN, gardenId)
            call.enqueue(object : Callback<ArrayList<DevisionList>?> {

                override fun onFailure(call: Call<ArrayList<DevisionList>?>, t: Throwable) {
                    listener.onFetchDivisionFailure(t.message.toString())
                }

                override fun onResponse(
                        call: Call<ArrayList<DevisionList>?>,
                        response: Response<ArrayList<DevisionList>?>) {

                    if (response.isSuccessful) {

                        if (response.body() != null) {
                            listener.onFetchDivisionSuccess(response.body())
                        }
                    } else {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        listener.onFetchDivisionFailure(jObjError.getString("error-message"))
                    }
                }
            })

        } catch (e: Exception) {
        }
    }

    fun FetchSection(divisionId: Int, listener: FlcListener) {

        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)

            val call = apiService.getAllFlcSections(Constants.TOKEN, divisionId)
            call.enqueue(object : Callback<ArrayList<SectionData>?> {

                override fun onFailure(call: Call<ArrayList<SectionData>?>, t: Throwable) {
                    listener.onFetchSectionFailure(t.message.toString())
                }

                override fun onResponse(
                        call: Call<ArrayList<SectionData>?>,
                        response: Response<ArrayList<SectionData>?>) {

                    if (response.isSuccessful) {

                        if (response.body() != null) {
                            listener.onFetchSectionSuccess(response.body())
                        }
                    } else {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        listener.onFetchSectionFailure(jObjError.getString("error-message"))
                    }
                }
            })

        } catch (e: Exception) {
        }
    }

    fun FetchSectionCode(search_key: String, listener: FlcListener) {

        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)

            val call = apiService.getSectionBySectionCode(Constants.TOKEN, search_key)
            call.enqueue(object : Callback<ArrayList<SectionData>?> {

                override fun onFailure(call: Call<ArrayList<SectionData>?>, t: Throwable) {
                    listener.onFetchSectionCodeFailure(t.message.toString())
                }

                override fun onResponse(
                        call: Call<ArrayList<SectionData>?>, response: Response<ArrayList<SectionData>?>) {

                    if (response.isSuccessful) {

                        if (response.body() != null) {
                            listener.onFetchSectionCodeSuccess(response.body())
                        }
                    } else {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        listener.onFetchSectionCodeFailure((jObjError.getString("error-message")))
                    }
                }
            })

        } catch (e: Exception) {

        }
    }

}