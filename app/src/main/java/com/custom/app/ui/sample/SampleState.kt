package com.custom.app.ui.sample

import com.custom.app.data.model.section.LocationItem

sealed class SampleState
object Loading : SampleState()
data class Error(val message: String?) : SampleState()
data class Locations(val locations: List<LocationItem>) : SampleState()
data class ScanDetail(val scanDetail: ScanDetailRes) : SampleState()
object ShowResult : SampleState()
object TokenExpire : SampleState()