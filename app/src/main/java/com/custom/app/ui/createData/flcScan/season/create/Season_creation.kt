package com.custom.app.ui.createData.flcScan.season.create

import android.app.Activity
import android.app.DatePickerDialog
import android.content.DialogInterface
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
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerRes
import com.custom.app.util.Constants
import kotlinx.android.synthetic.main.activity_season_creation.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class Season_creation : AppCompatActivity() {

    @Inject
    lateinit var customerInteractor: CustomerInteractor

    private lateinit var viewModel: SeasonCreateViewModel
    private var selectedCustomerId: Int = 0
    private var selectedCommodityId: Int = 0
    private lateinit var customerList: ArrayList<CustomerRes>
    private lateinit var commodityList: ArrayList<CommodityRes>
    private var dateFrom: Long? = null
    private var dateTo: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_season_creation)

        viewModel = ViewModelProvider(this,
                CreateSeasonViewModelFactory(SeasonCreateInteractor(),
                        customerInteractor))[SeasonCreateViewModel::class.java]

        viewModel.seasonCreateState.observe(::getLifecycle, ::updateUI)
        initView()
        viewModel.onGetCustomer()
    }

    fun initView() {
        toolbar.title = getString(R.string.create_season)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        btCreateSeason.text = getString(R.string.create_season)
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

    private fun updateUI(screenCreateState: ScreenState<SeasonCreateState>?) {
        when (screenCreateState) {
            ScreenState.Loading -> progressSeason.visibility = View.VISIBLE
            is ScreenState.Render -> processLoginState(screenCreateState.renderState)
        }
    }

    private fun processLoginState(renderCreateState: SeasonCreateState) {
        progressSeason.visibility = View.GONE
        when (renderCreateState) {
            SeasonCreateState.SeasonNameEmpty -> {
                AlertUtil.showSnackBar(season_layout, "Please enter season name", 2000)
            }
            SeasonCreateState.GetCustomerSuccess -> {
                customerList = viewModel.getCustomerList.value!!
                updateCustomerSpinner(customerList)
            }
            SeasonCreateState.GetCustomerFailure -> {
                AlertUtil.showSnackBar(season_layout, "Unable to fetch customers", 2000)
            }
            SeasonCreateState.SeasonCreateFailure -> {
                AlertUtil.showSnackBar(season_layout, "Unable to create season", 2000)
            }
            SeasonCreateState.GetCommoditySuccess -> {
                commodityList = viewModel.getCommodityList.value!!
                updateCommoditySpinner(commodityList)
            }
            SeasonCreateState.GetCommodityFailure -> {
                AlertUtil.showSnackBar(season_layout, "Unable to fetch seasons", 2000)
            }
            SeasonCreateState.SeasonCreateSuccess -> {
                resetForm()
                AlertUtil.showActionAlertDialog(this, "", "New season has been created !",
                        false, getString(R.string.btn_ok)) { dialog: DialogInterface?, which: Int ->
                    val intent = Intent()
                    intent.putExtra("result", "success")
                    setResult(BaseActivity.RESULT_OK, intent);
                    finish()
                }
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

                        selectedCommodityId = commodityList[position].commodity_id!!.toInt()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Another interface callback
                    }
                }
    }

    private fun resetForm() {
        et_season_name.setText("")
        et_season_equation.setText("")
        et_DateFrom.setText("")
        et_DateTo.setText("")
    }

    private fun datePickerDialog(textView: TextView) {

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year: Int, monthOfYear: Int, dayOfMonth: Int ->


                    val fmt = SimpleDateFormat("dd-MM-yyyy")
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

        val datePicker = dpDialog.datePicker
        val calendar = Calendar.getInstance()
        datePicker.maxDate = calendar.timeInMillis

        dpDialog.show()

    }

    private fun addSeason() {
        dateFrom = epochTime(et_DateFrom.text.toString())
        dateTo = epochTime(et_DateTo.text.toString())

        viewModel.onCreateSeason(et_season_name.text.toString(), et_season_equation.text.toString(), selectedCustomerId, selectedCommodityId, dateFrom!!, dateTo!!)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_ADD_REGION) {
            if (resultCode == Activity.RESULT_OK) {
                setResult(AppCompatActivity.RESULT_OK);
                finish()
            }
        }
    }

    private fun epochTime(strDate: String): Long {

        val fmtOut = SimpleDateFormat("dd-MM-yyyy")
        val date = fmtOut.parse(strDate)
        val epoch = date.time

        return epoch
    }

}