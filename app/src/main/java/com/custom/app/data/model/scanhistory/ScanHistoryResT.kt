package com.custom.app.data.model.scanhistory

import com.google.gson.annotations.SerializedName

class ScanHistoryResT {

    @SerializedName("total_count")
    var total_count: Int? = null
    @SerializedName("scan_history_data")
    var scan_history_data: ArrayList<ScanData>? = null

}

class ScanData{

    @SerializedName("batch_id")
    var batchId: String? = null
    @SerializedName("device_type_id")
    var deviceId: Int? = null
    @SerializedName("device_type")
    var deviceName: String? = null
    @SerializedName("device_serial_no")
    var deviceSerialNo: String? = null
    @SerializedName("scan_id")
    var scanId: String? = null
    @SerializedName("commodity_id")
    var commodityId: String? = null
    @SerializedName("commodity_name")
    var commodityName: String? = null
    @SerializedName("quality_score")
    var qualityScore: String? = null
    @SerializedName("total_count")
    var totalCount: String? = null
    @SerializedName("date_done")
    var dateDone: String? = null
    @SerializedName("analysis_results")
    var analysisResults: ArrayList<AnalysisResults>? = null
    @SerializedName("approval")
    var approval: Int? = null
}

class AnalysisResults {

    @SerializedName("analysis_type")
    var analysisType: String? = null
    @SerializedName("analysis_value")
    var analysisValue: String? = null
    @SerializedName("analysis_unit")
    var analysisUnit: String? = null
    @SerializedName("analysis_result")
    var analysisResult: String? = null

}