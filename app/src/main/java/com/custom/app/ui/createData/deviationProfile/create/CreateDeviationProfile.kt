package com.custom.app.ui.createData.deviationProfile.create

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_create_food_type.*
import kotlinx.android.synthetic.main.activity_deviation_profile.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.util.*
import javax.inject.Inject

class CreateDeviationProfile : AppCompatActivity() {

    @Inject
    lateinit var customerInteractor: CustomerInteractor

    private lateinit var viewModel: CreateDeviationViewModel
    private var selectedCustomerId: Int = 0
    private lateinit var customerList: ArrayList<CustomerRes>
    private var selectedProfileId: Int = 0
    private lateinit var profileList: ArrayList<ProfileListRes>
    var addedViewGroup: LinearLayout? = null
    var view: View? = null
    var deleteView: ImageView? = null
    var spStartBracket: Spinner? = null
    var spKey: Spinner? = null
    var spOperator: Spinner? = null
    var spMainOperator: Spinner? = null
    var etValue: EditText? = null
    var spEndBracket: Spinner? = null
    var selectedStartBracket: String? = null
    var selectedEndBracket: String? = null
    var selectedOperator: String? = null
    var selectedMainOperator: String? = null
    var selectedKey: String? = null

    val jsonRule: JsonObject? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deviation_profile)

        viewModel = ViewModelProvider(this,
                CreateDeviationViewModelFactory(CreateDeviationInteractor(this), customerInteractor, ProfileListInteractor()))[CreateDeviationViewModel::class.java]

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

        addedViewGroup = findViewById(R.id.addedViewGroup)
        var vi = applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = vi.inflate(R.layout.activity_deviation, null)

        addViewGroup.setOnClickListener {
            vi = applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = vi.inflate(R.layout.activity_deviation, null)

            addedViewGroup!!.addView(view, 0, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

        }

        deleteView = view!!.findViewById(R.id.deleteView)
        deleteView!!.setOnClickListener {
            //                addedViewGroup!!.removeView(view)
            (view!!.getParent() as ViewGroup).removeView(view)

        }

        spStartBracket = view!!.findViewById(R.id.spStartBracket)
        spStartBracket!!.setOnItemClickListener { parent, view, position, id ->
            selectedStartBracket = parent.getItemAtPosition(position).toString()
        }
        spKey = view!!.findViewById(R.id.spKey)
        spKey!!.setOnItemClickListener { parent, view, position, id ->
            selectedKey = parent.getItemAtPosition(position).toString()
        }
        spOperator = view!!.findViewById(R.id.spOperator)
        spOperator!!.setOnItemClickListener { parent, view, position, id ->
            selectedOperator = parent.getItemAtPosition(position).toString()
        }
        spMainOperator = view!!.findViewById(R.id.spMainOperator)
        spMainOperator!!.setOnItemClickListener { parent, view, position, id ->
            selectedMainOperator = parent.getItemAtPosition(position).toString()
        }
        spEndBracket = view!!.findViewById(R.id.spEndBracket)
        spEndBracket!!.setOnItemClickListener { parent, view, position, id ->
            selectedEndBracket = parent.getItemAtPosition(position).toString()
        }

        etValue = view!!.findViewById(R.id.etValue)

        validateViewGroup.setOnClickListener {

            jsonRule!!.addProperty("startBracket",selectedStartBracket)
            jsonRule.addProperty("selectedKey",selectedKey)
            jsonRule.addProperty("selectedOperator",selectedOperator)
            jsonRule.addProperty("selectedOperator",selectedOperator)
            jsonRule.addProperty("value",etValue!!.text.toString())

//            val str = selectedStartBracket + selectedKey + selectedOperator + etValue!!.text.toString() + selectedMainOperator

        }
    }

    private fun updateUI(screenCreateState: ScreenState<CreateDeviationState>?) {
        when (screenCreateState) {
            ScreenState.Loading -> progressDeviation.visibility = View.VISIBLE
            is ScreenState.Render -> processLoginState(screenCreateState.renderState)
        }
    }

    private fun processLoginState(renderCreateState: CreateDeviationState) {
        progressFoodType.visibility = View.GONE
        when (renderCreateState) {
            CreateDeviationState.GetCustomerError -> {
                AlertUtil.showToast(this, "Unable to fetch customers")
            }
            CreateDeviationState.GetCustomerSuccess -> {
                customerList = viewModel.getCustomerList.value!!
                updateCustomerSpinner(customerList)
            }
            CreateDeviationState.GetProfileSuccess -> {
                profileList = viewModel.getProfileResponse.value!!
                updateProfileSpinner(profileList)
            }
            CreateDeviationState.GetProfileError -> {
                AlertUtil.showToast(this, "Unable to fetch profile")
            }
            CreateDeviationState.GetCreateDeviationSuccess -> {
                resetForm()
                AlertUtil.showToast(this, "New deviation rule has been created")
            }
            CreateDeviationState.GetCreateDeviationError -> {
                AlertUtil.showToast(this, "Unable to create rule")
            }
        }
    }

    private fun updateCustomerSpinner(customerList: ArrayList<CustomerRes>) {

        val adapter = CustomerDropdownAdapter(this, customerList)
        spCustomers_deviation.adapter = adapter
        spCustomers_deviation.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                        selectedCustomerId = customerList[position].customer_id!!.toInt()
                        viewModel.onGetProfile(selectedCustomerId)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Another interface callback
                    }
                }
    }

    private fun updateProfileSpinner(profileList: ArrayList<ProfileListRes>) {

        val adapter = ProfileDropdownAdapter(this, profileList)
        spProfile_deviation.adapter = adapter
        spProfile_deviation.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                        selectedProfileId = profileList[position].profile_id!!.toInt()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Another interface callback
                    }
                }
    }

    private fun resetForm() {
        blMin.setText("")
        blMax.setText("")
        tempMin.setText("")
        tempMax.setText("")
        moistMin.setText("")
        moistMax.setText("")
        level1Hours.setText("")
        level1Minutes.setText("")
        level1Email.setText("")
        level2Hours.setText("")
        level2Minutes.setText("")
        level2Emails.setText("")
        level3Hours.setText("")
        level3Minutes.setText("")
        level3Emails.setText("")
        level4Hours.setText("")
        level4Minutes.setText("")
        level4Emails.setText("")
        level5Hours.setText("")
        level5Minutes.setText("")
        level5Emails.setText("")
    }

    private fun addDeviation() {
        viewModel.onCreateDeviation(selectedProfileId, selectedCustomerId, blMin.text.toString(), blMax.text.toString(), tempMin.text.toString(), tempMax.text.toString(),
                moistMin.text.toString(), moistMax.text.toString(), level1Hours.text.toString(), level1Minutes.text.toString(), level1Email.text.toString(),
                level2Hours.text.toString(), level2Minutes.text.toString(), level2Emails.text.toString(), level3Hours.text.toString(), level3Minutes.text.toString(),
                level3Emails.text.toString(), level4Hours.text.toString(), level4Minutes.text.toString(), level4Emails.text.toString(), level5Hours.text.toString(),
                level5Minutes.text.toString(), level5Emails.text.toString())
    }

//    (BL>20 AND Temp<10)
//    ((BL>20 AND BL<30) OR Temp<10)
}