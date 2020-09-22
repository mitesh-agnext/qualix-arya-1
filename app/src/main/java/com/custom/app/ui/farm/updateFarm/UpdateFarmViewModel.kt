package com.custom.app.ui.farm.updateFarm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.farm.ResParticularFarm

class UpdateFarmViewModel(val updateFramInteractor: UpdateFramInteractor): ViewModel(),
    UpdateFramInteractor.UpdateFarmFinishedListener
{

    /**Observable Data*/
    private val _updateFarmState: MutableLiveData<ScreenState<UpdateFarmState>> = MutableLiveData()
    val updateFarmState: LiveData<ScreenState<UpdateFarmState>>
        get() = _updateFarmState

    private val _particularFarm: MutableLiveData<ResParticularFarm> = MutableLiveData()
    val particularFarm: LiveData<ResParticularFarm>
        get() = _particularFarm

    /**Forward Flow*/
    fun getParticularFarmVM(token:String,farmId:String)
    {
        _updateFarmState.value = ScreenState.Loading
        updateFramInteractor.getParticularFarm(token,farmId,this)
    }


    /**Backward Flow*/
    override fun onGetParticularFarmSuccess(response: ResParticularFarm?) {
        _particularFarm.value=response
        _updateFarmState.value= ScreenState.Render(UpdateFarmState.GetParticularFarmSuccess)
    }

    override fun onGetParticularFarmFailure() {
        _updateFarmState.value= ScreenState.Render(UpdateFarmState.GetParticularFarmFailure)
    }
}

class UpdateFarmViewModelFactory(private val updateFramInteractor: UpdateFramInteractor) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return UpdateFarmViewModel(updateFramInteractor) as T
    }
}
