package com.custom.app.network

import com.custom.app.data.model.country.CityRes
import com.custom.app.data.model.country.CountryRes
import com.custom.app.data.model.country.StateRes
import com.custom.app.data.model.payment.PaymentHistoryRes
import com.custom.app.data.model.quantity.CenterDetailRes
import com.custom.app.data.model.role.RoleRes
import com.custom.app.data.model.scanhistory.ScanData
import com.custom.app.data.model.scanhistory.ScanHistoryResT
import com.custom.app.data.model.section.DivisionRes
import com.custom.app.data.model.section.GardenRes
import com.custom.app.data.model.section.LocationItem
import com.custom.app.data.model.section.SectionRes
import com.custom.app.data.model.senseNext.SNAnalysisRes
import com.custom.app.data.model.senseNext.SNDeviceRes
import com.custom.app.ui.createData.analytics.analysis.numberOfFarmers.FarmerDataRes
import com.custom.app.ui.createData.analytics.analysis.numberOfFarmers.NumberOfFarmerRes
import com.custom.app.ui.createData.analytics.analysis.payments.PaymentChartRes
import com.custom.app.ui.createData.analytics.analysis.payments.PaymentListRes
import com.custom.app.ui.createData.analytics.analysis.payments.PaymentRes
import com.custom.app.ui.createData.analytics.analysis.quality.*
import com.custom.app.ui.createData.analytics.analysis.quantity.*
import com.custom.app.ui.createData.analytics.analyticsScreen.CategoryTabsRes
import com.custom.app.ui.createData.coldstore.coldstoreList.ColdstoreRes
import com.custom.app.ui.createData.deviationProfile.list.DeviationListRes
import com.custom.app.ui.createData.flcScan.*
import com.custom.app.ui.createData.flcScan.season.create.CommodityRes
import com.custom.app.ui.createData.flcScan.season.list.SeasonRes
import com.custom.app.ui.createData.foodType.list.FoodTypeListRes
import com.custom.app.ui.createData.instlCenter.createInstallationCenter.InstallationCenterTypeRes
import com.custom.app.ui.createData.profile.list.ProfileListRes
import com.custom.app.ui.createData.profileType.list.ProfileTypeListRes
import com.custom.app.ui.createData.region.site.create.RegionRes
import com.custom.app.ui.createData.region.site.list.SiteListRes
import com.custom.app.ui.createData.rules.config.list.RuleConfigRes
import com.custom.app.ui.customer.list.CustomerRes
import com.custom.app.ui.dashboard.CenterData
import com.custom.app.ui.device.add.DeviceGroupRes
import com.custom.app.ui.device.add.DeviceSubTypeRes
import com.custom.app.ui.device.add.DeviceTypeRes
import com.custom.app.ui.device.add.SensorProfileRes
import com.custom.app.ui.device.assign.DeviationProfileRes
import com.custom.app.ui.device.assign.InstallationCenterRes
import com.custom.app.ui.device.list.DevicesData
import com.custom.app.ui.farm.ResAddFarm
import com.custom.app.ui.farm.ResCropVariety
import com.custom.app.ui.farm.ResCrops
import com.custom.app.ui.farm.ResParticularFarm
import com.custom.app.ui.farm.farmList.FarmRes
import com.custom.app.ui.farm.farmList.ResAllFarms
import com.custom.app.ui.farm.farmList.ResBasic
import com.custom.app.ui.home.SubscribedDeviceRes
import com.custom.app.ui.qualityAnalysis.ResAvgScanData
import com.custom.app.ui.qualityAnalysis.ResUserScans
import com.custom.app.ui.sample.ScanDetailRes
import com.custom.app.ui.user.list.UserDataRes
import com.google.gson.JsonObject
import io.reactivex.Single
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    @GET("/api/customer?p=0&l=50")
    fun getCustomers(@Header("authorization") authorization: String): Call<ArrayList<CustomerRes>>

    @GET("/api/customer?p=0&l=50")
    fun getCustomersSearch(@Header("authorization") authorization: String, @Query("key_search") key_search: String): Call<ArrayList<CustomerRes>>

    @GET("/api/customer?p=0&l=50&customer_type=PARTNER")
    fun getPartner(@Header("authorization") authorization: String): Call<ArrayList<CustomerRes>>

    @POST("/api/customer")
    fun addCustomerUser(@Header("authorization") authorization: String, @Body data: HashMap<String, Any>): Call<CustomerRes>

    @PUT("/api/customer/{customer_id}")
    fun updateCustomer(@Header("authorization") authorization: String, @Path("customer_id") customer_id: String, @Body data: HashMap<String, Any>): Call<CustomerRes>

    @DELETE("/api/customer/{customer_id}")
    fun deleteCustomer(@Header("authorization") authorization: String, @Path("customer_id") customer_id: String): Call<CustomerRes>

    @PUT("/api/customer/approve")
    fun approvePartner(@Header("authorization") authorization: String, @Body data: HashMap<String, Any>): Call<CustomerRes>

    @GET("/api/role")
    fun getRole(@Header("authorization") authorization: String): Call<ArrayList<RoleRes>>

    @GET("/api/user?p=0&l=50")
    fun getUsersSearch(@Header("authorization") authorization: String, @Query("key_search") key_search: String): Call<ArrayList<UserDataRes>>

    @POST("/api/user")
    fun addUser(@Header("authorization") authorization: String, @Body data: HashMap<String, Any>): Call<UserDataRes>

    @GET("/api/locations")
    fun getLocations(@Header("authorization") authorization: String): Call<ArrayList<LocationItem>>

    @GET("/api/gardens")
    fun getGarden(@Header("authorization") authorization: String, @Query("locationId") locationId: String): Call<ArrayList<GardenRes>>

    @GET("/api/divisions")
    fun getDivision(@Header("authorization") authorization: String, @Query("gardenId") gardenId: String): Call<ArrayList<DivisionRes>>

    @GET("api/sections")
    fun getSection(@Header("authorization") authorization: String, @Query("divisionId") divisionId: String): Call<ArrayList<SectionRes>>

    @POST("/api/sections")
    fun addSection(@Header("authorization") authorization: String, @Body data: HashMap<String, Any>): Call<SectionRes>

    @PUT("/api/sections/{sectionId}")
    fun updateSection(@Header("authorization") authorization: String,
                      @Path("sectionId") sectionId: String,
                      @Body data: HashMap<String, Any>): Call<SectionRes>

    @POST("/api/gardens")
    fun addGarden(@Header("authorization") authorization: String,
                  @Body data: HashMap<String, Any>): Call<GardenRes>

    @POST("/api/divisions")
    fun addDivision(@Header("authorization") authorization: String,
                    @Body data: HashMap<String, Any>): Call<DivisionRes>

    @DELETE("/api/sections/{sectionId}")
    fun deleteSection(@Header("authorization") authorization: String,
                      @Path("sectionId") sectionId: String): Call<SectionRes>

    @GET("api/scan/history?p=0&l=100")
    fun scanHistory(@Header("authorization") authorization: String, @QueryMap options: Map<String, String>): Call<ScanHistoryResT>

    @GET("api/scan/payment-history")
    fun getPaymentHistory(@Header("authorization") authorization: String, @QueryMap options: Map<String, String>): Call<ArrayList<PaymentHistoryRes>>

    @POST("/api/device/inventory")
    fun addDevices(@Header("authorization") authorization: String, @Body file: JsonObject): Call<ResponseBody>

    @PUT("/api/device/inventory/{device_id}")
    fun updateDevices(@Header("authorization") authorization: String, @Body file: JsonObject, @Path("device_id") deviceId: Int): Call<ResponseBody>

    @GET("/api/device/filter?p=0&l=1000")
    fun searchDevices(@Header("authorization") authorization: String, @Query("keyword") keyword: String, @Query("operation_type") operation_type: String): Call<ArrayList<DevicesData>>

    @GET("/api/device/types")
    fun getDeviceType(@Header("authorization") authorization: String): Call<ArrayList<DeviceTypeRes>>

    @GET("/api/device/group")
    fun getDeviceGroup(@Header("authorization") authorization: String): Call<ArrayList<DeviceGroupRes>>

    @GET("/api/device/sub-type")
    fun getDeviceSubType(@Header("authorization") authorization: String): Call<ArrayList<DeviceSubTypeRes>>

    @GET("/api/device/sensor-profile")
    fun getSensorProfile(@Header("authorization") authorization: String): Call<ArrayList<SensorProfileRes>>

    @GET("/api/location?p=0&l=1000")
    fun getInstallationCenters(@Header("authorization") authorization: String,
                               @Query("search_keyword") keyword: String,
                               @Query("customer_id") customer_id: String,
                               @Query("region_id") region_id: String): Call<ArrayList<InstallationCenterRes>>

    @POST("/api/commercials")
    fun createInstallationCenter(@Header("authorization") authorization: String,
                                 @Body file: JsonObject): Call<ResponseBody>

    @PUT("/api/commercials/{id}")
    fun updateInstallationCenter(@Header("authorization") authorization: String,
                                 @Body file: JsonObject, @Path("id") centerId: Int): Call<ResponseBody>

    @GET("/api/commercials/location-types")
    fun getInstallationCenterType(@Header("authorization") authorization: String): Call<ArrayList<InstallationCenterTypeRes>>

    @DELETE("/api/commercials/{id}")
    fun deleteInstallationCenter(@Header("authorization") authorization: String,
                                 @Path("id") device_id: String): Call<ResponseBody>

    @GET("/api/cold-stores?p=0&l=1000")
    fun getColdstores(@Header("authorization") authorization: String,
                      @Query("search_keyword") keyword: String,
                      @Query("customer_id") customer_id: Int): Call<ArrayList<ColdstoreRes>>

    @DELETE("/api/cold-stores/{id}")
    fun deleteColdstore(@Header("authorization") authorization: String,
                        @Path("id") device_id: String): Call<ResponseBody>

    @POST("/api/cold-stores")
    fun createColdstore(@Header("authorization") authorization: String,
                        @Body file: JsonObject): Call<ResponseBody>

    @PUT("/api/cold-stores/{id}")
    fun updateColdstore(@Header("authorization") authorization: String,
                        @Body file: JsonObject, @Path("id") centerId: Int): Call<ResponseBody>

    @PUT("/api/device/provision/{device_id}")
    fun deviceProvisioning(@Header("authorization") authorization: String, @Body file: JsonObject,
                           @Path("device_id") deviceId: Int): Call<ResponseBody>

    @DELETE("/api/device/{id}")
    fun deleteDevice(@Header("authorization") authorization: String,
                     @Path("id") device_id: String): Call<ResponseBody>

    @PUT("/api/user/{user_id}")
    fun updateUser(@Header("authorization") authorization: String,
                   @Path("user_id") user_id: String,
                   @Body data: HashMap<String, Any>): Call<UserDataRes>

    @DELETE("/api/user/{user_id}")
    fun deleteUser(@Header("authorization") authorization: String,
                   @Path("user_id") user_id: String): Call<UserDataRes>

    @GET("/api/countries")
    fun getCountry(@Header("authorization") authorization: String): Call<ArrayList<CountryRes>>

    @GET("/api/states")
    fun getState(@Header("authorization") authorization: String,
                 @Query("country_id") country_id: String): Call<ArrayList<StateRes>>

    @GET("/api/cities")
    fun getCity(@Header("authorization") authorization: String,
                @Query("state_id") state_id: String): Call<ArrayList<CityRes>>

    @POST("/api/regions")
    fun createRegion(@Header("authorization") authorization: String,
                     @Body file: JsonObject): Call<ResponseBody>

    @PUT("/api/regions/{id}")
    fun updateRegion(@Header("authorization") authorization: String,
                     @Body file: JsonObject, @Path("id") regionId: Int): Call<ResponseBody>

    @GET("/api/regions?p=0&l=10")
    fun getRegion(@Header("authorization") authorization: String,
                  @Query("customer_id") customer_id: String): Call<ArrayList<RegionRes>>

    @DELETE("/api/regions/{id}")
    fun deleteRegion(@Header("authorization") authorization: String,
                     @Path("id") region_id: String): Call<ResponseBody>

    @POST("/api/sites")
    fun createSite(@Header("authorization") authorization: String,
                   @Body file: JsonObject): Call<ResponseBody>

    @PUT("/api/sites/{id}")
    fun updateSite(@Header("authorization") authorization: String,
                   @Path("id") siteId: Int, @Body file: JsonObject): Call<ResponseBody>

    @GET("/api/sites?p=0&l=100")
    fun getSite(@Header("authorization") authorization: String,
                @Query("key_search") keyword: String,
                @Query("region_id") region_id: Int): Call<ArrayList<SiteListRes>>

    @DELETE("/api/sites/{id}")
    fun deleteSite(@Header("authorization") authorization: String, @Path("id") site_id: Int): Call<ResponseBody>

    @GET("/api/profile?p=0&l=100")
    fun getProfile(@Header("authorization") authorization: String, @Query("customer_id") customer_id: Int): Call<ArrayList<ProfileListRes>>

    @DELETE("/api/profile/{id}")
    fun deleteProfile(@Header("authorization") authorization: String, @Path("id") site_id: Int): Call<ResponseBody>

    @PUT("/api/profile/{id}")
    fun updateProfile(@Header("authorization") authorization: String, @Path("id") profileId: Int, @Body file: JsonObject): Call<ResponseBody>

    @POST("/api/profile")
    fun createProfile(@Header("authorization") authorization: String,
                      @Body file: JsonObject): Call<ResponseBody>

    @POST("/api/profile/type")
    fun createProfileType(@Header("authorization") authorization: String,
                          @Body file: JsonObject): Call<ResponseBody>

    @PUT("/api/profile/type/{id}")
    fun updateProfileType(@Header("authorization") authorization: String,
                          @Path("id") profileTypeId: Int, @Body file: JsonObject): Call<ResponseBody>

    @GET("/api/profile/type?p=0&l=100")
    fun getProfileType(@Header("authorization") authorization: String,
                       @Query("customer_id") customer_id: Int): Call<ArrayList<ProfileTypeListRes>>

    @DELETE("/api/profile/type/{id}")
    fun deleteProfileType(@Header("authorization") authorization: String,
                          @Path("id") site_id: Int): Call<ResponseBody>

    @POST("/api/profile/food/type")
    fun createFoodType(@Header("authorization") authorization: String,
                       @Body file: JsonObject): Call<ResponseBody>

    @PUT("/api/profile/food/type/{id}")
    fun updateFoodType(@Header("authorization") authorization: String,
                       @Path("id") foodTypeId: Int, @Body file: JsonObject): Call<ResponseBody>

    @GET("/api/profile/food/type?p=0&l=100")
    fun getFoodType(@Header("authorization") authorization: String,
                    @Query("customer_id") customer_id: Int): Call<ArrayList<FoodTypeListRes>>

    @DELETE("/api/profile/food/type/{id}")
    fun deleteFoodType(@Header("authorization") authorization: String,
                       @Path("id") foodType_id: Int): Call<ResponseBody>

    @POST("/api/regions")
    fun createRuleConfig(@Header("authorization") authorization: String,
                         @Body file: JsonObject): Call<ResponseBody>

    @PUT("/api/regions/{id}")
    fun updateRuleConfig(@Header("authorization") authorization: String,
                         @Body file: JsonObject, @Path("id") regionId: Int): Call<ResponseBody>

    @GET("/api/regions?p=0&l=10")
    fun getRuleConfig(@Header("authorization") authorization: String,
                      @Query("customer_id") customer_id: Int): Call<ArrayList<RuleConfigRes>>

    @DELETE("/api/regions/{id}")
    fun deleteRuleConfig(@Header("authorization") authorization: String,
                         @Path("id") region_id: Int): Call<ResponseBody>

    @POST("/api/flc-scan")
    fun addFlc(@Header("authorization") authorization: String?,
               @Body options: HashMap<String?, String?>?): Call<ResponseBody?>?

    @POST("api/image")
    fun uploadTeaImg(@Body file: RequestBody?): Call<ImageUploadResult?>?

    @FormUrlEncoded
    @POST("/api/cleandir")
    fun cleanDir(@Field("userId") userId: String?,
                 @Field("sectionId") sectionId: String?): Call<FlcCleanDirRes?>?

    @FormUrlEncoded
    @POST("api/flc")
    fun getFLC(@Field("userId") userId: String?,
               @Field("sectionId") sectionId: String?): Call<FlcResultRes?>?

    @GET("/api/locations")
    fun getAllFlcLocations(@Header("authorization") authorization: String): Call<ArrayList<LocationList>>

    @GET("/api/gardens")
    fun getAllFlcGardens(@Header("authorization") authorization: String,
                         @Query("locationId") locationId: Int): Call<ArrayList<GardenList>>

    @GET("/api/divisions")
    fun getAllFlcDivisions(@Header("authorization") authorization: String,
                           @Query("gardenId") gardenId: Int): Call<ArrayList<DevisionList>>

    @GET("/api/sections")
    fun getAllFlcSections(@Header("authorization") authorization: String,
                          @Query("divisionId") divisionId: Int): Call<ArrayList<SectionData>>

    @GET("/api/sections/search/code?p=0&l=10")
    fun getSectionBySectionCode(@Header("authorization") authorization: String,
                                @Query("search_key") search_key: String): Call<ArrayList<SectionData>>

    @POST("/api/seasons")
    fun createSeason(@Header("authorization") authorization: String,
                     @Body file: JsonObject): Call<ResponseBody>

    @GET("/api/seasons?p=0&l=100")
    fun getSeason(@Header("authorization") authorization: String,
                  @Query("customer_id") customerId: Int,
                  @Query("key_search") key_search: String): Call<ArrayList<SeasonRes>>

    @PUT("/api/seasons/{id}")
    fun updateSeason(@Header("authorization") authorization: String,
                     @Body file: JsonObject, @Path("id") seasonId: Int): Call<ResponseBody>

    @DELETE("/api/seasons/{id}")
    fun deleteSeason(@Header("authorization") authorization: String,
                     @Path("id") seasonId: Int): Call<ResponseBody>

    @GET("/api/commodity")
    fun getCommodity(@Header("authorization") authorization: String, @Query("customer_id") customerId: String): Call<ArrayList<CommodityRes>>

    @POST("/api/deviations")
    fun createDeviation(@Header("authorization") authorization: String, @Body file: JsonObject): Call<ResponseBody>

    @GET("/api/regions?p=0&l=10")
    fun getDeviation(@Header("authorization") authorization: String, @Query("customer_id") customer_id: Int): Call<ArrayList<DeviationListRes>>

    @DELETE("/api/regions/{id}")
    fun deleteDeviation(@Header("authorization") authorization: String, @Path("id") region_id: Int): Call<ResponseBody>

    @GET("/api/commodity")
    fun getCommodityByCategory(@Header("authorization") authorization: String, @Query("commodityCategoryId") category_id: Int): Call<ArrayList<CommodityRes>>

    @GET("/api/commodity/analytics")
    fun getAnalyticsName(@Header("authorization") authorization: String, @Query("commodity_id") commodity_id: String): Call<ArrayList<AnalyticsRes>>

    @GET("/api/commercials/location/types")
    fun getCommercialCenterByRegionId(@Header("authorization") authorization: String,
                                      @Query("region_id") region_id: String): Call<ArrayList<InstallationCenterTypeRes>>

    @GET("/api/commodity/categories")
    fun getCategoriesLists(@Header("authorization") authorization: String, @Query("customer_id") customer_id: String): Call<ArrayList<CategoryTabsRes>>

    @GET("/api/analytics/quantity/collections")
    fun getCollectionsByCenter(@Header("authorization") authorization: String, @Query("customer_id") customer_id: String,
                               @Query("commodity_id") commodity_id: String, @Query("device_serial_no") serialNumber: String,
                               @Query("inst_center_id") collection_center_id: String, @Query("date_to") date_to: String,
                               @Query("date_from") date_from: String, @Query("region_id") region_id: String): Call<ArrayList<CollectionByCenterRes>>

    @GET("/api/analytics/collections-time")
    fun getCollectionOverTime(@Header("authorization") authorization: String, @Query("customer_id") customer_id: String,
                              @Query("commodity_id") commodity_id: String, @Query("inst_center_id") collection_center_id: String,
                              @Query("date_to") date_to: String, @Query("date_from") date_from: String,
                              @Query("region_id") region_id: String): Call<ArrayList<CollectionOverTimeRes>>

    @GET("/api/analytics/quantity")
    fun getQuantity(@Header("authorization") authorization: String, @Query("customer_id") customer_id: String,
                    @Query("commodity_id") commodity_id: String, @Query("device_serial_no") serialNumber: String,
                    @Query("inst_center_id") collection_center_id: String, @Query("date_to") date_to: String,
                    @Query("date_from") date_from: String, @Query("region_id") region_id: String): Call<QuantityRes>

    @GET("/api/analytics/center-region")
    fun getCollectionCenterRegion(@Header("authorization") authorization: String, @Query("customer_id") customer_id: String,
                                  @Query("commodity_id") commodity_id: String, @Query("inst_center_id") collection_center_id: String,
                                  @Query("date_to") date_to: String, @Query("date_from") date_from: String,
                                  @Query("region_id") region_id: String): Call<ArrayList<CollectionCenterRegionRes>>

    @GET("/api/scan/filter")
    fun getQuality(@Header("authorization") authorization: String, @Query("customer_id") customer_id: String,
                   @Query("commodity_id") commodity_id: String, @Query("analysis_name") analysis_name: String,
                   @Query("inst_center_id") collection_center_id: String, @Query("date_to") date_to: String,
                   @Query("date_from") date_from: String, @Query("region_id") region_id: String): Call<ArrayList<QualityRes>>

    @GET("/api/scan/filter/quality/grade")
    fun getQualityGarde(@Header("authorization") authorization: String, @Query("commodity_id") commodity_id: String): Call<ArrayList<QualityGradeRes>>

    @GET("/api/scan/filter/quality/overtime")
    fun getQualityOverTime(@Header("authorization") authorization: String, @Query("customer_id") customer_id: String,
                           @Query("commodity_id") commodity_id: String, @Query("analysis_name") analysis_name: String,
                           @Query("inst_center_id") collection_center_id: String, @Query("date_to") date_to: String,
                           @Query("date_from") date_from: String, @Query("region_id") region_id: String): Call<ArrayList<QualityOverTimeRes>>

    @GET("/api/scan/grade/factors")
    fun getQualityRange(@Header("authorization") authorization: String, @Query("commodity_id") commodity_id: String,
                        @Query("cc_id") cc_id: String): Call<QualityRangeRes>

    @GET("/api/analytics/client/list")
    fun getFarmerList(@Header("authorization") authorization: String, @Query("customer_id") customer_id: String,
                      @Query("commodity_id") commodity_id: String, @Query("inst_center_id") collection_center_id: String,
                      @Query("date_to") date_to: String, @Query("date_from") date_from: String,
                      @Query("region_id") region_id: String): Call<ArrayList<NumberOfFarmerRes>>

    @GET("/api/analytics/clients")
    fun getFarmerData(@Header("authorization") authorization: String, @Query("customer_id") customer_id: String,
                      @Query("commodity_id") commodity_id: String, @Query("inst_center_id") collection_center_id: String,
                      @Query("date_to") date_to: String, @Query("date_from") date_from: String,
                      @Query("region_id") region_id: String): Call<FarmerDataRes>

    @GET("/api/analytics/payment-overtime")
    fun getPaymentOverTime(@Header("authorization") authorization: String, @Query("customer_id") customer_id: String,
                           @Query("commodity_id") commodity_id: String, @Query("inst_center_id") collection_center_id: String,
                           @Query("date_to") date_to: String, @Query("date_from") date_from: String,
                           @Query("region_id") region_id: String): Call<ArrayList<PaymentRes>>

    @GET("/api/analytics/payment-chart")
    fun getPaymentChart(@Header("authorization") authorization: String, @Query("customer_id") customer_id: String,
                        @Query("commodity_id") commodity_id: String, @Query("inst_center_id") collection_center_id: String,
                        @Query("date_to") date_to: String, @Query("date_from") date_from: String,
                        @Query("region_id") region_id: String): Call<ArrayList<PaymentChartRes>>

    @GET("/api/analytics/payment-list")
    fun getPaymentList(@Header("authorization") authorization: String, @Query("customer_id") customer_id: String,
                       @Query("commodity_id") commodity_id: String, @Query("inst_center_id") collection_center_id: String,
                       @Query("date_to") date_to: String, @Query("date_from") date_from: String,
                       @Query("region_id") region_id: String): Call<ArrayList<PaymentListRes>>

    @GET("/api/analytics/increment-decrement")
    fun getCollectionByDates(@Header("authorization") authorization: String, @Query("customer_id") customer_id: String,
                             @Query("commodity_id") commodity_id: String, @Query("inst_center_id") collection_center_id: String,
                             @Query("date_to") date_to: String, @Query("date_from") date_from: String,
                             @Query("region_id") region_id: String): Call<CollectionWeeklyMonthlyRes>

    @GET("/api/cold-stores/map/detail")
    fun getSNColdStore(@Header("authorization") authorization: String, @QueryMap options: Map<String, String>): Call<ArrayList<SNDeviceRes>>

    @GET("/api/iot-scan/performance/{deviceId}")
    fun getSNAnalysis(@Header("authorization") authorization: String, @Path("deviceId") deviceId: String): Call<ArrayList<SNAnalysisRes>>

    @GET("/api/user?p=0&l=10000")
    fun getUsers(@Header("authorization") authorization: String, @Query("customer_id") customer_id: String): Call<ArrayList<UserDataRes>>

    @GET("/api/deviations")
    fun getDeviationProfiles(@Header("authorization") authorization: String): Call<ArrayList<DeviationProfileRes>>

    @GET("/api/device/customer-subscribed")
    fun getSubscribedDevices(@Header("authorization") authorization: String): Call<SubscribedDeviceRes>

    @GET("/api/scan/detail")
    fun getScanDetail(@Header("authorization") authorization: String, @Query("scan_id") scan_id: String): Call<ScanDetailRes>

    @GET("/api/scan/detail")
    fun getScanDetail2(@Header("authorization") authorization: String, @Query("scan_id") scan_id: String): Call<ScanData>

    //Farmer

    //Get All Farms for Farmer
    @GET("/api/farms?p=0&l=20")
    fun getAllFarms(@Header("authorization") authorization: String): Call<ArrayList<ResAllFarms>>

    //Delete Farm
    @DELETE("/api/farms/{farmId}")
    fun deleteFarm(@Header("authorization") authorization: String, @Path("farmId") farmId: String): Call<ResBasic>

    //Get Crops
    @GET("/api/crops")
    fun getCrops(@Header("authorization") authorization: String): Call<ResCrops>

    //Get Crop variety
    @GET("/api/crop/verities/{cropId}")
    fun getCropVariety(@Header("authorization") authorization: String, @Path("cropId") cropId: String): Call<ResCropVariety>

    //Add Farm
    @POST("/api/farms")
    fun addFarm(@Header("authorization") authorization: String, @Body data: HashMap<String, Any>): Call<ResAddFarm>

    //Get Particular Farm
    @GET("/api/farms/{farmId}")
    fun getParticularFarm(@Header("authorization") authorization: String, @Path("farmId") farmId: String): Call<ResParticularFarm>

    //Update Farm
    @PUT("/api/farms")
    fun updateFarm(@Header("authorization") authorization: String, @Body data: HashMap<String, Any>): Call<ResAddFarm>

    @GET("/api/scan/filter/farmers")
    fun getScans(
            @Header("authorization") authorization: String,
            @Query("p") p: String, @Query("l") l: String, @Query("farmer_id") farmerId: String,
            @Query("dateFrom") dateFrom: String, @Query("dateTo") dateTo: String
    ): Call<ScanHistoryResT>

    @GET("/api/user/scans/avg")
    fun getAvgScanData(@Header("authorization") authorization: String): Call<ResAvgScanData>

    @GET("/api/plot")
    fun getPlots(@Query("farmer_id") farmerId: String): Call<ArrayList<FarmRes>>

    @POST("/api/plot")
    fun addPlot(@Body data: HashMap<String, Any>): Call<ResAddFarm>

    @PUT("/api/scan/approve")
    fun approveReject(@Header("authorization") authorization: String, @Body file: JsonObject): Call<ResponseBody>

    @GET("/api/analytics/quantity-details-id")
    fun centerDetails(@Header("authorization") authorization: String, @QueryMap query: Map<String, String>): Call<CenterData>

}