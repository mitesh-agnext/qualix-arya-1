package com.custom.app.ui.createData.profile.update

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
import com.custom.app.ui.createData.profile.create.ProfileCreateInteractor
import com.custom.app.ui.createData.profile.list.ProfileListRes
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerRes
import com.custom.app.ui.updateData.profile.update.ProfileUpdateInteractor
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_create_intl_centers.progressAddDevices
import kotlinx.android.synthetic.main.activity_create_profile.*
import kotlinx.android.synthetic.main.activity_create_region.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.util.*
import javax.inject.Inject

class ProfileUpdate : BaseActivity() {

    @Inject
    lateinit var customerInteractor: CustomerInteractor

    private lateinit var viewModel: ProfileUpdateViewModel
    private var selectedCustomerId: Int = 0
    private lateinit var customerList: ArrayList<CustomerRes>

    var profileId: Int? = null
    var testObject: ProfileListRes? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)

        val selectObject = intent.getStringExtra("selectObject")
        val gson = Gson()
        if (selectObject != null) {
            val type = object : TypeToken<ProfileListRes>() {}.type
            testObject = gson.fromJson(selectObject, type)
        }

        viewModel = ViewModelProvider(this,
                UpdateProfileViewModelFactory(ProfileUpdateInteractor(), ProfileCreateInteractor(),
                        customerInteractor))[ProfileUpdateViewModel::class.java]

        viewModel.profileUpdateState.observe(::getLifecycle, ::updateUI)
        initView()

        if (userManager.customerType.equals("SERVICE_PROVIDER")) {
            titleCustomers_profile.visibility = View.VISIBLE
            spLayoutCustomer_profile.visibility = View.VISIBLE
            viewModel.onGetCustomer()
        } else {
            titleCustomers_profile.visibility = View.GONE
            spLayoutCustomer_profile.visibility = View.GONE
            selectedCustomerId = userManager.customerId.toInt()
            updateView(viewModel.singleProfile)

        }
    }

    fun initView() {
        toolbar.title = getString(R.string.update_profile)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        btCreateProfile.text = "Update Profile"
        btCreateProfile.setOnClickListener {
            updateProfile()
        }
    }

    private fun updateUI(screenCreateState: ScreenState<ProfileUpdateState>?) {
        when (screenCreateState) {
            ScreenState.Loading -> progressAddDevices.visibility = View.VISIBLE
            is ScreenState.Render -> processLoginState(screenCreateState.renderState)
        }
    }

    private fun processLoginState(renderCreateState: ProfileUpdateState) {
        progressAddDevices.visibility = View.GONE
        when (renderCreateState) {
            ProfileUpdateState.ProfileNameEmpty -> {
                AlertUtil.showToast(this, "Please enter profile name")
            }
            ProfileUpdateState.GetCustomerSuccess -> {
                customerList = viewModel.getCustomerList.value!!
                updateCustomerSpinner(customerList)

                updateView(viewModel.singleProfile)
            }
            ProfileUpdateState.GetCustomerFailure -> {
                AlertUtil.showToast(this, "Unable to fetch customers")
            }
            ProfileUpdateState.ProfileUpdateFailure -> {
                AlertUtil.showToast(this, "Unable to update profile")
            }
            ProfileUpdateState.ProfileUpdateSuccess -> {
                resetForm()
                AlertUtil.showToast(this, "Profile has been updated")
            }
            ProfileUpdateState.MinTempEmpty -> {
                AlertUtil.showToast(this, "Please enter minimum temp.")
            }
            ProfileUpdateState.MaxTempEmpty -> {
                AlertUtil.showToast(this, "Please enter maximum temp.")
            }
        }
    }

    private fun updateCustomerSpinner(customerList: ArrayList<CustomerRes>) {

        val adapter = CustomerDropdownAdapter(this, customerList)
        spCustomers_profile.adapter = adapter
        spCustomers_profile.onItemSelectedListener =
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
        et_ProfileName_profile.setText("")
        et_profile_maxTemp.setText("")
        et_profile_minTemp.setText("")
    }

    private fun updateProfile() {
        viewModel.onUpdateProfile(et_ProfileName_profile.text.toString(), profileId!!, selectedCustomerId,
                et_profile_maxTemp.text.toString(), et_profile_minTemp.text.toString())
    }

    private fun updateView(singleRegion: ProfileListRes) {

        et_ProfileName_profile.setText(testObject!!.profile_name)
        et_profile_maxTemp.setText(testObject!!.max_temp)
        et_profile_minTemp.setText(testObject!!.min_temp)

        profileId = testObject!!.profile_id

    }
}