package com.custom.app.ui.createData.analytics.analysis.quality

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.core.app.util.AlertUtil
import com.custom.app.CustomApp
import com.custom.app.R
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.createData.adapters.AnalyticsDropdownAdapter
import com.custom.app.ui.createData.adapters.QualityRangeAdapter
import com.custom.app.ui.createData.analytics.analyticsScreen.AnalysisScreen
import com.custom.app.ui.createData.analytics.analyticsScreen.AnalyticsInteractor
import com.custom.app.ui.createData.analytics.utils.DemoBaseFragment
import com.custom.app.ui.createData.analytics.utils.MyMarkerView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import javax.inject.Inject

class Quality : DemoBaseFragment(), OnChartValueSelectedListener {

    private var pieView: PieChart? = null
    private var lineView: LineChart? = null

    private lateinit var viewModel: QualityViewModel
    private lateinit var qualityGradeList: ArrayList<QualityGradeRes>
    private lateinit var qualityResponse: ArrayList<QualityRes>
    private lateinit var qualityAnalytics: ArrayList<AnalyticsRes>
    private lateinit var qualityOverTime: ArrayList<QualityOverTimeRes>

    private var selectedCustomerId: String = ""
    private var selectedCommodityId: String = ""
    private var selectedRegionId: String = ""
    private var selectedCenterId: String = ""
    private var fromDate: String = ""
    private var toDate: String = ""

    private var analyticsNameId: Int = 0
    private var analyticsName: String = ""
    private var highestQuality: TextView? = null
    private var lowestQuality: TextView? = null
    private var averageQuality: TextView? = null
    private var listLayout_Quality: LinearLayout? = null
    private var spAnalytics: Spinner? = null
    private var qualityData: RecyclerView? = null
    private var qualityRulesTabs: TabLayout? = null

