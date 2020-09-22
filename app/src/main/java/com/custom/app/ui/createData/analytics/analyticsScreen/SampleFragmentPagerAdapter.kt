package com.custom.app.ui.createData.analytics.analyticsScreen

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.custom.app.ui.createData.analytics.analysis.numberOfFarmers.NumberOfFarmers
import com.custom.app.ui.createData.analytics.analysis.payments.Payments
import com.custom.app.ui.createData.analytics.analysis.quality.Quality
import com.custom.app.ui.createData.analytics.analysis.quantity.Quantity

class SampleFragmentPagerAdapter(fm: FragmentManager?, val context: Context, val customerId: String, val commodityId: String, val regionId: String,
                                 val centerId: String, val centerTypeId: String, val dateTo: String, val dateFrom: String) : FragmentPagerAdapter(fm!!) {
    val PAGE_COUNT = 4
    private val tabTitles = arrayOf("Quantity", "Quality", "Number of farmers", "Payments")
    override fun getCount(): Int {
        return PAGE_COUNT
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> Quantity.newInstance(customerId,commodityId, regionId, centerId, dateTo, dateFrom)
            1 -> Quality.newInstance(customerId,commodityId,regionId,centerId,dateTo,dateFrom)
            2 -> NumberOfFarmers.newInstance(customerId,commodityId,regionId,centerId,dateTo,dateFrom)
            3 -> Payments.newInstance(customerId,commodityId,regionId,centerId,dateTo,dateFrom)

            else -> {
                return Quantity()
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return tabTitles[position]
    }
}