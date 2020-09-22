package com.custom.app.ui.createData.rules.config.update

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.createData.rules.config.create.RuleConfigCreateInteractor
import com.custom.app.ui.createData.rules.config.list.RuleConfigRes
import com.custom.app.ui.customer.list.CustomerRes
import okhttp3.ResponseBody
import java.util.*

class RuleConfigUpdateViewModel(val createRuleConfigInteractor: RuleConfigCreateInteractor, val ruleConfigUpdateInteractor: RuleConfigUpdateInteractor) : ViewModel(),
        RuleConfigCreateInteractor.CreateRuleConfigListener, RuleConfigUpdateInteractor.UpdateRuleConfigListener {

    private val _ruleConfigUpdateState: MutableLiveData<ScreenState<RuleConfigUpdateState>> = MutableLiveData()
    val ruleConfigUpdateState: LiveData<ScreenState<RuleConfigUpdateState>>
        get() = _ruleConfigUpdateState

    private val _ruleConfigUpdateResponse: MutableLiveData<ResponseBody> = MutableLiveData()
    val ruleConfigUpdateResponse: LiveData<ResponseBody>
        get() = _ruleConfigUpdateResponse

    private val _getCustomerList: MutableLiveData<ArrayList<CustomerRes>> = MutableLiveData()
    val getCustomerList: LiveData<ArrayList<CustomerRes>>
        get() = _getCustomerList

    var singleRuleConfig: RuleConfigRes = RuleConfigRes()

    fun onGetCustomer() {
        _ruleConfigUpdateState.value = ScreenState.Loading
        createRuleConfigInteractor.getCustomer(this)
    }

    fun onUpdateRuleConfig(ruleConfig_name: String, ruleConfig_id: Int, customer_id: Int) {
        _ruleConfigUpdateState.value = ScreenState.Loading
        ruleConfigUpdateInteractor.updateRuleConfig(ruleConfig_id, ruleConfig_name, customer_id, this)
    }

    override fun onRuleConfigSuccess(body: ResponseBody) {}

    override fun onRuleConfigFailure() {}

    override fun onGetCustomerSuccess(body: ArrayList<CustomerRes>) {
        _getCustomerList.value = body
        _ruleConfigUpdateState.value = ScreenState.Render(RuleConfigUpdateState.GetCustomerSuccess)
    }

    override fun onGetCustomerFailure() {
        _ruleConfigUpdateState.value = ScreenState.Render(RuleConfigUpdateState.GetCustomerFailure)
    }

    override fun onRuleConfigNameEmpty() {
        _ruleConfigUpdateState.value = ScreenState.Render(RuleConfigUpdateState.RuleConfigNameEmpty)
    }

    override fun onUpdateRuleConfigSuccess(body: ResponseBody) {
        _ruleConfigUpdateResponse.value = body
        _ruleConfigUpdateState.value = ScreenState.Render(RuleConfigUpdateState.RuleConfigUpdateSuccess)

    }

    override fun onUpdateRuleConfigFailure() {
        _ruleConfigUpdateState.value = ScreenState.Render(RuleConfigUpdateState.RuleConfigUpdateFailure)
    }
}

class UpdateRuleConfigViewModelFactory(
        private val createRuleConfigInteractor: RuleConfigCreateInteractor,
        val ruleConfigUpdateInteractor: RuleConfigUpdateInteractor) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RuleConfigUpdateViewModel(createRuleConfigInteractor, ruleConfigUpdateInteractor) as T

    }
}