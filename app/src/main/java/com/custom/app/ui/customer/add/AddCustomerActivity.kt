package com.custom.app.ui.customer.add

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.base.app.ui.base.BaseActivity
import com.core.app.util.AlertUtil
import com.custom.app.CustomApp
import com.custom.app.R
import com.custom.app.ui.address.AddressInteractor
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerState
import com.custom.app.ui.user.add.AddUserActivity
import com.custom.app.util.Constants
import com.custom.app.util.Constants.REQUEST_ADD_CUSTOMER
import com.custom.app.util.MultiSelectionSpinner
import com.custom.app.util.Utils
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_add_customer.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject

class AddCustomerActivity : BaseActivity(), View.OnClickListener, AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener, MultiSelectionSpinner.OnMultipleItemsSelectedListener {

    @Inject
    lateinit var customerInteractor: CustomerInteractor

    @Inject
    lateinit var addressInteractor: AddressInteractor

    private lateinit var viewModel: CustomerViewModel
    private var addressCondition: Boolean = true
    private var bankCondition: Boolean = true
    var partnerNameArray = ArrayList<String>()
    var countryNameArray = ArrayList<String>()
    var stateNameArray = ArrayList<String>()
    var cityNameArray = ArrayList<String>()
    var commodityCategoryNameArray = ArrayList<String>()
    var cropCategoryArray = ArrayList<String>()

