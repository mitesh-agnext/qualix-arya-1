package com.custom.app.ui.createData.deviationProfile.list

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
import com.core.app.util.AlertUtil.showToast
import com.custom.app.CustomApp
import com.custom.app.R
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.createData.adapters.CustomerDropdownAdapter
import com.custom.app.ui.createData.deviationProfile.create.CreateDeviationProfile
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerRes
import com.custom.app.util.Constants
import kotlinx.android.synthetic.main.activity_device_list.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.util.*
import javax.inject.Inject

class DeviationListActivity : BaseActivity(), View.OnClickListener, DeviationListCallback {

    @Inject
    lateinit var customerInteractor: CustomerInteractor

    private lateinit var viewModel: DeviationListViewModel
    private lateinit var adapterSite: DeviationListAdapter
    lateinit var SearchBar: SearchView

    private var selectedCustomerId: Int = 0
    private lateinit var customerList: ArrayList<CustomerRes>

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_list)

        viewModel = ViewModelProvider(this,
                DeviationListViewModelFactory(DeviationListInteractor(),
                        customerInteractor))[DeviationListViewModel::class.java]
        viewModel.deviationListState.observe(::getLifecycle, ::updateUI)

        initView()
    }

    /**Initial View*/
    private fun initView() {
        toolbar.title = getString(R.string.food)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        SearchBar = findViewById(R.id.searchBar)
        filterLayout.visibility = View.VISIBLE
        regionLayout.visibility = View.GONE
        fbAdd.setOnClickListener(this)

        viewModel.onGetCustomerList()

    }

    private fun updateUI(screenStateSite: ScreenState<DeviationListState>?) {
        when (screenStateSite) {
            ScreenState.Loading -> progressListDevices.visibility = View.VISIBLE
            is ScreenState.Render -> processLoginState(screenStateSite.renderState)
        }
    }

    private fun processLoginState(renderStateSite: DeviationListState) {
        progressListDevices.visibility = View.GONE

        when (renderStateSite) {
            DeviationListState.DeviationListSuccess -> {
                updateRecycleView()
            }
            DeviationListState.DeviationListFailure -> {
                AlertUtil.showSnackBar(
                        listLayout,
                        viewModel.errorMessage,
                        2000
                )
            }
            DeviationListState.DeleteDeviationFailure -> {
                AlertUtil.showSnackBar(
                        listLayout,
                        viewModel.errorMessage,
                        2000
                )
            }
            DeviationListState.DeleteDeviationSuccess -> {
                viewModel.deviationList.value!!.removeAt(viewModel.deletedPos.value!!)
                viewModel.onGetDeviationList(selectedCustomerId)
            }
            DeviationListState.GetCustomerSuccess -> {
                customerList = viewModel.customerList.value!!
                updateCustomerSpinner(customerList)
            }
            DeviationListState.GetCustomerFailure -> {
                showToast(this, "Unable to fetch customers")
            }
        }
    }

    private fun updateRecycleView() {
        rvDevices.layoutManager = LinearLayoutManager(this)
        adapterSite = DeviationListAdapter(this, viewModel.deviationList.value!!, this)
        rvDevices.adapter = adapterSite
    }

    override fun onClick(view: View?) {
        when (view) {
            fbAdd -> {
                ActivityUtil.startActivity(this, CreateDeviationProfile::class.java, false)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun editDeviationCallback(pos: Int) {

    }

    fun deleteDeviation(position: Int, foodTypeId: Int) {
        viewModel.onDeleteDeviation(foodTypeId, position)
    }

    override fun deleteDeviationCallback(pos: Int, foodTypeId: Int) {
        showDeleteDialog(this, "Delete", "Are you sure, you want to delete", pos, foodTypeId)
    }

    override fun itemClickCallback(pos: Int) {}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_ADD_DEVICE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data!!.getStringExtra("result") == "success") {
                    viewModel.onGetDeviationList(selectedCustomerId)
                }
            }
        }
    }

    fun showDeleteDialog(context: Context, title: String, message: String, position: Int, foodTypeId: Int) {
        AlertDialog.Builder(context)
                .setTitle("$title")
                .setMessage("$message")

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setPositiveButton("Yes") { dialogInterface, which ->
                    deleteDeviation(position, foodTypeId)
                }
                .setIcon(R.drawable.ic_delete_white_24dp)
                .show()
    }

    private fun updateCustomerSpinner(customerList: ArrayList<CustomerRes>) {

        val adapter = CustomerDropdownAdapter(this, customerList)
        spCustomer.adapter = adapter
        spCustomer.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View,
                            position: Int,
                            id: Long) {

                        selectedCustomerId = customerList[position].customer_id!!.toInt()
                        viewModel.onGetDeviationList(selectedCustomerId)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Another interface callback
                    }
                }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onGetDeviationList(selectedCustomerId)
    }
}

interface DeviationListCallback {

    fun editDeviationCallback(pos: Int)
    fun deleteDeviationCallback(pos: Int, foodTypeId: Int)
    fun itemClickCallback(pos: Int)

}