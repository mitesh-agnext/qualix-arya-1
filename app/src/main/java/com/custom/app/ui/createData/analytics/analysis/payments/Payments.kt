package com.custom.app.ui.createData.analytics.analysis.payments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.core.app.util.AlertUtil
import com.custom.app.CustomApp
import com.custom.app.R
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.createData.analytics.analyticsScreen.AnalysisScreen
import com.custom.app.ui.createData.analytics.analyticsScreen.AnalyticsInteractor
import com.custom.app.ui.createData.analytics.utils.MyMarkerView
import com.custom.app.ui.createData.paymentChart.analysis.payments.PaymentViewModel
import com.custom.app.ui.createData.paymentChart.analysis.payments.PaymentViewModelFactory
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
import kotlinx.android.synthetic.main.fragment_payments.*
import java.text.SimpleDateFormat
import javax.inject.Inject

class Payments : Fragment(), OnChartValueSelectedListener {

    private var pieView: PieChart? = null
    private var lineView: LineChart? = null

    private lateinit var viewModel: PaymentViewModel
    private lateinit var paymentOverTime: ArrayList<PaymentRes>
    private lateinit var paymentChartRes: ArrayList<PaymentChartRes>
    private lateinit var paymentList: ArrayList<PaymentListRes>

    private var selectedCustomerId: String = ""
    private var selectedCommodityId: String = ""
    private var selectedRegionId: String = ""
    private var selectedCenterId: String = ""
    private var fromDate: String = ""
    private var toDate: String = ""
    private var rvPaymentList: RecyclerView? = null

    @Inject
    lateinit var interactor: AnalyticsInteractor

    companion object {
        fun newInstance(customerId: String, commodityId: String, regionId: String, centerId: String, date_to: String, date_from: String) =
                Payments().apply {
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
        val view: View = inflater.inflate(R.layout.fragment_payments, container, false)

        viewModel = ViewModelProvider(this, PaymentViewModelFactory(interactor))[PaymentViewModel::class.java]
        viewModel.paymentState.observe(::getLifecycle, ::updateUI)

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

        pieView = view.findViewById<PieChart>(R.id.chart1_payment)
        lineView = view.findViewById(R.id.chart3_payment) as LineChart
        rvPaymentList = view.findViewById(R.id.paymentList)

        viewModel.onGetPaymentList(selectedCustomerId, selectedCommodityId, selectedCenterId, fromDate, toDate, selectedRegionId)

        setPieView()
        setLineView()
        return view

    }

    private fun updateUI(screenStateSite: ScreenState<PaymentState>?) {
        when (screenStateSite) {
            ScreenState.Loading -> {
                //progressListDevices.visibility = View.VISIBLE
            }
            is ScreenState.Render -> processLoginState(screenStateSite.renderState)
        }
    }

    private fun processLoginState(renderStateSite: PaymentState) {
//        progressListDevices.visibility = View.GONE

        when (renderStateSite) {
            PaymentState.PaymentOverTimeSuccess -> {
                paymentOverTime = viewModel.payment.value!!
                setPaymentOverTimeData(paymentOverTime)
            }
            PaymentState.PaymentOverTimeFailure -> {
                AlertUtil.showSnackBar(listLayout_payment, viewModel.errorMessage, 2000)
            }
            PaymentState.PaymentChartFailure -> {
                AlertUtil.showSnackBar(listLayout_payment, viewModel.errorMessage, 2000)
            }
            PaymentState.PaymentChartSuccess -> {
                paymentChartRes = viewModel.paymentChart.value!!
                setPaymentChartData(paymentChartRes)
            }
            PaymentState.PaymentListSuccess -> {
                paymentList = viewModel.paymentList.value!!
                updateRecycleView(paymentList)
            }
            PaymentState.PaymentListFailure -> {
                AlertUtil.showSnackBar(listLayout_payment, viewModel.errorMessage, 2000)
            }
        }
    }

    private fun updateRecycleView(paymentList: ArrayList<PaymentListRes>) {
        rvPaymentList!!.layoutManager = LinearLayoutManager(requireContext())
        val adapterQualityRange = PaymentListAdapter(requireContext(), paymentList)
        rvPaymentList!!.adapter = adapterQualityRange
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

        viewModel.onPaymentChart(selectedCustomerId, selectedCommodityId, selectedCenterId, fromDate, toDate, selectedRegionId)
    }

    private fun setPaymentChartData(paymentChartRes: ArrayList<PaymentChartRes>) {
        val entries = ArrayList<PieEntry>()

        if (paymentChartRes.size > 0)
            for (i in 0 until paymentChartRes.size) {
                if (paymentChartRes[i].total_payment != null && paymentChartRes[i].inst_center_id != null)
                    entries.add(PieEntry(paymentChartRes[i].total_payment!!.toFloat(), paymentChartRes[i].inst_center_id))
            }
        val dataSet = PieDataSet(entries, "Payments")
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f

        val colors = ArrayList<Int>()
        for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)
        for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)
        for (c in ColorTemplate.VORDIPLOM_COLORS) colors.add(c)
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
        yAxis.axisMaximum = 20000f
        yAxis.axisMinimum = 0f

        viewModel.onGetPayment(selectedCustomerId, selectedCommodityId, selectedCenterId, fromDate, toDate, selectedRegionId)

        lineView!!.animateX(1500)

        val l: Legend = lineView!!.getLegend()
        l.form = Legend.LegendForm.LINE

    }

    private fun setPaymentOverTimeData(paymnetOverTime: ArrayList<PaymentRes>) {

        val values = ArrayList<Entry>()
        if (paymnetOverTime.size > 0)
            for (i in 0 until paymnetOverTime.size) {
                if (paymnetOverTime[i].total_payment != null && paymnetOverTime[i].date_done != null) {
                    val value = (paymnetOverTime[i].total_payment)!!.toFloat()
                    val date = (paymnetOverTime[i].date_done)!!.toFloat()
                    values.add(Entry(date, value))
                }
            }

        val xAxis = lineView!!.getXAxis()
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return SimpleDateFormat("dd MMM").format(value.toLong())
            }
        }
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        val set1 = LineDataSet(values, "Payments over time")
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
        dataSets.add(set1)
        val data = LineData(dataSets)
        lineView!!.setData(data)
        lineView!!.getData().notifyDataChanged()
        lineView!!.notifyDataSetChanged()

    }

    override fun onNothingSelected() {}

    override fun onValueSelected(e: Entry?, h: Highlight?) {}

}