    @Inject
    lateinit var interactor: AnalyticsInteractor
    companion object {
        fun newInstance(customerId: String, commodityId: String, regionId: String, centerId: String, date_to: String, date_from: String) =
                Quality().apply {
                    arguments = Bundle().apply {
                        putString("CUSTOMERID", customerId)
                        putString("COMMODITYID", commodityId)
                        putString("REGIONID", regionId)
                        putString("CENTERID", centerId)
                        putString("DATETO", date_to)
                        putString("DATEFROM", date_from)

                    }
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (requireActivity().application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_quality, container, false)

        viewModel = ViewModelProvider(this, QualityViewModelFactory(interactor))[QualityViewModel::class.java]
        viewModel.qualityState.observe(::getLifecycle, ::updateUI)

        spAnalytics = view.findViewById(R.id.spAnalytics)
        highestQuality = view.findViewById(R.id.highestQuality)
        lowestQuality = view.findViewById(R.id.lowestQuality)
        averageQuality = view.findViewById(R.id.averageQuality)
        listLayout_Quality = view.findViewById(R.id.listLayout_Quality)
        pieView = view.findViewById<PieChart>(R.id.chart1)
        lineView = view.findViewById(R.id.chart3)
        qualityData = view.findViewById(R.id.qualityData)
        qualityRulesTabs = view.findViewById(R.id.qualityRulesTabs)

//        selectedCommodityId = requireArguments().getInt("COMMODITYID")
//        viewModel.onAnalytics(selectedCommodityId)
//        setPieView()

        return view
    }

    private fun updateUI(screenStateSite: ScreenState<QualityState>?) {
        when (screenStateSite) {
            ScreenState.Loading -> {
                //progressListDevices.visibility = View.VISIBLE
            }
            is ScreenState.Render -> processLoginState(screenStateSite.renderState)
        }
    }

    private fun processLoginState(renderStateSite: QualityState) {
//        progressListDevices.visibility = View.GONE

        when (renderStateSite) {
            QualityState.QualityCountSuccess -> {
                if (viewModel.quality.value!!.size>0) {
                    qualityResponse = viewModel.quality.value!!
                    setQualityValues(qualityResponse)
                }
            }
            QualityState.QualityCountFailure -> {
                AlertUtil.showSnackBar(listLayout_Quality, viewModel.errorMessage, 2000)
            }
            QualityState.QualityGradeSuccess -> {
                if (viewModel.qualityGrade.value!!.size>0) {
                    qualityGradeList = viewModel.qualityGrade.value!!
                    setPieData(qualityGradeList)
                }
            }
            QualityState.QualityGradeFailure -> {
                AlertUtil.showSnackBar(listLayout_Quality, viewModel.errorMessage, 2000)
            }
            QualityState.AnalyticsSuccess -> {
                if (viewModel.analytics.value!!.size>0) {
                    qualityAnalytics = viewModel.analytics.value!!
                    updateAnalyticsSpinner(qualityAnalytics)
                }
            }
            QualityState.AnalyticsFailure -> {
                AlertUtil.showSnackBar(listLayout_Quality, viewModel.errorMessage, 2000)
            }
            QualityState.QualityRangeSuccess -> {
                if (viewModel.qualityRange.value!!.quality_rules!!.size>0) {
                    val qualityRange = viewModel.qualityRange.value!!
                    setTabs(qualityRange.quality_rules!!)
                }
            }
            QualityState.QualityRangeFailure -> {
                AlertUtil.showSnackBar(listLayout_Quality, viewModel.errorMessage, 2000)
            }
            QualityState.QualityOverTimeSuccess -> {
                if (viewModel.qualityOverTime.value!!.size>0) {
                    qualityOverTime = viewModel.qualityOverTime.value!!
                    setLineData(qualityOverTime)
                }
            }
            QualityState.QualityOverTimeFailure -> {
                AlertUtil.showSnackBar(listLayout_Quality, viewModel.errorMessage, 2000)
            }
        }
    }

    private fun setTabs(tabList: ArrayList<QualityRules>) {

        qualityRulesTabs!!.removeAllTabs()
        for (i in 0 until tabList.size) {
            val tabName = tabList[i].analysis_code
            qualityRulesTabs!!.addTab(qualityRulesTabs!!.newTab().setText(tabName))
        }
        updateRecycleView(tabList)
    }

    private fun updateRecycleView(qualityRuleList: ArrayList<QualityRules>) {
        qualityData!!.layoutManager = LinearLayoutManager(requireContext())
        val adapterQualityRange = QualityRangeAdapter(requireContext(), qualityRuleList)
        qualityData!!.adapter = adapterQualityRange
    }

    private fun updateAnalyticsSpinner(analytics: ArrayList<AnalyticsRes>) {

        val adapter = AnalyticsDropdownAdapter(requireContext(), analytics)
        spAnalytics!!.adapter = adapter
        spAnalytics!!.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                        analyticsNameId = analytics[position].commodity_analytic_id!!.toInt()
                        analyticsName = analytics[position].analytic_name!!

                        viewModel.onGetQuality(selectedCustomerId, selectedCommodityId, analyticsName, selectedCenterId, toDate, fromDate, selectedRegionId)
                        setLineView()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }
    }

    private fun setQualityValues(qualityResponse: ArrayList<QualityRes>) {

        highestQuality!!.text = qualityResponse[0].max_quality.toString()
        lowestQuality!!.text = qualityResponse[0].min_quality.toString()
        averageQuality!!.text = qualityResponse[0].avg_quality.toString()

    }

    private fun setPieView() {
        pieView!!.setUsePercentValues(false)
        pieView!!.getDescription().isEnabled = false
        pieView!!.setExtraOffsets(5f, 10f, 5f, 5f)
        pieView!!.setDragDecelerationFrictionCoef(0.95f)
        pieView!!.setDrawHoleEnabled(false)
        pieView!!.setRotationAngle(0f)
        pieView!!.setRotationEnabled(true)
        pieView!!.setHighlightPerTapEnabled(true)
        pieView!!.setOnChartValueSelectedListener(this)
        pieView!!.animateY(1400, Easing.EaseInOutQuad)

        val l = pieView!!.getLegend()
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.xEntrySpace = 7f
        l.yEntrySpace = 0f
        l.yOffset = 0f
        pieView!!.setEntryLabelColor(Color.WHITE)
        pieView!!.setEntryLabelTextSize(12f)

        viewModel.onGetQualityGrade(selectedCommodityId)
    }

