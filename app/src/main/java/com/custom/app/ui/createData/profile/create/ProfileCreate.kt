package com.custom.app.ui.createData.profile.create

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
import kotlinx.android.synthetic.main.activity_create_intl_centers.progressAddDevices
import kotlinx.android.synthetic.main.activity_create_profile.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.util.*
import javax.inject.Inject

class ProfileCreate : BaseActivity() {

    @Inject
    lateinit var customerInteractor: CustomerInteractor

    private lateinit var viewModel: ProfileCreateViewModel
    private var selectedCustomerId: Int = 0
    private lateinit var customerList: ArrayList<CustomerRes>

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)

        viewModel = ViewModelProvider(this,
                CreateProfileViewModelFactory(ProfileCreateInteractor(),
                        customerInteractor))[ProfileCreateViewModel::class.java]

        viewModel.profileCreateState.observe(::getLifecycle, ::updateUI)
        initView()

        if (userManager.customerType.equals("SERVICE_PROVIDER")) {
            titleCustomers_profile.visibility = View.VISIBLE
            spLayoutCustomer_profile.visibility = View.VISIBLE
            viewModel.onGetCustomer()
        }
        else {
            titleCustomers_profile.visibility = View.GONE
            spLayoutCustomer_profile.visibility = View.GONE
            selectedCustomerId = userManager.customerId.toInt()

        }
    }

    fun initView() {
        toolbar.title = getString(R.string.create_profile)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        btCreateProfile.text = "Create Profile"
        btCreateProfile.setOnClickListener {
            addProfile()
        }
    }

    private fun updateUI(screenCreateState: ScreenState<ProfileCreateState>?) {
        when (screenCreateState) {
            ScreenState.Loading -> progressAddDevices.visibility = View.VISIBLE
            is ScreenState.Render -> processLoginState(screenCreateState.renderState)
        }
    }

    private fun processLoginState(renderCreateState: ProfileCreateState) {
        progressAddDevices.visibility = View.GONE
        when (renderCreateState) {
            ProfileCreateState.ProfileNameEmpty -> {
                AlertUtil.showSnackBar(profile_layout, "Please enter profile name", 2000)
            }
            ProfileCreateState.GetCustomerSuccess -> {
                customerList = viewModel.getCustomerList.value!!
                updateCustomerSpinner(customerList)
            }
            ProfileCreateState.GetCustomerFailure -> {
                AlertUtil.showSnackBar(profile_layout, "Unable to fetch customers", 2000)
            }
            ProfileCreateState.ProfileCreateFailure -> {
                AlertUtil.showSnackBar(profile_layout, "Unable to create profile", 2000)
            }
            ProfileCreateState.ProfileCreateSuccess -> {
                resetForm()
                AlertUtil.showActionAlertDialog(this, "", "New profile has been created !",
                        false, getString(R.string.btn_ok)) { dialog: DialogInterface?, which: Int ->
                    val intent = Intent()
                    intent.putExtra("result", "success")
                    setResult(RESULT_OK, intent);
                    finish()
                }
            }
            ProfileCreateState.MinTempEmpty -> {
                AlertUtil.showSnackBar(profile_layout, "Please enter minimum temp.", 2000)
            }
            ProfileCreateState.MaxTempEmpty -> {
                AlertUtil.showSnackBar(profile_layout, "Please enter maximum temp.", 2000)
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

    private fun addProfile() {
        viewModel.onCreateProfile(et_ProfileName_profile.text.toString(), selectedCustomerId, et_profile_maxTemp.text.toString(),
                et_profile_minTemp.text.toString())
    }
}