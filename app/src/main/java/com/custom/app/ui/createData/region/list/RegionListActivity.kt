package com.custom.app.ui.createData.region.list

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
import com.custom.app.ui.createData.region.RegionSiteInteractor
import com.custom.app.ui.createData.region.create.RegionCreate
import com.custom.app.ui.createData.region.site.create.RegionRes
import com.custom.app.ui.createData.region.update.RegionUpdate
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerRes
import com.custom.app.util.Constants
import com.daimajia.swipe.SwipeLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_device_list.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.util.*
import javax.inject.Inject

class RegionListActivity : BaseActivity(), View.OnClickListener, RegionListCallback {

    @Inject
    lateinit var customerInteractor: CustomerInteractor

    @Inject
    lateinit var regionSiteInteractor: RegionSiteInteractor

    private lateinit var viewModel: RegionListViewModel
    private lateinit var adapterRegion: RegionListAdapter
    lateinit var SearchBar: SearchView
    var selectedCustomerId: Int? = null

    private lateinit var customerList: ArrayList<CustomerRes>

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_list)

        viewModel = ViewModelProvider(this,
                RegionListViewModelFactory(regionSiteInteractor, customerInteractor))[RegionListViewModel::class.java]
        viewModel.regionListState.observe(::getLifecycle, ::updateUI)
        initView()
    }

    /**Initial View*/
    private fun initView() {
        toolbar.title = getString(R.string.create_region)
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
            viewModel.onGetRegionList(selectedCustomerId!!)
        }
    }

    private fun updateUI(screenStateRegion: ScreenState<RegionListState>?) {
        when (screenStateRegion) {
            ScreenState.Loading -> progressListDevices.visibility = View.VISIBLE
            is ScreenState.Render -> processLoginState(screenStateRegion.renderState)
        }
    }

    private fun processLoginState(renderStateRegion: RegionListState) {
        progressListDevices.visibility = View.GONE

        when (renderStateRegion) {
            RegionListState.RegionListSuccess -> {
                updateRecycleView()
            }
            RegionListState.RegionListFailure -> {
                AlertUtil.showSnackBar(listLayout, viewModel.errorMassage, 2000)
            }
            RegionListState.DeleteRegionFailure -> {
                AlertUtil.showSnackBar(listLayout, viewModel.errorMassage, 2000)
            }
            RegionListState.GetCustomerListFailure -> {
                AlertUtil.showSnackBar(listLayout, viewModel.errorMassage, 2000)
            }
            RegionListState.GetCustomerListSuccess -> {
                customerList = viewModel.customerList.value!!
                updateCustomerSpinner(customerList)
            }
            RegionListState.DeleteRegionSuccess -> {
                viewModel.regionList.value!!.removeAt(viewModel.deletedPos.value!!)
                viewModel.onGetRegionList(selectedCustomerId!!)
            }
        }
    }

    private fun updateRecycleView() {
        rvDevices.layoutManager = LinearLayoutManager(this)
        adapterRegion = RegionListAdapter(this, viewModel.regionList.value!!, this)
        rvDevices.adapter = adapterRegion
    }

    override fun onClick(view: View?) {
        when (view) {
            fbAdd -> {
                ActivityUtil.startActivityResult(this, RegionCreate::class.java, Constants.REQUEST_ADD_REGION, false)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun editRegionCallback(pos: Int, layout: SwipeLayout) {
        val intent = Intent(this, RegionUpdate::class.java)
        val gson = Gson()
        val type = object : TypeToken<RegionRes>() {}.type
        val json = gson.toJson(viewModel.regionList.value!![pos], type)
        intent.putExtra("selectObject", json)
        startActivityForResult(intent, Constants.REQUEST_EDIT_REGION)
        layout.close()
    }

    fun deleteRegion(position: Int, regionId: Int) {
        viewModel.onDeleteRegion(regionId, position)
    }

    override fun deleteRegionCallback(pos: Int, regionId: Int, layout: SwipeLayout) {
        showDeleteDialog(this, "Delete", "Are you sure, you want to delete", pos, regionId, layout)
    }

    override fun itemClickCallback(pos: Int, layout: SwipeLayout) {}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_ADD_DEVICE || requestCode == Constants.REQUEST_EDIT_REGION) {
            if (resultCode == Activity.RESULT_OK) {
                viewModel.onGetRegionList(selectedCustomerId!!)
            }
        }
    }

    private fun updateCustomerSpinner(customerList: ArrayList<CustomerRes>) {

        val adapter = CustomerDropdownAdapter(this, customerList)
        spCustomer.adapter = adapter
        spCustomer.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                        selectedCustomerId = customerList[position].customer_id!!.toInt()
                        viewModel.onGetRegionList(selectedCustomerId!!)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                    }
                }
    }

    fun showDeleteDialog(context: Context, title: String, message: String, position: Int, regionId: Int, layout: SwipeLayout) {
        AlertDialog.Builder(context)
                .setTitle("$title")
                .setMessage("$message")

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setPositiveButton("Yes") { dialogInterface, which ->
                    deleteRegion(position, regionId)

                    layout.close()
                }
                .setIcon(R.drawable.ic_delete_white_24dp)
                .show()
    }

}

interface RegionListCallback {

    fun editRegionCallback(pos: Int, layout: SwipeLayout)
    fun deleteRegionCallback(pos: Int, regionId: Int, layout: SwipeLayout)
    fun itemClickCallback(pos: Int, layout: SwipeLayout)

}