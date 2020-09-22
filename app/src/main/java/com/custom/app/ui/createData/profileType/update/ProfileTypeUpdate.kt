package com.custom.app.ui.createData.profileType.update

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
import com.custom.app.ui.createData.profileType.create.ProfileTypeCreateInteractor
import com.custom.app.ui.createData.profileType.list.ProfileTypeListRes
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerRes
import com.custom.app.ui.updateData.profileType.update.ProfileTypeUpdateInteractor
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_create_intl_centers.progressAddDevices
import kotlinx.android.synthetic.main.activity_create_profile_type.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.util.*
import javax.inject.Inject

class ProfileTypeUpdate : BaseActivity() {

    @Inject
    lateinit var customerInteractor: CustomerInteractor

    private lateinit var viewModel: ProfileTypeCreateViewModel
    private var selectedCustomerId: Int = 0
    private lateinit var customerList: ArrayList<CustomerRes>

    var profileTypeId: Int? = null
    var testObject: ProfileTypeListRes? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile_type)

        val selectObject = intent.getStringExtra("selectObject")
        val gson = Gson()
        if (selectObject != null) {
            val type = object : TypeToken<ProfileTypeListRes>() {}.type
            testObject = gson.fromJson(selectObject, type)
        }

        viewModel = ViewModelProvider(this,
                UpdateProfileTypeViewModelFactory(ProfileTypeUpdateInteractor(),
                        ProfileTypeCreateInteractor(), customerInteractor))[ProfileTypeCreateViewModel::class.java]

        viewModel.profileTypeUpdateState.observe(::getLifecycle, ::updateUI)
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
            updateView(viewModel.singleProfileType)
        }

    }

    fun initView() {
        toolbar.title = getString(R.string.update_profile_type)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        btCreateProfileType.text = "Update Profile Type"
        btCreateProfileType.setOnClickListener {
            updateProfile()
        }
    }

    private fun updateUI(screenCreateState: ScreenState<ProfileTypeUpdateState>?) {
        when (screenCreateState) {
            ScreenState.Loading -> progressAddDevices.visibility = View.VISIBLE
            is ScreenState.Render -> processLoginState(screenCreateState.renderState)
        }
    }

    private fun processLoginState(renderCreateState: ProfileTypeUpdateState) {
        progressAddDevices.visibility = View.GONE
        when (renderCreateState) {
            ProfileTypeUpdateState.ProfileTypeNameEmpty -> {
                AlertUtil.showToast(this, "Please enter profile name")
            }
            ProfileTypeUpdateState.GetCustomerSuccess -> {
                customerList = viewModel.getCustomerList.value!!
                updateCustomerSpinner(customerList)

                updateView(viewModel.singleProfileType)
            }
            ProfileTypeUpdateState.GetCustomerFailure -> {
                AlertUtil.showToast(this, "Unable to fetch customers")
            }
            ProfileTypeUpdateState.ProfileTypeUpdateFailure -> {
                AlertUtil.showToast(this, "Unable to update profile type")
            }
            ProfileTypeUpdateState.ProfileTypeUpdateSuccess -> {
                resetForm()
                AlertUtil.showToast(this, "Profile type has been updated")
            }
        }
    }

    private fun updateCustomerSpinner(customerList: ArrayList<CustomerRes>) {

        val adapter = CustomerDropdownAdapter(this, customerList)
        spCustomers_profileType.adapter = adapter
        spCustomers_profileType.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                        if (customerList.isNotEmpty()) {
                            for (i in 0 until customerList.size) {
                                if (customerList[i].customer_id == testObject!!.customer_id) {
                                    spCustomers_profileType.setSelection(i)
                                }
                            }
                        }
                        selectedCustomerId = customerList[position].customer_id!!.toInt()
                    }
                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }
    }

    private fun resetForm() {
        et_name_profileType.setText("")
    }

    private fun updateProfile() {
        viewModel.onUpdateProfileType(et_name_profileType.text.toString(), profileTypeId!!,selectedCustomerId!!)
    }

    private fun updateView(singleProfileType: ProfileTypeListRes) {

        et_name_profileType.setText(testObject!!.profile_type_name)
        profileTypeId = testObject!!.profile_type_id

    }
}