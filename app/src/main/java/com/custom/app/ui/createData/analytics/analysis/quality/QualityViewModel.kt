package com.custom.app.ui.createData.analytics.analysis.quality

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.createData.analytics.analyticsScreen.AnalyticsInteractor
import com.custom.app.ui.createData.analytics.analyticsScreen.QualityInteractorCallback

class QualityViewModel(val QualityInteractor: AnalyticsInteractor) : ViewModel(),
        QualityInteractorCallback {

    private val _qualityState: MutableLiveData<ScreenState<QualityState>> = MutableLiveData()
    val qualityState: LiveData<ScreenState<QualityState>>
        get() = _qualityState

    private val _Quality: MutableLiveData<ArrayList<QualityRes>> = MutableLiveData()
    val quality: LiveData<ArrayList<QualityRes>>
        get() = _Quality

    private val _Analytics: MutableLiveData<ArrayList<AnalyticsRes>> = MutableLiveData()
    val analytics: LiveData<ArrayList<AnalyticsRes>>
        get() = _Analytics

    private val _QualityGrade: MutableLiveData<ArrayList<QualityGradeRes>> = MutableLiveData()
    val qualityGrade: LiveData<ArrayList<QualityGradeRes>>
        get() = _QualityGrade

    private val _QualityOverTime: MutableLiveData<ArrayList<QualityOverTimeRes>> = MutableLiveData()
    val qualityOverTime: LiveData<ArrayList<QualityOverTimeRes>>
        get() = _QualityOverTime

    private val _QualityRange: MutableLiveData<QualityRangeRes> = MutableLiveData()
    val qualityRange: LiveData<QualityRangeRes>
        get() = _QualityRange

    var errorMessage: String = "Error"

    fun onAnalytics(commodityId: String) {
        _qualityState.value = ScreenState.Loading
        QualityInteractor.analyticsName(this, commodityId)
    }

    fun onGetQuality(customerId: String, commodityId: String, analysis_name: String, ccId: String, date_to: String, date_from: String, regionId: String) {
        _qualityState.value = ScreenState.Loading
        QualityInteractor.allQuality(this, customerId, commodityId, analysis_name, ccId, date_to, date_from, regionId)
    }

    fun onGetQualityGrade(commodityId: String) {
        _qualityState.value = ScreenState.Loading
        QualityInteractor.qualityGrade(this, commodityId)
    }

    fun onGetQualityOverTime(customerId: String, commodityId: String, analysis_name: String, ccId: String, date_to: String, date_from: String, regionId: String) {
        _qualityState.value = ScreenState.Loading
        QualityInteractor.qualityOverTime(this, customerId, commodityId, analysis_name, ccId, date_to, date_from, regionId)
    }

    fun onGetQualityRange(commodityId: String, cc_Id: String) {
        _qualityState.value = ScreenState.Loading
        QualityInteractor.qualityRange(this, commodityId, cc_Id)
    }

    override fun allQualityApiSuccess(body: ArrayList<QualityRes>) {
        _Quality.value = body
        _qualityState.value = ScreenState.Render(QualityState.QualityCountSuccess)
    }

    override fun allQualityApiError(msg: String) {
        errorMessage = msg
        _qualityState.value = ScreenState.Render(QualityState.QualityCountFailure)
    }

    override fun qualityGradeSuccess(body: ArrayList<QualityGradeRes>) {
        _QualityGrade.value = body
        _qualityState.value = ScreenState.Render(QualityState.QualityGradeSuccess)
    }

    override fun qualityGradeError(msg: String) {
        errorMessage = msg
        _qualityState.value = ScreenState.Render(QualityState.QualityGradeFailure)
    }

    override fun analyticsSuccess(body: ArrayList<AnalyticsRes>) {
        _Analytics.value = body
        _qualityState.value = ScreenState.Render(QualityState.AnalyticsSuccess)
    }

    override fun analyticsError(msg: String) {
        errorMessage = msg
        _qualityState.value = ScreenState.Render(QualityState.AnalyticsFailure)
    }

    override fun qualityOverTimeSuccess(body: ArrayList<QualityOverTimeRes>) {
        _QualityOverTime.value = body
        _qualityState.value = ScreenState.Render(QualityState.QualityOverTimeSuccess)
    }

    override fun qualityOverTimeError(msg: String) {
        errorMessage = msg
        _qualityState.value = ScreenState.Render(QualityState.QualityOverTimeFailure)
    }

    override fun qualityRangeSuccess(body: QualityRangeRes) {
        _QualityRange.value = body
        _qualityState.value = ScreenState.Render(QualityState.QualityRangeSuccess)
    }

    override fun qualityRangeError(msg: String) {
        errorMessage = msg
        _qualityState.value = ScreenState.Render(QualityState.QualityRangeFailure)
    }
}

class QualityViewModelFactory(
        private val qualityInteractor: AnalyticsInteractor) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return QualityViewModel(qualityInteractor) as T

    }
}