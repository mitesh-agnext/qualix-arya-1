package com.custom.app.ui.senseNext.devicesAnalysis

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.data.model.senseNext.SNAnalysisRes

class SNAnalysisVM(val analysisInteractor: SNAnalysisInteractor) : ViewModel(),
        OnSNAnalysisListener {

    private val _analysisState: MutableLiveData<SNAnalysisState> = MutableLiveData()
    val analysisState: LiveData<SNAnalysisState>
        get() = _analysisState
    val analysisList = ArrayList<SNAnalysisRes>()
    var errorMsg:String=""


    //Forward
    fun getAnalysis(deviceId:String)
    {
        _analysisState.value = Loading
        analysisInteractor.getAnalysis(deviceId, this)
    }

    //Back
    override fun onAnalysisSuccess(body: ArrayList<SNAnalysisRes>) {
        analysisList.addAll(body)
        _analysisState.value = List
    }

    override fun onAnalysisFailure(msg: String) {
        errorMsg=msg
        _analysisState.value = Error
    }

    override fun onToken() {
        _analysisState.value = Token
    }
}

class SNAnalysisVMFactory(private val analysisInteractor: SNAnalysisInteractor) :
        ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SNAnalysisVM(analysisInteractor) as T
    }
}

