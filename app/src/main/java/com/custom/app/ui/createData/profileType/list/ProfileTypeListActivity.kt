package com.custom.app.ui.createData.profileType.list

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
import com.custom.app.ui.createData.profileType.create.ProfileTypeCreate
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerRes
import com.custom.app.util.Constants
import kotlinx.android.synthetic.main.activity_device_list.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.util.*
import javax.inject.Inject

class ProfileTypeListActivity : BaseActivity(), View.OnClickListener, ProfileTypeListCallback {

    @Inject
    lateinit var customerInteractor: CustomerInteractor

    private lateinit var viewModel: ProfileTypeListViewModel
    private lateinit var adapterSite: ProfileTypeListAdapter
    lateinit var SearchBar: SearchView

    private var selectedCustomerId: Int = 0
    private lateinit var customerList: ArrayList<CustomerRes>

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_list)

        viewModel = ViewModelProvider(this,
                ProfileTypeListViewModelFactory(ProfileTypeListInteractor(),
                        customerInteractor))[ProfileTypeListViewModel::class.java]
        viewModel.profileTypeListState.observe(::getLifecycle, ::updateUI)

        initView()
    }

    /**Initial View*/
    private fun initView() {
        toolbar.title = getString(R.string.profile_type)
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
            viewModel.onGetProfileTypeList(selectedCustomerId)
        }

    }

    private fun updateUI(screenStateSite: ScreenState<ProfileTypeListState>?) {
        when (screenStateSite) {
            ScreenState.Loading -> progressListDevices.visibility = View.VISIBLE
            is ScreenState.Render -> processLoginState(screenStateSite.renderState)
        }
    }

    private fun processLoginState(renderStateSite: ProfileTypeListState) {
        progressListDevices.visibility = View.GONE

        when (renderStateSite) {
            ProfileTypeListState.ProfileTypeListSuccess -> {
                updateRecycleView()
            }
            ProfileTypeListState.ProfileTypeListFailure -> {
                AlertUtil.showSnackBar(listLayout, viewModel.errorMessage, 2000)
            }
            ProfileTypeListState.DeleteProfileTypeFailure -> {
                AlertUtil.showSnackBar(listLayout, viewModel.errorMessage, 2000)
            }
            ProfileTypeListState.DeleteProfileTypeSuccess -> {
                viewModel.profileTypeList.value!!.removeAt(viewModel.deletedPos.value!!)
                viewModel.onGetProfileTypeList(selectedCustomerId)
            }
            ProfileTypeListState.GetCustomerSuccess -> {
                customerList = viewModel.customerList.value!!
                updateCustomerSpinner(customerList)
            }
            ProfileTypeListState.GetCustomerFailure -> {
                AlertUtil.showSnackBar(listLayout, viewModel.errorMessage, 2000)
            }
        }
    }

    private fun updateRecycleView() {
        rvDevices.layoutManager = LinearLayoutManager(this)
        adapterSite = ProfileTypeListAdapter(this, viewModel.profileTypeList.value!!, this)
        rvDevices.adapter = adapterSite
    }

    override fun onClick(view: View?) {
        when (view) {
            fbAdd -> {
                ActivityUtil.startActivityResult(this, ProfileTypeCreate::class.java, Constants.REQUEST_ADD_PROFILE_TYPE, false)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun editProfileTypeCallback(pos: Int) {

    }

    fun deleteProfileType(position: Int, profileTypeId: Int) {
        viewModel.onDeleteProfileType(profileTypeId, position)
    }

    override fun deleteProfileTypeCallback(pos: Int, profileTypeId: Int) {
        showDeleteDialog(this, "Delete", "Are you sure, you want to delete", pos, profileTypeId)
    }

    override fun itemClickCallback(pos: Int) {}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_ADD_DEVICE) {
            if (resultCode == Activity.RESULT_OK) {
                viewModel.onGetProfileTypeList(selectedCustomerId)
            }
        }
    }

    fun showDeleteDialog(context: Context, title: String, message: String, position: Int, profileTypeId: Int) {
        AlertDialog.Builder(context)
                .setTitle("$title")
                .setMessage("$message")

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setPositiveButton("Yes") { dialogInterface, which ->
                    deleteProfileType(position, profileTypeId)
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
                        viewModel.onGetProfileTypeList(selectedCustomerId)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onGetProfileTypeList(selectedCustomerId)
    }
}

interface ProfileTypeListCallback {

    fun editProfileTypeCallback(pos: Int)
    fun deleteProfileTypeCallback(pos: Int, profileTypeId: Int)
    fun itemClickCallback(pos: Int)

}