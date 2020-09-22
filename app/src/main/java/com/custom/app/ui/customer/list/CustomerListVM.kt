package com.custom.app.ui.customer.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.createData.analytics.analyticsScreen.CategoryTabsRes

class CustomerListVM(val customerInteractor: CustomerInteractor) : ViewModel(), CustomerListListener,
        DeleteCustomerListener {

    private val _customerState: MutableLiveData<ScreenState<CustomerState>> = MutableLiveData()
    val customerState: LiveData<ScreenState<CustomerState>>
        get() = _customerState

    private val _customerList: MutableLiveData<ArrayList<CustomerRes>> = MutableLiveData()
    val customerList: LiveData<ArrayList<CustomerRes>>
        get() = _customerList
    var categoryArray= ArrayList<CategoryTabsRes>()

    var errorMsg = "error"

    /* Forward Flow */
    fun getCustomerListVm() {
        _customerState.value = ScreenState.Loading
        customerInteractor.list(this)
    }

    fun getCustomerSearchVm(keyword: String) {
        _customerState.value = ScreenState.Loading
        customerInteractor.search(keyword, this)
    }

    fun deleteCustomer(pos: Int) {
        _customerState.value = ScreenState.Loading
        customerInteractor.delete(_customerList.value!![pos].customer_id.toString(), this)
    }

    fun approveCustomer(pos: Int, remark: String) {
        _customerState.value = ScreenState.Loading
        val body = HashMap<String, Any>()
        body["customer_id"] = _customerList.value!![pos].customer_id.toString()
        body["remarks"] = remark
        customerInteractor.approve(body, this)
    }

    /* Backward Flow */
    override fun onCustomerListSuccess(response: ArrayList<CustomerRes>) {
        _customerList.value = response
        _customerState.value = ScreenState.Render(CustomerState.CustomerListSuccess)
    }

    override fun onCustomerListFailure(msg: String) {
        errorMsg = msg
        _customerState.value = ScreenState.Render(CustomerState.CustomerListFailure)
    }


    override fun onApproveCustSuccess() {
        _customerState.value = ScreenState.Render(CustomerState.ApproveCustSuccess)
    }

    override fun onApproveCustFailure(msg: String) {
        errorMsg = msg
        _customerState.value = ScreenState.Render(CustomerState.ApproveCustFailure)
    }

    override fun onDeleteCustomerSuccess() {
        _customerState.value = ScreenState.Render(CustomerState.DeleteCustomerSuccess)
    }

    override fun onDeleteCustomerFailure(msg: String) {
        errorMsg = msg
        _customerState.value = ScreenState.Render(CustomerState.DeleteCustomerFailure)
    }

    override fun onTokenExpire() {
        _customerState.value = ScreenState.Render(CustomerState.TokenExpire)
    }

    /* ViewModel Factory Method */
    class CustomerListViewModelFactory(private val qualityAnaInterceptor: CustomerInteractor) :
            ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return CustomerListVM(qualityAnaInterceptor) as T
        }
    }
}