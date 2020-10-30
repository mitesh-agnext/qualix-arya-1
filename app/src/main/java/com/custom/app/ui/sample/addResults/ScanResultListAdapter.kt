package com.custom.app.ui.sample.addResults

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.custom.app.R
import com.custom.app.data.model.scan.UpdateScanRequest
import com.specx.scan.data.model.analysis.AnalysisItem
import kotlinx.android.synthetic.main.item_results.view.*


class ScanResultListAdapter(var context: Context, val scanResultList: List<AnalysisItem>) : RecyclerView.Adapter<ScanResultListAdapter.ViewHolder>() {

    val updatedList = ArrayList<UpdateScanRequest>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvParameter: TextView = view.tvParameter
        var etValue: EditText = view.etValue
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                return ViewHolder(
                        LayoutInflater.from(context).inflate(R.layout.item_results, parent, false)
                )
    }

    override fun getItemCount(): Int {
        return  scanResultList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var updateObject= UpdateScanRequest()
        updateObject.labResultValue=scanResultList[position].totalAmount
        updateObject.analysisName=scanResultList[position].name
        updatedList.add(updateObject)
        //updatedList[position].analysisName = scanResultList[position].name
        holder.tvParameter.text = scanResultList[position].name!!.toUpperCase()
        holder.etValue.setText(scanResultList[position].totalAmount!!.toUpperCase())
        holder.etValue.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updatedList[position].labResultValue = s.toString()
            }
        })
    }

    fun retrieveData():  List<UpdateScanRequest> {
        return updatedList
    }

}