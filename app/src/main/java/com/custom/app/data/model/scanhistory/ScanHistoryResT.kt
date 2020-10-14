package com.custom.app.data.model.scanhistory

import com.google.gson.annotations.SerializedName

class ScanHistoryResT {

    @SerializedName("total_count")
    var total_count: Int? = null

    @SerializedName("scan_history_data")
    var scan_history_data: ArrayList<ScanData>? = null

}

class ScanData {

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

    @SerializedName("analysis")
    var analysisResults: ArrayList<AnalysisResults>? = null

    @SerializedName("approval")
    var approval: Int? = null

    @SerializedName("msg_time_stamp")
    var msg_time_stamp: String? = null

    @SerializedName("weight")
    var weight: String? = null

}

class AnalysisResults {

    @SerializedName("analysisName")
    var analysisName: String? = null

    @SerializedName("result")
    var result: String? = null

    @SerializedName("amountUnit")
    var amountUnit: String? = null

    @SerializedName("totalAmount")
    var totalAmount: String? = null

    @SerializedName("byDensityResult")
    var byDensityResult: String? = null

}