package com.custom.app.ui.createData.analytics.analysis.quantity

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.core.app.util.AlertUtil
import com.custom.app.CustomApp
import com.custom.app.R
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.createData.analytics.analyticsScreen.AnalysisScreen
import com.custom.app.ui.createData.analytics.analyticsScreen.AnalyticsInteractor
import com.custom.app.ui.createData.analytics.utils.DemoBaseFragment
import com.custom.app.ui.createData.analytics.utils.MyMarkerView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class Quantity() : DemoBaseFragment(), OnChartValueSelectedListener {

    private var pieView: PieChart? = null
    private var combinedChart: CombinedChart? = null
    private var lineView: LineChart? = null
    private var filteredlineView: LineChart? = null

    private lateinit var viewModel: QuantityViewModel
    private lateinit var quantityResponse: QuantityRes
    var quantityCollection: ArrayList<CollectionByCenterRes> = ArrayList()
    var collectionOverTime: ArrayList<CollectionOverTimeRes> = ArrayList()
    var collectionWeeklyMonthly: ArrayList<CollectionOverTimeRes> = ArrayList()

    private var selectedCustomerId: String = ""
    private var selectedCommodityId: String = ""
    private var selectedRegionId: String = ""
    private var selectedCenterId: String = ""
    private var deviceSerialNumber: String = ""

    private var dateFrom: Long = 0
    private var dateTo: Long = 0

    private var fromDate: String = ""
    private var toDate: String = ""

    var selectedDateFrom: String = ""
    var selectedDateTo: String = ""

    private var highestQuantity: TextView? = null
    private var lowestQuantity: TextView? = null
    private var averageQuantity: TextView? = null
    private var totalQuantity: TextView? = null
    private var listLayout_Quantity: LinearLayout? = null

    var increment_decrementImg: ImageView? = null
    var quantityValue: TextView? = null
    var tvIncrementDecrement: TextView? = null

    val MONTH_DATA = "MONTH_DATA"
    val WEEK_DATA = "WEEK_DATA"
    val TODAY_DATA = "TODAY"

    var currentDay: String? = null
    var weekDay: String? = null
    var monthDay: String? = null
    var toDay: String? = null

    var filterList: ArrayList<String> = ArrayList()
    var filterData: ArrayList<String> = ArrayList()

    var tvMonthly: TextView? = null
    var tvWeekly: TextView? = null
    var tvDaily: TextView? = null

    @Inject
    lateinit var interactor: AnalyticsInteractor
    companion object {
        fun newInstance(customerId: String, commodityId: String, regionId: String, centerId: String, date_to: String, date_from: String) =
                Quantity().apply {
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

    fun init() {
        filterData.add("Jan")
        filterData.add("Feb")
        filterData.add("Mar")
        filterData.add("Apr")
        filterData.add("May")
        filterData.add("Jun")
        filterData.add("Jul")
        filterData.add("Aug")
        filterData.add("Sep")
        filterData.add("Oct")
        filterData.add("Nov")
        filterData.add("Dec")

        filterList.add("Monthly")
        filterList.add("Weekly")
        filterList.add("Today")

        var calendar = Calendar.getInstance()
        val date = calendar.time
        val dateFormat = SimpleDateFormat("dd-MM-yyyy")

        currentDay = dateFormat.format(date)

        weekDay = getCalculatedDate(currentDay!!, "dd-MM-yyyy", -7)
        monthDay = getCalculatedDate(currentDay!!, "dd-MM-yyyy", -30)
        toDay = getCalculatedDate(currentDay!!, "dd-MM-yyyy", -0)

    }

    fun getCalculatedDate(date: String, dateFormat: String, days: Int): String {
        val cal = Calendar.getInstance()
        val s = SimpleDateFormat(dateFormat)
        if (date.isNotEmpty()) {
            cal.time = s.parse(date)
        }
        cal.add(Calendar.DAY_OF_YEAR, days)
        return s.format(Date(cal.timeInMillis))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (requireActivity().application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_quantity, container, false)

        viewModel = ViewModelProvider(this, QuantityViewModelFactory(interactor))[QuantityViewModel::class.java]
        viewModel.quantityState.observe(::getLifecycle, ::updateUI)

        init()
        highestQuantity = view.findViewById(R.id.highestQuantity)
        lowestQuantity = view.findViewById(R.id.lowestQuantity)
        averageQuantity = view.findViewById(R.id.averageQuantity)
        totalQuantity = view.findViewById(R.id.totalQuantity)
        tvIncrementDecrement = view.findViewById(R.id.tvIncrementDecrement)
        quantityValue = view.findViewById(R.id.quantityValue)
        increment_decrementImg = view.findViewById(R.id.increment_decrementImg)
        tvMonthly = view.findViewById(R.id.monthly)
        tvWeekly = view.findViewById(R.id.weekly)
        tvDaily = view.findViewById(R.id.daily)
        listLayout_Quantity = view.findViewById(R.id.listLayout_Quantity) as LinearLayout

        pieView = view.findViewById(R.id.chart1) as PieChart
        combinedChart = view.findViewById(R.id.chart2)
        lineView = view.findViewById(R.id.chart3)
        filteredlineView = view.findViewById(R.id.chart4)

//        if (requireArguments().getInt("COMMODITYID") != 0) {
//            selectedCommodityId = requireArguments().getInt("COMMODITYID").toString()
//        }
//        if (requireArguments().getInt("CENTERID") != 0) {
//            selectedCenterId = requireArguments().getInt("CENTERID").toString()
//        }
//
//        toDate = requireArguments().getString("DATETO")!!
//        if (toDate.equals("null")) {
//            toDate = ""
//        }
//        fromDate = requireArguments().getString("DATEFROM")!!
//        if (fromDate.equals("null")) {
//            fromDate = ""
//        }
//
//        if (requireArguments().getInt("REGIONID") != 0) {
//            selectedRegionId = requireArguments().getInt("REGIONID").toString()
//        }
//
//        viewModel.onGetQuantity(selectedCommodityId, selectedCenterId, toDate, fromDate, selectedRegionId)

        tvMonthly!!.setOnClickListener {
            typeFilterData(MONTH_DATA)
        }
        tvWeekly!!.setOnClickListener {
            typeFilterData(WEEK_DATA)
        }
        tvDaily!!.setOnClickListener {
            typeFilterData(TODAY_DATA)
        }

//        setGraph()
//        setPieView()
//        setLineView()
        return view
    }

    override fun onResume() {
        super.onResume()

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

        viewModel.onGetQuantity(selectedCustomerId, selectedCommodityId, deviceSerialNumber, selectedCenterId, toDate, fromDate, selectedRegionId)

        typeFilterData(MONTH_DATA)

        setGraph()
        setPieView()
        setLineView()
    }

    private fun updateUI(screenStateSite: ScreenState<QuantityState>?) {
        when (screenStateSite) {
            ScreenState.Loading -> {
                //progressListDevices.visibility = View.VISIBLE
            }
            is ScreenState.Render -> processLoginState(screenStateSite.renderState)
        }
    }

    private fun processLoginState(renderStateSite: QuantityState) {
//        progressListDevices.visibility = View.GONE

        when (renderStateSite) {
            QuantityState.QuantityCountSuccess -> {
                quantityResponse = viewModel.quantity.value!!
                setQuantityValues(quantityResponse)
            }
            QuantityState.QuantityCountFailure -> {
                AlertUtil.showSnackBar(listLayout_Quantity, viewModel.errorMessage, 2000)
            }
            QuantityState.QuantityCollectionsFailure -> {
                AlertUtil.showSnackBar(listLayout_Quantity, viewModel.errorMessage, 2000)
            }
            QuantityState.QuantityCollectionsSuccess -> {
                quantityCollection.clear()
                quantityCollection = viewModel.collection.value!!
                if (quantityCollection.size != 0) {
                    setPieData(quantityCollection)
                }
            }
            QuantityState.CollectionOverTimeFailure -> {
                AlertUtil.showSnackBar(listLayout_Quantity, viewModel.errorMessage, 2000)
            }
            QuantityState.CollectionOverTimeSuccess -> {
                collectionOverTime = viewModel.collectionOverTime.value!!
                setLineData(collectionOverTime)
            }
            QuantityState.CollectionCenterRegionFailure -> {
                AlertUtil.showSnackBar(listLayout_Quantity, viewModel.errorMessage, 2000)
            }
            QuantityState.CollectionCenterRegionSuccess -> {
                if (viewModel.collectionCenterRegion.value != null) {
                    generateCombineGraph(viewModel.collectionCenterRegion.value!!)
                }
            }
            QuantityState.CollectionWeeklyMonthlySuccess -> {

                if (viewModel.collectionWeeklyMonthly.value!!.difference != null) {
                    quantityValue!!.text = viewModel.collectionWeeklyMonthly.value!!.difference.toString() + "Kg"
                    tvIncrementDecrement!!.text = viewModel.collectionWeeklyMonthly.value!!.difference_percentage.toString() + "% Since Last Month"
                }
                if (viewModel.collectionWeeklyMonthly.value!!.graph_data.size != 0) {
                    collectionWeeklyMonthly.clear()
                    collectionWeeklyMonthly = viewModel.collectionWeeklyMonthly.value!!.graph_data
                    setFilteredLineData(collectionWeeklyMonthly)
                }
            }
            QuantityState.CollectionWeeklyMonthlyFailure -> {
                AlertUtil.showSnackBar(listLayout_Quantity, viewModel.errorMessage, 2000)
            }
        }
    }

    private fun setQuantityValues(quantityResponse: QuantityRes) {

        if (selectedCommodityId.equals("3")) {
            highestQuantity!!.text = quantityResponse.max_quantity.toString() + " L"
            lowestQuantity!!.text = quantityResponse.min_quantity.toString() + " L"
            averageQuantity!!.text = quantityResponse.average_quantity.toString() + " L"
            totalQuantity!!.text = quantityResponse.total_quantity.toString() + " L"
        } else {
            highestQuantity!!.text = quantityResponse.max_quantity.toString() + " Kg"
            lowestQuantity!!.text = quantityResponse.min_quantity.toString() + " Kg"
            averageQuantity!!.text = quantityResponse.average_quantity.toString() + " Kg"
            totalQuantity!!.text = quantityResponse.total_quantity.toString() + " Kg"
        }
    }

    private fun setPieView() {
        pieView!!.setUsePercentValues(false)
        pieView!!.getDescription().isEnabled = false
        pieView!!.setExtraOffsets(5f, 10f, 5f, 5f)
        pieView!!.setDragDecelerationFrictionCoef(0.95f)
        pieView!!.setCenterText("Collection centers")
        pieView!!.setDrawHoleEnabled(true)
        pieView!!.setHoleColor(Color.WHITE)
        pieView!!.setTransparentCircleColor(Color.WHITE)
        pieView!!.setTransparentCircleAlpha(110)
        pieView!!.setHoleRadius(58f)
        pieView!!.setTransparentCircleRadius(61f)
        pieView!!.setDrawCenterText(true)
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

        viewModel.onGetCollection(selectedCustomerId, selectedCommodityId, deviceSerialNumber, selectedCenterId, toDate, fromDate, selectedRegionId)
    }

    private fun setPieData(quantityCollection: ArrayList<CollectionByCenterRes>) {
        val entries = ArrayList<PieEntry>()

        if (quantityCollection.size != 0) {
            for (i in 0 until quantityCollection.size) {
                if (quantityCollection[i].weight != null && quantityCollection[i].inst_center_id != null) {
                    entries.add(PieEntry(quantityCollection[i].weight!!.toFloat(), "CC" + quantityCollection[i].inst_center_id!!.toString()))
                }
            }
        }
        val dataSet = PieDataSet(entries, "Collection centers")
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

    private fun setGraph() {

        val xAxis: XAxis
        run {
            xAxis = combinedChart!!.xAxis
        }

        val yAxis: YAxis
        run {

            yAxis = combinedChart!!.axisLeft
            combinedChart!!.axisRight.isEnabled = false

            yAxis.axisMaximum = 200f
            yAxis.axisMinimum = 0f
        }

        combinedChart!!.axisLeft.setDrawLabels(true)
        combinedChart!!.xAxis.setDrawLabels(true)

        combinedChart!!.legend.isEnabled = false
        combinedChart!!.description.isEnabled = false

        combinedChart!!.isKeepPositionOnRotation = true
        combinedChart!!.fitScreen()
        combinedChart!!.setVisibleXRangeMaximum(8f)
        combinedChart!!.isDragEnabled = true
        combinedChart!!.isNestedScrollingEnabled = true
        combinedChart!!.setTouchEnabled(true)
        combinedChart!!.setOnChartValueSelectedListener(this)
        combinedChart!!.setScaleEnabled(true)

//        viewModel.onGetCollectionCenterRegion(selectedCustomerId, selectedCommodityId, selectedCenterId, toDate, fromDate, selectedRegionId)
    }

    private fun generateCombineGraph(collectionRegionList: ArrayList<CollectionCenterRegionRes>) {

        val regionCollectionList: ArrayList<Float> = ArrayList()
        val centerCollectionList: ArrayList<Float> = ArrayList()
        val scanDateList: ArrayList<String> = ArrayList()

        if (collectionRegionList.size != 0) {
            for (i in 0 until collectionRegionList.size) {
                if (collectionRegionList[i].region_collection != null && collectionRegionList[i].center_collection != null &&
                        collectionRegionList[i].scan_date != null) {
                    regionCollectionList.add(collectionRegionList[i].region_collection!!)
                    centerCollectionList.add(collectionRegionList[i].center_collection!!)
                    scanDateList.add(collectionRegionList[i].date_done!!.toString())
                }
            }
        }
        var dateArray: ArrayList<String> = ArrayList()
        for (j in 0 until scanDateList.size){

            dateArray.add(SimpleDateFormat("dd MMM").format(scanDateList[j].toLong()))

        }
        val combineData = CombinedData()
        combineData.setData(generateLineData(regionCollectionList))
        combineData.setData(generateBarData(centerCollectionList))

        combinedChart!!.xAxis.valueFormatter = IndexAxisValueFormatter(dateArray)

//        val xAxis = combinedChart!!.getXAxis()
//        xAxis.valueFormatter = object : ValueFormatter() {
//            override fun getFormattedValue(value: Float): String {
//                return SimpleDateFormat("dd MMM").format(value.toLong())
//            }
//        }

        combinedChart!!.xAxis.position = XAxis.XAxisPosition.BOTTOM
        combinedChart!!.xAxis.granularity = 1f
        combinedChart!!.xAxis.isGranularityEnabled = true
        combinedChart!!.data = combineData

        val minXRange: Float = 0f
        val maxXRange: Float = scanDateList.size.toFloat()
        combinedChart!!.setVisibleXRange(minXRange, maxXRange)
        combinedChart!!.invalidate()
        combinedChart!!.notifyDataSetChanged()

    }

    private fun generateBarData(centerCollectionList: ArrayList<Float>): BarData {

        val entries1 = ArrayList<BarEntry>()

        for (index in 0 until centerCollectionList.size) {
            entries1.add(BarEntry(index.toFloat(), centerCollectionList[index]))
        }

        val set1 = BarDataSet(entries1, "Region")
        set1.stackLabels = arrayOf("Stack 1", "Stack 2")
        set1.setColors(Color.BLUE)
        set1.axisDependency = YAxis.AxisDependency.LEFT

        val groupSpace = 0.06f
        val barSpace = 0.02f // x2 dataset
        val barWidth = 0.10f // x2 dataset
        val d = BarData(set1)
        d.barWidth = barWidth

        return d
    }

    private fun generateLineData(regionList: ArrayList<Float>): LineData {
        val data = LineData()

        val values = ArrayList<Entry>()

        for (index in 0 until regionList.size) {
            values.add(Entry(index.toFloat(), regionList[index]))
        }
        val set1: LineDataSet

        set1 = LineDataSet(values, "Center")
        set1.color = Color.GREEN
        set1.lineWidth = 1.8f
        set1.mode = LineDataSet.Mode.CUBIC_BEZIER
        set1.cubicIntensity = 0.2f

        set1.fillAlpha = 100

        set1.axisDependency = YAxis.AxisDependency.LEFT
        data.addDataSet(set1)

        return data
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
        lineView!!.setTouchEnabled(true)
        lineView!!.setOnChartValueSelectedListener(this)
        lineView!!.setDragEnabled(true)
        lineView!!.setScaleEnabled(true)
        lineView!!.setPinchZoom(true)
//        lineView!!.setVisibleXRangeMaximum(2f)

//        xAxis.valueFormatter = object : ValueFormatter() {
//            override fun getFormattedValue(value: Float): String {
//
//                return SimpleDateFormat("dd MMM").format(value.toLong())
//            }
//        }

        val yAxis: YAxis
        yAxis = lineView!!.getAxisLeft()
        lineView!!.getAxisRight().setEnabled(false)
        yAxis.axisMaximum = 200f
        yAxis.axisMinimum = 0f

        viewModel.onGetCollectionOverTime(selectedCustomerId, selectedCommodityId, selectedCenterId, toDate, fromDate, selectedRegionId)
        lineView!!.animateX(1500)
        val l: Legend = lineView!!.getLegend()
        l.form = Legend.LegendForm.LINE
    }

    private fun setLineData(collectionOverTime: ArrayList<CollectionOverTimeRes>) {
        val values = ArrayList<Entry>()
        val scanDateList: ArrayList<String> = ArrayList()

        if (collectionOverTime.size != 0) {
            for (i in 0 until collectionOverTime.size) {
                if (collectionOverTime[i].weight != null) {
                    val date = (collectionOverTime[i].date_done)!!.toFloat()
                    val value = (collectionOverTime[i].weight)!!.toFloat()
                    scanDateList.add(collectionOverTime[i].date_done.toString())
                    values.add(Entry(date, value))
                }
            }
        }
        val dateArray: ArrayList<String> = ArrayList()
        for (j in 0 until scanDateList.size){

            dateArray.add(SimpleDateFormat("dd MMM").format(scanDateList[j].toLong()))

        }
        val xAxis = lineView!!.xAxis

        xAxis.valueFormatter = IndexAxisValueFormatter(dateArray)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
//        xAxis.granularity=1f
        xAxis.isGranularityEnabled = true

        val set1 = LineDataSet(values, "Collections over time")
        set1.mode = LineDataSet.Mode.CUBIC_BEZIER
        set1.cubicIntensity = 0.2f
        set1.setDrawIcons(false)
        set1.color = Color.CYAN
        set1.setCircleColor(Color.CYAN)
        set1.setDrawCircleHole(false)
        set1.setDrawFilled(true)
        set1.fillFormatter = IFillFormatter { dataSet, dataProvider -> lineView!!.getAxisLeft().getAxisMinimum() }
        set1.fillColor = Color.CYAN
        set1.notifyDataSetChanged()

        val data = LineData(set1)
        data.setDrawValues(false)
        lineView!!.setData(data)
        lineView!!.getData().notifyDataChanged()
        lineView!!.notifyDataSetChanged()
        lineView!!.invalidate()
    }

    private fun setFilteredLineView() {

        filteredlineView!!.setBackgroundColor(Color.WHITE)
        filteredlineView!!.getDescription().setEnabled(false)
        filteredlineView!!.setTouchEnabled(true)
        filteredlineView!!.setOnChartValueSelectedListener(this)
        filteredlineView!!.setDrawGridBackground(false)
        val mv = MyMarkerView(requireContext(), R.layout.custom_marker_view)
        mv.setChartView(filteredlineView!!)
        filteredlineView!!.setMarker(mv)
        filteredlineView!!.setDragEnabled(true)
        filteredlineView!!.setScaleEnabled(true)

        filteredlineView!!.setPinchZoom(true)

        val yAxis: YAxis
        yAxis = filteredlineView!!.getAxisLeft()
        filteredlineView!!.getAxisRight().setEnabled(false)
        yAxis.axisMaximum = 200f
        yAxis.axisMinimum = 0f

        viewModel.onGetCollectionWeeklyMonthly(selectedCustomerId, selectedCommodityId, selectedCenterId, selectedDateTo, selectedDateFrom, selectedRegionId)

        filteredlineView!!.animateX(1500)

        val l: Legend = filteredlineView!!.getLegend()
        l.form = Legend.LegendForm.LINE
    }

    private fun setFilteredLineData(collectionWeeklyMonthly: ArrayList<CollectionOverTimeRes>) {
        val values = ArrayList<Entry>()

        if (collectionWeeklyMonthly.size != 0) {
            for (i in 0 until collectionWeeklyMonthly.size) {
                if (collectionWeeklyMonthly[i].weight != null) {
                    val date = (collectionWeeklyMonthly[i].date_done)!!.toFloat()
                    val value = (collectionWeeklyMonthly[i].weight)!!.toFloat()
                    values.add(Entry(date, value))
                }
            }
        }

        val xAxis = filteredlineView!!.getXAxis()
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return SimpleDateFormat("dd MMM").format(value.toLong())
            }
        }
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        var set1: LineDataSet = LineDataSet(values, "Collections")

        set1.setDrawIcons(false)
        set1.lineWidth = 1f
        set1.setDrawCircleHole(false)
        set1.valueTextSize = 9f
        set1.setDrawFilled(true)
        set1.fillFormatter = IFillFormatter { dataSet, dataProvider -> filteredlineView!!.getAxisLeft().getAxisMinimum() }
        set1.fillColor = R.color.blue
        set1.notifyDataSetChanged()
        val dataSets = ArrayList<ILineDataSet>()
        dataSets.add(set1)
        val data = LineData(dataSets)
        filteredlineView!!.setData(data)
        filteredlineView!!.getData().notifyDataChanged()
        filteredlineView!!.notifyDataSetChanged()
        filteredlineView!!.invalidate()
    }

    private fun typeFilterData(requestTime: String) {
        selectedDateFrom = currentDay!!
        selectedDateTo = currentDay!!
        val fmt = SimpleDateFormat("dd-MM-yyyy")
        val date = fmt.parse(currentDay)
        selectedDateTo = date.time.toString()

        when (requestTime) {
            WEEK_DATA -> {
                val fmt = SimpleDateFormat("dd-MM-yyyy")
                val date = fmt.parse(weekDay)
                selectedDateFrom = date.time.toString()
            }
            MONTH_DATA -> {
                val fmt = SimpleDateFormat("dd-MM-yyyy")
                val date = fmt.parse(monthDay)
                selectedDateFrom = date.time.toString()
            }
            TODAY_DATA -> {
                val fmt = SimpleDateFormat("dd-MM-yyyy")
                val date = fmt.parse(toDay)
                selectedDateFrom = date.time.toString()
            }
        }

        setFilteredLineView()
    }

    override fun onValueSelected(e: Entry?, h: Highlight) {}

    override fun onNothingSelected() {}

}