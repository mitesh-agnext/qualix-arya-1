package com.custom.app.ui.createData.foodType.update

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.lifecycle.ViewModelProvider
import com.base.app.ui.base.BaseActivity
import com.core.app.util.AlertUtil
import com.custom.app.CustomApp
import com.custom.app.R
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.createData.adapters.CustomerDropdownAdapter
import com.custom.app.ui.createData.foodType.create.FoodTypeCreateInteractor
import com.custom.app.ui.createData.foodType.list.FoodTypeListRes
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerRes
import com.custom.app.ui.updateData.foodType.update.FoodTypeUpdateInteractor
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_create_food_type.*
import kotlinx.android.synthetic.main.activity_create_region.*
import kotlinx.android.synthetic.main.activity_device_list.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.util.*
import javax.inject.Inject

class FoodTypeUpdate : BaseActivity() {

    @Inject
    lateinit var customerInteractor: CustomerInteractor

    private lateinit var viewModel: FoodTypeCreateViewModel
    private var selectedCustomerId: Int = 0
    private lateinit var customerList: ArrayList<CustomerRes>

    var foodTypeId: Int? = null
    var testObject: FoodTypeListRes? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_food_type)

        val selectObject = intent.getStringExtra("selectObject")
        val gson = Gson()
        if (selectObject != null) {
            val type = object : TypeToken<FoodTypeListRes>() {}.type
            testObject = gson.fromJson(selectObject, type)
        }

        viewModel = ViewModelProvider(this,
                UpdateFoodTypeViewModelFactory(FoodTypeUpdateInteractor(), FoodTypeCreateInteractor(),
                        customerInteractor))[FoodTypeCreateViewModel::class.java]

        viewModel.foodTypeUpdateState.observe(::getLifecycle, ::updateUI)
        initView()

        if (userManager.customerType.equals("SERVICE_PROVIDER")) {
            customerLayout.visibility = View.VISIBLE
            viewModel.onGetCustomer()
        }
        else {
            customerLayout.visibility = View.GONE
            selectedCustomerId = userManager.customerId.toInt()
            updateView(viewModel.singleFoodType)
        }
    }

    fun initView() {
        toolbar.title = getString(R.string.update_food_type)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        btCreateFoodType.text = "Update food type"
        btCreateFoodType.setOnClickListener {
            updateFood()
        }
    }

    private fun updateUI(screenCreateState: ScreenState<FoodTypeUpdateState>?) {
        when (screenCreateState) {
            ScreenState.Loading -> progressFoodType.visibility = View.VISIBLE
            is ScreenState.Render -> processLoginState(screenCreateState.renderState)
        }
    }

    private fun processLoginState(renderCreateState: FoodTypeUpdateState) {
        progressFoodType.visibility = View.GONE
        when (renderCreateState) {
            FoodTypeUpdateState.FoodTypeNameEmpty -> {
                AlertUtil.showToast(this, "Please enter food name")
            }
            FoodTypeUpdateState.GetCustomerSuccess -> {
                customerList = viewModel.getCustomerList.value!!
                updateCustomerSpinner(customerList)
                updateView(viewModel.singleFoodType)
            }
            FoodTypeUpdateState.GetCustomerFailure -> {
                AlertUtil.showToast(this, "Unable to fetch customers")
            }
            FoodTypeUpdateState.FoodTypeUpdateFailure -> {
                AlertUtil.showToast(this, "Unable to update food type")
            }
            FoodTypeUpdateState.FoodTypeUpdateSuccess -> {
                resetForm()
                AlertUtil.showToast(this, "Food type has been updated")
            }
        }
    }

    private fun updateCustomerSpinner(customerList: ArrayList<CustomerRes>) {

        val adapter = CustomerDropdownAdapter(this, customerList)
        spCustomers_foodType.adapter = adapter
        spCustomers_foodType.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View,
                            position: Int,
                            id: Long) {

                        if (customerList.isNotEmpty()) {
                            for (i in 0 until customerList.size) {
                                if (customerList[i].customer_id == testObject!!.customer_id) {
                                    spCustomers_region.setSelection(i)
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
        et_name_foodType.setText("")
    }

    private fun updateFood() {
        viewModel.onUpdateFoodType(et_name_foodType.text.toString(), foodTypeId!!,selectedCustomerId)
    }

    private fun updateView(singleRegion: FoodTypeListRes) {

        et_name_foodType.setText(testObject!!.food_type_name)
        foodTypeId = testObject!!.food_type_id

    }
}