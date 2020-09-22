package com.custom.app.ui.createData.deviationProfile.update

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.core.app.util.AlertUtil
import com.custom.app.CustomApp
import com.custom.app.R
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.createData.adapters.CustomerDropdownAdapter
import com.custom.app.ui.createData.adapters.ProfileDropdownAdapter
import com.custom.app.ui.createData.profile.list.ProfileListInteractor
import com.custom.app.ui.createData.profile.list.ProfileListRes
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerRes
import kotlinx.android.synthetic.main.activity_create_food_type.*
import kotlinx.android.synthetic.main.activity_deviation_profile.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.util.*
import javax.inject.Inject

class UpdateDeviationProfile : AppCompatActivity() {

    @Inject
    lateinit var customerInteractor: CustomerInteractor

    private lateinit var viewModel: CreateDeviationViewModel
    private var selectedCustomerId: Int = 0
    private lateinit var customerList: ArrayList<CustomerRes>
    private var selectedProfileId: Int = 0
    private lateinit var profileList: ArrayList<ProfileListRes>

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deviation_profile)

        viewModel = ViewModelProvider(this,
                CreateDeviationViewModelFactory(UpdateDeviationInteractor(), customerInteractor,
                        ProfileListInteractor()))[CreateDeviationViewModel::class.java]

        viewModel.createDeviationState.observe(::getLifecycle, ::updateUI)
        initView()

        viewModel.onGetCustomer()
    }

    fun initView() {
        toolbar.title = getString(R.string.deviations)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        btSubmitDeviation.text = "Create deviation rule"
        btSubmitDeviation.setOnClickListener {
            addDeviation()
        }
    }

    private fun updateUI(screenCreateState: ScreenState<UpdateDeviationState>?) {
        when (screenCreateState) {
            ScreenState.Loading -> progressDeviation.visibility = View.VISIBLE
            is ScreenState.Render -> processLoginState(screenCreateState.renderState)
        }
    }

    private fun processLoginState(renderCreateState: UpdateDeviationState) {
        progressFoodType.visibility = View.GONE
        when (renderCreateState) {
            UpdateDeviationState.GetCustomerError -> {
                AlertUtil.showToast(this, "Unable to fetch customers")
            }
            UpdateDeviationState.GetCustomerSuccess -> {
                customerList = viewModel.getCustomerList.value!!
                updateCustomerSpinner(customerList)
            }
            UpdateDeviationState.GetProfileSuccess -> {
                profileList = viewModel.getProfileResponse.value!!
                updateProfileSpinner(profileList)
            }
            UpdateDeviationState.GetProfileError -> {
                AlertUtil.showToast(this, "Unable to fetch profile")
            }
            UpdateDeviationState.GetCreateDeviationSuccess -> {
                resetForm()
                AlertUtil.showToast(this, "New deviation rule has been created")
            }
            UpdateDeviationState.GetCreateDeviationError -> {
                AlertUtil.showToast(this, "Unable to create rule")
            }
        }
    }

    private fun updateCustomerSpinner(customerList: ArrayList<CustomerRes>) {

        val adapter = CustomerDropdownAdapter(this, customerList)
        spCustomers_foodType.adapter = adapter
        spCustomers_foodType.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                        selectedCustomerId = customerList[position].customer_id!!.toInt()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Another interface callback
                    }
                }
    }

    private fun updateProfileSpinner(profileList: ArrayList<ProfileListRes>) {

        val adapter = ProfileDropdownAdapter(this, profileList)
        spCustomers_foodType.adapter = adapter
        spCustomers_foodType.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

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

    private fun addDeviation() {
//        viewModel.onCreateDeviation(blMin.text.toString(), selectedCustomerId, blMax.text.toString(),)
    }
}