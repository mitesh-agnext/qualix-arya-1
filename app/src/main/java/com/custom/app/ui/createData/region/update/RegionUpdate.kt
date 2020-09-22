package com.custom.app.ui.createData.region.update

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
import com.custom.app.ui.createData.region.site.create.RegionRes
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerRes
import com.custom.app.util.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_create_intl_centers.progressAddDevices
import kotlinx.android.synthetic.main.activity_create_region.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.util.*
import javax.inject.Inject

class RegionUpdate : BaseActivity() {

    @Inject
    lateinit var customerInteractor: CustomerInteractor

    @Inject
    lateinit var regionSiteInteractor: RegionSiteInteractor
    private lateinit var viewModel: RegionUpdateViewModel
    private var selectedCustomerId: Int = 0
    private lateinit var customerList: ArrayList<CustomerRes>

    var regionId: Int? = null
    var testObject: RegionRes? = null
    var adapter: CustomerDropdownAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_region)

        val selectObject = intent.getStringExtra("selectObject")
        val gson = Gson()
        if (selectObject != null) {
            val type = object : TypeToken<RegionRes>() {}.type
            testObject = gson.fromJson(selectObject, type)
        }

        viewModel = ViewModelProvider(this,
                UpdateRegionViewModelFactory(regionSiteInteractor))[RegionUpdateViewModel::class.java]

        viewModel.regionUpdateState.observe(::getLifecycle, ::updateUI)
        initView()

        if (userManager.customerType.equals("SERVICE_PROVIDER")) {
            titleCustomers_region.visibility = View.VISIBLE
            spLayoutCustomer_region.visibility = View.VISIBLE
            viewModel.onGetCustomer()
        } else {
            titleCustomers_region.visibility = View.GONE
            spLayoutCustomer_region.visibility = View.GONE
            selectedCustomerId = userManager.customerId.toInt()
            updateView(viewModel.singleRegion)
        }
    }

    fun initView() {
        toolbar.title = getString(R.string.update_region)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        btCreateRegion.text = "Update Region"
        btCreateRegion.setOnClickListener {
            addRegion()
        }
    }

    private fun updateUI(screenCreateState: ScreenState<RegionUpdateState>?) {
        when (screenCreateState) {
            ScreenState.Loading -> progressAddDevices.visibility = View.VISIBLE
            is ScreenState.Render -> processLoginState(screenCreateState.renderState)
        }
    }

    private fun processLoginState(renderCreateState: RegionUpdateState) {
        progressAddDevices.visibility = View.GONE
        when (renderCreateState) {
            RegionUpdateState.RegionNameEmpty -> {
                AlertUtil.showSnackBar(
                        regionParent,
                        "Please enter region name",
                        2000
                )
            }
            RegionUpdateState.GetCustomerSuccess -> {
                customerList = viewModel.getCustomerList.value!!
                updateCustomerSpinner(customerList)

            }
            RegionUpdateState.GetCustomerFailure -> {
                AlertUtil.showSnackBar(
                        regionParent,
                        "Unable to fetch customers",
                        2000
                )
            }
            RegionUpdateState.RegionUpdateFailure -> {
                AlertUtil.showSnackBar(
                        regionParent,
                        "Unable to update region",
                        2000
                )
            }
            RegionUpdateState.RegionUpdateSuccess -> {
                resetForm()
                AlertUtil.showActionAlertDialog(this, "", "Region has been updated !",
                        false, getString(R.string.btn_ok)) { dialog: DialogInterface?, which: Int ->
                    val intent = Intent()
                    intent.putExtra("result", "success")
                    setResult(RESULT_OK, intent);
                    finish()
                }

            }
        }
    }

    private fun updateCustomerSpinner(customerList: ArrayList<CustomerRes>) {

        adapter = CustomerDropdownAdapter(this, customerList)
        spCustomers_region.adapter = adapter
        spCustomers_region.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                        selectedCustomerId = customerList[position].customer_id!!.toInt()
                        updateView(viewModel.singleRegion)

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
        viewModel.onUpdateRegion(
                et_RegionName_region.text.toString(),
                regionId!!,
                selectedCustomerId
        )
    }

    private fun updateView(singleRegion: RegionRes) {

        if (customerList.isNotEmpty()) {
            for (i in 0 until customerList.size) {
                spCustomers_region.setSelection(customerList[i].customer_id.toString().indexOf(singleRegion.customer_id.toString()))
            }
            adapter!!.notifyDataSetChanged()
        }

        et_RegionName_region.setText(testObject!!.region_name)
        regionId = testObject!!.region_id

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