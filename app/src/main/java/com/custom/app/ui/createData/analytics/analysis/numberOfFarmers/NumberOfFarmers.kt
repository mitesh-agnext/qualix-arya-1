package com.custom.app.ui.createData.analytics.analysis.numberOfFarmers

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.core.app.util.AlertUtil
import com.custom.app.CustomApp
import com.custom.app.R
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.createData.analytics.analyticsScreen.AnalysisScreen
import com.custom.app.ui.createData.analytics.analyticsScreen.AnalyticsInteractor
import com.custom.app.ui.createData.analytics.utils.DemoBaseFragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import kotlinx.android.synthetic.main.fragment_numberoffarmers.*
import javax.inject.Inject

class NumberOfFarmers() : DemoBaseFragment(), NumberOfFarmersAdapter.ShowDetailCallback, OnChartValueSelectedListener {

    var layoutManager: LinearLayoutManager? = null
    var number_of_farmers: RecyclerView? = null
    var adapter: NumberOfFarmersAdapter? = null
    private lateinit var viewModel: NumberOfFarmerViewModel
    private lateinit var farmerList: ArrayList<NumberOfFarmerRes>
    private lateinit var farmerData: FarmerDataRes

    private var selectedCustomerId: String = ""
    private var selectedCommodityId: String = ""
    private var selectedRegionId: String = ""
    private var selectedCenterId: String = ""
    private var dateFrom: Long = 0
    private var dateTo: Long = 0
    private var fromDate: String = ""
    private var toDate: String = ""

    var collection_high_farmer: TextView? = null
    var name_high_farmer: TextView? = null
    var area_high_farmer: TextView? = null
    var difference_high_farmer: TextView? = null
    var collection_low_farmer: TextView? = null
    var name_low_farmer: TextView? = null
    var area_low_farmer: TextView? = null
    var difference_low_farmer: TextView? = null
    private lateinit var farmer_high_graph: LineChart
    private lateinit var farmer_low_graph: LineChart

    @Inject
    lateinit var interactor: AnalyticsInteractor

