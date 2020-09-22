package com.custom.app.ui.createData.rules.config.create

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
import com.custom.app.ui.customer.list.CustomerRes
import com.custom.app.util.Constants
import kotlinx.android.synthetic.main.activity_create_intl_centers.progressAddDevices
import kotlinx.android.synthetic.main.activity_create_ruleconfig.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.util.*

class RuleConfigCreate : BaseActivity() {

    private lateinit var viewModel: RuleConfigCreateViewModel
    private var selectedCustomerId: Int = 0
    private lateinit var customerList: ArrayList<CustomerRes>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_ruleconfig)

        viewModel = ViewModelProvider(this,
                CreateRuleConfigViewModelFactory(RuleConfigCreateInteractor()))[RuleConfigCreateViewModel::class.java]

        viewModel.ruleConfigCreateState.observe(::getLifecycle, ::updateUI)
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
        }
    }

    fun initView() {
        toolbar.title = getString(R.string.create_rule_config)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        btCreateRuleConfig.text = "Create RuleConfig"
        btCreateRuleConfig.setOnClickListener {
            addRuleConfig()
        }
    }

    private fun updateUI(screenCreateState: ScreenState<RuleConfigCreateState>?) {
        when (screenCreateState) {
            ScreenState.Loading -> progressAddDevices.visibility = View.VISIBLE
            is ScreenState.Render -> processLoginState(screenCreateState.renderState)
        }
    }

    private fun processLoginState(renderCreateState: RuleConfigCreateState) {
        progressAddDevices.visibility = View.GONE
        when (renderCreateState) {
            RuleConfigCreateState.RuleConfigNameEmpty -> {
                AlertUtil.showToast(this, "Please enter ruleConfig name")
            }
            RuleConfigCreateState.GetCustomerSuccess -> {
                customerList = viewModel.getCustomerList.value!!
                updateCustomerSpinner(customerList)
            }
            RuleConfigCreateState.GetCustomerFailure -> {
                AlertUtil.showToast(this, "Unable to fetch customers")
            }
            RuleConfigCreateState.RuleConfigCreateFailure -> {
                AlertUtil.showToast(this, "Unable to create ruleConfig")
            }
            RuleConfigCreateState.RuleConfigCreateSuccess -> {
                resetForm()
                val intent = Intent()
                intent.putExtra("result", "success")
                setResult(RESULT_OK, intent);
                finish()

                AlertUtil.showToast(this, "New ruleConfig has been created")

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
        viewModel.onCreateRuleConfig(et_RuleConfigName.text.toString(), selectedCustomerId
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === Constants.REQUEST_ADD_REGION) {
            if (resultCode === Activity.RESULT_OK) {
                setResult(AppCompatActivity.RESULT_OK);
                finish()
            }
        }
    }
}