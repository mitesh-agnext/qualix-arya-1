package com.agnext.qualixfarmer.fields.farmDetail

import android.content.Context

class FarmDetailInteractor (private val context: Context) {

    fun gatFarmDetail(listener: FarmDetailFinishedListener)
    {
        listener.onFarmDetailSuccess()
    }

    interface FarmDetailFinishedListener {
        fun onFarmDetailSuccess()
        fun onfFrmDetailFailure()
    }

}