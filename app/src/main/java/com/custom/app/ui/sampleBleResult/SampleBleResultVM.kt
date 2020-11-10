package com.custom.app.ui.sampleBleResult

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.ui.base.ScreenState

class SampleBleResultVM (val sampleBleResultInteractor: SampleBleResultInteractor) : ViewModel(),
        SampleBleResultInteractor.OnSampleBleResultListener {

    /**Observable Data*/

    private val _sampleBleResultState: MutableLiveData<SampleBleResultState> = MutableLiveData()
    val sampleBleResultState: LiveData<SampleBleResultState>
        get() = _sampleBleResultState

    /**Forward Flow*/
    fun postBleScan(request: HashMap<String, Any>)
    {
        _sampleBleResultState.value =SampleBleResultState.loading
        sampleBleResultInteractor.postBleScan(request,this)
    }

    /**Backward flow*/
    override fun onPostScanSuccess() {
        _sampleBleResultState.value =SampleBleResultState.postScanSuccess
    }

    override fun onPostScanFailure() {
        _sampleBleResultState.value =SampleBleResultState.postScanFailure
    }

    class SampleBleResultVMFactory(private val sampleBleResultInteractor: SampleBleResultInteractor) :
            ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SampleBleResultVM(sampleBleResultInteractor) as T
        }
    }

}
