package com.custom.app.ui.createData.foodType.list

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
import com.custom.app.ui.createData.foodType.create.FoodTypeCreate
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerRes
import com.custom.app.util.Constants
import kotlinx.android.synthetic.main.activity_device_list.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.util.*
import javax.inject.Inject

class FoodTypeListActivity : BaseActivity(), View.OnClickListener, FoodTypeListCallback {

    @Inject
    lateinit var customerInteractor: CustomerInteractor

    private lateinit var viewModel: FoodTypeListViewModel
    private lateinit var adapterSite: FoodTypeListAdapter
    lateinit var SearchBar: SearchView

    private var selectedCustomerId: Int = 0
    private lateinit var customerList: ArrayList<CustomerRes>

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_list)

        viewModel = ViewModelProvider(this,
                FoodTypeListViewModelFactory(FoodTypeListInteractor(), customerInteractor))[FoodTypeListViewModel::class.java]
        viewModel.foodTypeListState.observe(::getLifecycle, ::updateUI)

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

        if (userManager.customerType.equals("SERVICE_PROVIDER")) {
            customerLayout.visibility = View.VISIBLE
            viewModel.onGetCustomerList()
        }
        else {
            customerLayout.visibility = View.GONE
            selectedCustomerId = userManager.customerId.toInt()
            viewModel.onGetFoodTypeList(selectedCustomerId)
        }


    }

    private fun updateUI(screenStateSite: ScreenState<FoodTypeListState>?) {
        when (screenStateSite) {
            ScreenState.Loading -> progressListDevices.visibility = View.VISIBLE
            is ScreenState.Render -> processLoginState(screenStateSite.renderState)
        }
    }

    private fun processLoginState(renderStateSite: FoodTypeListState) {
        progressListDevices.visibility = View.GONE

        when (renderStateSite) {
            FoodTypeListState.FoodTypeListSuccess -> {
                updateRecycleView()
            }
            FoodTypeListState.FoodTypeListFailure -> {
                AlertUtil.showSnackBar(
                        listLayout,
                        viewModel.errorMessage,
                        2000
                )
            }
            FoodTypeListState.DeleteFoodTypeFailure -> {
                AlertUtil.showSnackBar(
                        listLayout,
                        viewModel.errorMessage,
                        2000
                )
            }
            FoodTypeListState.DeleteFoodTypeSuccess -> {
                viewModel.foodTypeList.value!!.removeAt(viewModel.deletedPos.value!!)
                viewModel.onGetFoodTypeList(selectedCustomerId)
            }
            FoodTypeListState.GetCustomerSuccess -> {
                customerList = viewModel.customerList.value!!
                updateCustomerSpinner(customerList)
            }
            FoodTypeListState.GetCustomerFailure -> {
                showToast(this, "Unable to fetch customers")
            }
        }
    }

    private fun updateRecycleView() {
        rvDevices.layoutManager = LinearLayoutManager(this)
        adapterSite = FoodTypeListAdapter(this, viewModel.foodTypeList.value!!, this)
        rvDevices.adapter = adapterSite
    }

    override fun onClick(view: View?) {
        when (view) {
            fbAdd -> {
                ActivityUtil.startActivity(this, FoodTypeCreate::class.java, false)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun editFoodTypeCallback(pos: Int) {

    }

    fun deleteFoodType(position: Int, foodTypeId: Int) {
        viewModel.onDeleteFoodType(this, foodTypeId, position)
    }

    override fun deleteFoodTypeCallback(pos: Int, foodTypeId: Int) {

        showDeleteDialog(this, "Delete", "Are you sure, you want to delete", pos, foodTypeId)
    }

    override fun itemClickCallback(pos: Int) {}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_ADD_DEVICE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data!!.getStringExtra("result") == "success") {
                    viewModel.onGetFoodTypeList(selectedCustomerId)
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
                    deleteFoodType(position, foodTypeId)
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
                        viewModel.onGetFoodTypeList(selectedCustomerId)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Another interface callback
                    }
                }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onGetFoodTypeList(selectedCustomerId)
    }
}

interface FoodTypeListCallback {

    fun editFoodTypeCallback(pos: Int)
    fun deleteFoodTypeCallback(pos: Int, foodTypeId: Int)
    fun itemClickCallback(pos: Int)

}