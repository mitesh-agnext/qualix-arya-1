package com.custom.app.ui.createData.analytics.analyticsScreen

import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import com.custom.app.ui.createData.analytics.analysis.numberOfFarmers.FarmerDataRes
import com.custom.app.ui.createData.analytics.analysis.numberOfFarmers.NumberOfFarmerRes
import com.custom.app.ui.createData.analytics.analysis.payments.PaymentChartRes
import com.custom.app.ui.createData.analytics.analysis.payments.PaymentListRes
import com.custom.app.ui.createData.analytics.analysis.payments.PaymentRes
import com.custom.app.ui.createData.analytics.analysis.quality.*
import com.custom.app.ui.createData.analytics.analysis.quantity.*
import com.custom.app.ui.createData.flcScan.season.create.CommodityRes
import com.custom.app.ui.createData.instlCenter.createInstallationCenter.InstallationCenterTypeRes
import com.custom.app.ui.createData.region.site.create.RegionRes
import com.custom.app.ui.device.assign.InstallationCenterRes
import com.user.app.data.UserManager
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class AnalyticsInteractor(val userManager: UserManager) {

    fun allCommodities(listener: AnalyticsInteractorCallback, category_id: Int) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
            val call = apiService.getCommodityByCategory("Bearer ${userManager.token}", category_id)
            call.enqueue(object : Callback<ArrayList<CommodityRes>> {
                override fun onResponse(
                        call: Call<ArrayList<CommodityRes>>, response: Response<ArrayList<CommodityRes>>) {
                    when (response.code()) {
                        200 -> {
                            listener.allCommodityApiSuccess(response.body()!!)
                        }
                        400 -> {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                listener.allCommodityApiError(jObjError.getString("error-message"))
                            } catch (e: Exception) {
                                listener.allCommodityApiError(e.message.toString())
                            }
                        }
                        else -> {
                            listener.allCommodityApiError("error to get all Commodities")
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<CommodityRes>>, t: Throwable) {
                    listener.allCommodityApiError(t.message.toString())
                }
            })

        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    fun allCenterType(listener: AnalyticsInteractorCallback, region_id: String) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
            val call = apiService.getCommercialCenterByRegionId("Bearer ${userManager.token}", region_id)
            call.enqueue(object : Callback<ArrayList<InstallationCenterTypeRes>> {
                override fun onResponse(
                        call: Call<ArrayList<InstallationCenterTypeRes>>, response: Response<ArrayList<InstallationCenterTypeRes>>) {

                    when (response.code()) {
                        200 -> {
                            if (response.body()!!.size > 0) {
                                val dummyData = InstallationCenterTypeRes()
                                dummyData.inst_center_type_desc = "All"
                                dummyData.commercial_location_type_id = 0
                                val dummyArray = ArrayList<InstallationCenterTypeRes>()
                                dummyArray.add(dummyData)
                                dummyArray.addAll(response.body()!!)
                                listener.allCCenterTypeSuccess(dummyArray)
                            }
                        }
                        400 -> {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                listener.allCCenterTypeError(jObjError.getString("error-message"))
                            } catch (e: Exception) {
                                listener.allCCenterTypeError(e.message.toString())
                            }
                        }
                        else -> {
                            listener.allCCenterTypeError("error to get all Center Type")
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<InstallationCenterTypeRes>>, t: Throwable) {
                    listener.allCCenterTypeError(t.message.toString())
                }
            })

        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    fun allRegion(listener: AnalyticsInteractorCallback, customerId: String) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)

            val call = apiService.getRegion("Bearer ${userManager.token}", customerId)
            call.enqueue(object : Callback<ArrayList<RegionRes>> {
                override fun onResponse(
                        call: Call<ArrayList<RegionRes>>,
                        response: Response<ArrayList<RegionRes>>) {
                    when (response.code()) {
                        200 -> {
                            val dummyData = RegionRes()
                            dummyData.region_name = "All"
                            dummyData.region_id = 0
                            val dummyArray = ArrayList<RegionRes>()
                            dummyArray.add(dummyData)
                            dummyArray.addAll(response.body()!!)
                            listener.onGetRegionSuccess(dummyArray)
                        }
                        400 -> {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                listener.onGetRegionFailure(jObjError.getString("error-message"))
                            } catch (e: Exception) {
                                listener.onGetRegionFailure(e.message.toString())
                            }
                        }
                        else -> {
                            listener.onGetRegionFailure("error to get Region")
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

    fun allCenter(listener: AnalyticsInteractorCallback, keyword: String, customer_id: String, region_id: String) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)

            val call = apiService.getInstallationCenters("Bearer ${userManager.token}", keyword, customer_id, region_id)
            call.enqueue(object : Callback<ArrayList<InstallationCenterRes>> {
                override fun onResponse(
                        call: Call<ArrayList<InstallationCenterRes>>,
                        response: Response<ArrayList<InstallationCenterRes>>) {
                    when (response.code()) {
                        200 -> {
                            if (response.body()!!.size>0) {
                                val dummyData = InstallationCenterRes()
                                dummyData.inst_center_name = "All"
                                dummyData.installation_center_id = 0
                                val dummyArray = ArrayList<InstallationCenterRes>()
                                dummyArray.add(dummyData)
                                dummyArray.addAll(response.body()!!)
                                listener.allCentersApiSuccess(dummyArray)
                            }
                        }
                        400 -> {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                listener.allCentersApiError(jObjError.getString("error-message"))
                            } catch (e: Exception) {
                                listener.allCentersApiError(e.message.toString())
                            }
                        }
                        else -> {
                            listener.allCentersApiError("error to all Centers")
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

    fun allCategories(listener: AnalyticsInteractorCallback, customerId: String) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
            val call = apiService.getCategoriesLists("Bearer ${userManager.token}", customerId)
            call.enqueue(object : Callback<ArrayList<CategoryTabsRes>> {
                override fun onResponse(
                        call: Call<ArrayList<CategoryTabsRes>>, response: Response<ArrayList<CategoryTabsRes>>) {
                    when (response.code()) {
                        200 -> {
                            listener.allCategoryApiSuccess(response.body()!!)
                        }
                        400 -> {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                listener.allCategoryApiError(jObjError.getString("error-message"))
                            } catch (e: Exception) {
                                listener.allCategoryApiError(e.message.toString())
                            }
                        }
                        else -> {
                            listener.allCategoryApiError("error to all Category")
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<CategoryTabsRes>>, t: Throwable) {
                    listener.allCategoryApiError(t.message.toString())
                }
            })

        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    fun farmerList(listener: NumberOfFarmerInteractorCallback, customerId: String, commodityId: String, cc_Id: String, dateFrom: String,
                   dateTo: String, regionId: String) {
        try {
            val apiService = ApiClient.getScmClient().create(ApiInterface::class.java)
            val call = apiService.getFarmerList("Bearer ${userManager.token}", customerId, commodityId, cc_Id, dateTo, dateFrom, regionId)
            call.enqueue(object : Callback<ArrayList<NumberOfFarmerRes>> {
                override fun onResponse(
                        call: Call<ArrayList<NumberOfFarmerRes>>, response: Response<ArrayList<NumberOfFarmerRes>>) {
                    when (response.code()) {
                        200 -> {
                            listener.farmerListSuccess(response.body()!!)
                        }
                        400 -> {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                listener.farmerListError(jObjError.getString("error-message"))
                            } catch (e: Exception) {
                                listener.farmerListError(e.message.toString())
                            }
                        }
                        else -> {
                            listener.farmerListError("error to farmer List")
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<NumberOfFarmerRes>>, t: Throwable) {
                    listener.farmerListError(t.message.toString())
                }
            })

        } catch (e: Exception) {
            Timber.e(e);
        }
    }

    fun farmerData(listener: NumberOfFarmerInteractorCallback, customerId: String, commodityId: String, cc_Id: String, dateFrom: String, dateTo: String,
                   regionId: String) {
        try {
            val apiService = ApiClient.getScmClient().create(ApiInterface::class.java)
            val call = apiService.getFarmerData("Bearer ${userManager.token}", customerId, commodityId, cc_Id, dateTo, dateFrom, regionId)
            call.enqueue(object : Callback<FarmerDataRes> {
                override fun onResponse(
                        call: Call<FarmerDataRes>, response: Response<FarmerDataRes>) {
                    when (response.code()) {
                        200 -> {
                            listener.farmerDataSuccess(response.body()!!)
                        }
                        400 -> {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                listener.farmerDataError(jObjError.getString("error-message"))
                            } catch (e: Exception) {
                                listener.farmerDataError(e.message.toString())
                            }
                        }
                        else -> {
                            listener.farmerDataError("error to farmer Data")
                        }
                    }
                }

                override fun onFailure(call: Call<FarmerDataRes>, t: Throwable) {
                    listener.farmerDataError(t.message.toString())
                }
            })

        } catch (e: Exception) {
            Timber.e(e);
        }
    }

    fun allPayment(listener: PaymentInteractorCallback, customerId: String, commodityId: String, cc_Id: String,
                   date_to: String, date_from: String, region_id: String) {
        try {
            val apiService = ApiClient.getScmClient().create(ApiInterface::class.java)
            val call = apiService.getPaymentOverTime("Bearer ${userManager.token}", customerId, commodityId, cc_Id, date_from, date_to, region_id)
            call.enqueue(object : Callback<ArrayList<PaymentRes>> {
                override fun onResponse(call: Call<ArrayList<PaymentRes>>, response: Response<ArrayList<PaymentRes>>) {

                    when (response.code()) {
                        200 -> {
                            listener.paymentSuccess(response.body()!!)
                        }
                        400 -> {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                listener.paymentError(jObjError.getString("error-message"))
                            } catch (e: Exception) {
                                listener.paymentError(e.message.toString())
                            }
                        }
                        else -> {
                            listener.paymentError("error to payment")
                        }
                    }

                }

                override fun onFailure(call: Call<ArrayList<PaymentRes>>, t: Throwable) {
                    listener.paymentError(t.message.toString())
                }
            })

        } catch (e: Exception) {
            Timber.e(e);
        }
    }

    fun paymentChart(listener: PaymentInteractorCallback, customerId: String, commodityId: String, cc_Id: String,
                     date_to: String, date_from: String, region_id: String) {
        try {
            val apiService = ApiClient.getScmClient().create(ApiInterface::class.java)
            val call = apiService.getPaymentChart("Bearer ${userManager.token}", customerId, commodityId, cc_Id, date_from, date_to, region_id)
            call.enqueue(object : Callback<ArrayList<PaymentChartRes>> {
                override fun onResponse(
                        call: Call<ArrayList<PaymentChartRes>>,
                        response: Response<ArrayList<PaymentChartRes>>) {
                    when (response.code()) {
                        200 -> {
                            listener.paymentChartSuccess(response.body()!!)
                        }
                        400 -> {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                listener.paymentChartError(jObjError.getString("error-message"))
                            } catch (e: Exception) {
                                listener.paymentChartError(e.message.toString())
                            }
                        }
                        else -> {
                            listener.paymentChartError("error to payment Chart")
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<PaymentChartRes>>, t: Throwable) {
                    listener.paymentChartError(t.message.toString())
                }
            })

        } catch (e: Exception) {
            Timber.e(e);
        }
    }

    fun paymentList(listener: PaymentInteractorCallback, customerId: String, commodityId: String, cc_Id: String,
                    date_to: String, date_from: String, region_id: String) {
        try {
            val apiService = ApiClient.getScmClient().create(ApiInterface::class.java)
            val call = apiService.getPaymentList("Bearer ${userManager.token}", customerId, commodityId, cc_Id, date_from, date_to, region_id)
            call.enqueue(object : Callback<ArrayList<PaymentListRes>> {
                override fun onResponse(call: Call<ArrayList<PaymentListRes>>, response: Response<ArrayList<PaymentListRes>>) {

                    when (response.code()) {
                        200 -> {
                            listener.paymentListSuccess(response.body()!!)
                        }
                        400 -> {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                listener.paymentListError(jObjError.getString("error-message"))
                            } catch (e: Exception) {
                                listener.paymentListError(e.message.toString())
                            }
                        }
                        else -> {
                            listener.paymentListError("error to payment list")
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<PaymentListRes>>, t: Throwable) {
                    listener.paymentListError(t.message.toString())
                }
            })

        } catch (e: Exception) {
            Timber.e(e);
        }
    }


    fun allQuality(listener: QualityInteractorCallback, customerId: String, commodityId: String, analysis_name: String, ccId: String, date_to: String, date_from: String, regionId: String) {
        try {
            val apiService = ApiClient.getScmClient().create(ApiInterface::class.java)
            val call = apiService.getQuality("Bearer ${userManager.token}", customerId, commodityId, analysis_name, ccId, date_to, date_from, regionId)
            call.enqueue(object : Callback<ArrayList<QualityRes>> {
                override fun onResponse(
                        call: Call<ArrayList<QualityRes>>, response: Response<ArrayList<QualityRes>>) {

                    when (response.code()) {
                        200 -> {
                            listener.allQualityApiSuccess(response.body()!!)
                        }
                        400 -> {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                listener.allQualityApiError(jObjError.getString("error-message"))
                            } catch (e: Exception) {
                                listener.allQualityApiError(e.message.toString())
                            }
                        }
                        else -> {
                            listener.allQualityApiError("error to all Quality")
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<QualityRes>>, t: Throwable) {
                    listener.allQualityApiError(t.message.toString())
                }
            })

        } catch (e: Exception) {
            Timber.e(e);
        }
    }

    fun qualityGrade(listener: QualityInteractorCallback, commodityId: String) {
        try {
            val apiService = ApiClient.getScmClient().create(ApiInterface::class.java)
            val call = apiService.getQualityGarde("Bearer ${userManager.token}", commodityId)
            call.enqueue(object : Callback<ArrayList<QualityGradeRes>> {
                override fun onResponse(call: Call<ArrayList<QualityGradeRes>>, response: Response<ArrayList<QualityGradeRes>>) {

                    when (response.code()) {
                        200 -> {
                            listener.qualityGradeSuccess(response.body()!!)
                        }
                        400 -> {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                listener.qualityGradeError(jObjError.getString("error-message"))
                            } catch (e: Exception) {
                                listener.qualityGradeError(e.message.toString())
                            }
                        }
                        else -> {
                            listener.qualityGradeError("error to get quality Grade")
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<QualityGradeRes>>, t: Throwable) {
                    listener.qualityGradeError(t.message.toString())
                }
            })

        } catch (e: Exception) {
            Timber.e(e);
        }
    }

    fun analyticsName(listener: QualityInteractorCallback, commodityId: String) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
            val call = apiService.getAnalyticsName("Bearer ${userManager.token}", commodityId)
            call.enqueue(object : Callback<ArrayList<AnalyticsRes>> {
                override fun onResponse(
                        call: Call<ArrayList<AnalyticsRes>>,
                        response: Response<ArrayList<AnalyticsRes>>) {
                    when (response.code()) {
                        200 -> {
                            listener.analyticsSuccess(response.body()!!)
                        }
                        400 -> {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                listener.analyticsError(jObjError.getString("error-message"))
                            } catch (e: Exception) {
                                listener.analyticsError(e.message.toString())
                            }
                        }
                        else -> {
                            listener.analyticsError("error to analytics")
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<AnalyticsRes>>, t: Throwable) {
                    listener.analyticsError(t.message.toString())
                }
            })

        } catch (e: Exception) {
            Timber.e(e);
        }
    }

    fun qualityOverTime(listener: QualityInteractorCallback, customerId: String, commodityId: String, analysis_name: String, ccId: String, date_to: String, date_from: String, regionId: String) {
        try {
            val apiService = ApiClient.getScmClient().create(ApiInterface::class.java)
            val call = apiService.getQualityOverTime("Bearer ${userManager.token}", customerId, commodityId, analysis_name, ccId, date_to, date_from, regionId)
            call.enqueue(object : Callback<ArrayList<QualityOverTimeRes>> {
                override fun onResponse(
                        call: Call<ArrayList<QualityOverTimeRes>>, response: Response<ArrayList<QualityOverTimeRes>>) {
                    when (response.code()) {
                        200 -> {
                            listener.qualityOverTimeSuccess(response.body()!!)
                        }
                        400 -> {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                listener.qualityOverTimeError(jObjError.getString("error-message"))
                            } catch (e: Exception) {
                                listener.qualityOverTimeError(e.message.toString())
                            }
                        }
                        else -> {
                            listener.qualityOverTimeError("error to quality Over Time")
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<QualityOverTimeRes>>, t: Throwable) {
                    listener.qualityOverTimeError(t.message.toString())
                }
            })

        } catch (e: Exception) {
            Timber.e(e);
        }
    }

    fun qualityRange(listener: QualityInteractorCallback, commodityId: String, cc_id: String) {
        try {
            val apiService = ApiClient.getScmClient().create(ApiInterface::class.java)
            val call = apiService.getQualityRange("Bearer ${userManager.token}", commodityId, cc_id)
            call.enqueue(object : Callback<QualityRangeRes> {
                override fun onResponse(call: Call<QualityRangeRes>, response: Response<QualityRangeRes>) {

                    when (response.code()) {
                        200 -> {
                            listener.qualityRangeSuccess(response.body()!!)
                        }
                        400 -> {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                listener.qualityRangeError(jObjError.getString("error-message"))
                            } catch (e: Exception) {
                                listener.qualityRangeError(e.message.toString())
                            }
                        }
                        else -> {
                            listener.qualityRangeError("error to get quality Range")
                        }
                    }
                }

                override fun onFailure(call: Call<QualityRangeRes>, t: Throwable) {
                    listener.qualityRangeError(t.message.toString())
                }
            })

        } catch (e: Exception) {
            Timber.e(e);
        }
    }

    fun allQuantity(listener: QuantityInteractorCallback, customerId: String, commodityId: String, ccId: String, date_to: String, date_from: String, regionId: String) {
        try {
            val apiService = ApiClient.getScmClient().create(ApiInterface::class.java)
            val call = apiService.getQuantity("Bearer ${userManager.token}", customerId, commodityId, ccId, date_to, date_from, regionId)
            call.enqueue(object : Callback<QuantityRes> {
                override fun onResponse(
                        call: Call<QuantityRes>,
                        response: Response<QuantityRes>) {
                    when (response.code()) {
                        200 -> {
                            listener.allQuantityApiSuccess(response.body()!!)
                        }
                        400 -> {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                listener.allQuantityApiError(jObjError.getString("error-message"))
                            } catch (e: Exception) {
                                listener.allQuantityApiError(e.message.toString())
                            }
                        }
                        else -> {
                            listener.allQuantityApiError("error to Quantity")
                        }
                    }
                }

                override fun onFailure(call: Call<QuantityRes>, t: Throwable) {
                    listener.allQuantityApiError(t.message.toString())
                }
            })

        } catch (e: Exception) {
            Timber.e(e);
        }
    }

    fun collectionByCenter(listener: QuantityInteractorCallback, customerId: String, commodityId: String, ccId: String, date_to: String, date_from: String, regionId: String) {
        try {
            val apiService = ApiClient.getScmClient().create(ApiInterface::class.java)
            val call = apiService.getCollectionsByCenter("Bearer ${userManager.token}", customerId, commodityId, ccId, date_to, date_from, regionId)
            call.enqueue(object : Callback<ArrayList<CollectionByCenterRes>> {
                override fun onResponse(
                        call: Call<ArrayList<CollectionByCenterRes>>, response: Response<ArrayList<CollectionByCenterRes>>) {

                    when (response.code()) {
                        200 -> {
                            listener.collectionByCenterSuccess(response.body()!!)
                        }
                        400 -> {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                listener.collectionByCenterError(jObjError.getString("error-message"))
                            } catch (e: Exception) {
                                listener.collectionByCenterError(e.message.toString())
                            }
                        }
                        else -> {
                            listener.collectionByCenterError("error to get collection by center ")
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<CollectionByCenterRes>>, t: Throwable) {
                    listener.collectionByCenterError(t.message.toString())
                }
            })

        } catch (e: Exception) {
            Timber.e(e);
        }
    }

    fun collectionOverTime(listener: QuantityInteractorCallback, customerId: String, commodityId: String, ccId: String, date_to: String, date_from: String, regionId: String) {
        try {
            val apiService = ApiClient.getScmClient().create(ApiInterface::class.java)
            val call = apiService.getCollectionOverTime("Bearer ${userManager.token}", customerId, commodityId, ccId, date_to, date_from, regionId)
            call.enqueue(object : Callback<ArrayList<CollectionOverTimeRes>> {
                override fun onResponse(
                        call: Call<ArrayList<CollectionOverTimeRes>>, response: Response<ArrayList<CollectionOverTimeRes>>) {

                    when (response.code()) {
                        200 -> {
                            listener.collectionOverTimeSuccess(response.body()!!)
                        }
                        400 -> {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                listener.collectionOverTimeError(jObjError.getString("error-message"))
                            } catch (e: Exception) {
                                listener.collectionOverTimeError(e.message.toString())
                            }
                        }
                        else -> {
                            listener.collectionOverTimeError("error to get collection by center ")
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<CollectionOverTimeRes>>, t: Throwable) {
                    listener.collectionByCenterError(t.message.toString())
                }
            })

        } catch (e: Exception) {
            Timber.e(e);
        }
    }

    fun collectionByCenterRegion(listener: QuantityInteractorCallback, customerId: String, commodityId: String, ccId: String, date_to: String, date_from: String, regionId: String) {
        try {
            val apiService = ApiClient.getScmClient().create(ApiInterface::class.java)
            val call = apiService.getCollectionCenterRegion("Bearer ${userManager.token}", customerId, commodityId, ccId, date_to, date_from, regionId)
            call.enqueue(object : Callback<ArrayList<CollectionCenterRegionRes>> {
                override fun onResponse(
                        call: Call<ArrayList<CollectionCenterRegionRes>>, response: Response<ArrayList<CollectionCenterRegionRes>>) {
                    when (response.code()) {
                        200 -> {
                            listener.collectionCenterRegionSuccess(response.body()!!)
                        }
                        400 -> {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                listener.collectionCenterRegionError(jObjError.getString("error-message"))
                            } catch (e: Exception) {
                                listener.collectionCenterRegionError(e.message.toString())
                            }
                        }
                        else -> {
                            listener.collectionCenterRegionError("error to get collection center region")
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<CollectionCenterRegionRes>>, t: Throwable) {
                    listener.collectionCenterRegionError(t.message.toString())
                }
            })

        } catch (e: Exception) {
            Timber.e(e);
        }
    }

    fun collectionWeeklyMonthly(listener: QuantityInteractorCallback, customerId: String, commodityId: String, ccId: String, date_to: String, date_from: String, regionId: String) {
        try {
            val apiService = ApiClient.getScmClient().create(ApiInterface::class.java)
            val call = apiService.getCollectionByDates("Bearer ${userManager.token}", customerId, commodityId, ccId, date_to, date_from, regionId)
            call.enqueue(object : Callback<CollectionWeeklyMonthlyRes> {
                override fun onResponse(
                        call: Call<CollectionWeeklyMonthlyRes>, response: Response<CollectionWeeklyMonthlyRes>) {
                    when (response.code()) {
                        200 -> {
                            listener.collectionWeeklyMonthlySuccess(response.body()!!)
                        }
                        400 -> {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                listener.collectionWeeklyMonthlyError(jObjError.getString("error-message"))
                            } catch (e: Exception) {
                                listener.collectionWeeklyMonthlyError(e.message.toString())
                            }
                        }
                        else -> {
                            listener.collectionWeeklyMonthlyError("error to collection Weekly Monthly")
                        }
                    }
                }

                override fun onFailure(call: Call<CollectionWeeklyMonthlyRes>, t: Throwable) {
                    listener.collectionWeeklyMonthlyError(t.message.toString())
                }
            })

        } catch (e: Exception) {
            Timber.e(e);
        }
    }

}

interface AnalyticsInteractorCallback {

    fun allCategoryApiSuccess(body: ArrayList<CategoryTabsRes>)
    fun allCategoryApiError(msg: String)

    fun allCommodityApiSuccess(body: ArrayList<CommodityRes>)
    fun allCommodityApiError(msg: String)

    fun allCCenterTypeSuccess(body: ArrayList<InstallationCenterTypeRes>)
    fun allCCenterTypeError(msg: String)

    fun onGetRegionSuccess(body: ArrayList<RegionRes>)
    fun onGetRegionFailure(msg: String)

    fun allCentersApiSuccess(body: ArrayList<InstallationCenterRes>)
    fun allCentersApiError(msg: String)
}

interface NumberOfFarmerInteractorCallback {
    fun farmerListSuccess(body: ArrayList<NumberOfFarmerRes>)
    fun farmerListError(msg: String)

    fun farmerDataSuccess(body: FarmerDataRes)
    fun farmerDataError(msg: String)
}

interface PaymentInteractorCallback {
    fun paymentSuccess(body: ArrayList<PaymentRes>)
    fun paymentError(msg: String)

    fun paymentChartSuccess(body: ArrayList<PaymentChartRes>)
    fun paymentChartError(msg: String)

    fun paymentListSuccess(body: ArrayList<PaymentListRes>)
    fun paymentListError(msg: String)

}

interface QualityInteractorCallback {

    fun allQualityApiSuccess(body: ArrayList<QualityRes>)
    fun allQualityApiError(msg: String)

    fun qualityGradeSuccess(body: ArrayList<QualityGradeRes>)
    fun qualityGradeError(msg: String)

    fun analyticsSuccess(body: ArrayList<AnalyticsRes>)
    fun analyticsError(msg: String)

    fun qualityOverTimeSuccess(body: ArrayList<QualityOverTimeRes>)
    fun qualityOverTimeError(msg: String)

    fun qualityRangeSuccess(body: QualityRangeRes)
    fun qualityRangeError(msg: String)
}

interface QuantityInteractorCallback {
    fun allQuantityApiSuccess(body: QuantityRes)
    fun allQuantityApiError(msg: String)

    fun collectionByCenterSuccess(body: ArrayList<CollectionByCenterRes>)
    fun collectionByCenterError(msg: String)

    fun collectionOverTimeSuccess(body: ArrayList<CollectionOverTimeRes>)
    fun collectionOverTimeError(msg: String)

    fun collectionCenterRegionSuccess(body: ArrayList<CollectionCenterRegionRes>)
    fun collectionCenterRegionError(msg: String)

    fun collectionWeeklyMonthlySuccess(body: CollectionWeeklyMonthlyRes)
    fun collectionWeeklyMonthlyError(msg: String)

}