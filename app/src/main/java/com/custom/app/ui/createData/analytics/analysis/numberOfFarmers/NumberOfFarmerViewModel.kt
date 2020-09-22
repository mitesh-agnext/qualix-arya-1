package com.custom.app.ui.createData.analytics.analysis.numberOfFarmers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.createData.analytics.analyticsScreen.AnalyticsInteractor
import com.custom.app.ui.createData.analytics.analyticsScreen.NumberOfFarmerInteractorCallback

class NumberOfFarmerViewModel(val NumberOfFarmerInteractor: AnalyticsInteractor) : ViewModel(),
        NumberOfFarmerInteractorCallback {

    private val _numberOfFarmerState: MutableLiveData<ScreenState<NumberOfFarmerState>> = MutableLiveData()
    val numberOfFarmerState: LiveData<ScreenState<NumberOfFarmerState>>
        get() = _numberOfFarmerState

    private val _NumberOfFarmer: MutableLiveData<ArrayList<NumberOfFarmerRes>> = MutableLiveData()
    val numberOfFarmer: LiveData<ArrayList<NumberOfFarmerRes>>
        get() = _NumberOfFarmer

    private val _FarmerData: MutableLiveData<FarmerDataRes> = MutableLiveData()
    val farmerData: LiveData<FarmerDataRes>
        get() = _FarmerData
    var errorMessage: String = "Error"

    fun onNumberOfFarmer(customerId: String, commodityId: String, ccId: String, dateFrom: String, dateTo: String, regionId: String) {
        _numberOfFarmerState.value = ScreenState.Loading
        NumberOfFarmerInteractor.farmerList(this, customerId, commodityId, ccId, dateFrom, dateTo, regionId)
    }

    fun onFarmerData(customerId: String, commodityId: String, ccId: String, dateFrom: String, dateTo: String, regionId: String) {
        _numberOfFarmerState.value = ScreenState.Loading
        NumberOfFarmerInteractor.farmerData(this, customerId, commodityId, ccId, dateFrom, dateTo, regionId)
    }

    override fun farmerListSuccess(body: ArrayList<NumberOfFarmerRes>) {
        _NumberOfFarmer.value = body
        _numberOfFarmerState.value = ScreenState.Render(NumberOfFarmerState.NumberOfFarmerSuccess)
    }

    override fun farmerListError(msg: String) {
        errorMessage = msg
        _numberOfFarmerState.value = ScreenState.Render(NumberOfFarmerState.NumberOfFarmerFailure)
    }

    override fun farmerDataSuccess(body: FarmerDataRes) {
        _FarmerData.value = body
        _numberOfFarmerState.value = ScreenState.Render(NumberOfFarmerState.FarmerDataSuccess)
    }

    override fun farmerDataError(msg: String) {
        errorMessage = msg
        _numberOfFarmerState.value = ScreenState.Render(NumberOfFarmerState.FarmerDataFailure)
    }
}

class NumberOfFarmerViewModelFactory(private val numberOfFarmerInteractor: AnalyticsInteractor) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NumberOfFarmerViewModel(numberOfFarmerInteractor) as T
    }
}