package com.custom.app.ui.user.add

import android.content.Context
import android.content.DialogInterface
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
import androidx.lifecycle.ViewModelProvider
import com.base.app.ui.base.BaseActivity
import com.core.app.util.AlertUtil
import com.custom.app.CustomApp
import com.custom.app.R
import com.custom.app.ui.address.AddressInteractor
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.user.list.UserInteractor
import com.custom.app.util.Constants.FLOW_ADD_CUSTOMER
import com.custom.app.util.Constants.FLOW_USER_LIST
import com.custom.app.util.MultiSelectionSpinner
import com.custom.app.util.Utils
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_add_user.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import javax.inject.Inject

class AddUserActivity : BaseActivity(), View.OnClickListener, AdapterView.OnItemSelectedListener,
        CompoundButton.OnCheckedChangeListener, MultiSelectionSpinner.OnMultipleItemsSelectedListener {

    @Inject
    lateinit var userInteractor: UserInteractor

    @Inject
    lateinit var addressInteractor: AddressInteractor

    private lateinit var viewModel: AddUserVM
    private lateinit var rawData: HashMap<String, Any>
    private lateinit var rawAddress: HashMap<String, Any>

    private val roleArray = ArrayList<String>()
    private val roleCodeArray = ArrayList<String>()
    var selectedRole = ArrayList<String>()
    var countryNameArray = ArrayList<String>()
    var stateNameArray = ArrayList<String>()
    var cityNameArray = ArrayList<String>()
    var checkCountry = 0
    var checkState = 0
    var checkCity = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user)
        initView()
    }

    private fun initView() {
        toolbar.title = getString(R.string.add_user)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        btAddCustomer.setOnClickListener(this)
        spinnerCountry.onItemSelectedListener = this
        spinnerState.onItemSelectedListener = this
        spinnerCity.onItemSelectedListener = this
        chAddress.setOnCheckedChangeListener(this)
        spRole.setListener(this, 1, "Select Roles")
        viewModel = ViewModelProvider(this,
                AddUserViewModelFactory(userInteractor, addressInteractor))[AddUserVM::class.java]
        viewModel.addUserState.observe(::getLifecycle, ::updateUI)

        if (intent.getSerializableExtra("flow") == FLOW_ADD_CUSTOMER) {
            tvRoles.visibility=View.GONE
            tilRole.visibility=View.GONE
            rawData = intent.getSerializableExtra("rawData") as HashMap<String, Any>
            rawAddress = intent.getSerializableExtra("rawAddress") as HashMap<String, Any>
        } else if (intent.getSerializableExtra("flow") == FLOW_USER_LIST) {
            chAddress.visibility = View.GONE
            btAddCustomer.text = getString(R.string.add_user)
            tvRoles.visibility=View.VISIBLE
            tilRole.visibility=View.VISIBLE
        }
        //Setting spinners
        viewModel.userRole()
        viewModel.addressCountry()
    }

    private fun updateUI(screenState: ScreenState<AddUserState>?) {
        when (screenState) {
            is ScreenState.Render -> processLoginState(screenState.renderState)
        }
    }

    private fun processLoginState(renderState: AddUserState) {
        when (renderState) {
            AddUserState.CountryFailure -> {
                countryNameArray.clear()
                countryNameArray.add("No Country")
                setSpinner(spinnerCountry, countryNameArray)
                AlertUtil.showSnackBar(addUser_layout, viewModel.errorMsg, 2000)
            }
            AddUserState.CountrySuccess -> {
                countryNameArray.clear()
                for (i in 0 until viewModel.countryArray.size) {
                    countryNameArray.add(viewModel.countryArray[i].country_name.toString())
                }
                setSpinner(spinnerCountry, countryNameArray)
            }
            AddUserState.StateFailure -> {
                stateNameArray.clear()
                stateNameArray.add("No State")
                setSpinner(spinnerState, stateNameArray)
                AlertUtil.showSnackBar(addUser_layout, viewModel.errorMsg, 2000)
            }
            AddUserState.StateSuccess -> {
                stateNameArray.clear()
                for (i in 0 until viewModel.stateArray.size) {
                    stateNameArray.add(viewModel.stateArray[i].state_name.toString())
                }
                setSpinner(spinnerState, stateNameArray)
            }
            AddUserState.CityFailure -> {
                cityNameArray.clear()
                cityNameArray.add("No city")
                setSpinner(spinnerCity, cityNameArray)
                AlertUtil.showSnackBar(addUser_layout, viewModel.errorMsg, 2000)
            }
            AddUserState.CitySuccess -> {
                cityNameArray.clear()
                for (i in 0 until viewModel.cityArray.size) {
                    cityNameArray.add(viewModel.cityArray[i].city_name.toString())
                }
                setSpinner(spinnerCity, cityNameArray)
            }
            AddUserState.UserRoleFailure -> {
                AlertUtil.showSnackBar(addUser_layout, viewModel.errorMsg, 2000)
            }
            AddUserState.UserRoleSuccess -> {
                for (i in 0 until viewModel.userRole.value!!.size) {
                    roleArray.add(viewModel.userRole.value!![i].role_desc.toString())
                    roleCodeArray.add(viewModel.userRole.value!![i].role_code.toString())
                }
                spRole!!.setItems(roleArray)
//                val adapter = ArrayAdapter(this@AddUserActivity, android.R.layout.simple_spinner_item, roleArray)
//                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                spRole.adapter = adapter
            }
            AddUserState.AddUserFailure -> {
                AlertUtil.showSnackBar(addUser_layout, viewModel.errorMsg, 2000)
            }
            AddUserState.AddUserSuccess -> {
                AlertUtil.showActionAlertDialog(this, "", "Customer and User successfully added !",
                        false, getString(R.string.btn_ok)) { dialog: DialogInterface?, which: Int ->
                    val intent = Intent()
                    intent.putExtra("result", "success")
                    setResult(RESULT_OK, intent);
                    finish()
                }
            }
            AddUserState.AddUserSingleFailure -> {
                AlertUtil.showSnackBar(addUser_layout, viewModel.errorMsg, 2000)
            }
            AddUserState.AddUserSingleSuccess -> {
                AlertUtil.showActionAlertDialog(this, "", "User added successfully !",
                        false, getString(R.string.btn_ok)) { dialog: DialogInterface?, which: Int ->
                    val intent = Intent()
                    intent.putExtra("result", "success")
                    setResult(RESULT_OK, intent);
                    finish()
                }
            }
            AddUserState.TokenExpire -> {
                AlertUtil.showSnackBar(addUser_layout, getString(R.string.token_expire), 2000)
                Utils.tokenExpire(this@AddUserActivity)
            }
        }
    }

    private fun setSpinner(spinner: Spinner, options: ArrayList<String>) {
        val adapter = ArrayAdapter(this@AddUserActivity, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    override fun onClick(view: View?) {
        when (view) {
            btAddCustomer -> {
                if (etFirstName.text.isNullOrEmpty() && etLastName.text.isNullOrEmpty() && etContactNumber.text.isNullOrEmpty()
                        && etEmail.text.isNullOrEmpty() && etAddressLine1.text.isNullOrEmpty()
                        && etPinCode.text.isNullOrEmpty()) {
                    AlertUtil.showToast(this, getString(R.string.add_all_parameter))
                } else {
                    // rawData
                    val requestUserData = HashMap<String, Any>()
                    requestUserData["first_name"] = etFirstName.text.toString()
                    requestUserData["last_name"] = etLastName.text.toString()
                    requestUserData["email"] = etEmail.text.toString()
                    requestUserData["contact_number"] = etContactNumber.text.toString()
                    requestUserData["is_2fa_required"] = 1

                    requestUserData["user_hierarchy"] = spUserType.selectedItem.toString()

                    val requestAddressData = HashMap<String, Any>()
                    requestAddressData["address1"] = etAddressLine1.text.toString()
                    requestAddressData["country"] = spinnerCountry.selectedItem.toString()
                    requestAddressData["state"] = spinnerState.selectedItem.toString()
                    requestAddressData["city"] = spinnerCity.selectedItem.toString()
                    requestAddressData["pincode"] = etPinCode.text.toString()

                    val addressList = ArrayList<HashMap<String, Any>>()
                    addressList.add(requestAddressData)
                    requestUserData["address"] = addressList

                    if (intent.getSerializableExtra("flow") == FLOW_ADD_CUSTOMER) {
                        selectedRole.add("admin")
                        requestUserData["roles"] = selectedRole
                        rawData["user"] = requestUserData
                        viewModel.addUserVM(rawData)
                    } else if (intent.getSerializableExtra("flow") == FLOW_USER_LIST) {
                        roleArray.add(viewModel.userRole.value!![spRole.selectedItemPosition].role_code.toString())
                        requestUserData["roles"] = selectedRole
                        viewModel.addUserSingleVM(requestUserData)
                    }
                }
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    override fun onItemSelected(spinner: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {

            if (chAddress.isChecked) {
                when (spinner) {
                    spinnerCountry -> {

                        if (++checkCountry > 1) {
                            if (viewModel.countryArray.size > 0)
                                viewModel.addressState(viewModel.countryArray[pos].country_id.toString())
                        }
                        //initialization
                        else {
                            if (viewModel.countryArray.size > 0) {
                                spinnerCountry.setSelection(countryNameArray.indexOf(rawAddress["country"].toString()))
                                viewModel.addressState(viewModel.countryArray[pos].country_id.toString())
                            }
                        }
                    }
                    spinnerState -> {
                        if (++checkState > 2) {
                            if (viewModel.stateArray.size > 0)
                                viewModel.addressCity(viewModel.stateArray[pos].state_id.toString())
                        }
                        //initialization
                        else {
                            if (viewModel.stateArray.size > 0) {
                                spinnerState.setSelection(stateNameArray.indexOf(rawAddress["state"].toString()))
                                viewModel.addressCity(viewModel.stateArray[pos].state_id.toString())
                            }
                        }
                    }
                    spinnerCity -> {
                        if (++checkCity > 2) {

                        }
                        //initialization
                        else {
                            if (viewModel.cityArray.size > 0) {
                                spinnerCity.setSelection(cityNameArray.indexOf(rawAddress["city"].toString()))
                            }
                        }
                    }
                }
            } else {
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


    override fun onCheckedChanged(compoundButton: CompoundButton?, check: Boolean) {
        when (compoundButton) {
            chAddress -> {
                if (check) {
                    etAddressLine1.setText(rawAddress["address1"].toString())
                    etPinCode.setText(rawAddress["pincode"].toString())
                    spinnerCountry.setSelection(rawAddress["countryPos"] as Int)
//                    if(viewModel.countryArray.size>0)
//                    viewModel.addressState(viewModel.countryArray[rawAddress["countryPos"] as Int].country_id.toString())
                    rawAddress["country"].toString()
                    rawAddress["state"].toString()
                    rawAddress["city"].toString()

                } else {
                    etAddressLine1.setText(" ")
                    etPinCode.setText(" ")
                    spinnerCountry.setSelection(0)
                     checkCountry = 0
                     checkState = 0
                     checkCity = 0
                }
            }
        }
    }

    override fun selectedStrings(strings: MutableList<String>?) {
    }

    override fun selectedIndices(indices: MutableList<Int>?, id: Int) {
        if (indices!!.size > 0) {
            if (id == 1) {
                selectedRole.clear()
                for (i in 0 until indices.size) {
                    selectedRole.add(roleCodeArray[indices[i]])
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
}