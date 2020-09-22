package com.custom.app.ui.createData.profileType.create

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
import kotlinx.android.synthetic.main.activity_create_intl_centers.progressAddDevices
import kotlinx.android.synthetic.main.activity_create_profile_type.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.util.*
import javax.inject.Inject

class ProfileTypeCreate : BaseActivity() {

    @Inject
    lateinit var customerInteractor: CustomerInteractor

    private lateinit var viewModel: ProfileTypeCreateViewModel
    private var selectedCustomerId: Int = 0
    private lateinit var customerList: ArrayList<CustomerRes>

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile_type)

        viewModel = ViewModelProvider(this,
                CreateProfileTypeViewModelFactory(ProfileTypeCreateInteractor(),
                        customerInteractor))[ProfileTypeCreateViewModel::class.java]

        viewModel.profileTypeCreateState.observe(::getLifecycle, ::updateUI)
        initView()

        if (userManager.customerType.equals("SERVICE_PROVIDER")) {
            titleCustomers_profileType.visibility = View.VISIBLE
            spLayoutCustomer_profileType.visibility = View.VISIBLE
            viewModel.onGetCustomer()
        }
        else {
            titleCustomers_profileType.visibility = View.GONE
            spLayoutCustomer_profileType.visibility = View.GONE
            selectedCustomerId = userManager.customerId.toInt()
        }

    }

    fun initView() {
        toolbar.title = getString(R.string.create_profile_type)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        btCreateProfileType.text = "Create Profile Type"
        btCreateProfileType.setOnClickListener {
            addProfileType()
        }
    }

    private fun updateUI(screenCreateState: ScreenState<ProfileTypeCreateState>?) {
        when (screenCreateState) {
            ScreenState.Loading -> progressAddDevices.visibility = View.VISIBLE
            is ScreenState.Render -> processLoginState(screenCreateState.renderState)
        }
    }

    private fun processLoginState(renderCreateState: ProfileTypeCreateState) {
        progressAddDevices.visibility = View.GONE
        when (renderCreateState) {
            ProfileTypeCreateState.ProfileTypeNameEmpty -> {
                AlertUtil.showToast(this, "Please enter profile type name")
            }
            ProfileTypeCreateState.GetCustomerSuccess -> {
                customerList = viewModel.getCustomerList.value!!
                updateCustomerSpinner(customerList)
            }
            ProfileTypeCreateState.GetCustomerFailure -> {
                AlertUtil.showToast(this, "Unable to fetch customers")
            }
            ProfileTypeCreateState.ProfileTypeCreateFailure -> {
                AlertUtil.showToast(this, "Unable to create profile type")
            }
            ProfileTypeCreateState.ProfileTypeCreateSuccess -> {
                resetForm()
                AlertUtil.showToast(this, "New profile type has been created")
            }
        }
    }

    private fun updateCustomerSpinner(customerList: ArrayList<CustomerRes>) {

        val adapter = CustomerDropdownAdapter(this, customerList)
        spCustomers_profileType.adapter = adapter
        spCustomers_profileType.onItemSelectedListener =
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
        et_name_profileType.setText("")
    }

    private fun addProfileType() {
        viewModel.onCreateProfileType(et_name_profileType.text.toString(), selectedCustomerId)
    }

}