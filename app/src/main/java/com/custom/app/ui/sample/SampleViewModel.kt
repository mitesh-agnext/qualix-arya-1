package com.custom.app.ui.sample

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.data.model.farmer.upload.FarmerItem
import com.custom.app.data.model.section.LocationItem
import com.specx.scan.data.model.sample.SampleItem

class DataFragmentViewModel(val sampleInteractor: SampleInteractor) : ViewModel(), SampleInteractor.DataCallback {

    val sampleStateLiveData: MutableLiveData<SampleState> = MutableLiveData()

    private val scanDetailLiveData: MutableLiveData<ScanDetailRes> = MutableLiveData()
    val scanDetail: LiveData<ScanDetailRes> get() = scanDetailLiveData

    private val locationsLiveData: MutableLiveData<List<LocationItem>> = MutableLiveData()
    val locations: LiveData<List<LocationItem>> get() = locationsLiveData

    fun fetchLocations() {
        sampleStateLiveData.value = Loading
//        sampleInteractor.fetchLocation(this)
    }

    override fun onLocationSuccess(locations: List<LocationItem>) {
        locationsLiveData.value = locations
        sampleStateLiveData.value = Locations(locations)
    }

    fun fetchScanDetail(scanId: String, batchId: String, deviceId: Int) {
        sampleStateLiveData.value = Loading
        sampleInteractor.fetchAndSaveScanDetail(scanId, batchId, deviceId, this)
    }

    fun updateScanDetail(batchId: String, farmer: FarmerItem, sample: SampleItem, scanDetail: ScanDetailRes) {
        sampleStateLiveData.value = Loading
        sampleInteractor.updateResultInDb(batchId, farmer, sample, scanDetail, this)
    }

    override fun onFetchScanDetailSuccess(scanDetail: ScanDetailRes) {
        scanDetailLiveData.value = scanDetail
        sampleStateLiveData.value = ScanDetail(scanDetail)
    }

    override fun onSaveScanDetailSuccess() {
        sampleStateLiveData.value = ShowResult
    }

    override fun onError(msg: String?) {
        sampleStateLiveData.value = Error(msg)
    }
}

class DataFragmentViewModelFactory(val sampleInteractor: SampleInteractor) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DataFragmentViewModel(sampleInteractor) as T
    }
}