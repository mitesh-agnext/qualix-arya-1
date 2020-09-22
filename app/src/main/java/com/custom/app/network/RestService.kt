package com.custom.app.network

import com.custom.app.data.model.business.AcceptedAvgRes
import com.custom.app.data.model.category.CategoryDetailItem
import com.custom.app.data.model.count.customer.TotalCustomerRes
import com.custom.app.data.model.count.device.TotalDeviceRes
import com.custom.app.data.model.count.order.PurchaseOrderRes
import com.custom.app.data.model.count.user.TotalUserRes
import com.custom.app.data.model.farmer.upload.FarmerItem
import com.custom.app.data.model.login.LoginRequest
import com.custom.app.data.model.login.LoginResponse
import com.custom.app.data.model.oauth.OauthResponse
import com.custom.app.data.model.password.change.ChangePasswordRequest
import com.custom.app.data.model.password.change.ChangePasswordResponse
import com.custom.app.data.model.password.forgot.ForgotPasswordRequest
import com.custom.app.data.model.password.forgot.ForgotPasswordResponse
import com.custom.app.data.model.quality.QualityMapItem
import com.custom.app.data.model.quantity.QuantityDetailRes
import com.custom.app.data.model.supplier.SupplierItem
import com.custom.app.ui.createData.analytics.analysis.quality.QualityOverTimeRes
import com.custom.app.ui.createData.analytics.analysis.quality.QualityRes
import com.custom.app.ui.createData.analytics.analysis.quantity.*
import com.custom.app.ui.user.list.UserDataRes
import com.specx.scan.data.model.analysis.AnalysisResponse
import com.specx.scan.data.model.analytic.AnalyticItem
import com.specx.scan.data.model.commodity.CommodityItem
import com.specx.scan.data.model.variety.VarietyItem
import io.reactivex.Single
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*
import java.util.*

interface RestService {

    @GET("/oauth/authorize")
    fun oauth(@QueryMap query: Map<String, String>): Single<OauthResponse>

    @GET("/api/otp/verify")
    fun verifyOtp(@Query("otp") otp: String): Single<LoginResponse>

    @POST("/login?bearer=mobile")
    fun login(@Body request: RequestBody): Single<LoginResponse>

    @POST("/api/user/device/token")
    fun fcmToken(@Header("Authorization") accessToken: String, @Body request: LoginRequest): Single<ResponseBody>

    @POST("/api/forgotPassword")
    fun forgotPassword(@Body request: ForgotPasswordRequest): Single<ForgotPasswordResponse>

    @GET("/api/vendor/verification/{query}")
    fun verifyFarmer(@Path("query") query: String): Single<FarmerItem>

    @POST("/api/vendor")
    fun uploadFarmer(@Body request: FarmerItem): Single<FarmerItem>

    @GET("/api/commodity")
    fun commodities(@QueryMap query: Map<String, String>): Single<List<CommodityItem>>

    @GET("/api/commodity/analytics")
    fun analyses(@QueryMap query: Map<String, String>): Single<List<AnalyticItem>>

    @GET("/api/commodity/varieties")
    fun varieties(@Query("commodityId") commodityId: String): Single<List<VarietyItem>>

    @GET("/api/commodity/categories")
    fun categories(): Single<List<CategoryDetailItem>>

    @GET("/api/analytics/quantity-details")
    fun quantityDetail(@QueryMap query: Map<String, String>): Single<QuantityDetailRes>

    @GET("/api/scan/filter/daily/scan/scount")
    fun scanCount(@QueryMap query: Map<String, String>): Single<AcceptedAvgRes>

    @GET("/api/scan/filter/variance-average")
    fun varianceAvg(@QueryMap query: Map<String, String>): Single<AcceptedAvgRes>

    @GET("/api/scan/filter/accepted-average")
    fun acceptedAvg(@QueryMap query: Map<String, String>): Single<AcceptedAvgRes>

    @GET("/api/scan/filter")
    fun quality(@QueryMap query: Map<String, String>): Single<QualityRes>

    @GET("/api/scan/filter/quality/overtime")
    fun qualityOverTime(@QueryMap query: Map<String, String>): Single<List<QualityOverTimeRes>>

    @GET("/api/analytics/quantity")
    fun quantity(@QueryMap query: Map<String, String>): Single<QuantityRes>

    @GET("/api/analytics/quantity/collections")
    fun collectionByCenter(@QueryMap query: Map<String, String>): Single<List<CollectionByCenterRes>>

    @GET("/api/analytics/collections-time")
    fun collectionOverTime(@QueryMap query: Map<String, String>): Single<List<CollectionOverTimeRes>>

    @GET("/api/analytics/center-region")
    fun collectionRegion(@QueryMap query: Map<String, String>): Single<List<CollectionCenterRegionRes>>

    @GET("/api/analytics/increment-decrement")
    fun collectionWeekly(@QueryMap query: Map<String, String>): Single<CollectionWeeklyMonthlyRes>

    @GET("/api/scan/filter/quality-map")
    fun qualityMap(@QueryMap query: Map<String, String>): Single<List<QualityMapItem>>

    @GET("/api/analytics/farmers/list")
    fun suppliers(@QueryMap query: Map<String, String>): Single<ArrayList<SupplierItem>>

    @GET("/api/device/widget")
    fun totalDevices(): Single<TotalDeviceRes>

    @GET("/api/device/unassigned")
    fun unassignedDevices(): Single<TotalDeviceRes>

    @GET("/api/customer/count")
    fun totalCustomers(): Single<TotalCustomerRes>

    @GET("/api/user/count")
    fun totalUsers(): Single<TotalUserRes>

    @GET("/api/device/po-count")
    fun purchaseOrders(): Single<PurchaseOrderRes>

    @DELETE("/api/scans/remove/analysis")
    fun removeAnalyses(@Query("batchId") batchId: String): Single<AnalysisResponse>

    @PUT("/api/user/changePassword")
    fun changePassword(@Body request: ChangePasswordRequest): Single<ChangePasswordResponse>

    @POST("/logout?client_id=client-mobile&bearer=mobile")
    fun logout(@Header("authorization") accessToken: String): Single<Object>

    @GET("/api/user?p=0&l=50")
    fun users(): Single<ArrayList<UserDataRes>>

    @GET("/api/user?p=0&l=50")
    fun usersForCustomer(@Query("customer_id") customer_id: String): Single<ArrayList<UserDataRes>>

}