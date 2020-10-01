package com.custom.app.ui.scan.list.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import okhttp3.ResponseBody

class ScanDetailVM(val scanDetailInteractor: ScanDetailInteractor) : ViewModel(), ScanDetailListener {

    val _scanDetailState: MutableLiveData<ScanDetailState> = MutableLiveData()
    val scanDetailState: LiveData<ScanDetailState>
        get() = _scanDetailState

    fun setApproval(scanId: Int, status: Int) {
        _scanDetailState.value = Loading
        scanDetailInteractor.approveReject(scanId, status, this)
    }

    override fun approvalSuccess(body: ResponseBody) {
        _scanDetailState.value = ApprovalSuccess
    }

    override fun approvalFailure(message: String) {
        _scanDetailState.value = Error(message)
        _scanDetailState.value = ApprovalFailure
    }

    override fun tokenExpire() {
        _scanDetailState.value = Token
    }

}

class ScanDetailViewModelFactory(private val scanDetailInteractor: ScanDetailInteractor) :
        ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ScanDetailVM(scanDetailInteractor) as T
    }
}