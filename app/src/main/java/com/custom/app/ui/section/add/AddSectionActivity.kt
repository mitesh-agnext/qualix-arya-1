package com.custom.app.ui.section.add

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.base.app.ui.base.BaseActivity
import com.core.app.util.AlertUtil
import com.custom.app.CustomApp
import com.custom.app.R
import com.custom.app.ui.base.ScreenState
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
import kotlinx.android.synthetic.main.activity_add_section.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.layout_progress.*
import javax.inject.Inject

class AddSectionActivity : BaseActivity(), OnMapReadyCallback, AdapterView.OnItemSelectedListener, View.OnClickListener {

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
    var rectOptions = PolygonOptions()
    var polyline: Polygon? = null
    var points = ArrayList<LatLng>()
    lateinit var dialogAddGarden: AlertDialog

    private lateinit var viewModel: SectionListVM
    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_section)
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
        btAddSection.setOnClickListener(this)
        ivAddGarden.setOnClickListener(this)
        ivAddDivision.setOnClickListener(this)

        val mapFragment =
                supportFragmentManager.findFragmentById(R.id.mapFieldDetail) as SupportMapFragment
        mapFragment.getMapAsync(this)

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
                if (viewModel.gardenArray.size > 0) {
                    for (i in 0 until viewModel.gardenArray.size) {
                        gardenNameArray.add(viewModel.gardenArray[i].name.toString())
                    }
                } else
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
                if (viewModel.divisionArray.size > 0) {
                    for (i in 0 until viewModel.divisionArray.size) {
                        divisionNameArray.add(viewModel.divisionArray[i].name.toString())
                    }
                } else
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
            }
            SectionState.GardenFailure -> {
                AlertUtil.showToast(this, viewModel.errorMessage)
            }
            SectionState.AddSectionSuccess -> {
                AlertUtil.showToast(this, "Section added successfully")
                setResult(RESULT_OK)
                finish()
            }
            SectionState.AddSectionFailure -> {
                AlertUtil.showToast(this, viewModel.errorMessage)
            }
            SectionState.AddDivisionFailure -> {
                if (dialogAddGarden != null)
                    dialogAddGarden.hide()
                AlertUtil.showToast(this, viewModel.errorMessage)
            }
            SectionState.AddDivisionSuccess -> {
                if (dialogAddGarden != null)
                    dialogAddGarden.hide()
                AlertUtil.showToast(this, "Division added successfully")
                viewModel.getLocation()
            }
            SectionState.AddGardenFailure -> {
                if (dialogAddGarden != null)
                    dialogAddGarden.hide()
                AlertUtil.showToast(this, viewModel.errorMessage)
            }
            SectionState.AddGardenSuccess -> {
                if (dialogAddGarden != null)
                    dialogAddGarden.hide()
                AlertUtil.showToast(this, "Garden added successfully")
                viewModel.getLocation()
            }
            SectionState.TokenExpire -> {
                AlertUtil.showToast(this, getString(R.string.token_expire))
                Utils.tokenExpire(this@AddSectionActivity)
            }
        }
        progress.visibility = View.GONE
    }

    private fun setSpinner(spinner: Spinner, options: ArrayList<String>) {
        val adapter = ArrayAdapter(this@AddSectionActivity, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
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
            ivAddGarden -> {
                showDialogAddGarden()
            }
            ivAddDivision -> {
                showDialogAddDivision()
            }
            btAddBoundary -> {
                val colorPrimary = Utils.fetchPrimaryColor(this)
                FieldPlotting().showBoundaryOptionDialog(this, colorPrimary)
            }
            btAddSection -> {
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
                    viewModel.addSection(requestData)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null)
            when (requestCode) {
                Constants.REQUEST_BOUNDARY_GEO_PLOTING -> {
                    points.clear()
                    points = data!!.getParcelableArrayListExtra(Constants.KEY_POINTS)!!
                    addFieldOnMap(points)
                }
                Constants.REQUEST_BOUNDARY_GEO_FENCING -> {
                    points.clear()
                    points = data!!.getParcelableArrayListExtra(Constants.KEY_POINTS)!!
                    addFieldOnMap(points)
                }
            }
    }

    // Draw field in map
    private fun addFieldOnMap(points: ArrayList<LatLng>) {

        btAddBoundary.visibility = View.GONE
        lnMap.visibility = View.VISIBLE
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

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
        mMap!!.mapType = GoogleMap.MAP_TYPE_SATELLITE
        mMap!!.uiSettings.isZoomControlsEnabled = true
    }

    private fun showDialogAddGarden() {
        var inflater: LayoutInflater = this@AddSectionActivity.layoutInflater
        var dialogView: View = inflater.inflate(R.layout.dialog_add_garden, null)
        var spLocation = dialogView.findViewById<Spinner>(R.id.spLocation)
        var etGardenName = dialogView.findViewById<EditText>(R.id.etGardenName)
        var addGarden = dialogView.findViewById<Button>(R.id.addGarden)
        var cancel = dialogView.findViewById<Button>(R.id.cancel)

        dialogAddGarden = AlertDialog.Builder(this)
                .setTitle(getString(R.string.add_garden))
                .setView(dialogView)
                .setCancelable(false)
                .show()
        if (locationNameArray.size > 0)
            setSpinner(spLocation, locationNameArray)
        // Click Listener
        cancel.setOnClickListener {
            dialogAddGarden.hide()
        }
        addGarden.setOnClickListener {
            if (etGardenName.text.toString() != null && spLocation.selectedItem != noData) {
                val requestData = HashMap<String, Any>()
                requestData["name"] = etGardenName.text.toString()
                requestData["location_id"] = viewModel.locationArray[spLocation.selectedItemPosition].location_id.toString()
                viewModel.addGarden(requestData)
            } else
                AlertUtil.showToast(this, "Fill all parameter")
        }
    }

    private fun showDialogAddDivision() {
        var inflater: LayoutInflater = this@AddSectionActivity.layoutInflater
        var dialogView: View = inflater.inflate(R.layout.dialog_add_division, null)
        var spGardenDialog = dialogView.findViewById<Spinner>(R.id.spGardenDialog)
        var etDivisionName = dialogView.findViewById<EditText>(R.id.etDivisionName)
        var addDivision = dialogView.findViewById<Button>(R.id.addDivision)
        var cancel = dialogView.findViewById<Button>(R.id.cancel)

        dialogAddGarden = AlertDialog.Builder(this)
                .setTitle(getString(R.string.add_division))
                .setView(dialogView)
                .setCancelable(false)
                .show()
        if (gardenNameArray.size > 0)
            setSpinner(spGardenDialog, gardenNameArray)
        // Click Listener
        cancel.setOnClickListener {
            dialogAddGarden.hide()
        }
        addDivision.setOnClickListener {
            if (etDivisionName.text.toString() != null && spGardenDialog.selectedItem != noData) {
                val requestData = HashMap<String, Any>()
                requestData["name"] = etDivisionName.text.toString()
                requestData["garden_id"] = viewModel.gardenArray[spGardenDialog.selectedItemPosition].garden_id.toString()
                viewModel.addDivision(requestData)
            } else
                AlertUtil.showToast(this, "Fill all parameter")
        }
    }
}
