package com.custom.app.ui.farm.updateFarm

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider

import com.base.app.ui.base.BaseActivity
import com.core.app.util.AlertUtil
import com.custom.app.R
import com.custom.app.ui.base.ScreenState
import com.custom.app.util.Constants
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import kotlinx.android.synthetic.main.activity_update_field.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.layout_progress.*

class UpdateFarmActivity : BaseActivity(), OnMapReadyCallback {
    private lateinit var viewModel: UpdateFarmViewModel
    var rectOptions = PolygonOptions()
    private var mMap: GoogleMap? = null
    var polyline: Polygon? = null
    var field_latitude = 0.0
    var field_longitude = 0.0
    var latlngList = ArrayList<LatLng>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_field)
        toolbar.title = "Update Farm"
        setSupportActionBar(toolbar)
        //enabling back button
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        //Map register
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapUpdateFieldDetail) as SupportMapFragment
        mapFragment.getMapAsync(this)

        viewModel = ViewModelProvider(
            this,
            UpdateFarmViewModelFactory(UpdateFramInteractor(this))
        )[UpdateFarmViewModel::class.java]
        viewModel.updateFarmState.observe(::getLifecycle, ::updateUI)
        val farmId: String = intent.extras!!.getString("farmId")!!

        viewModel.getParticularFarmVM(Constants.TOKEN, farmId)


    }

    /**UI  State Observer*/
    private fun updateUI(screenState: ScreenState<UpdateFarmState>?) {
        when (screenState) {
            ScreenState.Loading -> progress.visibility = View.VISIBLE
            is ScreenState.Render -> processLoginState(screenState.renderState)
        }
    }

    private fun processLoginState(renderState: UpdateFarmState) {
        progress.visibility = View.GONE
        when (renderState) {
            UpdateFarmState.GetParticularFarmFailure -> {
                AlertUtil.showToast(this, "Error to get the farm detail")
            }
            UpdateFarmState.GetParticularFarmSuccess -> {
                tvFarmCode.setText(viewModel.particularFarm.value!!.farmId)
                tvFarmName.setText(viewModel.particularFarm.value!!.farmerName)
                tvDistrict.setText(viewModel.particularFarm.value!!.district)
                tvArea.setText(viewModel.particularFarm.value!!.area)
                tvAddress.setText(viewModel.particularFarm.value!!.address)
                val farmIndics = viewModel.particularFarm.value!!.farmIndics
                if (farmIndics!!.size > 0) {
                    cdFarmMap.visibility = View.VISIBLE
                    for (i in 0 until farmIndics.size) {
                        latlngList.add(
                            LatLng(
                                farmIndics[i].latitude!!.toDouble(),
                                farmIndics[i].longitude!!.toDouble()
                            )
                        )
                    }
                    drawField()
                } else {
                    cdFarmMap.visibility = View.GONE
                }
            }

        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap

        mMap!!.mapType = GoogleMap.MAP_TYPE_SATELLITE
        mMap!!.uiSettings.isZoomControlsEnabled = true

        if (latlngList.size > 0)
            drawField()
    }

    private fun drawField() {
        for (i in 0 until latlngList.size) {

            field_latitude = latlngList[i].latitude.toDouble()
            field_longitude = latlngList[i].longitude.toDouble()
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

    //Back
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
