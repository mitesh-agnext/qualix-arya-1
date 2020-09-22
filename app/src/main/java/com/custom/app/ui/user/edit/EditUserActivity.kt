package com.custom.app.ui.user.edit

import android.content.DialogInterface
import android.content.Intent
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
import com.custom.app.ui.user.list.UserDataRes
import com.custom.app.ui.user.list.UserInteractor
import com.custom.app.util.Utils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_add_user.*
import kotlinx.android.synthetic.main.activity_edit_user.*
import kotlinx.android.synthetic.main.activity_edit_user.etAddressLine1
import kotlinx.android.synthetic.main.activity_edit_user.etContactNumber
import kotlinx.android.synthetic.main.activity_edit_user.etEmail
import kotlinx.android.synthetic.main.activity_edit_user.etFirstName
import kotlinx.android.synthetic.main.activity_edit_user.etLastName
import kotlinx.android.synthetic.main.activity_edit_user.etPinCode
import kotlinx.android.synthetic.main.activity_edit_user.spRole
import kotlinx.android.synthetic.main.activity_edit_user.spUserType
import kotlinx.android.synthetic.main.activity_edit_user.spinnerCity
import kotlinx.android.synthetic.main.activity_edit_user.spinnerCountry
import kotlinx.android.synthetic.main.activity_edit_user.spinnerState
import kotlinx.android.synthetic.main.custom_toolbar.*
import javax.inject.Inject

class EditUserActivity : BaseActivity(), View.OnClickListener, AdapterView.OnItemSelectedListener {

    @Inject
    lateinit var userInteractor: UserInteractor

    @Inject
    lateinit var addressInteractor: AddressInteractor

    lateinit var viewModel: EditUserVM
    lateinit var userId: String
    private val roleArray = ArrayList<String>()
    private val roleCodeArray = ArrayList<String>()
    var countryNameArray = ArrayList<String>()
    var stateNameArray = ArrayList<String>()
    var cityNameArray = ArrayList<String>()
    var testObject: UserDataRes? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)
        initView()
    }

    private fun initView() {
        toolbar.title = getString(R.string.update_user)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        btUpdateUser.setOnClickListener(this)
        spinnerCountry.onItemSelectedListener = this
        spinnerState.onItemSelectedListener = this

        val selectObject = intent.getStringExtra("selectObject")
        val gson = Gson()
        if (selectObject != null) {
            val type = object : TypeToken<UserDataRes>() {}.type
            testObject = gson.fromJson(selectObject, type)
            setDummyData(testObject!!)
        }
        //VM
        viewModel = ViewModelProvider(this,
                EditUserVMFactory(userInteractor, addressInteractor))[EditUserVM::class.java]
        viewModel.userStates.observe(::getLifecycle, ::updateUI)
        //Spinner data
        viewModel.userRole()
        viewModel.addressCountry()
    }

    private fun updateUI(screenState: ScreenState<EditUserState>?) {
        when (screenState) {
            //  ScreenState.Loading -> progress.visibility = View.VISIBLE
            is ScreenState.Render -> processLoginState(screenState.renderState)
        }
    }

    private fun processLoginState(renderState: EditUserState) {
        when (renderState) {
            EditUserState.UserRoleFailure -> {
                AlertUtil.showSnackBar(addUser_layout, viewModel.errorMassage, 2000)
            }
            EditUserState.UserRoleSuccess -> {
                for (i in 0 until viewModel.userRole.value!!.size) {
                    roleArray.add(viewModel.userRole.value!![i].role_desc.toString())
                    roleCodeArray.add(viewModel.userRole.value!![i].role_code.toString())
                }
                val adapter = ArrayAdapter(this@EditUserActivity, android.R.layout.simple_spinner_item, roleArray)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spRole.adapter = adapter
            }
            EditUserState.EditUserFailure -> {
                AlertUtil.showSnackBar(addUser_layout, viewModel.errorMassage, 2000)
            }
            EditUserState.EditUserSuccess -> {
                AlertUtil.showActionAlertDialog(this, "", getString(R.string.updated_successfully),
                        false, getString(R.string.btn_ok)) { dialog: DialogInterface?, which: Int ->
                    val intent = Intent()
                    intent.putExtra("result", "success")
                    setResult(RESULT_OK, intent);
                    finish()
                }
            }
            EditUserState.CountryFailure -> {
                countryNameArray.clear()
                countryNameArray.add("No Country")
                setSpinner(spinnerCountry, countryNameArray)
                AlertUtil.showSnackBar(addUser_layout, viewModel.errorMassage, 2000)
            }
            EditUserState.CountrySuccess -> {
                countryNameArray.clear()
                for (i in 0 until viewModel.countryArray.size) {
                    countryNameArray.add(viewModel.countryArray[i].country_name.toString())
                }
                setSpinner(spinnerCountry, countryNameArray)
            }
            EditUserState.StateFailure -> {
                stateNameArray.clear()
                stateNameArray.add("No State")
                setSpinner(spinnerState, stateNameArray)
                AlertUtil.showSnackBar(addUser_layout, viewModel.errorMassage, 2000)
            }
            EditUserState.StateSuccess -> {
                stateNameArray.clear()
                for (i in 0 until viewModel.stateArray.size) {
                    stateNameArray.add(viewModel.stateArray[i].state_name.toString())
                }
                setSpinner(spinnerState, stateNameArray)
            }
            EditUserState.CityFailure -> {
                cityNameArray.clear()
                cityNameArray.add("No city")
                setSpinner(spinnerCity, cityNameArray)
                AlertUtil.showSnackBar(addUser_layout, viewModel.errorMassage, 2000)
            }
            EditUserState.CitySuccess -> {
                cityNameArray.clear()
                for (i in 0 until viewModel.cityArray.size) {
                    cityNameArray.add(viewModel.cityArray[i].city_name.toString())
                }
                setSpinner(spinnerCity, cityNameArray)
            }
            EditUserState.TokenExpire -> {
                AlertUtil.showSnackBar(editUser_layout, getString(R.string.token_expire), 2000)
                Utils.tokenExpire(this@EditUserActivity)
            }
        }
    }

    private fun setDummyData(testObject: UserDataRes) {
        etFirstName.setText(testObject.first_name)
        etLastName.setText(testObject.last_name)
        etContactNumber.setText(testObject.contact_number)
        etEmail.setText(testObject.email)
        userId = testObject.user_id.toString()
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
        val adapter = ArrayAdapter(this@EditUserActivity, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    override fun onClick(view: View?) {
        when (view) {
            btUpdateUser -> {
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

                    val roleArray = ArrayList<String>()
                    roleArray.add(spRole.selectedItem.toString())
                    requestUserData["roles"] = roleArray
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
                    viewModel.updateUser(userId, requestUserData)
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