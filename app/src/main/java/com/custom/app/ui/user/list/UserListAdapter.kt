package com.custom.app.ui.user.list

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
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter
import kotlinx.android.synthetic.main.item_customer_list.view.cdItem
import kotlinx.android.synthetic.main.item_customer_list.view.lnDelete
import kotlinx.android.synthetic.main.item_customer_list.view.lnEdit
import kotlinx.android.synthetic.main.item_customer_list.view.swipeLayout
import kotlinx.android.synthetic.main.item_user_list.view.*

class UserListAdapter(val context: Context, val userList: ArrayList<UserDataRes>,
                      val mCallback: UserCallback) : RecyclerSwipeAdapter<UserListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val swipeLayout: SwipeLayout = view.swipeLayout
        val tvUserName: TextView = view.tvUserName
        val tvContactNumber: TextView = view.tvContactNumber
        val tvRole: TextView = view.tvRole
        val tvCity: TextView = view.tvCity
        val tvPin: TextView = view.tvPin
        val tvState: TextView = view.tvState
        val tvCountry: TextView = view.tvCountry
        val cdItem: CardView = view.cdItem
        val lnEdit: LinearLayout = view.lnEdit
        val lnDelete: LinearLayout = view.lnDelete
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_user_list, parent, false))
    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.swipeLayout
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mItemManger.bindView(holder.itemView, position)
        //Item click
        holder.lnDelete.setOnClickListener {
            holder.swipeLayout.close()
            mCallback.deleteUserCallback(position)
        }

        holder.lnEdit.setOnClickListener {
            holder.swipeLayout.close()
            mCallback.editUserCallback(position)
        }

        holder.tvUserName.text = userList[position].first_name
        holder.tvContactNumber.text = userList[position].contact_number

        if (userList[position].roles_list != null)
            if (userList[position].roles_list!!.size > 0)
                holder.tvRole.text = userList[position].roles_list?.get(0)?.role_desc ?: "Operator"
        if (userList[position].address != null)
            if (userList[position].address!!.size > 0) {
                holder.tvCity.text = userList[position].address?.get(0)?.city ?: "SAS Nagar"
                holder.tvPin.text = userList[position].address?.get(0)?.pincode ?: "1600054"
                holder.tvState.text = userList[position].address?.get(0)?.state ?: "Punjab"
                holder.tvCountry.text = userList[position].address?.get(0)?.country ?: "IN"
            }
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}