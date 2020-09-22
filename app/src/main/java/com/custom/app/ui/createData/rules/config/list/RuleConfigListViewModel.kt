package com.custom.app.ui.createData.rules.config.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerListListener
import com.custom.app.ui.customer.list.CustomerRes

class RuleConfigListViewModel(val ruleConfigListInteractor: RuleConfigListInteractor, val customerInteractor: CustomerInteractor) : ViewModel(),
        RuleConfigListInteractor.ListRuleConfigInteractorCallback, CustomerListListener {

    private val _RuleConfig_listState: MutableLiveData<ScreenState<RuleConfigListState>> = MutableLiveData()
    val ruleConfigListState: LiveData<ScreenState<RuleConfigListState>>
        get() = _RuleConfig_listState

    private val _ruleConfigList: MutableLiveData<ArrayList<RuleConfigRes>> = MutableLiveData()
    val ruleConfigList: LiveData<ArrayList<RuleConfigRes>>
        get() = _ruleConfigList

    private val _customerList: MutableLiveData<ArrayList<CustomerRes>> = MutableLiveData()
    val customerList: LiveData<ArrayList<CustomerRes>>
        get() = _customerList

    private val _deletedPos: MutableLiveData<Int> = MutableLiveData()
    val deletedPos: LiveData<Int>
        get() = _deletedPos

    var errorMessage: String = "Error"

    fun onGetRuleConfigList(customerId: Int) {
        _RuleConfig_listState.value = ScreenState.Loading
        ruleConfigListInteractor.allRuleConfig(this, customerId)
    }

    fun onDeleteRuleConfig(ruleConfigId: Int, position: Int) {
        _RuleConfig_listState.value = ScreenState.Loading
        ruleConfigListInteractor.deleteRuleConfig(this, ruleConfigId, position)
    }

    fun onGetCustomerList() {
        _RuleConfig_listState.value = ScreenState.Loading
        customerInteractor.list(this)
    }

    override fun allRuleConfigApiSuccess(body: ArrayList<RuleConfigRes>) {
        _ruleConfigList.value = body
        _RuleConfig_listState.value = ScreenState.Render(RuleConfigListState.RuleConfigListSuccess)
    }

    override fun allRuleConfigApiError() {
        _RuleConfig_listState.value = ScreenState.Render(RuleConfigListState.DeleteRuleConfigFailure)
    }

    override fun deleteRuleConfigSuccess(deletedPostion: Int) {
        _deletedPos.value = deletedPostion
        _RuleConfig_listState.value = ScreenState.Render(RuleConfigListState.DeleteRuleConfigSuccess)
    }

    override fun deleteRuleConfigFailure() {
        _RuleConfig_listState.value = ScreenState.Render(RuleConfigListState.DeleteRuleConfigFailure)
    }

    override fun onCustomerListSuccess(body: ArrayList<CustomerRes>) {
        _customerList.value = body
        _RuleConfig_listState.value = ScreenState.Render(RuleConfigListState.GetCustomerListSuccess)
    }

    override fun onCustomerListFailure(msg: String) {
        errorMessage = msg
        _RuleConfig_listState.value = ScreenState.Render(RuleConfigListState.GetCustomerListFailure)
    }

    override fun onTokenExpire() {}
}

class RuleConfigListViewModelFactory(
        private val ruleConfigsRuleConfigListInteractor: RuleConfigListInteractor, val customerInteractor: CustomerInteractor) :
        ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RuleConfigListViewModel(ruleConfigsRuleConfigListInteractor, customerInteractor) as T
    }
}