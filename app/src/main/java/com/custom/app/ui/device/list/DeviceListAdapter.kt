package com.custom.app.ui.device.list

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.custom.app.R
import com.custom.app.ui.device.assign.DeviceProvisionActivity
import com.custom.app.util.Utils
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.SwipeLayout.SwipeListener
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.item_customer_list.view.swipeLayout
import kotlinx.android.synthetic.main.item_device_list.view.*


class DeviceListAdapter(var context: Context, val deviceList: ArrayList<DevicesData>,
                        val activityStatus: Int, val mCallback: DeviceListCallback) : RecyclerSwipeAdapter<DeviceListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvSerialNumber = view.tvSerialNumber
        var tvDeviceTypeId = view.tvDeviceTypeId
        var tvHwRevision = view.tvHwRevision
        var tvFwRevision = view.tvFwRevision
        var tvStartOfLife = view.tvStartOfLife
        var tvEndOfLife = view.tvEndOfLife
        var tvSensorProfile = view.tvSensorProfileItem
        var tvDeviceSubType = view.tvDeviceSubTypeItem
        var tvDeviceGroup = view.tvDeviceGroupItem
        var mainLayout = view.mainLayout
        var inEdit_layout = view.lnEdit_device
        var InDelete_layout = view.lnDelete_layout
        var swipeLayout = view.swipeLayout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_device_list, parent, false)
        )
    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.swipeLayout;
    }

    override fun getItemCount(): Int {

        return deviceList.size
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

        if (deviceList[position].serial_number != null) {
            holder.tvSerialNumber.text = deviceList[position].serial_number.toString()
        }
        if (deviceList[position].device_type != null) {
            holder.tvDeviceTypeId.text = deviceList[position].device_type.toString()
        }
//        if (deviceList[position].device_group_desc != null) {
//            holder.tvDeviceGroup.text = deviceList[position].device_group_desc.toString()
//        }
//        if (deviceList[position].device_sub_type_desc != null) {
//            holder.tvDeviceSubType.text = deviceList[position].device_sub_type_desc.toString()
//        }
//        if (deviceList[position].sensor_profile_desc != null) {
//            holder.tvSensorProfile.text = deviceList[position].sensor_profile_desc.toString()
//        }

        if (deviceList[position].start_of_life != null) {
            holder.tvStartOfLife.text = Utils.timeStampDate(deviceList[position].start_of_life!!)
        }
        if (deviceList[position].end_of_life != null) {
            holder.tvEndOfLife.text = Utils.timeStampDate(deviceList[position].end_of_life!!)
        }
        if (deviceList[position].fw_revision != null) {
            holder.tvFwRevision.text = deviceList[position].fw_revision.toString()
        }
        if (deviceList[position].hw_revision != null) {
            holder.tvHwRevision.text = deviceList[position].hw_revision.toString()
        }

        if (activityStatus == 1) {
            holder.mainLayout.setOnClickListener {
                val intent = Intent(context, DeviceProvisionActivity::class.java)
                moveData(position, intent)
            }

        } else {
            holder.mainLayout.setOnClickListener {

                mCallback.itemClickCallback(position, deviceList)
            }
        }

        holder.inEdit_layout.setOnClickListener {

            mCallback.editDeviceCallback(position, deviceList)

        }

        holder.InDelete_layout.setOnClickListener {
            mCallback.deleteDeviceCallback(position, deviceList[position].device_id!!.toInt())
        }
    }

    fun moveData(position: Int, intent: Intent) {
        val gson = Gson()
        val type = object : TypeToken<DevicesData>() {}.type
        val json = gson.toJson(deviceList[position], type)
        intent.putExtra("selectObject", json)
        context.startActivity(intent)
    }
}