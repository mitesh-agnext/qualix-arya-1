package com.custom.app.ui.createData.region.di;

import com.custom.app.ui.createData.region.RegionSiteInteractor;
import com.user.app.data.UserManager;

import dagger.Module;
import dagger.Provides;

@Module
public class SiteRegionModule {

    @Provides
    RegionSiteInteractor provideRegionSiteInteractor(UserManager userManager) {
        return new RegionSiteInteractor(userManager);
    }
}