    private fun setPieData(qualityGradeList: ArrayList<QualityGradeRes>) {
        val entries = ArrayList<PieEntry>()

        if (qualityGradeList.size != 0) {
            for (i in 0 until qualityGradeList.size) {
                if (qualityGradeList[i].scanCount != null) {
                    entries.add(PieEntry(qualityGradeList[i].scanCount!!.toFloat(), qualityGradeList[i].grade))
                }
            }
        }
        val dataSet = PieDataSet(entries, "Quality")
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f

        val colors = ArrayList<Int>()
        for (c in ColorTemplate.VORDIPLOM_COLORS) colors.add(c)
        for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)
        for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)
        for (c in ColorTemplate.LIBERTY_COLORS) colors.add(c)
        for (c in ColorTemplate.PASTEL_COLORS) colors.add(c)
        colors.add(ColorTemplate.getHoloBlue())
        dataSet.colors = colors

        val data = PieData(dataSet)
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)
        pieView!!.data = data
        pieView!!.highlightValues(null)
        pieView!!.invalidate()

    }

    private fun setLineView() {

        lineView!!.setBackgroundColor(Color.WHITE)
        lineView!!.getDescription().setEnabled(false)
        lineView!!.setTouchEnabled(true)
        lineView!!.setOnChartValueSelectedListener(this)
        lineView!!.setDrawGridBackground(false)
        val mv = MyMarkerView(requireContext(), R.layout.custom_marker_view)
        mv.setChartView(lineView!!)
        lineView!!.setMarker(mv)
        lineView!!.setDragEnabled(true)
        lineView!!.setScaleEnabled(true)
        lineView!!.setScaleXEnabled(true)
        lineView!!.setScaleYEnabled(true)
        lineView!!.setPinchZoom(true)

        val yAxis: YAxis

        yAxis = lineView!!.getAxisLeft()
        lineView!!.getAxisRight().setEnabled(false)
        yAxis.axisMaximum = 200f
        yAxis.axisMinimum = 0f

        viewModel.onGetQualityOverTime(selectedCustomerId, selectedCommodityId, analyticsName, selectedCenterId, toDate, fromDate, selectedRegionId)

        lineView!!.animateX(1500)

        val l: Legend = lineView!!.getLegend()
        l.form = Legend.LegendForm.LINE

    }

    private fun setLineData(qualityOverTime: ArrayList<QualityOverTimeRes>) {

        val values = ArrayList<Entry>()
        if (qualityOverTime.size != 0) {
            for (i in 0 until qualityOverTime.size) {
                if (qualityOverTime[i].quality_avg != null) {
                    val value = (qualityOverTime[i].quality_avg)!!.toFloat()
                    val date = (qualityOverTime[i].date_done)!!.toFloat()
                    values.add(Entry(date, value))
                }
            }
        }

        val xAxis = lineView!!.getXAxis()
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return SimpleDateFormat("dd MMM").format(value.toLong())
            }
        }
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true
        val set1 = LineDataSet(values, "Quality over time")

        set1.setDrawIcons(false)
        set1.lineWidth = 1f
        set1.circleRadius = 3f
        set1.setDrawCircleHole(false)
        set1.formLineWidth = 1f
        set1.formSize = 15f
        set1.valueTextSize = 9f
        set1.setDrawFilled(true)
        set1.fillFormatter = IFillFormatter { dataSet, dataProvider -> lineView!!.getAxisLeft().getAxisMinimum() }
        set1.fillColor = R.color.blue
        set1.notifyDataSetChanged()

        val dataSets = ArrayList<ILineDataSet>()
        dataSets.add(set1) // add the data sets

        val data = LineData(dataSets)

        lineView!!.setData(data)
        lineView!!.getData().notifyDataChanged()
        lineView!!.notifyDataSetChanged()

    }

    override fun onValueSelected(e: Entry?, h: Highlight) {}

    override fun onNothingSelected() {}

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        if (isVisibleToUser) {

            selectedCustomerId = AnalysisScreen.selectedCustomerId
            selectedCommodityId = AnalysisScreen.selectedCommodityId
            selectedCenterId = AnalysisScreen.selectedCenterId
            toDate = AnalysisScreen.epochToDate.toString()
            if (toDate.equals("null")) {
                toDate = ""
            }
            fromDate = AnalysisScreen.epochFromDate.toString()
            if (fromDate.equals("null")) {
                fromDate = ""
            }
            selectedRegionId = AnalysisScreen.selectedRegionId

            viewModel.onAnalytics(selectedCommodityId)
            viewModel.onGetQualityRange(selectedCommodityId, selectedCenterId)

            setPieView()

        }
    }

}