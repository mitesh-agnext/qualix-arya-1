package com.custom.app.ui.senseNext.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.data.model.senseNext.SNDeviceRes
import com.custom.app.ui.createData.profile.list.ProfileListRes
import com.custom.app.ui.createData.profileType.list.ProfileTypeListRes
import com.custom.app.ui.createData.region.site.create.RegionRes
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerListListener
import com.custom.app.ui.customer.list.CustomerRes

class SNDeviceVM(val snDeviceInteractor: SNDeviceInteractor,
                 val customerInteractor: CustomerInteractor) : ViewModel(), SNDeviceListListener, CustomerListListener {

    private val _snDeviceListState: MutableLiveData<SNDeviceListState> = MutableLiveData()
    val snDeviceListState: LiveData<SNDeviceListState>
        get() = _snDeviceListState

    val devicesList = ArrayList<SNDeviceRes>()
    val customerList = ArrayList<CustomerRes>()
    val profileList = ArrayList<ProfileListRes>()
    val profileTypeList = ArrayList<ProfileTypeListRes>()
    val regionResList = ArrayList<RegionRes>()

    var errorMsg: String = "Error"

    var startDate: String = ""
    var endDate: String = ""
    var startDay: Int = 0
    var startMonth: Int = 0
    var endDay: Int = 0
    var endMonth: Int = 0

    //Forward
    fun getCustomerList() {
        _snDeviceListState.value = Loading
        customerInteractor.list(this)
    }

    fun getAllRegion(customerId: Int) {
        _snDeviceListState.value = Loading
        snDeviceInteractor.allRegion(customerId, this)
    }

    fun getAllProfileType(customerId: Int) {
        _snDeviceListState.value = Loading
        snDeviceInteractor.allProfileType(customerId, this)
    }

    fun getAllProfile(customerId: Int) {
        _snDeviceListState.value = Loading
        snDeviceInteractor.allProfile(customerId, this)
    }

    fun getDevicesList(data: HashMap<String, String>) {
        _snDeviceListState.value = Loading
        snDeviceInteractor.list(this, data)
    }

    //Back
    override fun onAllRegionSuccess(body: ArrayList<RegionRes>) {
        regionResList.clear()
        regionResList.addAll(body)
        _snDeviceListState.value = AllRegionSuccess
    }

    override fun onAllRegionFailure(msg: String) {
        errorMsg = msg
        _snDeviceListState.value = AllRegionFailure
    }

    override fun onAllProfileTypeSuccess(body: ArrayList<ProfileTypeListRes>) {
        profileTypeList.clear()
        profileTypeList.addAll(body)
        _snDeviceListState.value = AllProfileTypeSuccess
    }

    override fun onAllProfileTypeFailure(msg: String) {
        errorMsg = msg
        _snDeviceListState.value = AllProfileTypeFailure
    }

    override fun onAllProfileSuccess(body: ArrayList<ProfileListRes>) {
        profileList.clear()
        profileList.addAll(body)
        _snDeviceListState.value = AllProfileSuccess
    }

    override fun onAllProfileFailure(msg: String) {
        errorMsg = msg
        _snDeviceListState.value = AllProfileFailure
    }

    override fun onSNDeviceListSuccess(devices: ArrayList<SNDeviceRes>) {
        if (devices.size > 0) {
            devicesList.clear()
            devicesList.addAll(devices)
            _snDeviceListState.value = List(devicesList)
        }
        else
        {
            _snDeviceListState.value = NoRecord
        }
    }

    override fun onSNDeviceDeleteUserSuccess() {
    }

    override fun onError(msg: String?) {
        _snDeviceListState.value = Error(msg!!)
    }

    override fun onCustomerListSuccess(list: ArrayList<CustomerRes>) {
        customerList.clear()
        customerList.addAll(list)
        _snDeviceListState.value = CustomerList(list)
    }

    override fun onCustomerListFailure(msg: String) {
        _snDeviceListState.value = Error(msg!!)
        _snDeviceListState.value = CustomerError(msg!!)
    }

    override fun onTokenExpire() {
        _snDeviceListState.value = Token
    }
}

class SNDeviceVMFactory(private val sneviceInteractor: SNDeviceInteractor, val customerInteractor: CustomerInteractor) :
        ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SNDeviceVM(sneviceInteractor, customerInteractor) as T
    }
}
