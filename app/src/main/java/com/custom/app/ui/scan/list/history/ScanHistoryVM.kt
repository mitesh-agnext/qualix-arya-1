package com.custom.app.ui.scan.list.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.data.model.scanhistory.ScanData
import com.custom.app.ui.createData.flcScan.season.create.CommodityRes
import com.custom.app.ui.createData.region.site.create.RegionRes
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerListListener
import com.custom.app.ui.customer.list.CustomerRes
import com.custom.app.ui.device.add.DeviceTypeRes
import com.custom.app.ui.device.assign.InstallationCenterRes
import okhttp3.ResponseBody

class ScanHistoryVM(val scanHistoryInteractor: ScanHistoryInteractor, val customerInteractor: CustomerInteractor) : ViewModel(), ScanHistoryListener, CustomerListListener {

    val _scanHistoryState: MutableLiveData<ScanHistoryState> = MutableLiveData()
    val scanHistoryState: LiveData<ScanHistoryState>
        get() = _scanHistoryState

    val scanList = ArrayList<ScanData>()
    val customerList = ArrayList<CustomerRes>()
    val commodityList = ArrayList<CommodityRes>()
    val installationCenterList = ArrayList<InstallationCenterRes>()
    val regionList = ArrayList<RegionRes>()
    val deviceTypeList = ArrayList<DeviceTypeRes>()

    fun getCustomerList() {
        _scanHistoryState.value = Loading
        customerInteractor.list(this)
    }

    fun getCommodityListVm(customerId: Int) {
        _scanHistoryState.value = Loading
        scanHistoryInteractor.getCommodity(this, customerId)
    }

    fun getInstallationCentersVM(customerId: Int) {
        _scanHistoryState.value = Loading
        scanHistoryInteractor.getInstallationCenters(this, customerId)
    }

    fun getRegionVM(customerId: Int) {
        _scanHistoryState.value = Loading
        scanHistoryInteractor.getRegions(customerId, this)
    }

    fun getDeviceTypeVM() {
        _scanHistoryState.value = Loading
        scanHistoryInteractor.getDevicesType(this)
    }

    fun getScanHistory(option: MutableMap<String, String>) {
        scanHistoryInteractor.getScanHistoryIn(option, this)
    }

    fun setApproval(scanId: Int, status: Int) {
        _scanHistoryState.value = Loading
        scanHistoryInteractor.approveReject(scanId, status, this)
    }

    override fun scanHistorySuccess(body: ArrayList<ScanData>?) {
        scanList.clear()
        scanList.addAll(body!!)
        _scanHistoryState.value = ScanListSuccess
        _scanHistoryState.value = List(body)
    }

    override fun scanHistoryFailure(message: String) {
        _scanHistoryState.value = ScanListFailure
        _scanHistoryState.value = Error(message)
    }

    override fun commoditySuccess(body: ArrayList<CommodityRes>) {
        commodityList.clear()
        if (body.size > 0) {
            commodityList.addAll(body)
        }
        _scanHistoryState.value = CommodityList
    }

    override fun commodityFailure(msg: String) {
        _scanHistoryState.value = CommodityError
    }

    override fun installationCentersSuccess(body: ArrayList<InstallationCenterRes>) {
        installationCenterList.clear()
        if (body.size > 0) {
            installationCenterList.addAll(body)
        }
        _scanHistoryState.value = InstallationCentersSuccess
    }

    override fun installationCentersFailure(string: String) {
        _scanHistoryState.value = InstallationCentersFailure
    }

    override fun regionSuccess(body: ArrayList<RegionRes>) {
        regionList.clear()
        if (body.size > 0) {
            regionList.addAll(body)
        }
        _scanHistoryState.value = RegionSuccess
    }

    override fun regionFailure(string: String) {
        _scanHistoryState.value = RegionFailure
    }

    override fun approvalSuccess(body: ResponseBody) {
        _scanHistoryState.value = ApprovalSuccess
    }

    override fun approvalFailure(message: String) {
        _scanHistoryState.value = Error(message)
        _scanHistoryState.value = ApprovalFailure

    }

    override fun deviceTypeSuccess(body: ArrayList<DeviceTypeRes>) {
        deviceTypeList.clear()
        if (body.size > 0) {
            deviceTypeList.addAll(body)
        }
        _scanHistoryState.value = DeviceTypeSuccess
    }

    override fun deviceTypeFailure(string: String) {
        _scanHistoryState.value = DeviceTypeFailure
    }

    override fun tokenExpire() {
        _scanHistoryState.value = Token
    }

    override fun onCustomerListSuccess(list: ArrayList<CustomerRes>) {
        customerList.clear()
        customerList.addAll(list)
        _scanHistoryState.value = CustomerList
    }

    override fun onCustomerListFailure(msg: String) {
        customerList.clear()
        _scanHistoryState.value = CustomerError
    }

    override fun onTokenExpire() {
        _scanHistoryState.value = Token
    }
}

class ScanHistoryViewModelFactory(private val scanHistoryInteractor: ScanHistoryInteractor, val customerInteractor: CustomerInteractor) :
        ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ScanHistoryVM(scanHistoryInteractor, customerInteractor) as T
    }
}