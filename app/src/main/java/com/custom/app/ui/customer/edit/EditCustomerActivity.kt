package com.custom.app.ui.customer.edit

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.lifecycle.ViewModelProvider
import com.base.app.ui.base.BaseActivity
import com.core.app.util.AlertUtil
import com.custom.app.CustomApp
import com.custom.app.R
import com.custom.app.ui.address.AddressInteractor
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerRes
import com.custom.app.ui.customer.list.CustomerState
import com.custom.app.util.Utils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_edit_customer.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import javax.inject.Inject

class EditCustomerActivity : BaseActivity(), View.OnClickListener, AdapterView.OnItemSelectedListener {

    @Inject
    lateinit var customerInteractor: CustomerInteractor

    @Inject
    lateinit var addressInteractor: AddressInteractor

    lateinit var viewModel: EditCustomerVM
    lateinit var customerUUID: String
    lateinit var customerId: String
    var countryNameArray = ArrayList<String>()
    var stateNameArray = ArrayList<String>()
    var cityNameArray = ArrayList<String>()
    var testObject: CustomerRes? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_customer)
        initView()
    }

    private fun initView() {

        toolbar.title = getString(R.string.update_customer)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        btUpdateCustomer.setOnClickListener(this)
        spinnerCountry.onItemSelectedListener = this
        spinnerState.onItemSelectedListener = this

        //VM
        viewModel = ViewModelProvider(this,
                EditCustomerVMFactory(customerInteractor, addressInteractor))[EditCustomerVM::class.java]
        viewModel.customerStates.observe(::getLifecycle, ::updateUI)

        //spinner data
        viewModel.addressCountry()
        //Set default data in form
        val selectObject = intent.getStringExtra("selectObject")
        val gson = Gson()
        if (selectObject != null) {
            val type = object : TypeToken<CustomerRes>() {}.type
            testObject = gson.fromJson(selectObject, type)
            setDummyData(testObject!!)
        }
    }

    private fun updateUI(screenState: ScreenState<CustomerState>?) {
        when (screenState) {
            //  ScreenState.Loading -> progress.visibility = View.VISIBLE
            is ScreenState.Render -> processLoginState(screenState.renderState)
        }
    }

    private fun processLoginState(renderState: CustomerState) {
        // progress.visibility = View.GONE

        when (renderState) {
            CustomerState.EditCustomerFailure -> {
                AlertUtil.showToast(this, viewModel.errorMassage)
            }
            CustomerState.EditCustomerSuccess -> {
                AlertUtil.showToast(this, getString(R.string.updated_successfully))
                setResult(RESULT_OK);
                finish()
            }
            CustomerState.CountryFailure -> {
                countryNameArray.clear()
                countryNameArray.add("No Country")
                setSpinner(spinnerCountry, countryNameArray)
                AlertUtil.showToast(this, viewModel.errorMassage)
            }
            CustomerState.CountrySuccess -> {
                countryNameArray.clear()
                for (i in 0 until viewModel.countryArray.size) {
                    countryNameArray.add(viewModel.countryArray[i].country_name.toString())
                }
                setSpinner(spinnerCountry, countryNameArray)

            }
            CustomerState.StateFailure -> {
                stateNameArray.clear()
                stateNameArray.add("No State")
                setSpinner(spinnerState, stateNameArray)
                AlertUtil.showToast(this, viewModel.errorMassage)
            }
            CustomerState.StateSuccess -> {
                stateNameArray.clear()
                for (i in 0 until viewModel.stateArray.size) {
                    stateNameArray.add(viewModel.stateArray[i].state_name.toString())
                }
                setSpinner(spinnerState, stateNameArray)

            }
            CustomerState.CityFailure -> {
                cityNameArray.clear()
                cityNameArray.add("No city")
                setSpinner(spinnerCity, cityNameArray)
                AlertUtil.showToast(this, viewModel.errorMassage)
            }
            CustomerState.CitySuccess -> {
                cityNameArray.clear()
                for (i in 0 until viewModel.cityArray.size) {
                    cityNameArray.add(viewModel.cityArray[i].city_name.toString())
                }
                setSpinner(spinnerCity, cityNameArray)

            }
            CustomerState.TokenExpire -> {
                AlertUtil.showToast(this, getString(R.string.token_expire))
                Utils.tokenExpire(this@EditCustomerActivity)
            }
        }
    }

    private fun setDummyData(testObject: CustomerRes) {
        etCustomerName.setText(testObject.name)
        etOfficialEmail.setText(testObject.email)
        etContactNumber.setText(testObject.contact_number)
        etGst.setText(testObject.gst)
        etPan.setText(testObject.pan)
        customerUUID = testObject.customer_uuid.toString()
        customerId = testObject.customer_id.toString()
        if (testObject.bankDetails != null)
            if (testObject.bankDetails!!.size > 0) {
                etBankName.setText(testObject.bankDetails!![0].bank_name)
                etBankBranch.setText(testObject.bankDetails!![0].bank_address)
                etBankAccountNumber.setText(testObject.bankDetails!![0].bank_account_number)
                etBankBranch.setText(testObject.bankDetails!![0].branch)
            }
        if (testObject.address != null)
            if (testObject.address!!.size > 0) {
                etAddressLine1.setText(testObject.address!![0].address1)
                etPinCode.setText(testObject.address!![0].pincode)
                Handler().postDelayed({
                    setAddressSpinner()
                }, 2000)
            }
    }

    private fun setAddressSpinner() {
        spinnerCountry.setSelection(countryNameArray.indexOf(testObject!!.address!![0].country))
        spinnerState.setSelection(stateNameArray.indexOf(testObject!!.address!![0].state))
        spinnerCity.setSelection(cityNameArray.indexOf(testObject!!.address!![0].city))
    }

    private fun setSpinner(spinner: Spinner, options: ArrayList<String>) {
        val adapter = ArrayAdapter(this@EditCustomerActivity, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    override fun onClick(v: View?) {
        when (v) {
            btUpdateCustomer -> {
                if (etCustomerName.text.isNullOrEmpty() && etOfficialEmail.text.isNullOrEmpty() && etContactNumber.text.isNullOrEmpty() && etGst.text.isNullOrEmpty()
                        && etPan.text.isNullOrEmpty() && etAddressLine1.text.isNullOrEmpty() && etPinCode.text.isNullOrEmpty() && etBankName.text.isNullOrEmpty()
                        && etBankBranch.text.isNullOrEmpty() && etBankAccountNumber.text.isNullOrEmpty()) {
                    AlertUtil.showToast(this, getString(R.string.add_all_parameter))
                } else {
                    val requestData = HashMap<String, Any>()
                    requestData["customer_uid"] = customerUUID
                    requestData["customer_id"] = customerId
                    requestData["name"] = etCustomerName.text.toString()
                    requestData["email"] = etOfficialEmail.text.toString()
                    requestData["contact_number"] = etContactNumber.text.toString()
                    requestData["gst"] = etGst.text.toString()
                    requestData["pan"] = etPan.text.toString()

                    val requestAddressData = HashMap<String, Any>()
                    requestAddressData["address1"] = etAddressLine1.text.toString()
                    requestAddressData["country"] = spinnerCountry.selectedItem.toString()
                    requestAddressData["state"] = spinnerState.selectedItem.toString()
                    requestAddressData["city"] = spinnerCity.selectedItem.toString()
                    requestAddressData["pincode"] = etPinCode.text.toString()
                    val addressList = ArrayList<HashMap<String, Any>>()
                    addressList.add(requestAddressData)
                    requestData["address"] = addressList

                    val requestBankData = HashMap<String, Any>()
                    requestBankData["bank_name"] = etBankName.text.toString()
                    requestBankData["branch"] = etBankBranch.text.toString()
                    requestBankData["bank_account_number"] = etBankAccountNumber.text.toString()
                    requestBankData["ifsc"] = etBankIFSC.text.toString()
                    val bankList = ArrayList<HashMap<String, Any>>()
                    bankList.add(requestBankData)
                    requestData["bank_details"] = bankList
                    viewModel.updateCustomerVm(customerId, requestData)
                }
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    override fun onItemSelected(spinner: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
        when (spinner) {
            spinnerCountry -> {
                if (viewModel.countryArray.size > 0)
                    viewModel.addressState(viewModel.countryArray[pos].country_id.toString())
            }
            spinnerState -> {
                if (viewModel.stateArray.size > 0)
                    viewModel.addressCity(viewModel.stateArray[pos].state_id.toString())
            }
        }
    }
}