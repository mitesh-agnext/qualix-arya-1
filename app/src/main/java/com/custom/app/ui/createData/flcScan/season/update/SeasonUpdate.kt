package com.custom.app.ui.createData.flcScan.season.update

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.base.app.ui.base.BaseActivity
import com.core.app.util.AlertUtil
import com.custom.app.CustomApp
import com.custom.app.R
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.createData.adapters.CustomerDropdownAdapter
import com.custom.app.ui.createData.flcScan.commodity.create.CommodityDropdownAdapter
import com.custom.app.ui.createData.flcScan.season.create.CommodityRes
import com.custom.app.ui.createData.flcScan.season.create.SeasonCreateInteractor
import com.custom.app.ui.createData.flcScan.season.list.SeasonRes
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerRes
import com.custom.app.util.Constants
import com.custom.app.util.Utils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_season_creation.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class SeasonUpdate : AppCompatActivity() {

    @Inject
    lateinit var customerInteractor: CustomerInteractor

    private lateinit var viewModel: SeasonUpdateViewModel
    private var selectedCustomerId: Int = 0
    private var selectedCommodityId: Int = 0
    private lateinit var customerList: ArrayList<CustomerRes>
    private lateinit var commodityList: ArrayList<CommodityRes>
    private var dateFrom: Long? = null
    private var dateTo: Long? = null
    var seasonId: Int? = null
    var testObject: SeasonRes? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_season_creation)

        val selectObject = intent.getStringExtra("selectObject")
        val gson = Gson()
        if (selectObject != null) {
            val type = object : TypeToken<SeasonRes>() {}.type
            testObject = gson.fromJson(selectObject, type)
        }


        viewModel = ViewModelProvider(this,
                UpdateSeasonViewModelFactory(SeasonCreateInteractor(),
                        SeasonUpdateInteractor(), customerInteractor))[SeasonUpdateViewModel::class.java]

        viewModel.seasonUpdateState.observe(::getLifecycle, ::updateUI)
        initView()
        viewModel.onGetCustomer()
    }

    fun initView() {
        toolbar.title = getString(R.string.update_season)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        btCreateSeason.text = getString(R.string.update_season)
        btCreateSeason.setOnClickListener {
            addSeason()
        }
        et_DateFrom.setOnClickListener {
            datePickerDialog(et_DateFrom)
        }
        et_DateTo.setOnClickListener {
            datePickerDialog(et_DateTo)
        }
    }

    private fun updateUI(screenUpdateState: ScreenState<SeasonUpdateState>?) {
        when (screenUpdateState) {
            ScreenState.Loading -> progressSeason.visibility = View.VISIBLE
            is ScreenState.Render -> processLoginState(screenUpdateState.renderState)
        }
    }

    private fun processLoginState(renderUpdateState: SeasonUpdateState) {
        progressSeason.visibility = View.GONE
        when (renderUpdateState) {
            SeasonUpdateState.SeasonNameEmpty -> {
                AlertUtil.showToast(this, "Please enter season name")
            }
            SeasonUpdateState.GetCustomerSuccess -> {
                customerList = viewModel.getCustomerList.value!!
                updateCustomerSpinner(customerList)
            }
            SeasonUpdateState.GetCustomerFailure -> {
                AlertUtil.showToast(this, "Unable to fetch customers")
            }
            SeasonUpdateState.GetCommoditySuccess -> {
                commodityList = viewModel.getCommodityList.value!!
                updateCommoditySpinner(commodityList)
            }
            SeasonUpdateState.GetCommodityFailure -> {
                AlertUtil.showToast(this, "Unable to fetch commodities")
            }
            SeasonUpdateState.SeasonUpdateFailure -> {
                AlertUtil.showToast(this, "Unable to update season")
            }
            SeasonUpdateState.SeasonUpdateSuccess -> {
                val intent = Intent()
                intent.putExtra("result", "success")
                setResult(BaseActivity.RESULT_OK, intent);
                finish()

                AlertUtil.showToast(this, "Season has been updated")

            }
        }
    }

    private fun updateCustomerSpinner(customerList: ArrayList<CustomerRes>) {

        val adapter = CustomerDropdownAdapter(this, customerList)
        spCustomers_season.adapter = adapter
        spCustomers_season.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View,
                            position: Int,
                            id: Long) {

                        if (customerList.isNotEmpty()) {
                            for (i in 0 until customerList.size) {
                                if (customerList[i].customer_id == testObject!!.customer_id) {
                                    spCustomers_season.setSelection(i)
                                }
                            }
                        }

                        selectedCustomerId = customerList[position].customer_id!!.toInt()
                        viewModel.onGetCommodity(selectedCustomerId)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Another interface callback
                    }
                }
    }

    private fun updateCommoditySpinner(commodityList: ArrayList<CommodityRes>) {

        val adapter = CommodityDropdownAdapter(this, commodityList)
        spCommodity_season.adapter = adapter
        spCommodity_season.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View,
                            position: Int,
                            id: Long) {

                        if (commodityList.isNotEmpty()) {
                            for (i in 0 until commodityList.size) {
                                if (commodityList[i].commodity_id == testObject!!.commodity_id) {
                                    spCommodity_season.setSelection(i)
                                }
                            }
                        }

                        selectedCommodityId = commodityList[position].commodity_id!!.toInt()

                        updateView()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Another interface callback
                    }
                }
    }

    private fun datePickerDialog(textView: TextView) {

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year: Int, monthOfYear: Int, dayOfMonth: Int ->


                    val fmt = SimpleDateFormat("dd/MM/yyyy")
                    val month = monthOfYear + 1
                    val date = fmt.parse("$dayOfMonth-$month-$year")

                    val fmtOut = SimpleDateFormat("dd-MM-yyyy")

                    when (textView.id) {
                        R.id.et_DateFrom -> {
                            et_DateFrom.text = fmtOut.format(date)
                        }
                        R.id.et_DateTo -> {
                            et_DateTo.text = fmtOut.format(date)
                        }
                    }
                }, year, month, day
        )

        var datePicker = dpDialog.datePicker
        var calendar = Calendar.getInstance()
        datePicker.maxDate = calendar.timeInMillis

        dpDialog.show()

    }

    private fun addSeason() {
        dateFrom = epochTime(et_DateFrom.text.toString())
        dateTo = epochTime(et_DateTo.text.toString())

        viewModel.onUpdateSeason(et_season_name.text.toString(), et_season_equation.text.toString(), selectedCustomerId, selectedCommodityId,dateFrom!!, dateTo!!)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === Constants.REQUEST_ADD_REGION) {
            if (resultCode === Activity.RESULT_OK) {
                setResult(AppCompatActivity.RESULT_OK);
                finish()
            }
        }
    }

    private fun epochTime(strDate: String): Long {

        val fmtOut = SimpleDateFormat("dd/MM/yyyy")
        val date = fmtOut.parse(strDate)
        val epoch = date.time

        return epoch
    }

    private fun updateView() {

        et_season_name.setText(testObject!!.season_name)
        et_season_equation.setText(testObject!!.season_equation)

        et_DateFrom.text = Utils.timeStampDate(testObject!!.from_date!!.toLong())
        et_DateTo.text = Utils.timeStampDate(testObject!!.to_date!!.toLong())
        seasonId = testObject!!.season_id
    }
}