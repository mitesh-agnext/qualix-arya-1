package com.custom.app.ui.user.list

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.base.app.ui.base.BaseActivity
import com.core.app.util.AlertUtil
import com.custom.app.CustomApp
import com.custom.app.R
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.user.add.AddUserActivity
import com.custom.app.ui.user.edit.EditUserActivity
import com.custom.app.util.Constants
import com.custom.app.util.Constants.REQUEST_ADD_USER
import com.custom.app.util.Constants.REQUEST_EDIT_USER
import com.custom.app.util.Utils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_customer_list.fbAdd
import kotlinx.android.synthetic.main.activity_customer_list.searchBar
import kotlinx.android.synthetic.main.activity_user_list.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import javax.inject.Inject

class UserListActivity : BaseActivity(), View.OnClickListener, SearchView.OnQueryTextListener, AdapterView.OnItemSelectedListener, UserCallback {

    @Inject
    lateinit var interactor: UserInteractor

    @Inject
    lateinit var customerInteractor: CustomerInteractor

    private lateinit var viewModel: UserListViewModel
    private lateinit var adapter: UserListAdapter
    private val customerName = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)
        initView()
    }

    private fun initView() {
        toolbar.title = getString(R.string.user_management)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        spCustomer.onItemSelectedListener = this
        fbAdd.setOnClickListener(this)
        searchBar.setOnQueryTextListener(this)

        viewModel = ViewModelProvider(this,
                UserListViewModelFactory(interactor, customerInteractor))[UserListViewModel::class.java]
        viewModel.userListStateLiveData.observe(::getLifecycle, ::setViewState)
        if (userManager.customerType == "SERVICE_PROVIDER") {
            viewModel.getCustomerList()
            tvCustomer.visibility = View.VISIBLE
            spCustomer.visibility = View.VISIBLE
        } else {
            tvCustomer.visibility = View.GONE
            spCustomer.visibility = View.GONE
        }

        viewModel.getUserListVm(userManager.customerId)
        searchBar.isFocusable = false
    }

    private fun setViewState(state: UserListState) {
        when (state) {
            is Loading -> setProgress(true)
            is CustomerList -> {
                setProgress(false)
                customerName.clear()
                if (viewModel.customerList.size > 0) {
                    customerName.add(getString(R.string.select_customer))
                    if (viewModel.customerList.size > 0) {
                        for (i in 0 until viewModel.customerList.size) {
                            customerName.add(viewModel.customerList[i].name.toString())
                        }
                    }
                } else {
                    customerName.add(getString(R.string.no_data))
                }
                Utils.setSpinnerAdapter(this, customerName, spCustomer)
            }
            is CustomerError -> {
                setProgress(false)
                AlertUtil.showToast(this, "Error")
            }
            is List -> {
                setProgress(false)
                if (viewModel.userList.value!!.size > 0) {
                    tvNoData.visibility = View.GONE
                    rvUser.visibility = View.VISIBLE
                    adapter = UserListAdapter(this, viewModel.userList.value!!, this)
                    rvUser.adapter = adapter
                    rvUser.layoutManager = LinearLayoutManager(this)
                } else {
                    tvNoData.visibility = View.VISIBLE
                    rvUser.visibility = View.GONE
                }
            }
            is Delete -> {
                setProgress(false)
                adapter.notifyDataSetChanged()
            }
            is Error -> {
                setProgress(false)
                showError(state.message!!)
            }
            is TokenExpire -> {
                AlertUtil.showToast(this, getString(R.string.token_expire))
                Utils.tokenExpire(this@UserListActivity)
            }
        }
    }

    private fun setProgress(isLoading: Boolean) {
        if (isLoading) {
            showProgress()
        } else {
            hideProgress()
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun showProgress() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        progressBar.visibility = View.GONE
    }

    override fun onClick(view: View?) {
        when (view) {
            fbAdd -> {
                val intent = Intent(this, AddUserActivity::class.java)
                intent.putExtra("flow", Constants.FLOW_USER_LIST)
                intent.putExtra("rawData", "")
                intent.putExtra("rawAddress", "")
                startActivityForResult(intent, REQUEST_ADD_USER)
            }
        }
    }

    override fun editUserCallback(pos: Int) {
        val intent = Intent(this, EditUserActivity::class.java)
        val gson = Gson()
        val type = object : TypeToken<UserDataRes>() {}.type
        val json = gson.toJson(viewModel.userList.value!![pos], type)
        intent.putExtra("selectObject", json)
        startActivityForResult(intent, REQUEST_EDIT_USER)
    }

    override fun deleteUserCallback(pos: Int) {
        dialogConfirmDelete("Delete User", "Are you sure you want to delete user", this, pos)
    }

    override fun itemClick(pos: Int) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ADD_USER || requestCode == REQUEST_EDIT_USER) {
            if (resultCode == Activity.RESULT_OK) {
                viewModel.getUserListVm(userManager.customerId)
            }
        }
    }

    private fun dialogConfirmDelete(title: String?, message: String?, context: Context, pos: Int) {
        AlertDialog.Builder(context)
                .setTitle("$title")
                .setMessage("$message")
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    viewModel.deleteUser(pos)
                }
                .show()
    }

    override fun onQueryTextSubmit(keyword: String?): Boolean {
        viewModel.getUserListSearchVm(keyword!!)
        return false
    }

    override fun onQueryTextChange(keyword: String?): Boolean {
        viewModel.getUserListSearchVm(keyword!!)
        return false
    }

    override fun onDestroy() {
        super.onDestroy()

        viewModel.destroy()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    override fun onItemSelected(spinner: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
        when (spinner) {
            spCustomer -> {
                if (pos > 0) {
                    viewModel.getUserListVm(viewModel.customerList[pos - 1].customer_id.toString()!!)
                } else if (pos == 0) {
                    viewModel.getUserListVm(userManager.customerId)
                }
            }
        }
    }
}

interface UserCallback {

    fun editUserCallback(pos: Int)
    fun deleteUserCallback(pos: Int)
    fun itemClick(pos: Int)

}