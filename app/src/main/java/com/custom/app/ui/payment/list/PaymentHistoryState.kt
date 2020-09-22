package com.custom.app.ui.payment.list

import com.custom.app.data.model.payment.PaymentHistoryRes

sealed class PaymentHistoryState
object Loading : PaymentHistoryState()
data class Error(val message: String?) : PaymentHistoryState()
data class List(val scanHistory: ArrayList<PaymentHistoryRes>) : PaymentHistoryState()
object Delete : PaymentHistoryState()

object CustomerList : PaymentHistoryState()
object CustomerError : PaymentHistoryState()
object CommodityList : PaymentHistoryState()
object CommodityError : PaymentHistoryState()
object InstallationCentersSuccess : PaymentHistoryState()
object InstallationCentersFailure : PaymentHistoryState()
object RegionSuccess : PaymentHistoryState()
object RegionFailure : PaymentHistoryState()
object Token : PaymentHistoryState()