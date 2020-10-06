package com.custom.app.ui.scan.list.detail

import com.custom.app.data.model.scanhistory.ScanData

sealed class ScanDetailState
object Loading : ScanDetailState()
data class Error(val message: String?) : ScanDetailState()
data class List(val scanHistory: ArrayList<ScanData>) : ScanDetailState()
object Delete : ScanDetailState()

object ApprovalSuccess : ScanDetailState()
object ApprovalFailure : ScanDetailState()

object FetchScanSuccess : ScanDetailState()
object FetchScanFailure : ScanDetailState()
object Token : ScanDetailState()