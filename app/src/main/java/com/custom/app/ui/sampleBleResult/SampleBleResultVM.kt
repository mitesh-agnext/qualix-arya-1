package com.custom.app.ui.sampleBleResult

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.ui.base.ScreenState

class SampleBleResultVM (val sampleBleResultInteractor: SampleBleResultInteractor) : ViewModel(),
        SampleBleResultInteractor.OnSampleBleResultListener {

    /**Observable Data*/

    private val _sampleBleResultState: MutableLiveData<ScreenState<SampleBleResultState>> = MutableLiveData()
    val sampleBleResultState: LiveData<ScreenState<SampleBleResultState>>
        get() = _sampleBleResultState

    /**Forward Flow*/
    fun postBleScan(request: HashMap<String, String>)
    {
        _sampleBleResultState.value =ScreenState.Render(SampleBleResultState.loading)
        sampleBleResultInteractor.postBleScan(request,this)
    }

    /**Backward flow*/
    override fun onPostScanSuccess() {
        _sampleBleResultState.value =ScreenState.Render(SampleBleResultState.postScanSuccess)
    }

    var message =  "error"
    override fun onPostScanFailure(msg: String) {
        message = msg
        _sampleBleResultState.value =ScreenState.Render(SampleBleResultState.postScanFailure)
    }

    class SampleBleResultVMFactory(private val sampleBleResultInteractor: SampleBleResultInteractor) :
            ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SampleBleResultVM(sampleBleResultInteractor) as T
        }
    }

}
