package com.custom.app.ui.senseNext.deviceDetail

import android.content.Intent
import android.os.Bundle
import com.base.app.ui.base.BaseActivity
import com.custom.app.R
import com.custom.app.data.model.senseNext.SNDeviceRes
import com.custom.app.ui.senseNext.devicesAnalysis.SNAnalysisActivity
import com.custom.app.util.Utils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_s_n_device_detail.*
import kotlinx.android.synthetic.main.custom_toolbar.*

class SNDeviceDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_s_n_device_detail)
        initView()
    }

    fun initView() {
        toolbar.title = "Cold Store Detail"
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)


        val selectObject = intent.getStringExtra("selectObject")
        val gson = Gson()
        if (selectObject != null) {
            val type = object : TypeToken<SNDeviceRes>() {}.type
            var testObject: SNDeviceRes = gson.fromJson(selectObject, type)

            if (testObject.online != null)
                if (testObject.online!!) {
                    tvStatus.text = "Online"
                    ivStatus.setImageResource(R.drawable.ic_online);
                } else {
                    tvStatus.text = "Offline"
                    ivStatus.setImageResource(R.drawable.ic_offline);
                }
            if (testObject.escalationLevel != null) {
                tvLevel.text="Level ${testObject.escalationLevel}"
                when (testObject.escalationLevel) {
                    "0" -> {
                        ivEscalation.setImageResource(R.drawable.grey_down_thumb);
                    }
                    "1" -> {
                        ivEscalation.setImageResource(R.drawable.level1);
                    }
                    "2" -> {
                        ivEscalation.setImageResource(R.drawable.level2);
                    }
                    "3" -> {
                        ivEscalation.setImageResource(R.drawable.level3);
                    }
                    else->{
                        ivEscalation.setImageResource(R.drawable.like_green);
                        tvLevel.text = "No escalation"

                    }
                }
            }
            else{
                tvLevel.text = "No escalation"
            }
            tvProfile.text = testObject.profileName
            tvStoreMode.text = testObject.profileTypeName
            tvDetailCSTemp.text = "${testObject.temp} °C"
            tvDetailCSBattery.text = "${testObject.batteryLevel} %"
            tvProfileType.text=" ${testObject.profileTypeId}"
            tvFoodType.text=testObject.profileFoodTypeName
            if (testObject.date != null)
                tvUpdatedDate.text = "Last updated:  ${Utils.getTimeFromEpoch(testObject.date!!.toLong())}"
            else tvUpdatedDate.text = "Last updated : No record"

            btAnalysis.setOnClickListener {
                val deviceId = testObject.deviceId
                val intent = Intent(this@SNDeviceDetailActivity, SNAnalysisActivity::class.java)
                intent.putExtra("deviceId", deviceId)
                startActivity(intent)
            }
        }
    }
}
