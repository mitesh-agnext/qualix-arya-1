package com.custom.app.ui.section.update

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
import com.custom.app.data.model.section.SectionRes
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.section.add.FieldPlotting
import com.custom.app.ui.section.list.SectionInteractor
import com.custom.app.ui.section.list.SectionListVM
import com.custom.app.ui.section.list.SectionListVMFactory
import com.custom.app.ui.section.list.SectionState
import com.custom.app.util.Constants
import com.custom.app.util.Utils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_update_section.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.layout_progress.*
import javax.inject.Inject

class UpdateSectionActivity : BaseActivity(), OnMapReadyCallback, AdapterView.OnItemSelectedListener, View.OnClickListener {

    @Inject
    lateinit var interactor: SectionInteractor

    var locationNameArray = ArrayList<String>()
    var divisionNameArray = ArrayList<String>()
    var gardenNameArray = ArrayList<String>()
    var sectionNameArray = ArrayList<String>()
    val noData = "NO Data"

    private var mMap: GoogleMap? = null
    var field_latitude = 0.0
    var field_longitude = 0.0
    var polyline: Polygon? = null
    lateinit var points: ArrayList<LatLng>
    private lateinit var viewModel: SectionListVM
    var testObject: SectionRes? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_section)
        initView()
    }

    private fun initView() {
        toolbar.title = getString(R.string.section_management)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        spinnerDivision.onItemSelectedListener = this
        spinnerGarden.onItemSelectedListener = this
        spinnerLocation.onItemSelectedListener = this
        btAddBoundary.setOnClickListener(this)
        btUpdateSection.setOnClickListener(this)

        val mapFragment =
                supportFragmentManager.findFragmentById(R.id.mapFieldDetail) as SupportMapFragment
        mapFragment.getMapAsync(this)

        viewModel = ViewModelProvider(this,
                SectionListVMFactory(interactor))[SectionListVM::class.java]
        viewModel.sectionListState.observe(::getLifecycle, ::updateUI)

        //Set default data in form
        val selectObject = intent.getStringExtra("selectObject")
        val gson = Gson()
        if (selectObject != null) {
            val type = object : TypeToken<SectionRes>() {}.type
            testObject = gson.fromJson(selectObject, type)
            setDummyData(testObject!!)
        }
        //set location spinner
        viewModel.getLocation()
    }

    private fun setDummyData(testObject: SectionRes) {
        etSectionName.setText(testObject.name)
        etArea.setText(testObject.total_area)
        lnMap.visibility = View.VISIBLE
        points = ArrayList<LatLng>()
        for (i in 0 until testObject.section_indices!!.size) {
            points.add(LatLng(testObject.section_indices!![i].latitude!!.toDouble(), testObject.section_indices!![i].longitude!!.toDouble()))
        }
        Handler().postDelayed({
            if (points.size > 0)
                addFieldOnMap(points)
            else {
                lnMap.visibility = View.GONE
            }
        }, 2000)
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
                for (i in 0 until viewModel.gardenArray.size) {
                    gardenNameArray.add(viewModel.gardenArray[i].name.toString())
                }
                setSpinner(spinnerGarden, gardenNameArray)
                spinnerGarden.setSelection(gardenNameArray.indexOf(testObject!!.garden_name))
            }
            SectionState.GardenFailure -> {
                gardenNameArray.clear()
                gardenNameArray.add(noData)
                setSpinner(spinnerGarden, gardenNameArray)
                AlertUtil.showToast(this, viewModel.errorMessage)
            }
            SectionState.DivisionSuccess -> {
                divisionNameArray.clear()
                for (i in 0 until viewModel.divisionArray.size) {
                    divisionNameArray.add(viewModel.divisionArray[i].name.toString())
                }
                setSpinner(spinnerDivision, divisionNameArray)
                spinnerDivision.setSelection(divisionNameArray.indexOf(testObject!!.division_name))
            }
            SectionState.GardenFailure -> {
                divisionNameArray.clear()
                divisionNameArray.add(noData)
                setSpinner(spinnerGarden, divisionNameArray)
                AlertUtil.showToast(this, viewModel.errorMessage)
            }
            SectionState.GetSectionsSuccess -> {

            }
            SectionState.GardenFailure -> {
                AlertUtil.showToast(this, viewModel.errorMessage)
            }

            SectionState.UpdateSectionSuccess -> {
                setResult(RESULT_OK)
                finish()
            }
            SectionState.UpdateSectionFailure -> {
                AlertUtil.showToast(this, viewModel.errorMessage)
            }
            SectionState.TokenExpire -> {
                AlertUtil.showToast(this, getString(R.string.token_expire))
                Utils.tokenExpire(this@UpdateSectionActivity)
            }
        }
        progress.visibility = View.GONE
    }

    private fun setSpinner(spinner: Spinner, options: ArrayList<String>) {
        val adapter = ArrayAdapter(this@UpdateSectionActivity, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
        mMap!!.mapType = GoogleMap.MAP_TYPE_SATELLITE
        mMap!!.uiSettings.isZoomControlsEnabled = true

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
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

    override fun onClick(view: View?) {
        when (view) {
            btAddBoundary -> {
                val colorPrimary = Utils.fetchPrimaryColor(this)
                FieldPlotting().showBoundaryOptionDialog(this, colorPrimary)
            }
            btUpdateSection -> {
                if (etSectionCode.text.isNullOrEmpty() && etSectionName.text.isNullOrEmpty() && etArea.text.isNullOrEmpty() && etAddress.text.isNullOrEmpty()
                        && spinnerLocation.selectedItem == "No Data" && spinnerGarden.selectedItem == "No Data" && spinnerDivision.selectedItem == "No Data") {
                    AlertUtil.showToast(this, "Fill all fields")
                } else {
                    val requestData = HashMap<String, Any>()
                    requestData["division_id"] = viewModel.divisionArray[spinnerDivision.selectedItemPosition].division_id.toString()
                    requestData["name"] = etSectionName.text.toString()
                    requestData["total_area"] = etArea.text.toString()
                    if (points != null) {
                        requestData["section_indices"] = points
                    }
                    viewModel.updateSection(testObject!!.section_id.toString(), requestData)
                }

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null)
            when (requestCode) {
                Constants.REQUEST_BOUNDARY_GEO_PLOTING -> {
                    points = data!!.getParcelableArrayListExtra(Constants.KEY_POINTS)
                    addFieldOnMap(points)
                }
                Constants.REQUEST_BOUNDARY_GEO_FENCING -> {
                    points = data!!.getParcelableArrayListExtra(Constants.KEY_POINTS)
                    addFieldOnMap(points)
                }
            }
    }

    // Draw field in map
    private fun addFieldOnMap(points: ArrayList<LatLng>) {
        var rectOptions = PolygonOptions()
        lnMap.visibility = View.VISIBLE
        if (polyline != null) {
            polyline!!.remove()
            mMap!!.clear()
        }
        for (i in 0 until points.size) {
            field_latitude = points[i].latitude
            field_longitude = points[i].longitude
            rectOptions.add(LatLng(field_latitude, field_longitude))
        }

        if (rectOptions != null) {
            polyline = mMap!!.addPolygon(rectOptions)
        }

        val cameraPosition = CameraPosition.Builder()
                .target(LatLng(field_latitude, field_longitude))
                .zoom(15f)
                .build()
        mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }
}
