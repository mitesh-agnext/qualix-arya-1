package com.custom.app.ui.senseNext.list

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.util.Pair
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.base.app.ui.base.BaseActivity
import com.core.app.util.AlertUtil
import com.custom.app.CustomApp
import com.custom.app.R
import com.custom.app.data.model.senseNext.SNDeviceRes
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.senseNext.deviceDetail.SNDeviceDetailActivity
import com.custom.app.util.Utils
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_sense_next_device_list.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.layout_progress.*
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.collections.ArrayList


class SNDeviceListActivity : BaseActivity(), View.OnClickListener, ItemCallback, AdapterView.OnItemSelectedListener {

    @Inject
    lateinit var interactor: SNDeviceInteractor
    @Inject
    lateinit var customerInteractor: CustomerInteractor
    lateinit var adapter: SNDeviceAdapterval
    var expandIndicator: Boolean = true
    val customerName = ArrayList<String>()
    val regionName = ArrayList<String>()
    val profileName = ArrayList<String>()
    val profileTypeName = ArrayList<String>()
    var data = HashMap<String, String>()
    var filterData = HashMap<String, String>()
    private lateinit var viewModel: SNDeviceVM


    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sense_next_device_list)
        initView()
    }

    fun initView() {
        toolbar.title = "Sense Next"
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        lnFilter.setOnClickListener(this)
        lnStartDate.setOnClickListener(this)
        lnEndDate.setOnClickListener(this)
        tvApply.setOnClickListener(this)
        tvCancel.setOnClickListener(this)
        tvApply.setOnClickListener(this)
        tvCancel.setOnClickListener(this)

        spCustomer.onItemSelectedListener = this
        spProfile.onItemSelectedListener = this
        spProfileType.onItemSelectedListener = this
        spRegion.onItemSelectedListener = this
        spEscalation.onItemSelectedListener = this
        spBattery.onItemSelectedListener = this

        viewModel = ViewModelProvider(this,
                SNDeviceVMFactory(interactor, customerInteractor))[SNDeviceVM::class.java]

        viewModel.snDeviceListState.observe(::getLifecycle, ::setViewState)

        if (userManager.customerType == "SERVICE_PROVIDER") {
            viewModel.getCustomerList()
        } else {
            tvCustomer.visibility = View.GONE
            spCustomer.visibility = View.GONE
            allFilterApis(userManager.customerId.toInt())
            data["customer_id"] = userManager.customerId
            viewModel.getDevicesList(data)
        }
    }

    private fun setViewState(state: SNDeviceListState) {
        when (state) {
            is Loading -> {
                progress.visibility = View.VISIBLE
            }
            is CustomerList -> {
                progress.visibility = View.GONE
                customerName.clear()
                if (viewModel.customerList.size > 0) {
                    for (i in 0 until viewModel.customerList.size) {
                        customerName.add(viewModel.customerList[i].name.toString())
                    }
                    val adapter = ArrayAdapter(this@SNDeviceListActivity, android.R.layout.simple_spinner_item, customerName)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spCustomer.adapter = adapter
                }
            }
            is CustomerError -> {
                progress.visibility = View.GONE
                AlertUtil.showToast(this, viewModel.errorMsg)
            }


            is AllRegionSuccess -> {
                progress.visibility = View.GONE
                regionName.clear()
                if (viewModel.regionResList.size > 0) {
                    regionName.add("Select region")
                    for (i in 0 until viewModel.regionResList.size) {
                        regionName.add(viewModel.regionResList[i].region_name.toString())
                    }
                } else {
                    regionName.add("No Data")
                }
                val adapter = ArrayAdapter(this@SNDeviceListActivity, android.R.layout.simple_spinner_item, regionName)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spRegion.adapter = adapter
            }
            is AllRegionFailure -> {
                progress.visibility = View.GONE
                AlertUtil.showToast(this, viewModel.errorMsg)
            }

            is AllProfileSuccess -> {
                progress.visibility = View.GONE
                profileName.clear()
                if (viewModel.profileList.size > 0) {
                    profileName.add("Select Profile")
                    for (i in 0 until viewModel.profileList.size) {
                        profileName.add(viewModel.profileList[i].profile_name.toString())
                    }
                } else {
                    profileName.add("No Data")
                }
                val adapter = ArrayAdapter(this@SNDeviceListActivity, android.R.layout.simple_spinner_item, profileName)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spProfile.adapter = adapter
            }
            is AllProfileFailure -> {
                progress.visibility = View.GONE
                AlertUtil.showToast(this, viewModel.errorMsg)
            }

            is AllProfileTypeSuccess -> {
                progress.visibility = View.GONE
                profileTypeName.clear()
                if (viewModel.profileTypeList.size > 0) {
                    profileTypeName.add("Select Profile type")
                    for (i in 0 until viewModel.profileTypeList.size) {
                        profileTypeName.add(viewModel.profileTypeList[i].profile_type_name.toString())
                    }

                } else {
                    profileTypeName.add("No Data")
                }
                val adapter = ArrayAdapter(this@SNDeviceListActivity, android.R.layout.simple_spinner_item, profileTypeName)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spProfileType.adapter = adapter
            }

            is AllProfileTypeFailure -> {
                progress.visibility = View.GONE
                AlertUtil.showToast(this, viewModel.errorMsg)
            }
            is List -> {
                progress.visibility = View.GONE
                if (viewModel.devicesList.size > 0) {
                    rvDevices.visibility = View.VISIBLE
                    tvNoData.visibility = View.GONE
                    adapter = SNDeviceAdapterval(this, viewModel.devicesList, this)
                    rvDevices.adapter = adapter
                    rvDevices.layoutManager = LinearLayoutManager(this)
                }
            }
            is NoRecord -> {
                progress.visibility = View.GONE
                rvDevices.visibility = View.GONE
                tvNoData.visibility = View.VISIBLE
                expandIndicator = false
                lnFilterBody.visibility = View.GONE
                ivFilter.setBackgroundResource(R.drawable.ic_down)
            }
            is Delete -> {
                progress.visibility = View.GONE
                //  adapter.notifyDataSetChanged()
            }
            is Error -> {
                progress.visibility = View.GONE
//                showError(state.message!!)
            }
            is Token -> {
                progress.visibility = View.GONE
                AlertUtil.showToast(this, getString(R.string.token_expire))
                Utils.tokenExpire(this@SNDeviceListActivity)
            }
        }
    }

    override fun clickItem(position: Int) {
        var intent = Intent(this, SNDeviceDetailActivity::class.java)
        val gson = Gson()
        val type = object : TypeToken<SNDeviceRes>() {}.type
        val json = gson.toJson(viewModel.devicesList[position], type)
        intent.putExtra("selectObject", json)
        startActivity(intent)
    }

    override fun deleteItem() {
        // AlertUtil.showToast(this, "WEEE")
    }

    override fun onClick(view: View?) {
        when (view) {
            lnFilter -> {
                if (expandIndicator) {
                    expandIndicator = false
                    lnFilterBody.visibility = View.GONE
                    ivFilter.setBackgroundResource(R.drawable.ic_down)
                } else {
                    expandIndicator = true
                    lnFilterBody.visibility = View.VISIBLE
                    ivFilter.setBackgroundResource(R.drawable.ic_up)
                }
            }
            lnEndDate -> {
                dateRange()
            }
            lnStartDate -> {
                dateRange()
            }
            tvApply -> {
                hideFilter()
                viewModel.getDevicesList(filterData)
            }
            tvCancel -> {
                cancelClick()
                hideFilter()
                viewModel.getDevicesList(data)
            }
        }
    }

    private fun hideFilter() {
        expandIndicator = false
        lnFilterBody.visibility = View.GONE
        ivFilter.setBackgroundResource(R.drawable.ic_down)
    }

    private fun cancelClick() {
        tvStartDate.text = getString(R.string.start_date)
        tvEndDate.text = getString(R.string.end_date)
        spProfile.setSelection(0)
        spProfileType.setSelection(0)
        spRegion.setSelection(0)
        spEscalation.setSelection(0)
        spBattery.setSelection(0)
    }

    private fun dateRange() {
        val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        builder.setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
        val constraints = CalendarConstraints.Builder()
        val max = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli()
        constraints.setValidator(DateValidatorPointBackward.before(max))
        builder.setTheme(R.style.ThemeOverlay_MaterialComponents_MaterialCalendar)
        builder.setCalendarConstraints(constraints.build())
        var datePicker = builder.build()
        datePicker.addOnPositiveButtonClickListener(MaterialPickerOnPositiveButtonClickListener { selection: Pair<Long?, Long?> ->
            tvStartDate.text = LocalDate.ofEpochDay(TimeUnit.MILLISECONDS.toDays(selection.first!!)).format(dateFormatter)
            tvEndDate.text = LocalDate.ofEpochDay(TimeUnit.MILLISECONDS.toDays(selection.second!!)).format(dateFormatter)
            filterData["date_from"] = selection.first.toString()
            filterData["date_to"] = selection.second.toString()

        })
        datePicker.show(supportFragmentManager, datePicker.toString())

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    override fun onItemSelected(spinner: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
        when (spinner) {
            spCustomer -> {
                if (viewModel.customerList.size > 0) {
                    allFilterApis(viewModel.customerList[pos].customer_id!!)
                    data["customer_id"] = viewModel.customerList[pos].customer_id.toString()
                    filterData["customer_id"] = viewModel.customerList[pos].customer_id.toString()
                    viewModel.getDevicesList(data)
                }
            }
            spProfile -> {
                if (pos > 0) {
                    filterData["profile_type_id"] = viewModel.profileTypeList[pos - 1].profile_type_id.toString()
                } else if (pos == 0) {
                    filterData.remove("profile_type_id")
                }
            }
            spProfileType -> {
                if (pos > 0) {
                    filterData["profile_id"] = viewModel.profileList[pos - 1].profile_id.toString()
                } else if (pos == 0) {
                    filterData.remove("profile_id")
                }
            }

            spRegion -> {
                if (pos > 0) {
                    filterData["region_id"] = viewModel.regionResList[pos - 1].region_id.toString()
                } else if (pos == 0) {
                    filterData.remove("region_id")
                }
            }
            spEscalation -> {
                if (pos > 0) {
                    filterData["escalation_level"] = pos.toString()
                } else if (pos == 0) {
                    filterData.remove("escalation_level")
                }
            }
            spBattery -> {
                if (pos > 0) {
                    filterData["battery_level"] = pos.toString()
                } else if (pos == 0) {
                    filterData.remove("battery_level")
                }
            }
        }
    }

    private fun allFilterApis(customerId: Int) {
        viewModel.getAllProfile(customerId)
        viewModel.getAllProfileType(customerId)
        viewModel.getAllRegion(customerId)
    }
}

interface ItemCallback {
    fun clickItem(position: Int)
    fun deleteItem()
}

