package com.custom.app.ui.senseNext.list

import com.custom.app.data.model.senseNext.SNDeviceRes
import com.custom.app.ui.customer.list.CustomerRes

sealed class SNDeviceListState
object Loading : SNDeviceListState()
object Token : SNDeviceListState()
data class Error(val message: String?) : SNDeviceListState()
data class List(val scanHistory: ArrayList<SNDeviceRes>) : SNDeviceListState()
object Delete : SNDeviceListState()

data class CustomerList(val customerList: ArrayList<CustomerRes>) : SNDeviceListState()
data class  CustomerError(val customerMessage: String?) : SNDeviceListState()

object AllRegionSuccess : SNDeviceListState()
object AllRegionFailure : SNDeviceListState()
object AllProfileTypeSuccess : SNDeviceListState()
object AllProfileTypeFailure : SNDeviceListState()
object AllProfileSuccess : SNDeviceListState()
object AllProfileFailure : SNDeviceListState()
object NoRecord:SNDeviceListState()