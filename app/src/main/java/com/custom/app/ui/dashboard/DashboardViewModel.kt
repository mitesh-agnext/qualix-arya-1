package com.custom.app.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.ui.base.ScreenState

class DashboardViewModel(val dashboardInteractor: DashboardInteractor) : ViewModel(), CenterDetailInteractorCallback {

    private val _DashboardFragment_listState: MutableLiveData<ScreenState<DashboardFragmentState>> = MutableLiveData()
    val dashboardFragmentState: LiveData<ScreenState<DashboardFragmentState>>
        get() = _DashboardFragment_listState

    private val _centerList: MutableLiveData<CenterData> = MutableLiveData()
    val centerList: LiveData<CenterData>
        get() = _centerList

    var errorMessage: String = "Error"

    fun onGetCenterData(query: Map<String, String>) {
        _DashboardFragment_listState.value = ScreenState.Loading
        dashboardInteractor.centerData(this, query)
    }

    override fun centerDetailSucess(body: CenterData) {
    _centerList.value = body
    _DashboardFragment_listState.value = ScreenState.Render(DashboardFragmentState.ListCenterSuccess)

    }

    override fun centerDetailError(msg: String) {
        errorMessage = msg
        _DashboardFragment_listState.value = ScreenState.Render(DashboardFragmentState.ListCenterFailure)
    }
}

class DashboardFragmentViewModelFactory(private val dashboardInteractor: DashboardInteractor) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DashboardViewModel(dashboardInteractor) as T
    }
}