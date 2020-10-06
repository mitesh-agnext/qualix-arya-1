package com.custom.app.ui.sample

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import com.core.app.util.Util
import com.custom.app.data.model.farmer.upload.FarmerItem
import com.custom.app.data.model.section.LocationItem
import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import com.data.app.db.table.ResultTable
import com.google.gson.Gson
import com.specx.scan.data.model.commodity.CommodityItem
import com.specx.scan.data.model.sample.SampleItem
import com.squareup.sqlbrite3.BriteDatabase
import com.squareup.sqlbrite3.BriteDatabase.Transaction
import com.user.app.data.UserManager
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class SampleInteractor(val userManager: UserManager, val database: BriteDatabase) {

    fun fetchLocation(listener: DataCallback) {
        val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)

        val call = apiService.getLocations("Bearer ${userManager.token}")
        call.enqueue(object : Callback<ArrayList<LocationItem>> {
            override fun onResponse(call: Call<ArrayList<LocationItem>>, response: Response<ArrayList<LocationItem>>) {

                when {
                    response.isSuccessful -> {
                        if (response.code()==204){
                            listener.onError("No record found")
                        }
                        else {
                            listener.onLocationSuccess(response.body()!!)
                        }
                    }
                    else -> {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        when {
                            else -> listener.onError(jObjError.getString("error-message"))
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<LocationItem>>, t: Throwable) {
                listener.onError(t.message.toString())
            }
        })
    }

    fun fetchAndSaveScanDetail(scanId: String, batchId: String, deviceId: Int, listener: DataCallback) {
        val apiService = ApiClient.getScmClient().create(ApiInterface::class.java)

        val call = apiService.getScanDetail("Bearer ${userManager.token}", scanId)
        call.enqueue(object : Callback<ScanDetailRes> {
            override fun onResponse(call: Call<ScanDetailRes>, response: Response<ScanDetailRes>) {

                when {
                    response.isSuccessful -> {
                        if (response.code()==204){
                            listener.onError("No record found")
                        }
                        else {
                            listener.onFetchScanDetailSuccess(response.body()!!)
                            addResultInDb(scanId, deviceId, batchId, response.body()!!)
                        }
                    }
                    else -> {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        when {
                            else -> listener.onError(jObjError.getString("error-message"))
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ScanDetailRes>, t: Throwable) {
                listener.onError(t.message.toString())
            }
        })
    }

    fun addResultInDb(scanId: String, deviceId: Int, batchId: String, scanDetail: ScanDetailRes) {
        val transaction: Transaction = database.newTransaction()
        try {
            val sample = SampleItem(scanDetail.sample_id, scanDetail.weight);
            sample.scanId = scanId
            sample.quantityUnit = scanDetail.quantity_unit
            val commodity = CommodityItem(scanDetail.commodity_id, scanDetail.commodity_name);
            val values = ResultTable.Builder()
                    .userId(userManager.userId)
                    .batchId(if (deviceId == 1 || deviceId == 4) batchId else scanDetail.batch_id)
                    .location(scanDetail.location)
                    .sample(Gson().toJson(sample))
                    .commodity(Gson().toJson(commodity))
                    .serialNumber(scanDetail.device_serial_no)
                    .farmerCode(scanDetail.farmer_detail?.code)
                    .isUploaded(deviceId != 1 && deviceId != 4)

            values.scanResult(Gson().toJson(scanDetail.analysis))
                    .datetime(Util.getDatetime())

            val id: Long = database.insert(ResultTable.TABLE_NAME, SQLiteDatabase.CONFLICT_IGNORE, values.build())
            if (id == -1L) {
                database.update(ResultTable.TABLE_NAME, SQLiteDatabase.CONFLICT_NONE, values.build(),
                        ResultTable.BATCH_ID + " = ?", scanDetail.batch_id)
            }
            transaction.markSuccessful()
        } catch (e: SQLiteException) {
            Timber.e(e)
        } catch (e: NullPointerException) {
            Timber.e(e)
        } finally {
            transaction.end()
        }
    }

    fun updateResultInDb(batchId: String, farmer: FarmerItem, sample: SampleItem, scanDetail: ScanDetailRes, listener: DataCallback) {
        val transaction: Transaction = database.newTransaction()
        try {
            val commodity = CommodityItem(scanDetail.commodity_id, scanDetail.commodity_name)
            val values = ResultTable.Builder()
                    .userId(userManager.userId)
                    .batchId(batchId)
                    .sample(Gson().toJson(sample))
                    .commodity(Gson().toJson(commodity))
                    .serialNumber(scanDetail.device_serial_no)
                    .farmerCode(if (farmer.code != "X") farmer.code else null)
                    .isUploaded(false)

            values.scanResult(Gson().toJson(scanDetail.analysis))
                    .datetime(Util.getDatetime())

            database.update(ResultTable.TABLE_NAME, SQLiteDatabase.CONFLICT_NONE, values.build(),
                    ResultTable.BATCH_ID + " = ?", batchId)

            transaction.markSuccessful()

            listener.onSaveScanDetailSuccess()
        } catch (e: SQLiteException) {
            Timber.e(e)
            listener.onError(e.message)
        } catch (e: NullPointerException) {
            Timber.e(e)
            listener.onError(e.message)
        } finally {
            transaction.end()
        }
    }

    interface DataCallback {

        fun onLocationSuccess(locations: List<LocationItem>)
        fun onFetchScanDetailSuccess(scanDetail: ScanDetailRes)
        fun onSaveScanDetailSuccess()
        fun onError(msg: String?)

    }
}