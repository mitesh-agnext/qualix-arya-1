package com.custom.app.ui.customer.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.custom.app.R
import com.custom.app.ui.customer.list.CustomerRes
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_customer_detail.*
import kotlinx.android.synthetic.main.custom_toolbar.*

class CustomerDetailActivity : AppCompatActivity() {
    var testObject: CustomerRes? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_detail)
    }

    fun initView() {
        toolbar.title = getString(R.string.customer_management)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        //Set default data in form
        val selectObject = intent.getStringExtra("selectObject")
        val gson = Gson()
        if (selectObject != null) {
            val type = object : TypeToken<CustomerRes>() {}.type
            testObject = gson.fromJson(selectObject, type)
            setDummyData(testObject!!)
        }
        etCustomerName.isEnabled = false
        etOfficialEmail.isEnabled = false
        etContactNumber.isEnabled = false
        etGst.isEnabled = false
        etPan.isEnabled = false
        etAddressLine1.isEnabled = false
        etBankName.isEnabled = false
        etBankBranch.isEnabled = false
        etBankAccountNumber.isEnabled = false
    }

    private fun setDummyData(testObject: CustomerRes) {
        etCustomerName.setText(testObject.name)
        etOfficialEmail.setText(testObject.email)
        etContactNumber.setText(testObject.contact_number)
        etGst.setText(testObject.gst)
        etPan.setText(testObject.pan)
        etAddressLine1.setText(testObject.address!![0].address1)
        etBankName.setText(testObject.bankDetails!![0].bank_name)
        etBankBranch.setText(testObject.bankDetails!![0].bank_address)
        etBankAccountNumber.setText(testObject.bankDetails!![0].bank_account_number)
    }
}
