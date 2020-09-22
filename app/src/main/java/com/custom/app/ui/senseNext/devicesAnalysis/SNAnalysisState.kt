package com.custom.app.ui.senseNext.devicesAnalysis

sealed class SNAnalysisState
object Loading : SNAnalysisState()
object Token : SNAnalysisState()
object Error : SNAnalysisState()
object List : SNAnalysisState()