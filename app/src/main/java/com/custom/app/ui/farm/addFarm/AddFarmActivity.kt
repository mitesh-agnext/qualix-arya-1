package com.custom.app.ui.farm.addFarm

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.base.app.ui.base.BaseActivity
import com.core.app.util.AlertUtil
import com.custom.app.R
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.farm.CropData
import com.custom.app.ui.farm.SeasonData
import com.custom.app.ui.section.add.FieldPlotting
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
import kotlinx.android.synthetic.main.activity_add_farm.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.layout_progress.*

class AddFarmActivity : BaseActivity(), View.OnClickListener, AdapterView.OnItemSelectedListener,
        OnMapReadyCallback {
    private var mMap: GoogleMap? = null
    var field_latitude = 0.0
    var field_longitude = 0.0
    var rectOptions = PolygonOptions()
    var polyline: Polygon? = null

    var seasonArray: ArrayList<SeasonData> = ArrayList()
    var seasonSpinnerArray: ArrayList<String> = ArrayList()
    private val REQUEST_SEASON = 100
    private val REQUEST_BOUNDARY_GEO_PLOTING = 101
    private val REQUEST_BOUNDARY_GEO_FENCING = 102
    var points = ArrayList<LatLng>()
    private lateinit var viewModel: AddFarmViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_farm)
        initView()
    }

    fun initView() {
        //setting toolbar
        toolbar.title = "Add Farm"
        setSupportActionBar(toolbar)
        //enabling back button
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        //Click register
        btnAddFarm.setOnClickListener(this)
        btAddBoundary.setOnClickListener(this)

        spinnerCrop.onItemSelectedListener = this
        spinnerCropVariety.onItemSelectedListener = this

        getDataReady()

        //get map ready
        val mapFragment =
                supportFragmentManager.findFragmentById(R.id.mapFieldDetail) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //getting VM instance
        viewModel = ViewModelProvider(
                this,
                AddFarmViewModelFactory(AddFarmInteractor(this))
        )[AddFarmViewModel::class.java]
        viewModel.addFarmState.observe(::getLifecycle, ::updateUI)

        //Call to get all crop api
        viewModel.getCrop(Constants.TOKEN)

    }

    /**UI  State Observer*/
    private fun updateUI(screenState: ScreenState<AddFarmState>?) {
        when (screenState) {
            ScreenState.Loading -> progress.visibility = View.VISIBLE
            is ScreenState.Render -> processLoginState(screenState.renderState)
        }
    }

    private fun processLoginState(renderState: AddFarmState) {
        progress.visibility = View.GONE
        when (renderState) {

            //Get Corp Success
            AddFarmState.GetCorpSuccess -> {
                var cropSpinnerArray = ArrayList<String>()
                if (viewModel.getCropList.value != null) {
                    for (i in 0 until viewModel.getCropList.value!!.cropData!!.size) {
                        cropSpinnerArray.add(viewModel.getCropList.value!!.cropData!![i].name.toString())
                    }
                    val adapter = ArrayAdapter<String>(
                            applicationContext,
                            R.layout.spinner_layout,
                            cropSpinnerArray
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerCrop.adapter = adapter
                }
            }

            AddFarmState.GetCropFailure -> {
                AlertUtil.showToast(this, getString(R.string.not_able_to_get_crops))
            }

            //Get Crop Variety
            AddFarmState.GetCropVarietyFailure -> {
                AlertUtil.showToast(this, getString(R.string.not_able_to_get_crops_variety))
            }
            AddFarmState.GetCropVarietySuccess -> {
                var cropVarietySpinnerArray = ArrayList<String>()
                if (viewModel.getCropVarietyList.value != null) {
                    for (i in 0 until viewModel.getCropVarietyList.value!!.cropVarietyData!!.size) {
                        cropVarietySpinnerArray.add(viewModel.getCropVarietyList.value!!.cropVarietyData!![i].name.toString())
                    }
                    val adapter = ArrayAdapter<String>(
                            applicationContext,
                            R.layout.spinner_layout,
                            cropVarietySpinnerArray
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerCropVariety.adapter = adapter
                }

            }

            //Add farm
            AddFarmState.AddFarmSuccess -> {
                AlertUtil.showToast(this, "Farm Successfully added")
                setResult(AppCompatActivity.RESULT_OK)
                finish()
            }
            AddFarmState.AddFarmFailure -> {
                AlertUtil.showToast(this, "Error to add farm")
            }

            //Expire Token
            AddFarmState.ExpireToken -> {
                //   logout(this)
            }


        }
    }


    private fun getDataReady() {
        seasonArray.add(SeasonData("kharif", "1", "Rice", "var 1", "01/05/2020", "01/10/2020"))
        seasonArray.add(SeasonData("Rabi", "2", "Wheat", "var 2", "01/10/2020", "01/05/2020"))
        for (i in 0 until seasonArray.size) {
            seasonSpinnerArray.add(seasonArray[i].season)
        }
    }


    /**Logical Method*/
    //Save data and Callback
    private fun addFarm() {
        if (tvFarmCode.text!!.isNotEmpty() && tvFarmName.text!!.isNotEmpty() && tvDistrict.text!!.isNotEmpty() && tvArea.text!!.isNotEmpty() && tvAddress.text!!.isNotEmpty()) {
            var data = HashMap<String, Any>()
            data["plot_name"] = tvFarmName.text.toString()
            data["district"] = tvDistrict.text.toString()
            data["area"] = tvArea.text.toString()
            data["address"] = tvAddress.text.toString()
            //   data["farmer_id"]=SessionClass(this).getUserId()

            if (spinnerCrop != null && spinnerCrop.selectedItem != null) {
                data["crop_id"] = viewModel.getCropList.value!!.cropData!![spinnerCrop.selectedItemPosition].cropId.toString()
            }

            if (spinnerCropVariety != null && spinnerCropVariety.selectedItem != null) {
                data["crop_verity_id"] = viewModel.getCropVarietyList.value!!.cropVarietyData!![spinnerCropVariety.selectedItemPosition].cropVerityId.toString()
            }
            if (points != null) {
                data["coordinates"] = points
            }

            spinnerCrop.selectedItem
            viewModel.addFarm(Constants.TOKEN, data)

        } else {
            AlertUtil.showToast(this, getString(R.string.fill_all_fields))
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

    /**Override method*/
    //Onclick
    override fun onClick(view: View?) {
        when (view) {

            btnAddFarm -> {
                addFarm()
            }

            btAddBoundary -> {
                val colorPrimary = Utils.fetchPrimaryColor(this)
                FieldPlotting().showBoundaryOptionDialog(this, colorPrimary)
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, p3: Long) {
        when (parent) {
            spinnerCrop -> {
                val crops = viewModel.getCropList.value!!.cropData as ArrayList<CropData>
                viewModel.getCropVariety(Constants.TOKEN, crops[pos].cropId.toString())
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_SEASON -> {
                if (data != null) {
                    val selectObject = data.getStringExtra("selectObject")
                    val gson = Gson()
                    if (selectObject != null) {
                        val type = object : TypeToken<SeasonData>() {}.type
                        var data: SeasonData = gson.fromJson(selectObject, type)
                        seasonSpinnerArray.clear()
                        seasonArray.clear()
                        seasonArray.add(data)
                        getDataReady()
                    }
                }
            }
            REQUEST_BOUNDARY_GEO_PLOTING -> {
                if (data != null) {
                    points =
                            data!!.getParcelableArrayListExtra(Constants.KEY_POINTS)!!
                    Log.e("point", "$points")
                    addFieldOnMap(points)
                }
            }
            REQUEST_BOUNDARY_GEO_FENCING -> {
                if (data != null) {
                    points =
                            data!!.getParcelableArrayListExtra(Constants.KEY_POINTS)!!
                    addFieldOnMap(points)

                }
            }
        }
    }

    //On Map Ready
    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap

        mMap!!.mapType = GoogleMap.MAP_TYPE_SATELLITE
        mMap!!.uiSettings.isZoomControlsEnabled = true

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
