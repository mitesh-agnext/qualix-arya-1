package com.custom.app.ui.createData.flcScan.season.list

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
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
import com.custom.app.ui.createData.adapters.CustomerDropdownAdapter
import com.custom.app.ui.createData.flcScan.season.create.Season_creation
import com.custom.app.ui.createData.flcScan.season.update.SeasonUpdate
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerRes
import com.custom.app.util.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_device_list.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.util.*
import javax.inject.Inject

class SeasonListActivity : AppCompatActivity(), View.OnClickListener, SeasonListCallback {

    @Inject
    lateinit var customerInteractor: CustomerInteractor

    private lateinit var viewModel: SeasonListViewModel
    private lateinit var adapterSeason: SeasonListAdapter
    lateinit var SearchBar: SearchView
    var selectedCustomerId: Int? = null

    var newTextQuery: String? = ""

    private lateinit var customerList: ArrayList<CustomerRes>

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_list)

        viewModel = ViewModelProvider(this,
                SeasonListViewModelFactory(SeasonListInteractor(), customerInteractor))[SeasonListViewModel::class.java]
        viewModel.regionListState.observe(::getLifecycle, ::updateUI)
        initView()
    }

    /**Initial View*/
    private fun initView() {
        toolbar.title = getString(R.string.create_season)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        SearchBar = findViewById(R.id.searchBar)
        filterLayout.visibility = View.VISIBLE
        regionLayout.visibility = View.GONE

        fbAdd.setOnClickListener(this)
        viewModel.onGetCustomerList()

        SearchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean { // do something on text submit
                viewModel.onGetSeasonList(selectedCustomerId!!, query)
                newTextQuery = query
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean { // do something when text changes
                viewModel.onGetSeasonList(selectedCustomerId!!, newText)
                newTextQuery = newText
                return false
            }
        })

    }

    private fun updateUI(screenStateSeason: ScreenState<SeasonListState>?) {
        when (screenStateSeason) {
            ScreenState.Loading -> progressListDevices.visibility = View.VISIBLE
            is ScreenState.Render -> processLoginState(screenStateSeason.renderState)
        }
    }

    private fun processLoginState(renderStateSeason: SeasonListState) {
        progressListDevices.visibility = View.GONE

        when (renderStateSeason) {
            SeasonListState.SeasonListSuccess -> {
                updateRecycleView()
            }
            SeasonListState.SeasonListFailure -> {
                AlertUtil.showSnackBar(
                        listLayout,
                        viewModel.errorMassage,
                        2000
                )
            }
            SeasonListState.DeleteSeasonFailure -> {
                AlertUtil.showSnackBar(
                        listLayout,
                        viewModel.errorMassage,
                        2000
                )
            }
            SeasonListState.GetCustomerListFailure -> {
                AlertUtil.showSnackBar(
                        listLayout,
                        viewModel.errorMassage,
                        2000
                )
            }
            SeasonListState.GetCustomerListSuccess -> {
                customerList = viewModel.customerList.value!!
                updateCustomerSpinner(customerList)
            }
            SeasonListState.DeleteSeasonSuccess -> {
                viewModel.regionList.value!!.removeAt(viewModel.deletedPos.value!!)
                viewModel.onGetSeasonList(selectedCustomerId!!, newTextQuery!!)
            }
        }
    }

    private fun updateRecycleView() {
        rvDevices.layoutManager = LinearLayoutManager(this)
        adapterSeason = SeasonListAdapter(this, viewModel.regionList.value!!, this)
        rvDevices.adapter = adapterSeason
    }

    override fun onClick(view: View?) {
        when (view) {
            fbAdd -> {
                ActivityUtil.startActivityResult(this, Season_creation::class.java, Constants.REQUEST_ADD_REGION, false)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun editSeasonCallback(pos: Int) {
        val intent = Intent(this, SeasonUpdate::class.java)
        val gson = Gson()
        val type = object : TypeToken<SeasonRes>() {}.type
        val json = gson.toJson(viewModel.regionList.value!![pos], type)
        intent.putExtra("selectObject", json)
        startActivityForResult(intent, Constants.REQUEST_EDIT_REGION)
    }

    fun deleteSeason(position: Int, regionId: Int) {
        viewModel.onDeleteSeason(regionId, position)
    }

    override fun deleteSeasonCallback(pos: Int, regionId: Int) {
        showDeleteDialog(this, "Delete", "Are you sure, you want to delete", pos, regionId)
    }

    override fun itemClickCallback(pos: Int) {}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_ADD_DEVICE || requestCode == Constants.REQUEST_EDIT_REGION) {
            if (resultCode == Activity.RESULT_OK) {
                viewModel.onGetSeasonList(selectedCustomerId!!, newTextQuery!!)
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
                        viewModel.onGetSeasonList(selectedCustomerId!!, newTextQuery!!)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                    }
                }
    }

    fun showDeleteDialog(context: Context, title: String, message: String, position: Int, regionId: Int) {
        AlertDialog.Builder(context)
                .setTitle("$title")
                .setMessage("$message")

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setPositiveButton("Yes") { dialogInterface, which ->
                    deleteSeason(position, regionId)
                }
                .setIcon(R.drawable.ic_delete_white_24dp)
                .show()
    }

}

interface SeasonListCallback {

    fun editSeasonCallback(pos: Int)
    fun deleteSeasonCallback(pos: Int, regionId: Int)
    fun itemClickCallback(pos: Int)

}