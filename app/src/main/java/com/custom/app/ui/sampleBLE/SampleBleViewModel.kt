package com.custom.app.ui.sampleBLE

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.data.model.bleScan.LocationResponse
import com.custom.app.data.model.bleScan.response.*
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.sampleBLE.SampleBleState.locationSuccess
import com.custom.app.ui.sampleBleResult.SampleBleResultInteractor
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SampleBleViewModel(val sampleBleInteractor: SampleBleInteractor) : ViewModel(),
        SampleBleInteractor.OnSampleBleInteractorListener {
    private val _sampleBleStateState: MutableLiveData<ScreenState<SampleBleState>> = MutableLiveData()
    val sampleBleStateState: LiveData<ScreenState<SampleBleState>>
        get() = _sampleBleStateState

    val locationArray = ArrayList<LocationResponse>()
    val commodityArray = ArrayList<Commodity>()
    val varietyArray = ArrayList<Variety>()

    //Forward
    fun getLocation() {
        _sampleBleStateState.value = ScreenState.Render(SampleBleState.loading)
        sampleBleInteractor.getLocation(this)
    }

    fun getCommodity() {
        _sampleBleStateState.value = ScreenState.Render(SampleBleState.loading)
        sampleBleInteractor.getCommodity(this)
    }

    override fun onLocationSuccess(list: ArrayList<LocationResponse>) {
        locationArray.clear()
        locationArray.addAll(list!!)
        _sampleBleStateState.value = ScreenState.Render(locationSuccess)
    }

    //Backward
    override fun onLocationFailure() {
        _sampleBleStateState.value = ScreenState.Render(SampleBleState.locationFailure)

    }

    override fun onCommoditySuccess(commodityList: CommodityVarietyResponse) {
        commodityArray.clear()
        varietyArray.clear()

        for (data in commodityList.commodityDetails[0].commodities) {
            commodityArray.add(data)
            for (variety in data.varieties) {
                varietyArray.add(variety)
            }
        }
        _sampleBleStateState.value = ScreenState.Render(SampleBleState.commoditySuccess)
    }

    override fun onCommodityFailure() {
        _sampleBleStateState.value = ScreenState.Render(SampleBleState.commodityFailure)
    }

    class SampleBleViewModelFactory(private val sampleBleInteractor: SampleBleInteractor) :
            ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SampleBleViewModel(sampleBleInteractor) as T
        }
    }

    fun insertSensorDataToServer(sensorData:SensorData,repository: SensorDataRepository){
        sampleBleInteractor.postBleScan(sensorData,repository)
    }
}