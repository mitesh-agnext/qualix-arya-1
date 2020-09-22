package com.custom.app.ui.payment.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.data.model.payment.PaymentHistoryRes
import com.custom.app.ui.createData.flcScan.season.create.CommodityRes
import com.custom.app.ui.createData.region.site.create.RegionRes
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerListListener
import com.custom.app.ui.customer.list.CustomerRes
import com.custom.app.ui.device.assign.InstallationCenterRes

class PaymentHistoryVM(val payHistoryInteractor: PaymentHistoryInteractor, val customerInteractor: CustomerInteractor) : ViewModel(), PaymentHistoryListener, CustomerListListener {

    val _paymentHistoryState: MutableLiveData<PaymentHistoryState> = MutableLiveData()

    val paymentHistoryStateLiveData: LiveData<PaymentHistoryState>
        get() = _paymentHistoryState
    val customerList = ArrayList<CustomerRes>()
    val paymentList = ArrayList<PaymentHistoryRes>()
    val commodityList = ArrayList<CommodityRes>()
    val installationCenterList = ArrayList<InstallationCenterRes>()
    val regionList = ArrayList<RegionRes>()

    fun getCustomerList() {
        _paymentHistoryState.value = Loading
        customerInteractor.list(this)
    }

    fun getPaymentHistory(data: MutableMap<String, String>) {
        payHistoryInteractor.getPaymentHistoryIn(data, this)
    }

    fun getCommodityListVm(customerId: Int) {
        _paymentHistoryState.value = Loading
        payHistoryInteractor.getCommodity(this, customerId)
    }

    fun getInstallationCentersVM(customerId: Int) {
        _paymentHistoryState.value = Loading
        payHistoryInteractor.getInstallationCenters(this, customerId)
    }

    fun getRegionVM(customerId: Int) {
        _paymentHistoryState.value = Loading
        payHistoryInteractor.getRegions(customerId, this)
    }


    override fun paymentHistorySuccess(body: ArrayList<PaymentHistoryRes>?) {
        paymentList.clear()
        paymentList.addAll(body!!)
        _paymentHistoryState.value = List(body!!)
    }

    override fun paymentHistoryFailure(massage: String) {
        _paymentHistoryState.value = Error(massage)
    }

    override fun tokenExpire() {
    }

    override fun commoditySuccess(body: ArrayList<CommodityRes>) {
        commodityList.clear()
        if (body.size > 0) {
            commodityList.addAll(body)
        }
        _paymentHistoryState.value = CommodityList
    }

    override fun commodityFailure(msg: String) {
        _paymentHistoryState.value = CommodityError
    }

    override fun installationCentersSuccess(body: ArrayList<InstallationCenterRes>) {
        installationCenterList.clear()
        if (body.size > 0) {
            installationCenterList.addAll(body)
        }
        _paymentHistoryState.value = InstallationCentersSuccess
    }

    override fun installationCentersFailure(string: String) {
        _paymentHistoryState.value = InstallationCentersFailure
    }

    override fun regionSuccess(body: ArrayList<RegionRes>) {
        regionList.clear()
        if (body.size > 0) {
            regionList.addAll(body)
        }
        _paymentHistoryState.value = RegionSuccess
    }

    override fun regionFailure(string: String) {
    }

    override fun onCustomerListSuccess(list: ArrayList<CustomerRes>) {
        customerList.clear()
        customerList.addAll(list)
        _paymentHistoryState.value = CustomerList
    }

    override fun onCustomerListFailure(msg: String) {
        customerList.clear()
        _paymentHistoryState.value = CustomerError
    }

    override fun onTokenExpire() {
        _paymentHistoryState.value = Token
    }

}

class PaymentHistoryViewModelFactory(private val paymentHistoryInteractor: PaymentHistoryInteractor, private val customerInteractor: CustomerInteractor) :
        ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PaymentHistoryVM(paymentHistoryInteractor, customerInteractor) as T
    }
}