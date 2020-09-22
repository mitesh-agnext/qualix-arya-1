package com.custom.app.ui.payment.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.custom.app.R
import com.custom.app.data.model.payment.PaymentHistoryRes
import java.text.SimpleDateFormat
import java.util.*

class PaymentHistoryAdapter(val context: Context, val paymentList: ArrayList<PaymentHistoryRes>,
                            val mCallback: ListCallBack) : RecyclerView.Adapter<PaymentHistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvValue1 = view.findViewById<TextView>(R.id.tvValue1)
        val tvValue2 = view.findViewById<TextView>(R.id.tvValue2)
        val tvValue3 = view.findViewById<TextView>(R.id.tvValue3)
        val tvValue4 = view.findViewById<TextView>(R.id.tvValue4)
        val tvValue5 = view.findViewById<TextView>(R.id.tvValue5)

        val cdItem = view.findViewById<LinearLayout>(R.id.cdItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_payment_history, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return paymentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvValue1.text = paymentList[position].paymentSum
        holder.tvValue2.text = paymentList[position].clientName
        holder.tvValue3.text = paymentList[position].commodityId
        holder.tvValue4.text = paymentList[position].quality
        val itemLong = (paymentList[position].doneOn!!.toLong() / 1000)
        val d = Date(itemLong * 1000L)
        val itemDateStr: String = SimpleDateFormat("dd-MMM HH:mm").format(d)
        holder.tvValue5.text = itemDateStr
        holder.cdItem.setOnClickListener {
            mCallback.onItemClick(position)
        }
    }
}

interface ListCallBack {
    fun onItemClick(pos: Int)
}