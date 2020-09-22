package com.custom.app.ui.scan.list.scanFrg

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.custom.app.R

class ScanContainerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_continer)
        initFrag()
    }

    private fun initFrag() {
        var scanHistoryFragment: ScanHistoryFragment = ScanHistoryFragment()
        supportFragmentManager.beginTransaction().add(R.id.container, scanHistoryFragment)
                .commit()
    }

}
