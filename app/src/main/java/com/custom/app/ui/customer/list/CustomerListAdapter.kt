package com.custom.app.ui.customer.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.custom.app.R
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.SwipeLayout.SwipeListener
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter
import kotlinx.android.synthetic.main.item_customer_list.view.*

class CustomerListAdapter(val context: Context, val customerList: ArrayList<CustomerRes>,
                          val mCallback: CustomerCallback, val customerType: String) : RecyclerSwipeAdapter<CustomerListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCustomerId: TextView = view.tvCustomerId
        val tvAddressLine1: TextView = view.tvAddressLine1
        val tvCustomerName: TextView = view.tvCustomerName
        val swipeLayout: SwipeLayout = view.swipeLayout
        val tvCity: TextView = view.tvCity
        val tvPin: TextView = view.tvPin
        val tvState: TextView = view.tvState
        val tvCountry: TextView = view.tvCountry
        val tvApprove: TextView = view.tvApprove
        val cdItem: CardView = view.cdItem
        val lnEdit: LinearLayout = view.lnEdit
        val lnDelete: LinearLayout = view.lnDelete
        val tvCustomerStatus: TextView = view.tvCustomerStatus

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_customer_list, parent, false)
        )
    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.swipeLayout; }

    override fun getItemCount(): Int {
        return customerList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mItemManger.bindView(holder.itemView, position)
        holder.lnDelete.setOnClickListener {
            holder.swipeLayout.close()
            mCallback.deleteCustomerCallback(position)
        }

        holder.lnEdit.setOnClickListener {
            holder.swipeLayout.close()
            mCallback.editCustomerCallback(position)
        }

        holder.cdItem.setOnClickListener {
        }

        holder.swipeLayout.addSwipeListener(object : SwipeListener {
            override fun onStartOpen(layout: SwipeLayout) {
                mItemManger.closeAllExcept(layout)
            }

            override fun onOpen(layout: SwipeLayout) {}
            override fun onStartClose(layout: SwipeLayout) {}
            override fun onClose(layout: SwipeLayout) {}
            override fun onUpdate(layout: SwipeLayout, leftOffset: Int, topOffset: Int) {}
            override fun onHandRelease(layout: SwipeLayout, xvel: Float, yvel: Float) {}
        })
        holder.tvApprove.setOnClickListener { mCallback.approveClick(position) }

        if (customerType == "PARTNER") {
            if (customerList[position].status == "1")
                holder.tvApprove.visibility = View.VISIBLE
        }
        holder.tvCustomerId.text = customerList[position].customer_id.toString()
        holder.tvCustomerName.text = customerList[position].name.toString()
        holder.tvCustomerStatus.text = customerList[position].statusName.toString()
        if (customerList[position].address != null)
            if (customerList[position].address!!.size > 0) {
                holder.tvCity.text = customerList[position].address?.get(0)?.city ?: "--"
                holder.tvAddressLine1.text = customerList[position].address?.get(0)?.address1
                        ?: "--"
                holder.tvPin.text = customerList[position].address?.get(0)?.pincode ?: "--"
                holder.tvState.text = customerList[position].address?.get(0)?.state ?: "--"
                holder.tvCountry.text = customerList[position].address?.get(0)?.country ?: "--"
            } else {
                holder.tvCity.text = "--"
                holder.tvAddressLine1.text = "--"
                holder.tvPin.text = "--"
                holder.tvState.text = "--"
                holder.tvCountry.text = "--"
            }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}