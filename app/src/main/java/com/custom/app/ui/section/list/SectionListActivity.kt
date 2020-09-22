package com.custom.app.ui.section.list

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.base.app.ui.base.BaseActivity
import com.core.app.util.AlertUtil
import com.custom.app.CustomApp
import com.custom.app.R
import com.custom.app.data.model.section.SectionRes
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.section.add.AddSectionActivity
import com.custom.app.ui.section.detail.SectionDetailActivity
import com.custom.app.ui.section.update.UpdateSectionActivity
import com.custom.app.util.Constants.REQUEST_ADD_FARM_OPTION
import com.custom.app.util.Constants.REQUEST_FARM_OPTION
import com.custom.app.util.Utils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_farm_list.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.layout_progress.*
import timber.log.Timber
import javax.inject.Inject

class SectionListActivity : BaseActivity(), View.OnClickListener, FarmCallback, AdapterView.OnItemSelectedListener {

    @Inject
    lateinit var interactor: SectionInteractor

    private lateinit var viewModel: SectionListVM
    var locationNameArray = ArrayList<String>()
    var divisionNameArray = ArrayList<String>()
    var gardenNameArray = ArrayList<String>()
    var sectionNameArray = ArrayList<String>()
    val noData = "No Data"

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_farm_list)
        initView()
    }

    private fun initView() {
        toolbar.title = getString(R.string.section_management)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        fbAdd.setOnClickListener(this)
        spinnerDivision.onItemSelectedListener = this
        spinnerGarden.onItemSelectedListener = this
        spinnerLocation.onItemSelectedListener = this

        viewModel = ViewModelProvider(this,
                SectionListVMFactory(interactor))[SectionListVM::class.java]
        viewModel.sectionListState.observe(::getLifecycle, ::updateUI)

        //set location spinner
        viewModel.getLocation()
    }

    private fun updateUI(screenState: ScreenState<SectionState>?) {
        when (screenState) {
            ScreenState.Loading -> progress.visibility = View.VISIBLE
            is ScreenState.Render -> processLoginState(screenState.renderState)
        }
    }

    private fun processLoginState(renderState: SectionState) {
        when (renderState) {
            SectionState.LocationSuccess -> {
                locationNameArray.clear()
                for (i in 0 until viewModel.locationArray.size) {
                    locationNameArray.add(viewModel.locationArray[i].name.toString())
                }
                setSpinner(spinnerLocation, locationNameArray)
            }
            SectionState.LocationFailure -> {
                locationNameArray.clear()
                locationNameArray.add(noData)
                setSpinner(spinnerLocation, locationNameArray)
                AlertUtil.showToast(this, viewModel.errorMessage)
            }
            SectionState.GardenSuccess -> {
                gardenNameArray.clear()
                if (viewModel.gardenArray.size > 0)
                    for (i in 0 until viewModel.gardenArray.size) {
                        gardenNameArray.add(viewModel.gardenArray[i].name.toString())
                    }
                else
                    gardenNameArray.add(noData)
                setSpinner(spinnerGarden, gardenNameArray)
            }
            SectionState.GardenFailure -> {
                gardenNameArray.clear()
                gardenNameArray.add(noData)
                setSpinner(spinnerGarden, gardenNameArray)
                AlertUtil.showToast(this, viewModel.errorMessage)
            }
            SectionState.DivisionSuccess -> {
                divisionNameArray.clear()
                if (viewModel.divisionArray.size > 0)
                    for (i in 0 until viewModel.divisionArray.size) {
                        divisionNameArray.add(viewModel.divisionArray[i].name.toString())
                    }
                else
                    divisionNameArray.add(noData)
                setSpinner(spinnerDivision, divisionNameArray)
            }
            SectionState.GardenFailure -> {
                divisionNameArray.clear()
                divisionNameArray.add(noData)
                setSpinner(spinnerGarden, divisionNameArray)
                AlertUtil.showToast(this, viewModel.errorMessage)
            }
            SectionState.GetSectionsSuccess -> {
                var qualityAnalysisAdapter = SectionListAdapter(this, viewModel.sectionArray, this)
                rvSection.adapter = qualityAnalysisAdapter
                rvSection.layoutManager = LinearLayoutManager(this)
            }
            SectionState.GardenFailure -> {
                AlertUtil.showToast(this, viewModel.errorMessage)
            }
            SectionState.DeleteSectionSuccess -> {
                AlertUtil.showToast(this, "Section Deleted Successfully")
                viewModel.getSection(viewModel.divisionArray[spinnerDivision.selectedItemPosition].division_id.toString())
            }
            SectionState.DeleteSectionFailure -> {
                AlertUtil.showToast(this, viewModel.errorMessage)
            }
            SectionState.TokenExpire -> {
                AlertUtil.showToast(this,getString(R.string.token_expire))
                Utils.tokenExpire(this@SectionListActivity)            }
        }
        progress.visibility = View.GONE
    }

    private fun setSpinner(spinner: Spinner, options: ArrayList<String>) {
        val adapter = ArrayAdapter(this@SectionListActivity, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    override fun onClick(view: View?) {
        when (view) {
            fbAdd -> {
                var intent = Intent(this, AddSectionActivity::class.java)
                startActivityForResult(intent, REQUEST_ADD_FARM_OPTION)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_FARM_OPTION) {
            if (resultCode == Activity.RESULT_OK) {
                viewModel.getSection(viewModel.divisionArray[spinnerDivision.selectedItemPosition].division_id.toString())
            }
        }
       else if(requestCode == REQUEST_ADD_FARM_OPTION) {
            if (resultCode == Activity.RESULT_OK) {
                viewModel.getLocation()
            }
        }
    }

    /*RecycleView item operation*/
    override fun editCustomerCallback(pos: Int) {
        var intent = Intent(this, UpdateSectionActivity::class.java)
        val gson = Gson()
        val type = object : TypeToken<SectionRes>() {}.type
        val json = gson.toJson(viewModel.sectionArray[pos], type)
        intent.putExtra("selectObject", json)
        startActivityForResult(intent, REQUEST_FARM_OPTION)
    }

    override fun deleteCustomerCallback(pos: Int) {
        dialogConfirmDelete("Delete Section", "Are you sure you want to delete section", this, pos)
    }

    private fun dialogConfirmDelete(title: String?, message: String?, context: Context, pos: Int) {
        AlertDialog.Builder(context)
                .setTitle("$title")
                .setMessage("$message")
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    viewModel!!.deleteSection(viewModel.sectionArray[pos].section_id.toString())
                    viewModel.sectionArray.removeAt(pos)
                }
                .show()
    }

    override fun itemClick(pos: Int) {
        var intent = Intent(this, SectionDetailActivity::class.java)
        val gson = Gson()
        val type = object : TypeToken<SectionRes>() {}.type
        val json = gson.toJson(viewModel.sectionArray[pos], type)
        intent.putExtra("selectObject", json)
        startActivity(intent)
    }

    /*Spinner listener*/
    override fun onNothingSelected(p0: AdapterView<*>?) {
        Timber.e("Nothing")
    }

    override fun onItemSelected(spinner: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
        when (spinner) {
            spinnerLocation -> {
                if (viewModel.locationArray.size > 0)
                    viewModel.getGarden(viewModel.locationArray[pos].location_id.toString())
            }
            spinnerGarden -> {
                if (viewModel.gardenArray.size > 0)
                    viewModel.getDivision(viewModel.gardenArray[pos].garden_id.toString())
            }
            spinnerDivision -> {
                if (viewModel.divisionArray.size > 0)
                    viewModel.getSection(viewModel.divisionArray[pos].division_id.toString())
            }
        }
    }
}