    companion object {
        fun newInstance(customerId: String, commodityId: String, regionId: String, centerId: String, date_to: String, date_from: String) =
                NumberOfFarmers().apply {
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

        val view: View = inflater.inflate(R.layout.fragment_numberoffarmers, container, false)

        collection_high_farmer = view.findViewById(R.id.collection_high_farmer)
        name_high_farmer = view.findViewById(R.id.name_high_farmer)
        area_high_farmer = view.findViewById(R.id.area_high_farmer)
        difference_high_farmer = view.findViewById(R.id.difference_high_farmer)
        collection_low_farmer = view.findViewById(R.id.collection_low_farmer)
        name_low_farmer = view.findViewById(R.id.name_low_farmer)
        area_low_farmer = view.findViewById(R.id.area_low_farmer)
        difference_low_farmer = view.findViewById(R.id.difference_low_farmer)
        farmer_high_graph = view.findViewById(R.id.farmer_high_graph)
        farmer_low_graph = view.findViewById(R.id.farmer_low_graph)

        viewModel = ViewModelProvider(this, NumberOfFarmerViewModelFactory(interactor))[NumberOfFarmerViewModel::class.java]
        viewModel.numberOfFarmerState.observe(::getLifecycle, ::updateUI)

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

        number_of_farmers = view.findViewById(R.id.number_of_farmers)
        layoutManager = LinearLayoutManager(activity)
        number_of_farmers!!.layoutManager = layoutManager

        viewModel.onNumberOfFarmer(selectedCustomerId, selectedCommodityId, selectedCenterId, fromDate, toDate, selectedRegionId)

        setfarmer_high_graph()
        setfarmer_low_graph()
        return view

    }

    private fun updateUI(screenStateSite: ScreenState<NumberOfFarmerState>?) {
        when (screenStateSite) {
            ScreenState.Loading -> {
                //progressListDevices.visibility = View.VISIBLE
            }
            is ScreenState.Render -> processLoginState(screenStateSite.renderState)
        }
    }

    private fun processLoginState(renderStateSite: NumberOfFarmerState) {
//        progressListDevices.visibility = View.GONE

        when (renderStateSite) {
            NumberOfFarmerState.NumberOfFarmerSuccess -> {
                farmerList = viewModel.numberOfFarmer.value!!
                setFarmerList(farmerList)
            }
            NumberOfFarmerState.NumberOfFarmerFailure -> {
                AlertUtil.showSnackBar(listLayout_numberOfFarmers, viewModel.errorMessage, 2000)
            }
            NumberOfFarmerState.FarmerDataFailure -> {
                AlertUtil.showSnackBar(listLayout_numberOfFarmers, viewModel.errorMessage, 2000)
            }
            NumberOfFarmerState.FarmerDataSuccess -> {
                farmerData = viewModel.farmerData.value!!
                setFarmerData(farmerData)
            }
        }
    }

    private fun setFarmerList(farmerList: ArrayList<NumberOfFarmerRes>) {

        adapter = NumberOfFarmersAdapter(requireContext(), farmerList, this)
        number_of_farmers!!.adapter = adapter

    }

    private fun setFarmerData(farmerDataResponse: FarmerDataRes) {

        name_high_farmer!!.text = "Farmer " + farmerDataResponse.max_farmer_id.toString()
        collection_high_farmer!!.text = "Collection " + farmerDataResponse.max_quantity.toString()
        area_high_farmer!!.text = "Center " + farmerDataResponse.max_inst_center_id.toString()
        difference_high_farmer!!.text = farmerDataResponse.increment.toString() + " %"

        name_low_farmer!!.text = "Farmer " + farmerDataResponse.min_farmer_id.toString()
        collection_low_farmer!!.text = "Collection " + farmerDataResponse.min_quantity.toString()
        area_low_farmer!!.text = "Center " + farmerDataResponse.min_inst_center_id.toString()
        difference_low_farmer!!.text = farmerDataResponse.decrement.toString() + " %"

        val incrementGraphData: ArrayList<Increment_graph_data> = ArrayList()
        for (i in 0 until farmerDataResponse.increment_graph_data!!.size) {
            incrementGraphData.add(farmerDataResponse.increment_graph_data!![i])
        }
        val decrementGraphData: ArrayList<Decrement_graph_data> = ArrayList()
        for (i in 0 until farmerDataResponse.decrement_graph_data!!.size) {
            decrementGraphData.add(farmerDataResponse.decrement_graph_data!![i])
        }
        setFarmerHighData(incrementGraphData)
        setFarmerLowerData(decrementGraphData)
    }

    private fun setfarmer_high_graph() {

        farmer_high_graph.setBackgroundColor(Color.WHITE)
        farmer_high_graph.getDescription().setEnabled(false)
        farmer_high_graph.setDrawGridBackground(false)
        farmer_high_graph.getLegend().setEnabled(false)
        farmer_high_graph.getAxisRight().setEnabled(false)
        farmer_high_graph.getAxisLeft().setEnabled(false)
        farmer_high_graph.getXAxis().setEnabled(false)

        viewModel.onFarmerData(selectedCustomerId, selectedCommodityId, selectedCenterId, fromDate, toDate, selectedRegionId)
        farmer_high_graph.animateXY(2000, 2000)

    }

    private fun setFarmerHighData(incrementGraphData: ArrayList<Increment_graph_data>) {
        val values = ArrayList<Entry>()

        if (incrementGraphData.size != 0) {
            for (i in 0 until incrementGraphData.size) {
                if (incrementGraphData[i].increment_graph_total_weight != null) {
                    val value = (incrementGraphData[i].increment_graph_total_weight!!).toFloat()
                    values.add(Entry(i.toFloat(), value))
                }
            }
        }

        val set1: LineDataSet
        set1 = LineDataSet(values, "Collections over time")
        set1.setDrawIcons(false)
        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER)
        set1.setCubicIntensity(0.5f)
        set1.setDrawCircles(false)
        set1.setLineWidth(2.5f)
        set1.setCircleRadius(4f)
        set1.fillFormatter = IFillFormatter { dataSet, dataProvider -> farmer_high_graph.getAxisLeft().getAxisMinimum() }
        set1.notifyDataSetChanged()
        val dataSets = ArrayList<ILineDataSet>()
        dataSets.add(set1)

        val data = LineData(dataSets)
        data.setDrawValues(false)
        farmer_high_graph.setData(data)
        farmer_high_graph.getData().notifyDataChanged()
        farmer_high_graph.notifyDataSetChanged()

    }

    private fun setfarmer_low_graph() {

        farmer_low_graph.setBackgroundColor(Color.WHITE)
        farmer_low_graph.getDescription().setEnabled(false)
        farmer_low_graph.setDrawGridBackground(false)
        farmer_low_graph.getLegend().setEnabled(false)
        farmer_low_graph.getAxisRight().setEnabled(false)
        farmer_low_graph.getAxisLeft().setEnabled(false)
        farmer_low_graph.getXAxis().setEnabled(false)
        farmer_low_graph.animateXY(2000, 2000)

    }

    private fun setFarmerLowerData(decrementGraphData: ArrayList<Decrement_graph_data>) {
        val values = ArrayList<Entry>()

        if (decrementGraphData.size != 0) {
            for (i in 0 until decrementGraphData.size) {
                if (decrementGraphData[i].decrement_graph_total_weight != null) {
                    val value = (decrementGraphData[i].decrement_graph_total_weight)!!.toFloat()
                    values.add(Entry(i.toFloat(), value))
                }
            }
        }

        val set1: LineDataSet
        set1 = LineDataSet(values, "Collections over time")
        set1.setDrawIcons(false)
        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER)
        set1.setCubicIntensity(0.5f)
        set1.setDrawCircles(false)
        set1.setLineWidth(2.5f)
        set1.setCircleRadius(4f)
        set1.fillFormatter = IFillFormatter { dataSet, dataProvider -> farmer_low_graph.getAxisLeft().getAxisMinimum() }
        set1.notifyDataSetChanged()

        val dataSets = ArrayList<ILineDataSet>()
        dataSets.add(set1)

        val data = LineData(dataSets)
        data.setDrawValues(false)
        farmer_low_graph.setData(data)
        farmer_low_graph.getData().notifyDataChanged()
        farmer_low_graph.notifyDataSetChanged()

    }

    override fun showDetail(position: Int) {}

    override fun onNothingSelected() {}

    override fun onValueSelected(e: Entry?, h: Highlight?) {}

}