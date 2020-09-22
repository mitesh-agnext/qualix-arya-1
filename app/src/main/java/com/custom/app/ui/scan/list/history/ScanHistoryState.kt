package com.custom.app.ui.scan.list.history

import com.custom.app.data.model.scanhistory.ScanData

sealed class ScanHistoryState
object Loading : ScanHistoryState()
data class Error(val message: String?) : ScanHistoryState()
data class List(val scanHistory: ArrayList<ScanData>) : ScanHistoryState()
object Delete : ScanHistoryState()

object CustomerList : ScanHistoryState()
object CustomerError : ScanHistoryState()
object CommodityList : ScanHistoryState()
object CommodityError : ScanHistoryState()
object InstallationCentersSuccess : ScanHistoryState()
object InstallationCentersFailure : ScanHistoryState()
object RegionSuccess : ScanHistoryState()
object RegionFailure : ScanHistoryState()

object ScanListSuccess: ScanHistoryState()
object ScanListFailure: ScanHistoryState()

object DeviceTypeSuccess : ScanHistoryState()
object DeviceTypeFailure : ScanHistoryState()
object Token : ScanHistoryState()