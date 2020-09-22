package com.custom.app.ui.createData.rules.config.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.customer.list.CustomerRes
import okhttp3.ResponseBody
import java.util.*

class RuleConfigCreateViewModel(val createRuleConfigInteractor: RuleConfigCreateInteractor) : ViewModel(),
        RuleConfigCreateInteractor.CreateRuleConfigListener {

    private val _ruleConfigCreateState: MutableLiveData<ScreenState<RuleConfigCreateState>> = MutableLiveData()
    val ruleConfigCreateState: LiveData<ScreenState<RuleConfigCreateState>>
        get() = _ruleConfigCreateState

    private val _ruleConfigCreateResponse: MutableLiveData<ResponseBody> = MutableLiveData()
    val ruleConfigCreateResponse: LiveData<ResponseBody>
        get() = _ruleConfigCreateResponse

    private val _getCustomerList: MutableLiveData<ArrayList<CustomerRes>> = MutableLiveData()
    val getCustomerList: LiveData<ArrayList<CustomerRes>>
        get() = _getCustomerList

    fun onGetCustomer() {
        _ruleConfigCreateState.value = ScreenState.Loading
        createRuleConfigInteractor.getCustomer(this)
    }

    fun onCreateRuleConfig(ruleConfig_name: String, customer_id: Int) {
        _ruleConfigCreateState.value = ScreenState.Loading
        createRuleConfigInteractor.addRuleConfig(ruleConfig_name,customer_id,this)
    }

    override fun onRuleConfigSuccess(body: ResponseBody) {
        _ruleConfigCreateResponse.value = body
        _ruleConfigCreateState.value = ScreenState.Render(RuleConfigCreateState.RuleConfigCreateSuccess)
    }

    override fun onRuleConfigFailure() {
        _ruleConfigCreateState.value = ScreenState.Render(RuleConfigCreateState.RuleConfigCreateFailure)
    }

    override fun onGetCustomerSuccess(body: ArrayList<CustomerRes>) {
        _getCustomerList.value = body
        _ruleConfigCreateState.value = ScreenState.Render(RuleConfigCreateState.GetCustomerSuccess)
    }

    override fun onGetCustomerFailure() {
        _ruleConfigCreateState.value = ScreenState.Render(RuleConfigCreateState.GetCustomerFailure)
    }

    override fun onRuleConfigNameEmpty() {
        _ruleConfigCreateState.value = ScreenState.Render(RuleConfigCreateState.RuleConfigNameEmpty)
    }
}

class CreateRuleConfigViewModelFactory(
        private val ruleConfigInteractor: RuleConfigCreateInteractor) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RuleConfigCreateViewModel(ruleConfigInteractor) as T
    }
}