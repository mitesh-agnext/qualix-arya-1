package com.custom.app.ui.createData.profile.list

import android.content.Context
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
import com.custom.app.ui.createData.profile.create.ProfileCreate
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerRes
import kotlinx.android.synthetic.main.activity_device_list.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.util.*
import javax.inject.Inject

class ProfileListActivity : BaseActivity(), View.OnClickListener, ProfileListCallback {

    @Inject
    lateinit var customerInteractor: CustomerInteractor

    private lateinit var viewModel: ProfileListViewModel
    private lateinit var adapterSite: ProfileListAdapter
    lateinit var SearchBar: SearchView

    private var selectedCustomerId: Int = 0
    private lateinit var customerList: ArrayList<CustomerRes>

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_list)

        viewModel = ViewModelProvider(this, ProfilesListViewModelFactory(ProfileListInteractor(), customerInteractor))[ProfileListViewModel::class.java]
        viewModel.profileListState.observe(::getLifecycle, ::updateUI)

        initView()
    }

    /**Initial View*/
    private fun initView() {
        toolbar.title = getString(R.string.profile)
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
            viewModel.onGetProfileList(selectedCustomerId)
        }
    }

    private fun updateUI(screenStateSite: ScreenState<ProfileListState>?) {
        when (screenStateSite) {
            ScreenState.Loading -> progressListDevices.visibility = View.VISIBLE
            is ScreenState.Render -> processLoginState(screenStateSite.renderState)
        }
    }

    private fun processLoginState(renderStateSite: ProfileListState) {
        progressListDevices.visibility = View.GONE

        when (renderStateSite) {
            ProfileListState.ProfileListSuccess -> {
                updateRecycleView()
            }
            ProfileListState.ProfileListFailure -> {
                AlertUtil.showSnackBar(listLayout, viewModel.errorMessage, 2000)
            }
            ProfileListState.DeleteProfileFailure -> {
                AlertUtil.showSnackBar(listLayout, viewModel.errorMessage, 2000)
            }
            ProfileListState.DeleteProfileSuccess -> {
                viewModel.profileList.value!!.removeAt(viewModel.deletedPos.value!!)
                viewModel.onGetProfileList(selectedCustomerId)

            }
            ProfileListState.GetCustomerSuccess -> {
                customerList = viewModel.customerList.value!!
                updateCustomerSpinner(customerList)
            }
            ProfileListState.GetCustomerFailure -> {
                AlertUtil.showSnackBar(listLayout, viewModel.errorMessage, 2000)
            }
        }
    }

    private fun updateRecycleView() {
        rvDevices.layoutManager = LinearLayoutManager(this)
        adapterSite = ProfileListAdapter(this, viewModel.profileList.value!!, this)
        rvDevices.adapter = adapterSite
    }

    override fun onClick(view: View?) {
        when (view) {
            fbAdd -> {
                ActivityUtil.startActivity(this, ProfileCreate::class.java, false)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun editProfileCallback(pos: Int) {}

    fun deleteProfile(position: Int, profileId: Int) {
        viewModel.onDeleteProfile(this, profileId, position)
    }

    override fun deleteProfileCallback(pos: Int, siteId: Int) {

        showDeleteDialog(this, "Delete", "Are you sure, you want to delete", pos, siteId)
    }

    override fun itemClickCallback(pos: Int) {}

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == Constants.REQUEST_ADD_DEVICE) {
//            if (resultCode == Activity.RESULT_OK) {
//                if (data!!.getStringExtra("result") == "success") {
//                    viewModel.onGetProfileList(selectedCustomerId)
//                }
//            }
//        }
//    }

    fun showDeleteDialog(context: Context, title: String, message: String, position: Int, profileId: Int) {
        AlertDialog.Builder(context)
                .setTitle("$title")
                .setMessage("$message")

                .setPositiveButton("Yes") { dialogInterface, which ->
                    deleteProfile(position, profileId)
                }
                .setIcon(R.drawable.ic_delete_white_24dp)
                .show()
    }

    private fun updateCustomerSpinner(customerList: ArrayList<CustomerRes>) {

        val adapter = CustomerDropdownAdapter(this, customerList)
        spCustomer.adapter = adapter
        spCustomer.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                        selectedCustomerId = customerList[position].customer_id!!.toInt()
                        viewModel.onGetProfileList(selectedCustomerId)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                    }
                }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onGetProfileList(selectedCustomerId)
    }

}

interface ProfileListCallback {
    fun editProfileCallback(pos: Int)
    fun deleteProfileCallback(pos: Int, siteId: Int)
    fun itemClickCallback(pos: Int)
}