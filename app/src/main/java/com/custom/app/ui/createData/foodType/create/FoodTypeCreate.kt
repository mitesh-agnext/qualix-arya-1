package com.custom.app.ui.createData.foodType.create

import android.content.DialogInterface
import android.content.Intent
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
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerRes
import kotlinx.android.synthetic.main.activity_create_food_type.*
import kotlinx.android.synthetic.main.activity_device_list.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.util.*
import javax.inject.Inject

class FoodTypeCreate : BaseActivity() {

    @Inject
    lateinit var customerInteractor: CustomerInteractor

    private lateinit var viewModel: FoodTypeCreateViewModel
    private var selectedCustomerId: Int = 0
    private lateinit var customerList: ArrayList<CustomerRes>

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_food_type)

        viewModel = ViewModelProvider(this,
                CreateFoodTypeViewModelFactory(FoodTypeCreateInteractor(), customerInteractor))[FoodTypeCreateViewModel::class.java]

        viewModel.foodTypeCreateState.observe(::getLifecycle, ::updateUI)
        initView()

        if (userManager.customerType.equals("SERVICE_PROVIDER")) {
            customerLayout.visibility = View.VISIBLE
            viewModel.onGetCustomer()
        }
        else {
            customerLayout.visibility = View.GONE
            selectedCustomerId = userManager.customerId.toInt()
        }

    }

    fun initView() {
        toolbar.title = getString(R.string.create_food_type)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        btCreateFoodType.text = "Create food type"
        btCreateFoodType.setOnClickListener {
            addFoodType()
        }
    }

    private fun updateUI(screenCreateState: ScreenState<FoodTypeCreateState>?) {
        when (screenCreateState) {
            ScreenState.Loading -> progressFoodType.visibility = View.VISIBLE
            is ScreenState.Render -> processLoginState(screenCreateState.renderState)
        }
    }

    private fun processLoginState(renderCreateState: FoodTypeCreateState) {
        progressFoodType.visibility = View.GONE
        when (renderCreateState) {
            FoodTypeCreateState.FoodTypeNameEmpty -> {
                AlertUtil.showSnackBar(foodType_layout, "Please enter food type name", 2000)
            }
            FoodTypeCreateState.GetCustomerSuccess -> {
                customerList = viewModel.getCustomerList.value!!
                updateCustomerSpinner(customerList)
            }
            FoodTypeCreateState.GetCustomerFailure -> {
                AlertUtil.showSnackBar(foodType_layout, "Unable to fetch customers", 2000)
            }
            FoodTypeCreateState.FoodTypeCreateFailure -> {
                AlertUtil.showSnackBar(foodType_layout, "Unable to create food type", 2000)
            }
            FoodTypeCreateState.FoodTypeCreateSuccess -> {
                resetForm()
                AlertUtil.showActionAlertDialog(this, "", "New food type has been created !",
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

        val adapter = CustomerDropdownAdapter(this, customerList)
        spCustomers_foodType.adapter = adapter
        spCustomers_foodType.onItemSelectedListener =
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
        et_name_foodType.setText("")
    }

    private fun addFoodType() {
        viewModel.onCreateFoodType(et_name_foodType.text.toString(), selectedCustomerId)
    }

}