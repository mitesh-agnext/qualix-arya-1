package com.custom.app.ui.sampleBLE

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.data.model.bleScan.CommodityResponse
import com.custom.app.data.model.bleScan.LocationResponse
import com.custom.app.ui.base.ScreenState

class SampleBleViewModel(val sampleBleInteractor: SampleBleInteractor) : ViewModel(),
        SampleBleInteractor.OnSampleBleInteractorListener
{
    private val _sampleBleStateState: MutableLiveData<SampleBleState> = MutableLiveData()
    val sampleBleStateState: LiveData<SampleBleState>
        get() = _sampleBleStateState

    val locationArray= ArrayList<LocationResponse>()
    val commodityArray= ArrayList<CommodityResponse>()

    //Forward
    fun getLocation()
    {
        _sampleBleStateState.value =SampleBleState.loading
        sampleBleInteractor.getLocation(this)
    }

    fun getCommodity()
    {
        _sampleBleStateState.value =SampleBleState.loading
        sampleBleInteractor.getCommodity(this)
    }
    override fun onLocationSuccess(locationList: ArrayList<LocationResponse>) {
        locationList.clear()
        locationList.addAll(locationList!!)
        _sampleBleStateState.value = SampleBleState.locationSuccess
    }

    //Backward
    override fun onLocationFailure() {
        _sampleBleStateState.value = SampleBleState.locationFailure

    }

    override fun onCommoditySuccess(commodityList: ArrayList<CommodityResponse>) {
        commodityList.clear()
        commodityArray.addAll(commodityList)
        _sampleBleStateState.value = SampleBleState.commoditySuccess

    }

    override fun onCommodityFailure() {
        _sampleBleStateState.value = SampleBleState.commodityFailure

    }

    class SampleBleViewModelFactory(private val sampleBleInteractor: SampleBleInteractor) :
            ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SampleBleViewModel(sampleBleInteractor) as T
        }
    }

}