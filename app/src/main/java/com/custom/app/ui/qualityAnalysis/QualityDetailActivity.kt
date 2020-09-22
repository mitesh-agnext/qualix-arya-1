package com.custom.app.ui.qualityAnalysis

//import com.agnext.qualixfarmer.R
//import com.agnext.qualixfarmer.base.BaseActivity
//import com.agnext.qualixfarmer.network.Response.ScanDetail
import android.os.Bundle
import android.view.View
import com.base.app.ui.base.BaseActivity
import com.custom.app.R
import com.custom.app.data.model.scanhistory.ScanHistoryResT
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_quality_detail.*
import kotlinx.android.synthetic.main.custom_toolbar.*

class QualityDetailActivity : BaseActivity() {
    lateinit var scanDetailObject: ScanDetail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quality_detail)

        //Init View
        initViews()
        setData()
    }

    /**Init View*/
    private fun initViews() {
        toolbar.title=getString(R.string.scan_detail)
        setSupportActionBar(toolbar)
        //enabling back button
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
    }

    /**Set Data*/
    private fun setData()
    {
        //getting data from intent
        val selectObject = intent.getStringExtra("selectObject")
        val gson = Gson()
        if (selectObject != null) {
            val type = object : TypeToken<ScanHistoryResT>() {}.type
            scanDetailObject = gson.fromJson(selectObject, type)
        }

        if(scanDetailObject!=null) {
            tvFLC.text = scanDetailObject.qualityScore
            tvGarden.text = scanDetailObject.ccName
            tvSection.text = scanDetailObject.sectionName
            tvDate.text = "${getString(R.string.date)}  ${scanDetailObject.dateDone}"
           // tvTime.text = "${getString(R.string.time)}  ${scanDetailObject.timeCreatedOn}"
            tvOneLeafBud.text = scanDetailObject.oneLeafBud
            tvTwoLeafBud.text = scanDetailObject.twoLeafBud
            tvThreeLeafBud.text = scanDetailObject.threeLeafBud
            tvOneLeafBanjhi.text = scanDetailObject.oneLeafBanjhi
            tvTwoLeafBanjhi.text = scanDetailObject.twoLeafBanjhi
            tvOneBudCount.text = scanDetailObject.oneBudCount
            tvOneLeafCount.text = scanDetailObject.oneLeafCount
            tvTwoLeafCount.text = scanDetailObject.twoLeafCount
            tvOneBanjhiCount.text = scanDetailObject.oneBanjhiCount
            if(scanDetailObject.areaCovered!=null)
            {
                cdSession.visibility= View.VISIBLE
                tvAreaCovered.text=scanDetailObject.areaCovered
                seasonId.text=scanDetailObject.seasonId
                tvTotalWeight.text=scanDetailObject.totalWeight
            }
        }
    }

    /**System Override methods*/
    //1 OnBack Press
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
