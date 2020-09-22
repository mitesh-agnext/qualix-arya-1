package com.agnext.qualixfarmer.fields.farmDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.ui.base.ScreenState

class FarmDetailViewModel (val farmDetailInteractor: FarmDetailInteractor) : ViewModel(),
    FarmDetailInteractor.FarmDetailFinishedListener
{
     /**Observable Data*/
     private val _farmState: MutableLiveData<ScreenState<FarmDetailState>> = MutableLiveData()
    val farmState: LiveData<ScreenState<FarmDetailState>>
        get() = _farmState

    /**Forward flow*/
    fun getFarmDetail()
    {
        _farmState.value = ScreenState.Loading
        farmDetailInteractor.gatFarmDetail(this)
    }

    /**Backward flow*/
    override fun onFarmDetailSuccess() {
        _farmState.value = ScreenState.Render(FarmDetailState.farmDetailSuccess)
    }

    override fun onfFrmDetailFailure() {
        _farmState.value = ScreenState.Render(FarmDetailState.farmDetailFailure)
    }

}

class FarmDetailViewModelFactory(private val farmDetailInteractor: FarmDetailInteractor) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FarmDetailViewModel(farmDetailInteractor) as T
    }
}