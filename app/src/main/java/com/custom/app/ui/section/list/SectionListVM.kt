package com.custom.app.ui.section.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.data.model.section.DivisionRes
import com.custom.app.data.model.section.GardenRes
import com.custom.app.data.model.section.LocationItem
import com.custom.app.data.model.section.SectionRes
import com.custom.app.ui.base.ScreenState
import retrofit2.Response

class SectionListVM(val sectionInteractor: SectionInteractor) : ViewModel(), SectionCallback {
    private val _sectionListState: MutableLiveData<ScreenState<SectionState>> = MutableLiveData()
    val sectionListState: LiveData<ScreenState<SectionState>>
        get() = _sectionListState
    var locationArray = ArrayList<LocationItem>()
    var divisionArray = ArrayList<DivisionRes>()
    var gardenArray = ArrayList<GardenRes>()
    var sectionArray = ArrayList<SectionRes>()
    var errorMessage: String = "error"

    /*Forward flow*/
    fun getLocation() {
        _sectionListState.value = ScreenState.Loading
        sectionInteractor.getLocation(this)
    }

    fun getGarden(locationID: String) {
        _sectionListState.value = ScreenState.Loading
        sectionInteractor.getGarden(locationID, this)
    }

    fun getDivision(gardenId: String) {
        _sectionListState.value = ScreenState.Loading
        sectionInteractor.getDivision(gardenId, this)
    }

    fun getSection(divisionId: String) {
        _sectionListState.value = ScreenState.Loading
        sectionInteractor.getSection(divisionId, this)
    }

    fun addSection(request: HashMap<String, Any>) {
        _sectionListState.value = ScreenState.Loading
        sectionInteractor.addSection(request, this)
    }

    fun updateSection(sectionId: String, request: HashMap<String, Any>) {
        _sectionListState.value = ScreenState.Loading
        sectionInteractor.updateSection(sectionId, request, this)
    }

    fun deleteSection(sectionId: String) {
        _sectionListState.value = ScreenState.Loading
        sectionInteractor.deleteSection(sectionId, this)
    }

    fun addGarden(request: HashMap<String, Any>) {
        _sectionListState.value = ScreenState.Loading
        sectionInteractor.addGarden(request, this)
    }

    fun addDivision(request: HashMap<String, Any>) {
        _sectionListState.value = ScreenState.Loading
        sectionInteractor.addDivision(request, this)
    }

    /*Backward flow*/
    override fun onLocationSuccess(response: ArrayList<LocationItem>) {
        locationArray.clear()
        locationArray.addAll(response!!)
        _sectionListState.value = ScreenState.Render(SectionState.LocationSuccess)
    }

    override fun onLocationFailure(message: String) {
        errorMessage = message
        _sectionListState.value = ScreenState.Render(SectionState.LocationFailure)
    }

    override fun onGardenSuccess(response: ArrayList<GardenRes>) {
        gardenArray.clear()
        gardenArray.addAll(response)
        _sectionListState.value = ScreenState.Render(SectionState.GardenSuccess)
    }

    override fun onGardenFailure(message: String) {
        errorMessage = message
        _sectionListState.value = ScreenState.Render(SectionState.GardenFailure)
    }

    override fun onDivisionSuccess(response: ArrayList<DivisionRes>) {
        divisionArray.clear()
        divisionArray.addAll(response)
        _sectionListState.value = ScreenState.Render(SectionState.DivisionSuccess)
    }

    override fun onDivisionFailure(message: String) {
        errorMessage = message
        _sectionListState.value = ScreenState.Render(SectionState.DivisionFailure)
    }

    override fun onGetSectionsSuccess(response: ArrayList<SectionRes>) {
        sectionArray.clear()
        sectionArray.addAll(response!!)
        _sectionListState.value = ScreenState.Render(SectionState.GetSectionsSuccess)
    }

    override fun onGetSectionsFailure(message: String) {
        errorMessage = message
        _sectionListState.value = ScreenState.Render(SectionState.GetSectionsFailure)
    }

    override fun onAddSectionFailure(message: String) {
        errorMessage = message
        _sectionListState.value = ScreenState.Render(SectionState.AddSectionFailure)
    }

    override fun onUpdateSectionSuccess(body: Response<SectionRes>) {
        _sectionListState.value = ScreenState.Render(SectionState.UpdateSectionSuccess)
    }

    override fun onUpdateSectionFailure(message: String) {
        _sectionListState.value = ScreenState.Render(SectionState.UpdateSectionFailure)
    }

    override fun onDeleteSectionSuccess(body: Response<SectionRes>) {
        _sectionListState.value = ScreenState.Render(SectionState.DeleteSectionSuccess)
    }


    override fun onDeleteSectionFailure(message: String) {
        errorMessage = message
        _sectionListState.value = ScreenState.Render(SectionState.DeleteSectionFailure)
    }

    override fun onAddGardenSuccess(response: Response<GardenRes>) {
        _sectionListState.value = ScreenState.Render(SectionState.AddGardenSuccess)
    }

    override fun onAddGardenFailure(message: String) {
        _sectionListState.value = ScreenState.Render(SectionState.AddGardenFailure)
    }

    override fun onAddDivisionSuccess(response: Response<DivisionRes>) {
        _sectionListState.value = ScreenState.Render(SectionState.AddDivisionSuccess)
    }

    override fun onAddDivisionFailure(message: String) {
        _sectionListState.value = ScreenState.Render(SectionState.AddGardenFailure)
    }

    override fun onAddSectionSuccess(body: SectionRes) {
        _sectionListState.value = ScreenState.Render(SectionState.AddSectionSuccess)
    }

    override fun onTokenExpire() {
        _sectionListState.value = ScreenState.Render(SectionState.TokenExpire)
    }
}

class SectionListVMFactory(private val sectionInteractor: SectionInteractor) :
        ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SectionListVM(sectionInteractor) as T
    }
}