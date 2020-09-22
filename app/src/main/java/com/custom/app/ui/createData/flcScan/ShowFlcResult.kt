package com.custom.app.ui.createData.flcScan

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.custom.app.R
import com.custom.app.ui.base.ScreenState
import kotlinx.android.synthetic.main.activity_fine_leaf_count.*

class ShowFlcResult : DialogFragment() {

    private lateinit var viewModel: FlcViewModel
    var tvx: TextView? = null
    var tvoneBud: TextView? = null
    var tvoneLeaf: TextView? = null
    var tvoneLeafBud: TextView? = null
    var tvtwoLeafBud: TextView? = null
    var tvthreeLeafBud: TextView? = null
    var tvoneBanjhi: TextView? = null
    var tvoneLeafBunjhi: TextView? = null
    var tvtwoLeafBunjhi: TextView? = null
    var tvCoarseCount: TextView? = null
    var tvtwoLeaf: TextView? = null
    var tvthreeLeaf: TextView? = null
    var tvflcPercent: TextView? = null
    var flcHeading: TextView? = null
    var showFlc: LinearLayout? = null
    var add_flc_dialog_result: TextView? = null

    var oneBud_Count: Int? = null
    var oneLeaf_Count: Int? = null
    var twoLeaf_Count: Int? = null
    var threeLeaf_Count: Int? = null
    var oneLeafBud: Int? = null
    var twoLeafBud: Int? = null
    var threeLeafBud: Int? = null
    var oneBanjhi_Count: Int? = null
    var oneLeafBanji: Int? = null
    var twoLeafBanji: Int? = null
    var totalLeafCount: Int? = null
    var totalWeight: Double? = null
    var areaCovered: Double? = null
    var seasonId: Long? = null
    var fineCount: Int? = null
    var assistId: String? = null
    var secId: String? = null
    var userId: String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.dialog_flc, container, false)

        viewModel = ViewModelProvider(this, FlcViewModelFactory(FlcInteractor()))[FlcViewModel::class.java]
        viewModel.flcState.observe(::getLifecycle, ::updateUI)

        val intent = Intent()
        userId = intent.getStringExtra("userId")
        secId = intent.getStringExtra("secId")

        init(view)

        return view
    }

    private fun updateUI(screenState: ScreenState<FlcState>?) {
        when (screenState) {
            ScreenState.Loading -> {
                progressFLC.visibility = View.VISIBLE
            }
            is ScreenState.Render -> processLoginState(screenState.renderState)
        }
    }

    private fun processLoginState(renderState: FlcState) {

        progressFLC.visibility = View.GONE
        when (renderState) {

            FlcState.ClearDataFailure -> {
            }
            FlcState.ClearDataSuccess -> {
            }
            FlcState.FetchFLCResultFailure -> {
                showResults(0)
            }
            FlcState.FetchFLCResultSuccess -> {
                showResults(1)
            }
            FlcState.SaveFLCFailure -> {
                tvx!!.visibility = View.VISIBLE
            }
            FlcState.SaveFLCSuccess -> {
                tvx!!.visibility = View.VISIBLE
            }
        }
    }

    fun init(view: View){

        tvx = view.findViewById(R.id.tvx)
        tvoneBud = view.findViewById(R.id.tvOneBudCount)
        tvoneLeaf = view.findViewById(R.id.tvOneLeafCount)
        tvtwoLeaf = view.findViewById(R.id.tvTwoLeafCount)
        tvthreeLeaf = view.findViewById(R.id.tvThreeLeafCount)
        tvoneLeafBud = view.findViewById(R.id.tvOneLeafBud)
        tvtwoLeafBud = view.findViewById(R.id.tvTwoLeafBud)
        tvthreeLeafBud = view.findViewById(R.id.tvThreeLeafBud)

        tvoneBanjhi = view.findViewById(R.id.tvOneBanjhi)
        tvoneLeafBunjhi = view.findViewById(R.id.tvOneLeafBunjhi)
        tvtwoLeafBunjhi = view.findViewById(R.id.tvTwoLeafBunjhi)

        tvflcPercent = view.findViewById(R.id.tvflcPercent)
        flcHeading = view.findViewById(R.id.flcHeading)
        tvCoarseCount = view.findViewById(R.id.tvCoarseCount_flc)

        showFlc = view.findViewById(R.id.showFLC)
        add_flc_dialog_result = view.findViewById(R.id.add_flc_dialog)

        add_flc_dialog_result!!.visibility = View.GONE

    }

    fun showResults(status: Int){
        if (status == 0) {
            showFlc!!.visibility = View.GONE
            add_flc_dialog_result!!.text = resources.getString(R.string.clear_data)
            confirm!!.isClickable = true
            add_flc_dialog_result!!.visibility = View.VISIBLE
            tvx!!.visibility = View.VISIBLE
            tvx!!.text = resources.getString(R.string.something_went_wrong) + "\n"

        } else if (status == 1) {
            confirm!!.isClickable = true
            tvx!!.visibility = View.VISIBLE
            showFlc!!.visibility = View.GONE
            if (viewModel.fetchFlcResult.value != null) {

                if (viewModel.fetchFlcResult.value!!.Total_Bunches.toString().equals("null") || viewModel.fetchFlcResult.value!!.Total_Bunches == null) {
                    tvx!!.text =
                            resources.getString(R.string.something_went_wrong) + "\n"
                } else {
                    if (viewModel.fetchFlcResult.value!!.fine_count == null) {
                        fineCount = 0
                    }
                    if (viewModel.fetchFlcResult.value!!.OneBud_Count == null) {
                        oneBud_Count = 0
                    } else {
                        oneBud_Count = viewModel.fetchFlcResult.value!!.OneBud_Count
                    }
                    if (viewModel.fetchFlcResult.value!!.OneLeaf_Count == null) {
                        oneLeaf_Count = 0
                    } else {
                        oneLeaf_Count = viewModel.fetchFlcResult.value!!.OneLeaf_Count
                    }
                    if (viewModel.fetchFlcResult.value!!.TwoLeaf_Count == null) {
                        twoLeaf_Count = 0
                    } else {
                        twoLeaf_Count = viewModel.fetchFlcResult.value!!.TwoLeaf_Count
                    }
                    if (viewModel.fetchFlcResult.value!!.ThreeLeaf_Count == null) {
                        threeLeaf_Count = 0
                    } else {
                        threeLeaf_Count = viewModel.fetchFlcResult.value!!.ThreeLeaf_Count
                    }
                    if (viewModel.fetchFlcResult.value!!.OneLeafBud_Count == null) {
                        oneLeafBud = 0
                    } else {
                        oneLeafBud = viewModel.fetchFlcResult.value!!.OneLeafBud_Count
                    }
                    if (viewModel.fetchFlcResult.value!!.TwoLeafBud_Count == null) {
                        twoLeafBud = 0
                    } else {
                        twoLeafBud = viewModel.fetchFlcResult.value!!.TwoLeafBud_Count
                    }
                    if (viewModel.fetchFlcResult.value!!.ThreeLeafBud_Count == null) {
                        threeLeafBud = 0
                    } else {
                        threeLeafBud = viewModel.fetchFlcResult.value!!.ThreeLeafBud_Count
                    }
                    if (viewModel.fetchFlcResult.value!!.OneBanjhi_Count == null) {
                        oneBanjhi_Count = 0
                    } else {
                        oneBanjhi_Count = viewModel.fetchFlcResult.value!!.OneBanjhi_Count
                    }
                    if (viewModel.fetchFlcResult.value!!.OneLeafBanjhi_Count == null) {
                        oneLeafBanji = 0
                    } else {
                        oneLeafBanji = viewModel.fetchFlcResult.value!!.OneLeafBanjhi_Count
                    }
                    if (viewModel.fetchFlcResult.value!!.TwoLeafBanjhi_Count == null) {
                        twoLeafBanji = 0
                    } else {
                        twoLeafBanji = viewModel.fetchFlcResult.value!!.TwoLeafBanjhi_Count
                    }
                    if (viewModel.fetchFlcResult.value!!.Total_Bunches == null) {
                        totalLeafCount = 0
                    } else {
                        totalLeafCount = viewModel.fetchFlcResult.value!!.Total_Bunches
                    }

                    val A = oneLeafBud!!
                    val B = twoLeafBud!!
                    val C = threeLeafBud!! / 2
                    val D = oneLeafBanji!!
                    val E = twoLeafBanji!! + oneBanjhi_Count!! + oneBud_Count!! + oneLeaf_Count!! + twoLeaf_Count!! + threeLeaf_Count!!

                    val sumx = (A + B + C + D).toDouble()

                    var xpercent = (sumx / totalLeafCount!!) * 100

                    flcHeading!!.visibility = View.GONE

                    if (xpercent.isNaN()) {
                        tvx!!.visibility = View.VISIBLE
                        xpercent = 0.0
                    } else {
                        tvx!!.visibility = View.VISIBLE
                        tvx!!.text =
                                resources.getString(R.string.fine_shoot) + " " + String.format("%.2f", xpercent) + " %"
                    }
                    tvoneBud!!.visibility = View.VISIBLE
                    tvoneLeaf!!.visibility = View.VISIBLE
                    tvtwoLeaf!!.visibility = View.VISIBLE
                    tvthreeLeaf!!.visibility = View.VISIBLE
                    tvCoarseCount!!.visibility = View.VISIBLE
                    tvoneLeafBud!!.visibility = View.VISIBLE
                    tvtwoLeafBud!!.visibility = View.VISIBLE
                    tvthreeLeafBud!!.visibility = View.VISIBLE

                    tvoneLeafBunjhi!!.visibility = View.VISIBLE
                    tvtwoLeafBunjhi!!.visibility = View.VISIBLE

                    tvoneLeaf!!.text = resources.getString(R.string.one_leaf) + oneLeaf_Count.toString()
                    tvtwoLeaf!!.text = resources.getString(R.string.two_leaves) + twoLeaf_Count.toString()
                    tvoneBud!!.text = resources.getString(R.string.one_bud) + oneBud_Count.toString()
                    tvthreeLeaf!!.text = resources.getString(R.string.three_leaves) + threeLeaf_Count.toString()
                    tvoneLeafBud!!.text = resources.getString(R.string.one_leaf_bud) + A
                    tvtwoLeafBud!!.text = resources.getString(R.string.two_leaves_bud) + B
                    tvthreeLeafBud!!.text = resources.getString(R.string.three_leaves_bud) + C
                    tvCoarseCount!!.text = "Coarse Count : " + E
                    tvoneBanjhi!!.text = resources.getString(R.string.one_banjhi) + oneBanjhi_Count.toString()
                    if (D > 20) {
                        tvoneLeafBunjhi!!.setTextColor(ContextCompat.getColor(requireContext(), R.color.red)
                        )
                    }
                    tvoneLeafBunjhi!!.text = resources.getString(R.string.one_leaf_banjhi) + D
                    tvtwoLeafBunjhi!!.text = resources.getString(R.string.two_leaves_banjhi) + twoLeafBanji.toString()

                    if (totalWeight != etweight_addLeave!!.text.toString().toDoubleOrNull()) {
                        totalWeight = etweight_addLeave!!.text.toString().toDoubleOrNull()
                    } else {
                        totalWeight = 0.0
                    }
                    if (areaCovered == null) {
                        areaCovered = 0.0
                    }
                    if (seasonId == null) {
                        seasonId = 1
                    }
//                    viewModel.SaveFlc(A.toString(), B.toString(), C.toString(), D.toString(), twoLeafBanji.toString(), oneBanjhi_Count.toString(),
//                            oneBud_Count.toString(), oneLeaf_Count.toString(), twoLeaf_Count.toString(), threeLeaf_Count.toString(), xpercent, totalLeafCount!!.toDouble(),
//                            secId!!, totalWeight!!, areaCovered!!)

                    add_flc_dialog_result!!.text = resources.getString(R.string.ok)

                }

            } else {
                add_flc_dialog_result!!.text = resources.getString(R.string.ok)

            }
            add_flc_dialog_result!!.visibility = View.VISIBLE

        }

    }

    object instance{
        fun newInstance(num: Int): ShowFlcResult? {
            val dialogFragment = ShowFlcResult()
            val bundle = Bundle()
            bundle.putInt("num", num)
            dialogFragment.setArguments(bundle)
            return dialogFragment
        }
    }
}