package com.custom.app.ui.createData.profile.list

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.custom.app.R
import com.custom.app.ui.createData.profile.update.ProfileUpdate
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.SwipeLayout.SwipeListener
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.item_region_list.view.*

class ProfileListAdapter(var context: Context, val profileList: ArrayList<ProfileListRes>,
                         val mCallback: ProfileListCallback) : RecyclerSwipeAdapter<ProfileListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvProfileName = view.tvRegionName
        var mainLayout = view.mainLayout_region
        var inEdit_layout = view.lnEdit_region
        var InDelete_layout = view.lnDelete_region
        var swipeLayout = view.swipeLayout_region
//        var titleRegionName = view.titleRegionName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_region_list, parent, false)
        )
    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.swipeLayout_region;
    }

    override fun getItemCount(): Int {
        return profileList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mItemManger.bindView(holder.itemView, position)
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

    holder.tvProfileName.text = profileList[position].profile_name.toString()

    holder.inEdit_layout.setOnClickListener{
        val intent = Intent(context, ProfileUpdate::class.java)
        moveData(position, intent)
    }

    holder.InDelete_layout.setOnClickListener{
        mCallback.deleteProfileCallback(position, profileList[position].profile_id!!.toInt())
    }

//    holder.titleRegionName.text = "Profile name"

}

fun moveData(position: Int, intent: Intent) {
    val gson = Gson()
    val type = object : TypeToken<ProfileListRes>() {}.type
    val json = gson.toJson(profileList[position], type)
    intent.putExtra("selectObject", json)
    context.startActivity(intent)
}
}