package com.custom.app.ui.device.list

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.core.app.util.ActivityUtil
import com.core.app.util.AlertUtil
import com.custom.app.CustomApp
import com.custom.app.R
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.device.add.AddDeviceActivity
import com.custom.app.ui.device.update.UpdateDeviceActivity
import com.custom.app.ui.devices.deviceList.DeviceDetailActivity
import com.custom.app.util.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_device_list.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import javax.inject.Inject

class DeviceListActivity : AppCompatActivity(), View.OnClickListener, DeviceListCallback {

    @Inject
    lateinit var interactor: DeviceInteractor

    private lateinit var viewModel: DeviceListViewModel
    private lateinit var adapterDevice: DeviceListAdapter
    lateinit var SearchBar: SearchView
    var activityStatus: Int? = null
    var newTextQuery: String? = ""
    var listStatus: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_list)

        viewModel = ViewModelProvider(this, DevicesListViewModelFactory(interactor))[DeviceListViewModel::class.java]
        viewModel.deviceListState.observe(::getLifecycle, ::updateUI)

        val intent = intent
        activityStatus = intent.getIntExtra("activityStatus", 0)

        initView()
    }

    private fun initView() {

        if(activityStatus==1){
            toolbar.title = getString(R.string.device_provisioning)
        }
        else {
            toolbar.title = getString(R.string.device_management)
        }

        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        SearchBar = findViewById(R.id.searchBar)
        filterLayout.visibility = View.GONE
        fbAdd.setOnClickListener(this)

        if (activityStatus == 0) {
            fbAdd.visibility = View.VISIBLE
            listStatus = "inventory"
        } else {
            fbAdd.visibility = View.GONE
            listStatus = ""
        }

        viewModel.onGetDeviceList(newTextQuery!!, listStatus!!)

        SearchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean { // do something on text submit
                viewModel.onGetDeviceList(query, listStatus!!)
                newTextQuery = query
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean { // do something when text changes
                viewModel.onGetDeviceList(newText, listStatus!!)
                newTextQuery = newText
                return false
            }
        })

    }

    private fun updateUI(screenStateDevice: ScreenState<DeviceListState>?) {
        when (screenStateDevice) {
            ScreenState.Loading -> progressListDevices.visibility = View.VISIBLE
            is ScreenState.Render -> processLoginState(screenStateDevice.renderState)
        }
    }

    private fun processLoginState(renderStateDevice: DeviceListState) {
        progressListDevices.visibility = View.GONE

        when (renderStateDevice) {
            DeviceListState.ListDevicesSuccess -> {
                updateRecycleView()
            }
            DeviceListState.ListDevicesFailure -> {
                AlertUtil.showSnackBar(listLayout, "Unable to get list of devices", 2000)
            }
            DeviceListState.GetDevicesHasConnectionFailure -> {
                AlertUtil.showSnackBar(listLayout, "Unable to get list of devices", 2000)
            }
            DeviceListState.DeleteDevicesFailure -> {
                AlertUtil.showSnackBar(listLayout, "Unable to delete device", 2000)
            }
            DeviceListState.DeleteDevicesSuccess -> {
                viewModel.deviceList.value!!.removeAt(viewModel.deletedPos.value!!)
                adapterDevice.notifyDataSetChanged()
            }
            DeviceListState.GetDevicesTokenExpired -> {
                AlertUtil.showSnackBar(listLayout, "Token expired", 2000)
            }
        }
    }

    private fun updateRecycleView() {
        rvDevices.layoutManager = LinearLayoutManager(this)
        adapterDevice = DeviceListAdapter(this, viewModel.deviceList.value!!, activityStatus!!, this)
        rvDevices.adapter = adapterDevice
    }

    override fun onClick(view: View?) {
        when (view) {
            fbAdd -> {
                ActivityUtil.startActivityResult(this, AddDeviceActivity::class.java, Constants.REQUEST_ADD_DEVICE, false)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun editDeviceCallback(pos: Int, deviceList: ArrayList<DevicesData>) {
        val intent = Intent(this, UpdateDeviceActivity::class.java)
        moveData(pos, intent, deviceList)

    }

    fun deleteDevice(position: Int, deviceId: Int) {
        viewModel.onDeleteDevice(deviceId.toString(), position)
    }

    override fun deleteDeviceCallback(pos: Int, deviceId: Int) {

        showDeleteDialog(this, "Delete", "Are you sure, you want to delete", pos, deviceId)
    }

    override fun itemClickCallback(pos: Int, deviceList: ArrayList<DevicesData>) {
        val intent = Intent(this, DeviceDetailActivity::class.java)
        moveData(pos, intent, deviceList)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_ADD_DEVICE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data!!.getStringExtra("result") == "success") {
                    viewModel.onGetDeviceList(newTextQuery!!, listStatus!!)
                }
            }
        }
    }

    fun showDeleteDialog(context: Context, title: String, message: String, position: Int, deviceId: Int) {
        AlertDialog.Builder(context).setTitle("$title")
                .setMessage("$message")

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setPositiveButton("Yes") { dialogInterface, which ->
                    deleteDevice(position, deviceId)
                }
                .setIcon(R.drawable.ic_delete_white_24dp)
                .show()
    }

    fun moveData(position: Int, intent: Intent, deviceList: ArrayList<DevicesData>) {
        val gson = Gson()
        val type = object : TypeToken<DevicesData>() {}.type
        val json = gson.toJson(deviceList[position], type)
        intent.putExtra("selectObject", json)
        startActivityForResult(intent, Constants.REQUEST_ADD_DEVICE)
    }

}

interface DeviceListCallback {

    fun editDeviceCallback(pos: Int, deviceList: ArrayList<DevicesData>)
    fun deleteDeviceCallback(pos: Int, deviceId: Int)
    fun itemClickCallback(pos: Int, deviceList: ArrayList<DevicesData>)

}