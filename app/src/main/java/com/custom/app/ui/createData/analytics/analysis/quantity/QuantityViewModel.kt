package com.custom.app.ui.createData.analytics.analysis.quantity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.createData.analytics.analyticsScreen.AnalyticsInteractor
import com.custom.app.ui.createData.analytics.analyticsScreen.QuantityInteractorCallback

class QuantityViewModel(val QuantityInteractor: AnalyticsInteractor) : ViewModel(), QuantityInteractorCallback {

    private val _QuantityState: MutableLiveData<ScreenState<QuantityState>> = MutableLiveData()
    val quantityState: LiveData<ScreenState<QuantityState>>
        get() = _QuantityState

    private val _Quantity: MutableLiveData<QuantityRes> = MutableLiveData()
    val quantity: LiveData<QuantityRes>
        get() = _Quantity

    private val _Collections: MutableLiveData<ArrayList<CollectionByCenterRes>> = MutableLiveData()
    val collection: LiveData<ArrayList<CollectionByCenterRes>>
        get() = _Collections

    private val _CollectionOverTime: MutableLiveData<ArrayList<CollectionOverTimeRes>> = MutableLiveData()
    val collectionOverTime: LiveData<ArrayList<CollectionOverTimeRes>>
        get() = _CollectionOverTime

    private val _CollectionCenterRegion: MutableLiveData<ArrayList<CollectionCenterRegionRes>> = MutableLiveData()
    val collectionCenterRegion: LiveData<ArrayList<CollectionCenterRegionRes>>
        get() = _CollectionCenterRegion

    private val _CollectionWeeklyMonthly: MutableLiveData<CollectionWeeklyMonthlyRes> = MutableLiveData()
    val collectionWeeklyMonthly: LiveData<CollectionWeeklyMonthlyRes>
        get() = _CollectionWeeklyMonthly

    var errorMessage: String = "Error"

    fun onGetQuantity(customerId: String, commodityId: String, ccId: String, date_to: String, date_from: String, regionId: String) {
        _QuantityState.value = ScreenState.Loading
        QuantityInteractor.allQuantity(this, customerId, commodityId, ccId, date_to, date_from, regionId)
    }

    fun onGetCollection(customerId: String, commodityId: String, ccId: String, date_to: String, date_from: String, regionId: String) {
        _QuantityState.value = ScreenState.Loading
        QuantityInteractor.collectionByCenter(this, customerId, commodityId, ccId, date_to, date_from, regionId)
    }

    fun onGetCollectionOverTime(customerId: String, commodityId: String, ccId: String, date_to: String, date_from: String, regionId: String) {
        _QuantityState.value = ScreenState.Loading
        QuantityInteractor.collectionOverTime(this, customerId, commodityId, ccId, date_to, date_from, regionId)
    }

    fun onGetCollectionCenterRegion(customerId: String, commodityId: String, ccId: String, date_to: String, date_from: String, regionId: String) {
        _QuantityState.value = ScreenState.Loading
        QuantityInteractor.collectionByCenterRegion(this, customerId, commodityId, ccId, date_to, date_from, regionId)
    }

    fun onGetCollectionWeeklyMonthly(customerId: String, commodityId: String, ccId: String, date_to: String, date_from: String, regionId: String) {
        _QuantityState.value = ScreenState.Loading
        QuantityInteractor.collectionWeeklyMonthly(this, customerId, commodityId, ccId, date_to, date_from, regionId)
    }

    override fun allQuantityApiSuccess(body: QuantityRes) {
        _Quantity.value = body
        _QuantityState.value = ScreenState.Render(QuantityState.QuantityCountSuccess)
    }

    override fun allQuantityApiError(msg: String) {
        errorMessage = msg
        _QuantityState.value = ScreenState.Render(QuantityState.QuantityCountFailure)
    }

    override fun collectionByCenterSuccess(body: ArrayList<CollectionByCenterRes>) {
        if (body.size > 0) {
            _Collections.value = body
            _QuantityState.value = ScreenState.Render(QuantityState.QuantityCollectionsSuccess)
        }
    }

    override fun collectionByCenterError(msg: String) {
        errorMessage = msg
        _QuantityState.value = ScreenState.Render(QuantityState.QuantityCollectionsFailure)
    }

    override fun collectionOverTimeSuccess(body: ArrayList<CollectionOverTimeRes>) {
        _CollectionOverTime.value = body
        _QuantityState.value = ScreenState.Render(QuantityState.CollectionOverTimeSuccess)
    }

    override fun collectionOverTimeError(msg: String) {
        errorMessage = msg
        _QuantityState.value = ScreenState.Render(QuantityState.CollectionOverTimeFailure)
    }

    override fun collectionCenterRegionSuccess(body: ArrayList<CollectionCenterRegionRes>) {
        _CollectionCenterRegion.value = body
        _QuantityState.value = ScreenState.Render(QuantityState.CollectionCenterRegionSuccess)
    }

    override fun collectionCenterRegionError(msg: String) {
        errorMessage = msg
        _QuantityState.value = ScreenState.Render(QuantityState.CollectionCenterRegionFailure)
    }

    override fun collectionWeeklyMonthlySuccess(body: CollectionWeeklyMonthlyRes) {
        _CollectionWeeklyMonthly.value = body
        _QuantityState.value = ScreenState.Render(QuantityState.CollectionWeeklyMonthlySuccess)
    }

    override fun collectionWeeklyMonthlyError(msg: String) {
        errorMessage = msg
        _QuantityState.value = ScreenState.Render(QuantityState.CollectionWeeklyMonthlyFailure)
    }
}

class QuantityViewModelFactory(private val QuantityInteractor: AnalyticsInteractor) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return QuantityViewModel(QuantityInteractor) as T
    }
}