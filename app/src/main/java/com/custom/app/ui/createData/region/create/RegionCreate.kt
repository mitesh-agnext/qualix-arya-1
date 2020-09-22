package com.custom.app.ui.createData.region.create

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.base.app.ui.base.BaseActivity
import com.core.app.util.AlertUtil
import com.custom.app.CustomApp
import com.custom.app.R
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.createData.adapters.CustomerDropdownAdapter
import com.custom.app.ui.createData.region.RegionSiteInteractor
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerRes
import com.custom.app.util.Constants
import kotlinx.android.synthetic.main.activity_create_intl_centers.progressAddDevices
import kotlinx.android.synthetic.main.activity_create_region.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.util.*
import javax.inject.Inject

class RegionCreate : BaseActivity() {

    @Inject
    lateinit var customerInteractor: CustomerInteractor

    @Inject
    lateinit var regionSiteInteractor: RegionSiteInteractor

    private lateinit var viewModel: RegionCreateViewModel
    private var selectedCustomerId: Int = 0
    private lateinit var customerList: ArrayList<CustomerRes>

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_region)

        viewModel = ViewModelProvider(this, CreateRegionViewModelFactory(regionSiteInteractor))[RegionCreateViewModel::class.java]

        viewModel.regionCreateState.observe(::getLifecycle, ::updateUI)
        initView()

        if (userManager.customerType.equals("SERVICE_PROVIDER")) {
            titleCustomers_region.visibility = View.VISIBLE
            spLayoutCustomer_region.visibility = View.VISIBLE
            viewModel.onGetCustomer()
        } else {
            titleCustomers_region.visibility = View.GONE
            spLayoutCustomer_region.visibility = View.GONE
            selectedCustomerId = userManager.customerId.toInt()
        }
    }

    fun initView() {
        toolbar.title = getString(R.string.create_region)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        btCreateRegion.text = getString(R.string.create_region)
        btCreateRegion.setOnClickListener {
            addRegion()
        }
    }

    private fun updateUI(screenCreateState: ScreenState<RegionCreateState>?) {
        when (screenCreateState) {
            ScreenState.Loading -> progressAddDevices.visibility = View.VISIBLE
            is ScreenState.Render -> processLoginState(screenCreateState.renderState)
        }
    }

    private fun processLoginState(renderCreateState: RegionCreateState) {
        progressAddDevices.visibility = View.GONE
        when (renderCreateState) {
            RegionCreateState.RegionNameEmpty -> {
                AlertUtil.showSnackBar(
                        regionParent,
                        "Please enter region name",
                        2000
                )
            }
            RegionCreateState.GetCustomerSuccess -> {
                customerList = viewModel.getCustomerList.value!!
                updateCustomerSpinner(customerList)
            }
            RegionCreateState.GetCustomerFailure -> {
                AlertUtil.showSnackBar(
                        regionParent,
                        "Unable to fetch customers",
                        2000
                )
            }
            RegionCreateState.RegionCreateFailure -> {
                AlertUtil.showSnackBar(
                        regionParent,
                        "Unable to create region",
                        2000
                )
            }
            RegionCreateState.RegionCreateSuccess -> {
                resetForm()
                AlertUtil.showActionAlertDialog(this, "", "New region has been created !",
                        false, getString(R.string.btn_ok)) { dialog: DialogInterface?, which: Int ->
                    val intent = Intent()
                    intent.putExtra("result", "success")
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        }
    }

    private fun updateCustomerSpinner(customerList: ArrayList<CustomerRes>) {

        val adapter = CustomerDropdownAdapter(this, customerList)
        spCustomers_region.adapter = adapter
        spCustomers_region.onItemSelectedListener =
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
        et_RegionName_region.setText("")
    }

    private fun addRegion() {
        viewModel.onCreateRegion(et_RegionName_region.text.toString(), selectedCustomerId
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