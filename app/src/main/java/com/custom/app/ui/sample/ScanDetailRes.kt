package com.custom.app.ui.sample

import com.custom.app.data.model.farmer.upload.FarmerItem
import com.specx.scan.data.model.analysis.AnalysisItem

class ScanDetailRes {

    var batch_id: String? = null
    val lot_id: String? = null
    val sample_id: String? = null
    val commodity_id: String? = null
    val commodity_name: String? = null
    val scan_by_user_code: String? = null
    val location: String? = null
    val device_serial_no: String? = null
    val truck_number: String? = null
    var weight = 0.0
    val quantity_unit: String? = null
    val area_covered: String? = null
    val farmer_detail: FarmerItem? = null
    val analysis: ArrayList<AnalysisItem>? = null

}