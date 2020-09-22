package com.custom.app.ui.createData.rules.config.list

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
import com.core.app.util.AlertUtil.showToast
import com.custom.app.CustomApp
import com.custom.app.R
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.createData.adapters.CustomerDropdownAdapter
import com.custom.app.ui.createData.rules.config.create.RuleConfigCreate
import com.custom.app.ui.createData.rules.config.update.RuleConfigUpdate
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerRes
import com.custom.app.util.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_device_list.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.util.*
import javax.inject.Inject

class RuleConfigListActivity : BaseActivity(), View.OnClickListener, RuleConfigListCallback {

    @Inject
    lateinit var customerInteractor: CustomerInteractor

    private lateinit var viewModel: RuleConfigListViewModel
    private lateinit var adapterRuleConfig: RuleConfigListAdapter
    lateinit var SearchBar: SearchView
    var selectedCustomerId: Int? = null

    private lateinit var customerList: ArrayList<CustomerRes>

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_list)

        viewModel = ViewModelProvider(this,
                RuleConfigListViewModelFactory(RuleConfigListInteractor(),
                        customerInteractor))[RuleConfigListViewModel::class.java]
        viewModel.ruleConfigListState.observe(::getLifecycle, ::updateUI)
        initView()
    }

    /**Initial View*/
    private fun initView() {
        toolbar.title = getString(R.string.create_rule_config)
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
            viewModel.onGetRuleConfigList(selectedCustomerId!!)
        }

    }

    private fun updateUI(screenStateRuleConfig: ScreenState<RuleConfigListState>?) {
        when (screenStateRuleConfig) {
            ScreenState.Loading -> progressListDevices.visibility = View.VISIBLE
            is ScreenState.Render -> processLoginState(screenStateRuleConfig.renderState)
        }
    }

    private fun processLoginState(renderStateRuleConfig: RuleConfigListState) {
        progressListDevices.visibility = View.GONE

        when (renderStateRuleConfig) {
            RuleConfigListState.RuleConfigListSuccess -> {
                updateRecycleView()
            }
            RuleConfigListState.RuleConfigListFailure -> {
                showToast(this, "Unable to fetch ruleConfigs")
            }
            RuleConfigListState.DeleteRuleConfigFailure -> {
                showToast(this, "Unable to delete ruleConfig")
            }
            RuleConfigListState.GetCustomerListFailure -> {
                showToast(this, "Unable to fetch customers")
            }
            RuleConfigListState.GetCustomerListSuccess -> {
                customerList = viewModel.customerList.value!!
                updateCustomerSpinner(customerList)
            }
            RuleConfigListState.DeleteRuleConfigSuccess -> {
                viewModel.ruleConfigList.value!!.removeAt(viewModel.deletedPos.value!!)
                viewModel.onGetRuleConfigList(selectedCustomerId!!)
            }
        }
    }

    private fun updateRecycleView() {
        rvDevices.layoutManager = LinearLayoutManager(this)
        adapterRuleConfig = RuleConfigListAdapter(this, viewModel.ruleConfigList.value!!, this)
        rvDevices.adapter = adapterRuleConfig
    }

    override fun onClick(view: View?) {
        when (view) {
            fbAdd -> {
                ActivityUtil.startActivityResult(this, RuleConfigCreate::class.java,Constants.REQUEST_ADD_REGION, false)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun editRuleConfigCallback(pos: Int) {
        val intent = Intent(this, RuleConfigUpdate::class.java)
        val gson = Gson()
        val type = object : TypeToken<RuleConfigRes>() {}.type
        val json = gson.toJson(viewModel.ruleConfigList.value!![pos], type)
        intent.putExtra("selectObject", json)
        startActivityForResult(intent, Constants.REQUEST_EDIT_REGION)
    }

    fun deleteRuleConfig(position: Int, ruleConfigId: Int) {
        viewModel.onDeleteRuleConfig(ruleConfigId, position)
    }

    override fun deleteRuleConfigCallback(pos: Int, ruleConfigId: Int) {
        showDeleteDialog(this, "Delete", "Are you sure, you want to delete", pos, ruleConfigId)
    }

    override fun itemClickCallback(pos: Int) {}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_ADD_DEVICE || requestCode == Constants.REQUEST_EDIT_REGION) {
            if (resultCode == Activity.RESULT_OK) {
                viewModel.onGetRuleConfigList(selectedCustomerId!!)
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
                        viewModel.onGetRuleConfigList(selectedCustomerId!!)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                    }
                }
    }

    fun showDeleteDialog(context: Context, title: String, message: String, position: Int, ruleConfigId: Int) {
        AlertDialog.Builder(context)
                .setTitle("$title")
                .setMessage("$message")

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setPositiveButton("Yes") { dialogInterface, which ->
                    deleteRuleConfig(position, ruleConfigId)
                }
                .setIcon(R.drawable.ic_delete_white_24dp)
                .show()
    }

}

interface RuleConfigListCallback {

    fun editRuleConfigCallback(pos: Int)
    fun deleteRuleConfigCallback(pos: Int, ruleConfigId: Int)
    fun itemClickCallback(pos: Int)

}