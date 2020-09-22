package com.custom.app.ui.createData.rules.config.update

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.base.app.ui.base.BaseActivity
import com.core.app.util.AlertUtil
import com.custom.app.R
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.createData.adapters.CustomerDropdownAdapter
import com.custom.app.ui.createData.rules.config.create.RuleConfigCreateInteractor
import com.custom.app.ui.createData.rules.config.list.RuleConfigRes
import com.custom.app.ui.customer.list.CustomerRes
import com.custom.app.util.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_create_intl_centers.progressAddDevices
import kotlinx.android.synthetic.main.activity_create_ruleconfig.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.util.*

class RuleConfigUpdate : BaseActivity() {

    private lateinit var viewModel: RuleConfigUpdateViewModel
    private var selectedCustomerId: Int = 0
    private lateinit var customerList: ArrayList<CustomerRes>

    var ruleConfigId: Int? = null
    var testObject: RuleConfigRes? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_ruleconfig)

        val selectObject = intent.getStringExtra("selectObject")
        val gson = Gson()
        if (selectObject != null) {
            val type = object : TypeToken<RuleConfigRes>() {}.type
            testObject = gson.fromJson(selectObject, type)
        }

        viewModel = ViewModelProvider(this,
                UpdateRuleConfigViewModelFactory(RuleConfigCreateInteractor(),
                        RuleConfigUpdateInteractor()))[RuleConfigUpdateViewModel::class.java]

        viewModel.ruleConfigUpdateState.observe(::getLifecycle, ::updateUI)
        initView()

        if (userManager.customerType.equals("SERVICE_PROVIDER")) {
            titleCustomers_ruleConfig.visibility = View.VISIBLE
            spLayoutCustomer_ruleConfig.visibility = View.VISIBLE
            viewModel.onGetCustomer()
        }
        else {
            titleCustomers_ruleConfig.visibility = View.GONE
            spLayoutCustomer_ruleConfig.visibility = View.GONE
            selectedCustomerId = userManager.customerId.toInt()
            updateView(viewModel.singleRuleConfig)
        }
    }

    fun initView() {
        toolbar.title = getString(R.string.update_rule_config)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        btCreateRuleConfig.text = "Update RuleConfig"
        btCreateRuleConfig.setOnClickListener {
            addRuleConfig()
        }
    }

    private fun updateUI(screenCreateState: ScreenState<RuleConfigUpdateState>?) {
        when (screenCreateState) {
            ScreenState.Loading -> progressAddDevices.visibility = View.VISIBLE
            is ScreenState.Render -> processLoginState(screenCreateState.renderState)
        }
    }

    private fun processLoginState(renderCreateState: RuleConfigUpdateState) {
        progressAddDevices.visibility = View.GONE
        when (renderCreateState) {
            RuleConfigUpdateState.RuleConfigNameEmpty -> {
                AlertUtil.showToast(this, "Please enter ruleConfig name")
            }
            RuleConfigUpdateState.GetCustomerSuccess -> {
                customerList = viewModel.getCustomerList.value!!
                updateCustomerSpinner(customerList)

                updateView(viewModel.singleRuleConfig)
            }
            RuleConfigUpdateState.GetCustomerFailure -> {
                AlertUtil.showToast(this, "Unable to fetch customers")
            }
            RuleConfigUpdateState.RuleConfigUpdateFailure -> {
                AlertUtil.showToast(this, "Unable to update ruleConfig")
            }
            RuleConfigUpdateState.RuleConfigUpdateSuccess -> {
                resetForm()
                val intent = Intent()
                intent.putExtra("result", "success")
                setResult(RESULT_OK, intent);
                finish()
                AlertUtil.showToast(this, "RuleConfig has been updated")

            }
        }
    }

    private fun updateCustomerSpinner(customerList: ArrayList<CustomerRes>) {

        val adapter = CustomerDropdownAdapter(this, customerList)
        spCustomers_ruleConfig.adapter = adapter
        spCustomers_ruleConfig.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View,
                            position: Int,
                            id: Long) {
                        if (customerList.isNotEmpty()) {
                            for (i in 0 until customerList.size) {
                                if (customerList[i].customer_id == testObject!!.customer_id) {
                                    spCustomers_ruleConfig.setSelection(i)
                                }
                            }
                        }

                        selectedCustomerId = customerList[position].customer_id!!.toInt()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Another interface callback
                    }
                }
    }

    private fun resetForm() {
        et_RuleConfigName.setText("")
    }

    private fun addRuleConfig() {
        viewModel.onUpdateRuleConfig(
                et_RuleConfigName.text.toString(),
                ruleConfigId!!,
                selectedCustomerId
        )
    }

    private fun updateView(singleRuleConfig: RuleConfigRes) {

        et_RuleConfigName.setText(testObject!!.rule_config_name)
        ruleConfigId = testObject!!.rule_config_id

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === Constants.REQUEST_EDIT_REGION) {
            if (resultCode === Activity.RESULT_OK) {
                setResult(AppCompatActivity.RESULT_OK);
                finish()
            }
        }
    }
}