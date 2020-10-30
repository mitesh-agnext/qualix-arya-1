package com.custom.app.ui.sample.addResults

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.base.app.ui.base.BaseActivity
import com.custom.app.R
import com.custom.app.data.model.scan.UpdateScanRequest
import com.custom.app.data.model.scan.UpdateScanRes
import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import com.custom.app.ui.home.HomeActivity
import com.custom.app.util.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.specx.scan.data.model.analysis.AnalysisItem
import com.specx.scan.data.model.result.ResultItem
import kotlinx.android.synthetic.main.activity_add_scan_result.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddScanResult : BaseActivity(), View.OnClickListener {


    lateinit var testObject: ResultItem
    private lateinit var adapterScans: ScanResultListAdapter
    var analysis = ArrayList<AnalysisItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_scan_result)
        btn_upload.setOnClickListener(this)

        val selectObject = intent.getStringExtra("selectObject")
        val gson = Gson()
        if (selectObject != null) {
            val type = object : TypeToken<ResultItem>() {}.type
            testObject = gson.fromJson(selectObject, type)
        }
        initView()
    }

    private fun initView() {
        toolbar.title = getString(R.string.btn_addResults)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        updateRecycleView()
    }

    private fun updateRecycleView() {
        scanResults.layoutManager = LinearLayoutManager(this)
        adapterScans = ScanResultListAdapter(this, testObject.analyses)
        scanResults.adapter = adapterScans
    }

    override fun onClick(view: View?) {
        when (view) {
            btn_upload -> {
                updateScan()
            }
        }
    }

    private fun updateScan() {
        var updatedscan = ArrayList<UpdateScanRequest>()
        updatedscan = adapterScans.retrieveData() as ArrayList<UpdateScanRequest>

        val gson: Gson = Gson()
        val type = object : TypeToken<ArrayList<UpdateScanRequest>>() {}.type
        val listString: String = gson.toJson(updatedscan, type)
        val jsonArray: JSONArray = JSONArray(listString)
        val apiService = ApiClient.getScmClient().create(ApiInterface::class.java)
        val call = apiService.updateScan(userManager.token,
                testObject.sample.scanId,
                listString)
        call.enqueue(object : Callback<UpdateScanRes> {
            override fun onResponse(call: Call<UpdateScanRes>, response: Response<UpdateScanRes>) {
                when (response.code()) {
                    200 -> {
                        Log.e("Response", response.body()!!.message.toString())
                        Toast.makeText(this@AddScanResult, "Successfully added", Toast.LENGTH_LONG).show()
                        moveToHome()
                    }
                    else -> {
                        Toast.makeText(this@AddScanResult, "Error", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<UpdateScanRes>, t: Throwable) {
                Toast.makeText(this@AddScanResult, "Error", Toast.LENGTH_LONG).show()
            }
        })
    }

    fun moveToHome() {
        val bundle = Bundle()
        bundle.putString(Constants.FLOW, Constants.NAV_SPLASH)
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtras(bundle)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}