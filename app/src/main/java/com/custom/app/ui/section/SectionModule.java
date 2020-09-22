package com.custom.app.ui.section;

import com.custom.app.network.ApiInterface;
import com.custom.app.ui.section.list.SectionInteractor;

import dagger.Module;
import dagger.Provides;

@Module
public class SectionModule {

    @Provides
    SectionInteractor provideSectionInteractor(ApiInterface apiService) {
        return new SectionInteractor(apiService);
    }
}