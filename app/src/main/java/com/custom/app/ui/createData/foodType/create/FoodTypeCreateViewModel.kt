package com.custom.app.ui.createData.foodType.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerListListener
import com.custom.app.ui.customer.list.CustomerRes
import okhttp3.ResponseBody

class FoodTypeCreateViewModel(val foodTypeCreateInteractor: FoodTypeCreateInteractor, val customerInteractor: CustomerInteractor) : ViewModel(),
        FoodTypeCreateInteractor.CreateFoodTypeListener, CustomerListListener {

    private val _foodTypeCreateState: MutableLiveData<ScreenState<FoodTypeCreateState>> = MutableLiveData()
    val foodTypeCreateState: LiveData<ScreenState<FoodTypeCreateState>>
        get() = _foodTypeCreateState

    private val _foodTypeCreateResponse: MutableLiveData<ResponseBody> = MutableLiveData()
    val foodTypeCreateResponse: LiveData<ResponseBody>
        get() = _foodTypeCreateResponse

    private val _getCustomerList: MutableLiveData<ArrayList<CustomerRes>> = MutableLiveData()
    val getCustomerList: LiveData<ArrayList<CustomerRes>>
        get() = _getCustomerList

    var errorMessage:String = "Error"

    fun onGetCustomer() {
        _foodTypeCreateState.value = ScreenState.Loading
        customerInteractor.list(this)
    }

    fun onCreateFoodType(food_name: String, customer_id: Int) {
        _foodTypeCreateState.value = ScreenState.Loading
        foodTypeCreateInteractor.addFoodType(food_name, customer_id, this)
    }

    override fun onFoodTypeSuccess(body: ResponseBody) {
        _foodTypeCreateResponse.value = body
        _foodTypeCreateState.value = ScreenState.Render(FoodTypeCreateState.FoodTypeCreateSuccess)
    }

    override fun onFoodTypeFailure() {
        _foodTypeCreateState.value = ScreenState.Render(FoodTypeCreateState.FoodTypeCreateFailure)
    }

    override fun onFoodTypeNameEmpty() {
        _foodTypeCreateState.value = ScreenState.Render(FoodTypeCreateState.FoodTypeNameEmpty)
    }

    override fun onCustomerListSuccess(response: ArrayList<CustomerRes>) {
        _getCustomerList.value = response
        _foodTypeCreateState.value = ScreenState.Render(FoodTypeCreateState.GetCustomerSuccess)
    }

    override fun onCustomerListFailure(msg: String) {
        errorMessage = msg
        _foodTypeCreateState.value = ScreenState.Render(FoodTypeCreateState.GetCustomerFailure)
    }

    override fun onTokenExpire() {}
}

class CreateFoodTypeViewModelFactory(
        private val foodTypeInteractor: FoodTypeCreateInteractor, val customerInteractor: CustomerInteractor) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FoodTypeCreateViewModel(foodTypeInteractor, customerInteractor) as T
    }
}