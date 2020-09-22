package com.specx.scan.network;

import com.specx.scan.data.model.assay.AssayResponse;
import com.specx.scan.data.model.factor.DataFactorResponse;
import com.specx.scan.data.model.result.UploadResultResponse;
import com.specx.scan.data.model.upload.UploadScanResponse;

import java.util.Map;

import io.reactivex.Single;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.QueryMap;

public interface ScanService {

    @POST("/api/physical-scans/upload/physical-image")
    @Headers({"Physical-Scan: true"})
    Single<AssayResponse> analyse(@QueryMap Map<String, Object> query, @Body RequestBody request);

    @GET("/api/scans")
    Single<DataFactorResponse> dataFactor(@QueryMap Map<String, Object> query);

    @POST("/api/chemical/result")
    @Headers({"Chemical-Result: true"})
    Single<UploadResultResponse> uploadChemicalSpectra(@Body RequestBody request);

    @POST("/api/scan")
    Single<UploadScanResponse> uploadChemicalResult(@Body RequestBody request);

    @PUT("/api/scan")
    Single<UploadScanResponse> updateChemicalResult(@Body RequestBody request);

}