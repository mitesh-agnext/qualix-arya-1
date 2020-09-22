package com.custom.app.ui.createData.foodType.list

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerListListener
import com.custom.app.ui.customer.list.CustomerRes

class FoodTypeListViewModel(val foodTypeListInteractor: FoodTypeListInteractor, val customerInteractor: CustomerInteractor) : ViewModel(),
        FoodTypeListInteractor.ListFoodTypeInteractorCallback, CustomerListListener {

    private val _FoodType_listState: MutableLiveData<ScreenState<FoodTypeListState>> = MutableLiveData()
    val foodTypeListState: LiveData<ScreenState<FoodTypeListState>>
        get() = _FoodType_listState

    private val _foodTypeList: MutableLiveData<ArrayList<FoodTypeListRes>> = MutableLiveData()
    val foodTypeList: LiveData<ArrayList<FoodTypeListRes>>
        get() = _foodTypeList

    private val _customerList: MutableLiveData<ArrayList<CustomerRes>> = MutableLiveData()
    val customerList: LiveData<ArrayList<CustomerRes>>
        get() = _customerList

    private val _deletedPos: MutableLiveData<Int> = MutableLiveData()
    val deletedPos: LiveData<Int>
        get() = _deletedPos

    var errorMessage: String = "Error"

    fun onGetCustomerList() {
        _FoodType_listState.value = ScreenState.Loading
        customerInteractor.list(this)
    }

    fun onGetFoodTypeList(customer_id: Int) {
        _FoodType_listState.value = ScreenState.Loading
        foodTypeListInteractor.allFoodType(customer_id,this)
    }

    fun onDeleteFoodType(context: Context, foodTypeId: Int, position: Int) {
        _FoodType_listState.value = ScreenState.Loading
        foodTypeListInteractor.deleteFoodType(this, foodTypeId,position)
    }

    override fun allFoodTypeApiSuccess(body: ArrayList<FoodTypeListRes>) {
        _foodTypeList.value = body
        _FoodType_listState.value = ScreenState.Render(FoodTypeListState.FoodTypeListSuccess)
    }

    override fun allFoodTypeApiError(msg: String) {
        errorMessage = msg
        _FoodType_listState.value = ScreenState.Render(FoodTypeListState.FoodTypeListFailure)
    }

    override fun deleteFoodTypeSuccess(deletedPostion: Int) {
        _deletedPos.value = deletedPostion
        _FoodType_listState.value = ScreenState.Render(FoodTypeListState.DeleteFoodTypeSuccess)
    }

    override fun deleteFoodTypeFailure(msg: String) {
        errorMessage = msg
        _FoodType_listState.value = ScreenState.Render(FoodTypeListState.DeleteFoodTypeFailure)
    }

    override fun onCustomerListSuccess(response: ArrayList<CustomerRes>) {
        _customerList.value = response
        _FoodType_listState.value = ScreenState.Render(FoodTypeListState.GetCustomerSuccess)
    }

    override fun onCustomerListFailure(msg: String) {
        errorMessage = msg
        _FoodType_listState.value = ScreenState.Render(FoodTypeListState.GetCustomerFailure)
    }

    override fun onTokenExpire() {}
}

class FoodTypeListViewModelFactory(
        private val foodTypeListInteractor: FoodTypeListInteractor, val customerInteractor: CustomerInteractor) :
        ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FoodTypeListViewModel(foodTypeListInteractor, customerInteractor) as T
    }
}