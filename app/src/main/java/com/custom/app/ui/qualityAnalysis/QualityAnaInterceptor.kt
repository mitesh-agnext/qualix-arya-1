package com.custom.app.ui.qualityAnalysis

import android.content.Context
import com.custom.app.data.model.scanhistory.ScanData
import com.custom.app.data.model.scanhistory.ScanHistoryResT
import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QualityAnaInterceptor(private val context: Context) {
    val apiService = ApiClient.getScmClient().create(ApiInterface::class.java)

    /**API HIT  to get scan list */
    fun getScansListIn(
            token: String,
            startDay: String,
            endDay: String,
            listener: OnQualityAnaFinishedListener
    ) {
        val call = apiService.getScans(token, "0", "100","1", endDay, startDay)
        call.enqueue(object : Callback<ScanHistoryResT> {
            override fun onResponse(
                    call: Call<ScanHistoryResT>,
                    response: Response<ScanHistoryResT>
            ) {

                when (response.code()) {
                    200 -> {
                        if (response.body()!!.scan_history_data!!.size >0)
                            listener.onScansListSuccess(response.body()!!.scan_history_data!!)
                        else
                            listener.onNoScansListSuccess()
                    }
                    401 -> {
                        listener.onTokenExpired()
                    }
                    else -> {
                        listener.onScansListFailure()
                    }
                }
            }

            override fun onFailure(call: Call<ScanHistoryResT>, t: Throwable) {
                listener.onScansListFailure()
            }
        })


    }

    /**API HIT to get Avg of Scan Data*/
    fun getAvgScanData(token: String, listener: OnQualityAnaFinishedListener) {
        val call = apiService.getAvgScanData(token)
        call.enqueue(object : Callback<ResAvgScanData> {
            override fun onFailure(call: Call<ResAvgScanData>, t: Throwable) {
                listener.onAvgScansDataFailure()
            }

            override fun onResponse(
                    call: Call<ResAvgScanData>,
                    response: Response<ResAvgScanData>
            ) {

                if (response.isSuccessful) {
                    if (response.body()!!.data != null)
                        listener.onAvgScansDataSuccess(response.body()!!.data[0])
                    else
                        listener.onAvgScansDataFailure()

                } else {

                    if (response.code() == 401) {
                        listener.onAvgScansDataFailure()
                    } else {
                        listener.onAvgScansDataFailure()
                    }

                }
            }
        })

    }

    fun getMonthFlcDataIn(listener: OnQualityAnaFinishedListener) {
        listener.onMonthFlcDataSuccess()
    }

    interface OnQualityAnaFinishedListener {
        //Scan list
        fun onScansListSuccess(scanList: ArrayList<ScanData>)
        fun onNoScansListSuccess()
        fun onScansListFailure()

        //Avg Scans Data
        fun onAvgScansDataSuccess(data: AvgData)
        fun onAvgScansDataFailure()

        fun onMonthFlcDataSuccess()
        fun onMonthFlcDataFailure()
        fun onTokenExpired()
    }

}