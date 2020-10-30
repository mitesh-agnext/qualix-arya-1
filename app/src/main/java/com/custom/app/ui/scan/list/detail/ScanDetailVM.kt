package com.custom.app.ui.scan.list.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.data.model.scanhistory.ScanData
import com.custom.app.ui.sample.ScanDetailRes
import okhttp3.ResponseBody

class ScanDetailVM(val scanDetailInteractor: ScanDetailInteractor) : ViewModel(), ScanDetailListener {

    val _scanDetailState: MutableLiveData<ScanDetailState> = MutableLiveData()
    val scanDetailState: LiveData<ScanDetailState>
        get() = _scanDetailState
    var scanDetail = ScanData()

    fun setApproval(scanId: Int, status: Int, message: String) {
        _scanDetailState.value = Loading
        scanDetailInteractor.approveReject(scanId, status, message,this)
    }

    fun getScanDetail(scanId: String) {
        _scanDetailState.value = Loading
        scanDetailInteractor.fetchScanDetail(scanId, this)

    }

    override fun approvalSuccess(body: ResponseBody) {
        _scanDetailState.value = ApprovalSuccess
    }

    override fun approvalFailure(message: String) {
        _scanDetailState.value = Error(message)
        _scanDetailState.value = ApprovalFailure
    }

    override fun fetchScanDetailSuccess(response: ScanData) {
        scanDetail = response
        _scanDetailState.value = FetchScanSuccess
    }

    override fun fetchScanDetailFailure(error: String) {
        _scanDetailState.value = FetchScanFailure
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