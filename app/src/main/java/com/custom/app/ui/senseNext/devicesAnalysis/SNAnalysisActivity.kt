package com.custom.app.ui.senseNext.devicesAnalysis

import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.base.app.ui.base.BaseActivity
import com.core.app.util.AlertUtil
import com.custom.app.CustomApp
import com.custom.app.R
import com.custom.app.data.model.senseNext.SNAnalysisRes
import com.custom.app.ui.createData.analytics.utils.MyMarkerView
import com.custom.app.util.Utils
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import kotlinx.android.synthetic.main.activity_s_n_analysis.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.layout_progress.*
import javax.inject.Inject

class SNAnalysisActivity : BaseActivity(), OnChartValueSelectedListener {
    private lateinit var viewModel: SNAnalysisVM
    @Inject
    lateinit var interactor: SNAnalysisInteractor
    lateinit var deviceId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_s_n_analysis)
        initView()
    }

    fun initView() {
        toolbar.title = "Analysis"
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        deviceId = intent.getStringExtra("deviceId")
        viewModel = ViewModelProvider(this,
                SNAnalysisVMFactory(interactor))[SNAnalysisVM::class.java]
        viewModel.analysisState.observe(::getLifecycle, ::setViewState)

        setFilteredLineView()
    }

    private fun setViewState(state: SNAnalysisState) {
        when (state) {
            is Loading -> {
                progress.visibility = View.VISIBLE
            }
            is List -> {
                progress.visibility = View.GONE
                setFilteredLineData(viewModel.analysisList)
            }
            is Error -> {
                progress.visibility = View.GONE
                AlertUtil.showToast(this, viewModel.errorMsg)
            }
            is Token -> {
                progress.visibility = View.GONE
                AlertUtil.showToast(this, getString(R.string.token_expire))
                Utils.tokenExpire(this@SNAnalysisActivity)
            }
        }
    }

    private fun setFilteredLineView() {
        filteredlineView!!.setBackgroundColor(Color.WHITE)
        filteredlineView!!.getDescription().setEnabled(false)
        filteredlineView!!.setTouchEnabled(true)
        filteredlineView!!.setOnChartValueSelectedListener(this)
        filteredlineView!!.setDrawGridBackground(false)

        val mv = MyMarkerView(this, R.layout.custom_marker_view)
        mv.setChartView(filteredlineView!!)
        filteredlineView!!.setMarker(mv)
        filteredlineView!!.setDragEnabled(true)
        filteredlineView!!.setScaleEnabled(true)
        filteredlineView!!.setPinchZoom(true)

        val xAxis: XAxis
        xAxis = filteredlineView!!.getXAxis()

        val yAxis: YAxis
        yAxis = filteredlineView!!.getAxisLeft()
        filteredlineView!!.getAxisRight().setEnabled(false)
        yAxis.axisMaximum = 200f
        yAxis.axisMinimum = 0f

        viewModel.getAnalysis(deviceId)

        filteredlineView!!.animateX(1500)

        val l: Legend = filteredlineView!!.getLegend()
        l.form = Legend.LegendForm.LINE
    }

    private fun setFilteredLineData(collectionWeeklyMonthly: ArrayList<SNAnalysisRes>) {
        val values = ArrayList<Entry>()

        if (collectionWeeklyMonthly.size != 0) {
            for (i in 0 until collectionWeeklyMonthly.size) {
                val value = (collectionWeeklyMonthly[i].temp)!!.toFloat()
                values.add(Entry(i.toFloat(), value))
            }
        }
        val set1: LineDataSet
        if (filteredlineView!!.getData() != null && filteredlineView!!.getData().getDataSetCount() > 0) {
            set1 = filteredlineView!!.getData().getDataSetByIndex(0) as LineDataSet
            set1.values = values
            set1.notifyDataSetChanged()
            filteredlineView!!.getData().notifyDataChanged()
            filteredlineView!!.notifyDataSetChanged()
        } else {
            set1 = LineDataSet(values, "Temp")
            set1.setDrawIcons(false)

            set1.enableDashedLine(10f, 5f, 0f)

            set1.color = Color.BLACK
            set1.setCircleColor(Color.BLACK)

            set1.lineWidth = 1f
            set1.circleRadius = 3f

            set1.setDrawCircleHole(false)

            set1.formLineWidth = 1f
            set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            set1.formSize = 15f

            set1.valueTextSize = 9f

            set1.enableDashedHighlightLine(10f, 5f, 0f)

            set1.setDrawFilled(true)
            set1.fillFormatter = IFillFormatter { dataSet, dataProvider -> filteredlineView!!.getAxisLeft().getAxisMinimum() }

            set1.fillColor = R.color.blue

            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(set1) // add the data sets

            val data = LineData(dataSets)

            filteredlineView!!.setData(data)
        }
    }

    override fun onNothingSelected() {
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
    }
}
