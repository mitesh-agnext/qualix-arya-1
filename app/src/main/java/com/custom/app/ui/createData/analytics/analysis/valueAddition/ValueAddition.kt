package com.custom.app.ui.createData.analytics.analysis.valueAddition

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.custom.app.R
import com.custom.app.ui.createData.analytics.utils.DemoBaseFragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart

class ValueAddition(commodityId: Int) : DemoBaseFragment() {

    private var pieView: PieChart? = null
    private var barView: BarChart? = null
    private var lineView: LineChart? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_numberoffarmers, container, false)

        return view
    }
}