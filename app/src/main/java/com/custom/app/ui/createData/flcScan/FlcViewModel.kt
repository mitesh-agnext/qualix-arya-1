package com.custom.app.ui.createData.flcScan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.custom.app.ui.base.ScreenState
import okhttp3.ResponseBody
import java.io.File

class FlcViewModel(private val flcInteractor: FlcInteractor) : ViewModel(),
        FlcInteractor.FlcListener {

    private val _flcState: MutableLiveData<ScreenState<FlcState>> = MutableLiveData()
    val flcState: LiveData<ScreenState<FlcState>>
        get() = _flcState

    private val _uploadImage: MutableLiveData<ImageUploadResult> = MutableLiveData()
    val uploadImage: LiveData<ImageUploadResult?>
        get() = _uploadImage

    private val _saveFlc: MutableLiveData<ResponseBody?> = MutableLiveData()
    val saveFlc: LiveData<ResponseBody?>
        get() = _saveFlc

    private val _fetchFlcResult: MutableLiveData<FlcResultRes?> = MutableLiveData()
    val fetchFlcResult: LiveData<FlcResultRes?>
        get() = _fetchFlcResult

    private val _fetchFlcLocations: MutableLiveData<ArrayList<LocationList>?> = MutableLiveData()
    val fetchFlcLocations: LiveData<ArrayList<LocationList>?>
        get() = _fetchFlcLocations

    private val _fetchFlcGardens: MutableLiveData<ArrayList<GardenList>?> = MutableLiveData()
    val fetchFlcGardens: LiveData<ArrayList<GardenList>?>
        get() = _fetchFlcGardens

    private val _fetchFlcDivision: MutableLiveData<ArrayList<DevisionList>?> = MutableLiveData()
    val fetchFlcDivision: LiveData<ArrayList<DevisionList>?>
        get() = _fetchFlcDivision

    private val _fetchFlcSections: MutableLiveData<ArrayList<SectionData>?> = MutableLiveData()
    val fetchFlcSections: LiveData<ArrayList<SectionData>?>
        get() = _fetchFlcSections

    private val _fetchFlcSectionCode: MutableLiveData<ArrayList<SectionData>?> = MutableLiveData()
    val fetchFlcSectionCode: LiveData<ArrayList<SectionData>?>
        get() = _fetchFlcSectionCode

    private val _clearData: MutableLiveData<FlcCleanDirRes?> = MutableLiveData()
    val clearData: LiveData<FlcCleanDirRes?>
        get() = _clearData

    var errorMessage: String = "Error"

    fun UploadImage(file: File, userId: String, secId: String) {
        _flcState.value = ScreenState.Loading
        flcInteractor.UploadImageServerNew(file, userId, secId, this)
    }

    fun SaveFlc(oneLeafBud: String, twoLeafBud: String, threeLeafBud: String, oneLeafBanjhi: String, twoLeafBanjhi: String, oneBanjhiCount: String,
                oneBudCount: String, oneLeafCount: String, twoLeafCount: String, threeLeafCount: String, qualityScore: Double, totalCount: Double,
                secId: String, agent_code: String, totalWeight: Double, areaCovered: Double) {

        _flcState.value = ScreenState.Loading
        flcInteractor.saveFlc(oneLeafBud, twoLeafBud, threeLeafBud, oneLeafBanjhi, twoLeafBanjhi, oneBanjhiCount, oneBudCount, oneLeafCount,
                twoLeafCount, threeLeafCount, qualityScore, totalCount, secId, agent_code, totalWeight, areaCovered, this)

    }

    fun FetchFlc(secId: String, userId: String) {
        _flcState.value = ScreenState.Loading
        flcInteractor.FetchResult(secId, userId, this)
    }

    fun FetchLocations() {
        _flcState.value = ScreenState.Loading
        flcInteractor.FetchLocations(this)
    }

    fun FetchGardens(locationId: Int) {
        _flcState.value = ScreenState.Loading
        flcInteractor.FetchGarden(locationId, this)
    }

    fun FetchDivisions(gardenId: Int) {
        _flcState.value = ScreenState.Loading
        flcInteractor.FetchDivision(gardenId, this)
    }

    fun FetchSections(divisionId: Int) {
        _flcState.value = ScreenState.Loading
        flcInteractor.FetchSection(divisionId, this)
    }

    fun FetchSectionCode(search_key: String) {
        _flcState.value = ScreenState.Loading
        flcInteractor.FetchSectionCode(search_key, this)
    }

    fun ClearData(secId: String, userId: String) {
        _flcState.value = ScreenState.Loading
        flcInteractor.ClearData(secId, userId, this)
    }

    override fun onUploadImageSuccess(resResultFlc: ImageUploadResult?) {
        _uploadImage.value = resResultFlc!!
        _flcState.value = ScreenState.Render(FlcState.UploadImageSuccess)
    }

    override fun onUploadImageFailure(msg: String) {
        errorMessage = msg
        _flcState.value = ScreenState.Render(FlcState.UploadImageFailure)
    }

    override fun onSaveFLCSuccess(resSaveFlc: ResponseBody?) {
        _saveFlc.value = resSaveFlc!!
        _flcState.value = ScreenState.Render(FlcState.SaveFLCSuccess)
    }

    override fun onSaveFLCFailure(msg: String) {
        errorMessage = msg
        _flcState.value = ScreenState.Render(FlcState.SaveFLCFailure)
    }

    override fun onSaveFLCTokenExpire() {
        _flcState.value = ScreenState.Render(FlcState.SaveFLCTokenExpire)
    }

    override fun onFetchFLCResultSuccess(resFetchFlc: FlcResultRes?) {
        _fetchFlcResult.value = resFetchFlc
        _flcState.value = ScreenState.Render(FlcState.FetchFLCResultSuccess)
    }

    override fun onFetchFLCResultFailure(msg: String) {
        errorMessage = msg
        _flcState.value = ScreenState.Render(FlcState.FetchFLCResultFailure)
    }

    override fun onClearDataSuccess(resClearData: FlcCleanDirRes?) {
        _clearData.value = resClearData
        _flcState.value = ScreenState.Render(FlcState.ClearDataSuccess)
    }

    override fun onClearDataFailure(msg: String) {
        errorMessage = msg
        _flcState.value = ScreenState.Render(FlcState.ClearDataFailure)
    }

    override fun onFetchLocationSuccess(resFetchLocation: ArrayList<LocationList>?) {
        _fetchFlcLocations.value = resFetchLocation!!
        _flcState.value = ScreenState.Render(FlcState.FetchFLCLocationSuccess)
    }

    override fun onFetchLocationFailure(msg: String) {
        errorMessage = msg
        _flcState.value = ScreenState.Render(FlcState.FetchFLCLocationFailure)
    }

    override fun onFetchGardenSuccess(resFetchGarden: ArrayList<GardenList>?) {
        _fetchFlcGardens.value = resFetchGarden!!
        _flcState.value = ScreenState.Render(FlcState.FetchFLCGardenSuccess)
    }

    override fun onFetchGardenFailure(msg: String) {
        errorMessage = msg
        _flcState.value = ScreenState.Render(FlcState.FetchFLCGardenFailure)
    }

    override fun onFetchDivisionSuccess(resFetchDivision: ArrayList<DevisionList>?) {
        _fetchFlcDivision.value = resFetchDivision!!
        _flcState.value = ScreenState.Render(FlcState.FetchFLCDivisionSuccess)
    }

    override fun onFetchDivisionFailure(msg: String) {
        errorMessage = msg
        _flcState.value = ScreenState.Render(FlcState.FetchFLCDivisionFailure)
    }

    override fun onFetchSectionSuccess(resFetchSection: ArrayList<SectionData>?) {
        _fetchFlcSections.value = resFetchSection!!
        _flcState.value = ScreenState.Render(FlcState.FetchFLCSectionSuccess)
    }

    override fun onFetchSectionFailure(msg: String) {
        errorMessage = msg
        _flcState.value = ScreenState.Render(FlcState.FetchFLCSectionFailure)
    }

    override fun onFetchSectionCodeSuccess(resFetchSection: ArrayList<SectionData>?) {
        _fetchFlcSectionCode.value = resFetchSection
        _flcState.value = ScreenState.Render(FlcState.FetchFLCSectionCodeSuccess)
    }

    override fun onFetchSectionCodeFailure(msg: String) {
        errorMessage = msg
        _flcState.value = ScreenState.Render(FlcState.FetchFLCSectionCodeFailure)
    }

    override fun onSectionIdEmpty() {
        _flcState.value = ScreenState.Render(FlcState.SectionIdEmpty)
    }

    override fun onAgentCodeEmpty() {
        _flcState.value = ScreenState.Render(FlcState.AgentCodeEmpty)
    }
}

class FlcViewModelFactory(private val flcInteractor: FlcInteractor) :
        ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FlcViewModel(flcInteractor) as T
    }
}