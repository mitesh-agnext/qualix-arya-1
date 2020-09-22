package com.custom.app.ui.createData.analytics.analysis.payments

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.custom.app.R
import com.custom.app.util.Utils

class PaymentListAdapter(val context: Context, val paymentList: ArrayList<PaymentListRes>) : RecyclerView.Adapter<PaymentListAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val paymentFarmerName = view.findViewById<TextView>(R.id.paymentFarmerName)
        val paymentCompany = view.findViewById<TextView>(R.id.paymentCompany)
        val paymentCommodity = view.findViewById<TextView>(R.id.paymentCommodity)
        val paymentDate = view.findViewById<TextView>(R.id.paymentDate)
        val paymentAmount = view.findViewById<TextView>(R.id.paymentAmount)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_payment_list, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return paymentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (paymentList[position].farmer_Id != null){
            holder.paymentFarmerName.text = "Farmer "+ paymentList[position].farmer_Id.toString()
        }
        else {
            holder.paymentFarmerName.text = "No record found"
        }

        if (paymentList[position].company_id != null) {
            holder.paymentCompany.text = "Client " + paymentList[position].company_id.toString()
        }
        else {
            holder.paymentCompany.text = "No record found"
        }
        if (paymentList[position].commodity_id != null){
            holder.paymentCommodity.text = "Commodity " +  paymentList[position].commodity_id.toString()
        }
        else {
            holder.paymentCommodity.text = "No record found"
        }
        if (paymentList[position].payment_date != null) {
            holder.paymentDate.text = Utils.timeStampDate(paymentList[position].payment_date!!).toString()
        }
        else {
            holder.paymentDate.text = "No record found"
        }
        if (paymentList[position].payment_amount != null){
            holder.paymentAmount.text = paymentList[position].payment_amount.toString()
        }
        else {
            holder.paymentAmount.text = "No record found"
        }

    }
}