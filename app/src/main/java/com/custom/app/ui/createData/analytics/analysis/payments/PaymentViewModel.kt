package com.custom.app.ui.createData.paymentChart.analysis.payments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.createData.analytics.analysis.payments.PaymentChartRes
import com.custom.app.ui.createData.analytics.analysis.payments.PaymentListRes
import com.custom.app.ui.createData.analytics.analysis.payments.PaymentRes
import com.custom.app.ui.createData.analytics.analysis.payments.PaymentState
import com.custom.app.ui.createData.analytics.analyticsScreen.AnalyticsInteractor
import com.custom.app.ui.createData.analytics.analyticsScreen.PaymentInteractorCallback

class PaymentViewModel(val PaymentInteractor: AnalyticsInteractor) : ViewModel(),
        PaymentInteractorCallback {

    private val _paymentState: MutableLiveData<ScreenState<PaymentState>> = MutableLiveData()
    val paymentState: LiveData<ScreenState<PaymentState>>
        get() = _paymentState

    private val _Payment: MutableLiveData<ArrayList<PaymentRes>> = MutableLiveData()
    val payment: LiveData<ArrayList<PaymentRes>>
        get() = _Payment

    private val _PaymentChart: MutableLiveData<ArrayList<PaymentChartRes>> = MutableLiveData()
    val paymentChart: LiveData<ArrayList<PaymentChartRes>>
        get() = _PaymentChart

    private val _PaymentList: MutableLiveData<ArrayList<PaymentListRes>> = MutableLiveData()
    val paymentList: LiveData<ArrayList<PaymentListRes>>
        get() = _PaymentList

    var errorMessage: String = "Error"

    fun onPaymentChart(customerId: String, commodityId: String, cc_Id: String, date_from: String, date_to: String, region_id: String) {
        _paymentState.value = ScreenState.Loading
        PaymentInteractor.paymentChart(this, customerId, commodityId, cc_Id, date_from, date_to, region_id)
    }

    fun onGetPayment(customerId: String, commodityId: String, cc_Id: String, date_from: String, date_to: String, region_id: String) {
        _paymentState.value = ScreenState.Loading
        PaymentInteractor.allPayment(this, customerId, commodityId, cc_Id, date_from, date_to, region_id)
    }

    fun onGetPaymentList(customerId: String, commodityId: String, cc_Id: String, date_from: String, date_to: String, region_id: String) {
        _paymentState.value = ScreenState.Loading
        PaymentInteractor.paymentList(this, customerId, commodityId, cc_Id, date_from, date_to, region_id)
    }

    override fun paymentSuccess(body: ArrayList<PaymentRes>) {
        _Payment.value = body
        _paymentState.value = ScreenState.Render(PaymentState.PaymentOverTimeSuccess)
    }

    override fun paymentError(msg: String) {
        errorMessage = msg
        _paymentState.value = ScreenState.Render(PaymentState.PaymentOverTimeFailure)
    }

    override fun paymentChartSuccess(body: ArrayList<PaymentChartRes>) {
        _PaymentChart.value = body
        _paymentState.value = ScreenState.Render(PaymentState.PaymentChartSuccess)
    }

    override fun paymentChartError(msg: String) {
        errorMessage = msg
        _paymentState.value = ScreenState.Render(PaymentState.PaymentChartFailure)
    }

    override fun paymentListSuccess(body: ArrayList<PaymentListRes>) {
        _PaymentList.value = body
        _paymentState.value = ScreenState.Render(PaymentState.PaymentListSuccess)
    }

    override fun paymentListError(msg: String) {
        errorMessage = msg
        _paymentState.value = ScreenState.Render(PaymentState.PaymentListFailure)
    }
}

class PaymentViewModelFactory(
        private val paymentInteractor: AnalyticsInteractor) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PaymentViewModel(paymentInteractor) as T

    }
}