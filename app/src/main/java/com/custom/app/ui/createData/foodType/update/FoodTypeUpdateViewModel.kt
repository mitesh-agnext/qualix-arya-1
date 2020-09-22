package com.custom.app.ui.createData.foodType.update

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.createData.foodType.create.FoodTypeCreateInteractor
import com.custom.app.ui.createData.foodType.list.FoodTypeListRes
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerListListener
import com.custom.app.ui.customer.list.CustomerRes
import com.custom.app.ui.updateData.foodType.update.FoodTypeUpdateInteractor
import okhttp3.ResponseBody

class FoodTypeCreateViewModel(val foodTypeUpdateInteractor: FoodTypeUpdateInteractor,
                              val foodTypeCreateInteractor: FoodTypeCreateInteractor, val customerInteractor: CustomerInteractor) : ViewModel(),
        FoodTypeUpdateInteractor.UpdateFoodTypeListener,
        FoodTypeCreateInteractor.CreateFoodTypeListener, CustomerListListener {

    private val _foodTypeUpdateState: MutableLiveData<ScreenState<FoodTypeUpdateState>> = MutableLiveData()
    val foodTypeUpdateState: LiveData<ScreenState<FoodTypeUpdateState>>
        get() = _foodTypeUpdateState

    private val _foodTypeUpdateResponse: MutableLiveData<ResponseBody> = MutableLiveData()
    val foodTypeUpdateResponse: LiveData<ResponseBody>
        get() = _foodTypeUpdateResponse

    private val _getCustomerList: MutableLiveData<ArrayList<CustomerRes>> = MutableLiveData()
    val getCustomerList: LiveData<ArrayList<CustomerRes>>
        get() = _getCustomerList

    var singleFoodType: FoodTypeListRes = FoodTypeListRes()

    var errorMessage = "Error"

    fun onGetCustomer() {
        _foodTypeUpdateState.value = ScreenState.Loading
        customerInteractor.list(this)
    }

    fun onUpdateFoodType(food_type_name: String, food_type_id: Int, customer_id: Int) {
        _foodTypeUpdateState.value = ScreenState.Loading
        foodTypeUpdateInteractor.updateFoodType(food_type_name, food_type_id, customer_id, this)
    }

    override fun onFoodTypeUpdateSuccess(body: ResponseBody) {
        _foodTypeUpdateResponse.value = body
        _foodTypeUpdateState.value = ScreenState.Render(FoodTypeUpdateState.FoodTypeUpdateSuccess)
    }

    override fun onFoodTypeUpdateFailure() {
        _foodTypeUpdateState.value = ScreenState.Render(FoodTypeUpdateState.FoodTypeUpdateFailure)
    }

    override fun onFoodTypeSuccess(body: ResponseBody) {}

    override fun onFoodTypeFailure() {}

    override fun onFoodTypeNameEmpty() {
        _foodTypeUpdateState.value = ScreenState.Render(FoodTypeUpdateState.FoodTypeNameEmpty)
    }

    override fun onCustomerListSuccess(response: ArrayList<CustomerRes>) {
        _getCustomerList.value = response
        _foodTypeUpdateState.value = ScreenState.Render(FoodTypeUpdateState.GetCustomerSuccess)
    }

    override fun onCustomerListFailure(msg: String) {
        errorMessage = msg
        _foodTypeUpdateState.value = ScreenState.Render(FoodTypeUpdateState.GetCustomerFailure)
    }

    override fun onTokenExpire() {}

}

class UpdateFoodTypeViewModelFactory(
        val foodTypeUpdateInteractor: FoodTypeUpdateInteractor, private val foodTypecreateInteractor: FoodTypeCreateInteractor,
        val customerInteractor: CustomerInteractor) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FoodTypeCreateViewModel(foodTypeUpdateInteractor, foodTypecreateInteractor, customerInteractor) as T
    }
}