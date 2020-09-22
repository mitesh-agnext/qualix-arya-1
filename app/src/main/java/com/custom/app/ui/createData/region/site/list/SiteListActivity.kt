package com.custom.app.ui.createData.region.site.list

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
import com.custom.app.ui.createData.region.site.create.CreateSite
import com.custom.app.ui.createData.region.site.create.RegionRes
import com.custom.app.ui.createData.region.site.create.adapters.RegionDropdownAdapter
import com.custom.app.ui.createData.region.site.update.UpdateSite
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerRes
import com.custom.app.util.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_device_list.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.util.*
import javax.inject.Inject

class SiteListActivity : BaseActivity(), View.OnClickListener, SiteListCallback {

    @Inject
    lateinit var customerInteractor: CustomerInteractor

    @Inject
    lateinit var regionSiteInteractor: RegionSiteInteractor
    private lateinit var viewModel: SiteListViewModel
    private lateinit var adapterSite: SiteListAdapter
    lateinit var SearchBar: SearchView
    var selectedCustomerId: Int? = null
    var selectedRegionId: Int? = null

    var newTextQuery: String? = ""

    private lateinit var customerList: ArrayList<CustomerRes>
    private lateinit var siteList: ArrayList<SiteListRes>
    private lateinit var regionList: ArrayList<RegionRes>

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_list)

        viewModel = ViewModelProvider(this,
                SitesListViewModelFactory(regionSiteInteractor, customerInteractor))[SiteListViewModel::class.java]
        viewModel.siteListState.observe(::getLifecycle, ::updateUI)

        if (userManager.customerType.equals("SERVICE_PROVIDER")) {
            customerLayout.visibility = View.VISIBLE
            viewModel.onGetCustomerList()
        } else {
            customerLayout.visibility = View.GONE
            selectedCustomerId = userManager.customerId.toInt()
            viewModel.onGetRegionList(selectedCustomerId!!)
        }

        initView()
    }

    private fun initView() {
        toolbar.title = getString(R.string.create_site)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        SearchBar = findViewById(R.id.searchBar)
        regionLayout.visibility = View.VISIBLE

        fbAdd.setOnClickListener(this)

        SearchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean { // do something on text submit
                viewModel.onGetSiteList(query, selectedRegionId!!)
                newTextQuery = query
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean { // do something when text changes
                if (selectedRegionId != null) {
                    viewModel.onGetSiteList(newText, selectedRegionId!!)
                    newTextQuery = newText
                }
                return false
            }
        })
    }

    private fun updateUI(screenStateSite: ScreenState<SiteListState>?) {
        when (screenStateSite) {
            ScreenState.Loading -> progressListDevices.visibility = View.VISIBLE
            is ScreenState.Render -> processLoginState(screenStateSite.renderState)
        }
    }

    private fun processLoginState(renderStateSite: SiteListState) {
        progressListDevices.visibility = View.GONE

        when (renderStateSite) {
            SiteListState.SiteListFailure -> {
                AlertUtil.showSnackBar(
                        listLayout,
                        viewModel.errorMessage,
                        2000
                )
            }
            SiteListState.SiteListSuccess -> {
                updateRecycleView()
            }
            SiteListState.GetCustomerListFailure -> {
                AlertUtil.showSnackBar(
                        listLayout,
                        viewModel.errorMessage,
                        2000
                )
            }
            SiteListState.GetCustomerListSuccess -> {
                customerList = viewModel.customerList.value!!
                updateCustomerSpinner(customerList)
            }
            SiteListState.RegionListFailure -> {
                AlertUtil.showSnackBar(
                        listLayout,
                        viewModel.errorMessage,
                        2000
                )
            }
            SiteListState.RegionListSuccess -> {
                regionList = viewModel.regionList.value!!
                updateRegionSpinner(regionList)
            }
            SiteListState.DeleteSiteFailure -> {
                AlertUtil.showSnackBar(
                        listLayout,
                        viewModel.errorMessage,
                        2000
                )
            }
            SiteListState.DeleteSiteSuccess -> {
                viewModel.siteList.value!!.removeAt(viewModel.deletedPos.value!!)
                viewModel.onGetSiteList(newTextQuery!!, selectedRegionId!!)
            }
        }
    }

    private fun updateRecycleView() {
        rvDevices.layoutManager = LinearLayoutManager(this)
        adapterSite = SiteListAdapter(this, viewModel.siteList.value!!, this)
        rvDevices.adapter = adapterSite
    }

    override fun onClick(view: View?) {
        when (view) {
            fbAdd -> {
                ActivityUtil.startActivityResult(this, CreateSite::class.java, Constants.REQUEST_ADD_SITE, false)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun editSiteCallback(pos: Int) {

        val intent = Intent(this, UpdateSite::class.java)
        val gson = Gson()
        val type = object : TypeToken<SiteListRes>() {}.type
        val json = gson.toJson(viewModel.siteList.value!![pos], type)
        intent.putExtra("selectObject", json)
        startActivityForResult(intent, Constants.REQUEST_EDIT_SITE)


    }

    fun deleteSite(position: Int, siteId: Int) {
        viewModel.onDeleteSite(siteId, position)
    }

    override fun deleteSiteCallback(pos: Int, siteId: Int) {
        showDeleteDialog(this, "Delete", "Are you sure, you want to delete", pos, siteId)
    }

    override fun itemClickCallback(pos: Int) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_ADD_SITE || requestCode == Constants.REQUEST_EDIT_SITE) {
            if (resultCode == Activity.RESULT_OK) {
                viewModel.onGetSiteList(newTextQuery!!, selectedRegionId!!)
            }
        }
    }

    fun showDeleteDialog(context: Context, title: String, message: String, position: Int, siteId: Int) {
        AlertDialog.Builder(context)
                .setTitle("$title")
                .setMessage("$message")

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setPositiveButton("Yes") { dialogInterface, which ->
                    deleteSite(position, siteId)
                }
                .setIcon(R.drawable.ic_delete_white_24dp)
                .show()
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

    private fun updateRegionSpinner(regionList: ArrayList<RegionRes>) {

        val adapter = RegionDropdownAdapter(this, regionList)
        spRegion.adapter = adapter
        spRegion.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                        selectedRegionId = regionList[position].region_id!!.toInt()
                        viewModel.onGetSiteList(newTextQuery!!, selectedRegionId!!)

                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }
    }
}

interface SiteListCallback {

    fun editSiteCallback(pos: Int)
    fun deleteSiteCallback(pos: Int, siteId: Int)
    fun itemClickCallback(pos: Int)

}