    var partnerClick = false

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_customer)
        initView()
    }

    private fun initView() {
        toolbar.title = getString(R.string.add_customer)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        btAddCustomer.setOnClickListener(this)
        tvAddress.setOnClickListener(this)
        tvCustomer.setOnClickListener(this)
        tvPartner.setOnClickListener(this)
        tvBankDetail.setOnClickListener(this)
        spinnerCountry.onItemSelectedListener = this
        spinnerState.onItemSelectedListener = this
        spinnerPartnerName.onItemSelectedListener = this
        spinnerCropCategory!!.setListener(this, 1, "Select Commodity Category")

        viewModel = ViewModelProvider(this, AddCustomerViewModelFactory(customerInteractor, addressInteractor))[CustomerViewModel::class.java]
        viewModel.addCustomerState.observe(::getLifecycle, ::updateUI)

        hideAddressFields()
        hideBankFields()

        viewModel.addressCountry()

        viewModel.getPartners()
        viewModel.getCategory("")
        tvCustomer.performClick()

    }

    private fun updateUI(screenState: ScreenState<CustomerState>?) {
        when (screenState) {
            is ScreenState.Render -> processLoginState(screenState.renderState)
        }
    }

    private fun processLoginState(renderState: CustomerState) {
        //  progress.visibility = View.GONE
        when (renderState) {
            CustomerState.GetPartnerSuccess -> {
                partnerNameArray.clear()
                for (i in 0 until viewModel.partnerList.size) {
                    partnerNameArray.add(viewModel.partnerList[i].name.toString())
                }
                setSpinner(spinnerPartnerName, partnerNameArray)
            }

            CustomerState.GetPartnerFailure -> {
                partnerNameArray.clear()
                partnerNameArray.add("No Country")
                setSpinner(spinnerPartnerName, partnerNameArray)
                AlertUtil.showToast(this, viewModel.errorMsg)
            }

            CustomerState.AllCategoryApiSuccess -> {
                commodityCategoryNameArray.clear()
                for (i in 0 until viewModel.categoryArray.size) {
                    commodityCategoryNameArray.add(viewModel.categoryArray[i].commodity_category_name.toString())
                }
                spinnerCropCategory!!.setItems(commodityCategoryNameArray)
                // setSpinner(spinnerCropCategory, commodityCategoryNameArray)
            }

            CustomerState.AllCategoryApiError -> {
                commodityCategoryNameArray.clear()
                commodityCategoryNameArray.add("No commodity category")
                // setSpinner(spinnerCropCategory, partnerNameArray)
                AlertUtil.showToast(this, viewModel.errorMsg)

            }

            CustomerState.CountryFailure -> {
                countryNameArray.clear()
                countryNameArray.add("No Country")
                setSpinner(spinnerCountry, countryNameArray)
                AlertUtil.showToast(this, viewModel.errorMsg)
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
                AlertUtil.showToast(this, viewModel.errorMsg)
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
                AlertUtil.showToast(this, viewModel.errorMsg)
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
                Utils.tokenExpire(this@AddCustomerActivity)
            }
            CustomerState.AddCustomerFailure -> {
                AlertUtil.showToast(this, "Error to add customer")
            }
            CustomerState.AddCustomerSuccess -> {

                val requestData = HashMap<String, Any>()
                requestData["name"] = etCustomerName.text.toString()
                requestData["email"] = etOfficialEmail.text.toString()
                requestData["contact_number"] = etContactNumber.text.toString()
                requestData["gst"] = etGst.text.toString()
                requestData["pan"] = etPan.text.toString()
                requestData["commodity_category_ids"] = cropCategoryArray
                val requestAddressData = HashMap<String, Any>()
                requestAddressData["address1"] = etAddressLine1.text.toString()
                requestAddressData["country"] = spinnerCountry.selectedItem.toString()
                requestAddressData["countryPos"] = spinnerCountry.selectedItemPosition
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
                requestBankData["ifsc"] = etBankIfsc.text.toString()
                val bankList = ArrayList<HashMap<String, Any>>()
                bankList.add(requestBankData)
                requestData["bank_details"] = bankList
                if (partnerClick) {
                    requestData["is_partner"] = true
                    requestData["partner_id"] = ""

                } else {
                    if (spinnerPartnerName.selectedItem.toString() != "Select Partner")
                        requestData["partner_id"] = viewModel.partnerList[spinnerPartnerName.selectedItemPosition - 1].customer_id.toString()
                    requestData["is_partner"] = false
                }

                val intent = Intent(this, AddUserActivity::class.java)
                intent.putExtra("flow", Constants.FLOW_ADD_CUSTOMER)

                intent.putExtra("rawData", requestData)
                intent.putExtra("rawAddress", requestAddressData)
                startActivityForResult(intent, REQUEST_ADD_CUSTOMER)
                AlertUtil.showToast(this, getString(R.string.now_add_user_detail))
            }
        }
    }

    private fun hideAddressFields() {
        if (addressCondition) {
            addressCondition = false
            til_address_line1.visibility = View.GONE
            tvCountry.visibility = View.GONE
            spCountry.visibility = View.GONE
            tvState.visibility = View.GONE
            spState.visibility = View.GONE
            tvCity.visibility = View.GONE
            spCity.visibility = View.GONE
            til_pin_code.visibility = View.GONE
            tvAddress.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(this, R.drawable.ic_add_circle_white_24dp), null)
        } else {
            addressCondition = true
            til_address_line1.visibility = View.VISIBLE
            tvCountry.visibility = View.VISIBLE
            spCountry.visibility = View.VISIBLE
            tvState.visibility = View.VISIBLE
            spState.visibility = View.VISIBLE
            tvCity.visibility = View.VISIBLE
            spCity.visibility = View.VISIBLE
            til_pin_code.visibility = View.VISIBLE

            tvAddress.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(this, R.drawable.ic_remove_white_24dp), null)
        }
    }

    private fun hideBankFields() {
        if (bankCondition) {
            bankCondition = false
            tilBankName.visibility = View.GONE
            tilBankBranch.visibility = View.GONE
            tilBankAccountNumber.visibility = View.GONE
            tilBankIfsc.visibility = View.GONE
            etBankIfsc.visibility = View.GONE
            tvBankDetail.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(this, R.drawable.ic_add_circle_white_24dp), null)

        } else {
            bankCondition = true
            tilBankName.visibility = View.VISIBLE
            tilBankBranch.visibility = View.VISIBLE
            tilBankAccountNumber.visibility = View.VISIBLE
            tilBankIfsc.visibility = View.VISIBLE
            etBankIfsc.visibility = View.VISIBLE
            tvBankDetail.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(this, R.drawable.ic_remove_white_24dp), null)
        }
    }

    private fun setSpinner(spinner: Spinner, options: ArrayList<String>) {
        val adapter = ArrayAdapter(this@AddCustomerActivity, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onClick(v: View?) {
        when (v) {
            btAddCustomer -> {
                if (etCustomerName.text.isNullOrEmpty() || etOfficialEmail.text.isNullOrEmpty() || etContactNumber.text.isNullOrEmpty() || etGst.text.isNullOrEmpty()
                        || etPan.text.isNullOrEmpty() || etAddressLine1.text.isNullOrEmpty() || etPinCode.text.isNullOrEmpty() || etBankName.text.isNullOrEmpty()
                        || etBankBranch.text.isNullOrEmpty() || etBankAccountNumber.text.isNullOrEmpty()) {
                    AlertUtil.showToast(this, getString(R.string.add_all_parameter))
                } else {

                    checkPan(etPan.text.toString(), etGst.text.toString())

                }
            }
            tvAddress -> hideAddressFields()
            tvBankDetail -> hideBankFields()
            tvCustomer -> {
                partnerClick = false
                spPartnerId.visibility = View.VISIBLE
                tvPartnerId.visibility = View.VISIBLE
                tvCustomer.setBackgroundResource(R.drawable.bg_left_curb_selected)
                tvPartner.setBackgroundResource(R.drawable.bg_right_curb_unselected)
                tvCustomer.setTextColor(ContextCompat.getColor(context(), R.color.colorWhite))
                tvPartner.setTextColor(ContextCompat.getColor(context(), R.color.black))
            }

            tvPartner -> {
                partnerClick = true
                spPartnerId.visibility = View.GONE
                tvPartnerId.visibility = View.GONE
                tvCustomer.setBackgroundResource(R.drawable.bg_left_curb_unselected)
                tvPartner.setBackgroundResource(R.drawable.bg_right_curb_selected)
                tvCustomer.setTextColor(ContextCompat.getColor(context(), R.color.black))
                tvPartner.setTextColor(ContextCompat.getColor(context(), R.color.colorWhite))
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ADD_CUSTOMER) {
            if (resultCode == Activity.RESULT_OK) {
                setResult(AppCompatActivity.RESULT_OK)
                finish()
            }
        }
    }

    override fun onCheckedChanged(box: CompoundButton?, check: Boolean) {
        if (check) {
            spPartnerId.visibility = View.GONE
            tvPartnerId.visibility = View.GONE
        } else {
            spPartnerId.visibility = View.VISIBLE
            tvPartnerId.visibility = View.VISIBLE
        }
    }

    override fun selectedStrings(strings: MutableList<String>?) {
    }

    override fun selectedIndices(indices: MutableList<Int>?, id: Int) {
        if (indices!!.size > 0) {
            if (id == 1) {
                cropCategoryArray.clear()
                for (i in 0 until indices.size) {
                    cropCategoryArray.add(viewModel.categoryArray[i].commodity_category_id.toString())
                }
            }
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is TextInputEditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    fun checkPan(s: String, gst: String) {

        val pattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}")
        val matcher = pattern.matcher(s)
        if (matcher.matches()) {
            isValidGSTNo(gst)
        } else {

        }
    }

    fun isValidGSTNo(str: String?): Boolean {

        val regex = ("^[0-9]{2}[A-Z]{5}[0-9]{4}" + "[A-Z]{1}[1-9A-Z]{1}" + "Z[0-9A-Z]{1}$")
        val p = Pattern.compile(regex)
        if (str == null) {
            return false
        }

        val m: Matcher = p.matcher(str)

        return m.matches()
    }
}