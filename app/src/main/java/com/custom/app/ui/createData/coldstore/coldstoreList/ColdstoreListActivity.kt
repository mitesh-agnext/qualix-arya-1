package com.custom.app.ui.createData.coldstore.coldstoreList

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.base.app.ui.base.BaseActivity
import com.core.app.util.ActivityUtil
import com.core.app.util.AlertUtil
import com.custom.app.CustomApp
import com.custom.app.R
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.createData.adapters.CustomerDropdownAdapter
import com.custom.app.ui.createData.coldstore.createColdstore.ColdstoreInteractor
import com.custom.app.ui.createData.coldstore.createColdstore.CreateColdstore
import com.custom.app.ui.createData.coldstore.updateColdstore.UpdateColdstore
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerRes
import com.custom.app.util.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_device_list.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.util.*
import javax.inject.Inject

class ColdstoreListActivity : BaseActivity(), View.OnClickListener, CenterListCallback {

    @Inject
    lateinit var customerInteractor: CustomerInteractor

    @Inject
    lateinit var coldstoreInteractor: ColdstoreInteractor

    private lateinit var viewModel: ColdstoreListViewModel
    private lateinit var adapterDevice: ColdstoreListAdapter
    lateinit var SearchBar: SearchView
    var newTextQuery: String? = ""
    var selectedCustomerId: Int? = null

    private lateinit var customerList: ArrayList<CustomerRes>

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_list)

        viewModel = ViewModelProvider(this, CenterListViewModelFactory(coldstoreInteractor, customerInteractor))[ColdstoreListViewModel::class.java]
        viewModel.center_listState.observe(::getLifecycle, ::updateUI)

        initView()
    }

    private fun initView() {
        toolbar.title = getString(R.string.coldstores)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        SearchBar = findViewById(R.id.searchBar)
        filterLayout.visibility = View.VISIBLE
        regionLayout.visibility = View.GONE
        fbAdd.setOnClickListener(this)

        if (userManager.customerType.equals("SERVICE_PROVIDER")) {
            customerLayout.visibility = View.VISIBLE
            viewModel.onGetCustomerList()
        }
        else {
            customerLayout.visibility = View.GONE
            selectedCustomerId = userManager.customerId.toInt()
            viewModel.onGetCenterList(newTextQuery!!,selectedCustomerId!!)
        }

        SearchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean { // do something on text submit
                viewModel.onGetCenterList(query, selectedCustomerId!!)
                newTextQuery = query
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean { // do something when text changes
                viewModel.onGetCenterList(newText, selectedCustomerId!!)
                newTextQuery = newText
                return false
            }
        })
    }

    private fun updateUI(screenStateDevice: ScreenState<ColdstoreListState>?) {
        when (screenStateDevice) {
            ScreenState.Loading -> progressListDevices.visibility = View.VISIBLE
            is ScreenState.Render -> processLoginState(screenStateDevice.renderState)
        }
    }

    private fun processLoginState(renderStateDevice: ColdstoreListState) {
        progressListDevices.visibility = View.GONE

        when (renderStateDevice) {
            ColdstoreListState.ListCenterSuccess -> {
                updateRecycleView()
            }
            ColdstoreListState.ListCenterFailure -> {
                AlertUtil.showSnackBar(
                        listLayout,
                        viewModel.errorMessage,
                        2000
                )
            }
            ColdstoreListState.GetCustomerListFailure -> {
                AlertUtil.showSnackBar(
                        listLayout,
                        viewModel.errorMessage,
                        2000
                )
            }
            ColdstoreListState.GetCustomerListSuccess -> {
                customerList = viewModel.customerList.value!!
                updateCustomerSpinner(customerList)
            }
            ColdstoreListState.GetDevicesHasConnectionFailure -> {
                AlertUtil.showSnackBar(
                        listLayout,
                        viewModel.errorMessage,
                        2000
                )
            }
            ColdstoreListState.CenterDeleteFailure -> {
                AlertUtil.showSnackBar(
                        listLayout,
                        viewModel.errorMessage,
                        2000
                )
            }
            ColdstoreListState.CenterDeleteSuccess -> {
                viewModel.centerList.value!!.removeAt(viewModel.deletedPos.value!!)
                viewModel.onGetCenterList(newTextQuery!!, selectedCustomerId!!)
            }
            ColdstoreListState.GetDevicesTokenExpired -> {
                AlertUtil.showSnackBar(
                        listLayout,
                        viewModel.errorMessage,
                        2000
                )
            }
        }
    }

    private fun updateRecycleView() {
        rvDevices.layoutManager = LinearLayoutManager(this)
        adapterDevice = ColdstoreListAdapter(this, viewModel.centerList.value!!, this)
        rvDevices.adapter = adapterDevice
    }

    override fun onClick(view: View?) {
        when (view) {
            fbAdd -> {
                ActivityUtil.startActivityResult(this, CreateColdstore::class.java,Constants.REQUEST_ADD_COLDSTORE ,false)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun deleteCenter(position: Int, deviceId: Int) {
        viewModel.onDeleteCenter(deviceId.toString(), position)

    }

    override fun editCenterCallback(pos: Int) {
        val intent = Intent(this, UpdateColdstore::class.java)
        val gson = Gson()
        val type = object : TypeToken<ColdstoreRes>() {}.type
        val json = gson.toJson(viewModel.centerList.value!![pos], type)
        intent.putExtra("selectObject", json)
        startActivityForResult(intent,Constants.REQUEST_EDIT_COLDSTORE)

    }

    override fun deleteCenterCallback(pos: Int, centerId: Int) {
        showDeleteDialog(this, "Delete", "Are you sure, you want to delete coldstore?", pos, centerId)
    }

    override fun itemClickCallback(pos: Int) {
    }

    private fun updateCustomerSpinner(customerList: ArrayList<CustomerRes>) {

        val adapter = CustomerDropdownAdapter(this, customerList)
        spCustomer.adapter = adapter
        spCustomer.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                        selectedCustomerId = customerList[position].customer_id!!.toInt()
                        viewModel.onGetCenterList(newTextQuery!!, selectedCustomerId!!)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                    }
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_ADD_COLDSTORE || requestCode == Constants.REQUEST_EDIT_COLDSTORE) {
            if (resultCode == Activity.RESULT_OK) {
                    viewModel.onGetCenterList(newTextQuery!!, selectedCustomerId!!)
            }
        }
    }

    fun showDeleteDialog(context: Context, title: String, message: String, position: Int, centerId: Int) {
        AlertDialog.Builder(context)
                .setTitle("$title")
                .setMessage("$message")

                .setPositiveButton("Yes") { dialogInterface, which ->
                    deleteCenter(position, centerId)
                }
                .setIcon(R.drawable.ic_delete_white_24dp)
                .show()
    }
}

interface CenterListCallback {
    fun editCenterCallback(pos: Int)
    fun deleteCenterCallback(pos: Int, deviceId: Int)
    fun itemClickCallback(pos: Int)
}