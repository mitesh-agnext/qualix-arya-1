package com.custom.app.ui.devices.deviceList

import android.os.Bundle
import android.view.View
import com.base.app.ui.base.BaseActivity
import com.custom.app.R
import com.custom.app.ui.device.list.DevicesData
import com.custom.app.util.Utils.Companion.timeStampDate
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_device_detail.*
import kotlinx.android.synthetic.main.custom_toolbar.*

class DeviceDetailActivity : BaseActivity() {

    var testObject: DevicesData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_detail)

        toolbar.title = getString(R.string.device_detail)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        val selectObject = intent.getStringExtra("selectObject")
        val gson = Gson()
        if (selectObject != null) {
            val type = object : TypeToken<DevicesData>() {}.type
            testObject = gson.fromJson(selectObject, type)
        }

        if (testObject!!.device_type_id.equals("3")) {
            deviceSubType_layout.visibility = View.VISIBLE
            deviceSensorProfile_layout.visibility = View.VISIBLE
            deviceGroup_layout.visibility = View.VISIBLE
            startOfService_layout.visibility = View.VISIBLE
            endOfService_layout.visibility = View.VISIBLE
        } else {
            deviceSubType_layout.visibility = View.GONE
            deviceSensorProfile_layout.visibility = View.GONE
            deviceGroup_layout.visibility = View.GONE
            startOfService_layout.visibility = View.GONE
            endOfService_layout.visibility = View.GONE
        }

        tvDeviceType_detail.text = testObject!!.device_type
        tvDeviceGroup_detail.text = testObject!!.device_group_desc
        tvDeviceSubType_detail.text = testObject!!.device_sub_type_desc
        tvSensorProfile_detail.text = testObject!!.sensor_profile_desc
        tvSerialNumber_detail.text = testObject!!.serial_number
        tvHwRevision_detail.text = testObject!!.hw_revision
        tvFwRevision_detail.text = testObject!!.fw_revision
        tvStartOfLife_detail.text = timeStampDate(testObject!!.start_of_life!!)
        tvEndOfLife_detail.text = timeStampDate(testObject!!.end_of_life!!)
//        tvStartOfService_detail.text = timeStampDate(testObject!!.start_of_service!!)
//        tvEndOfService_detail.text = timeStampDate(testObject!!.end_of_service!!)
    }
}