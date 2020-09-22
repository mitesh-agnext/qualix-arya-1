package com.custom.app.ui.senseNext.di;

import com.custom.app.network.ApiInterface;
import com.custom.app.ui.senseNext.devicesAnalysis.SNAnalysisInteractor;
import com.custom.app.ui.senseNext.list.SNDeviceInteractor;

import dagger.Module;
import dagger.Provides;

@Module
public class SNDeviceModule {

    @Provides
    SNDeviceInteractor provideSNDeviceInteractor(ApiInterface apiService) {
        return new SNDeviceInteractor( apiService);
    }

    @Provides
    SNAnalysisInteractor provideSNAnalysisInteractor() {
        return new SNAnalysisInteractor();
    }
}