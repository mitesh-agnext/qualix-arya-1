package com.custom.app.ui.payment.detail

import android.os.Bundle
import com.base.app.ui.base.BaseActivity
import com.custom.app.R
import com.custom.app.data.model.payment.PaymentHistoryRes
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_payment_detail.*
import kotlinx.android.synthetic.main.custom_toolbar.*

class PaymentDetailActivity : BaseActivity() {

    lateinit var testObject: PaymentHistoryRes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_detail)
        initView()
    }

    fun initView() {
        toolbar.title = getString(R.string.payment_history)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        setData()
    }

    /**Set Data in screen*/
    private fun setData() {
        //getting data from intent
        val selectObject = intent.getStringExtra("selectObject")
        val gson = Gson()
        if (selectObject != null) {
            val type = object : TypeToken<PaymentHistoryRes>() {}.type
            testObject = gson.fromJson(selectObject, type)
        }

        if (testObject != null) {
            tvValue1.text = testObject.paymentSum
            tvValue2.text = testObject.clientName
            tvValue3.text = testObject.commodityId
            tvValue4.text = testObject.quality
//            tvValue5.text = testObject.date
        }
    }
}