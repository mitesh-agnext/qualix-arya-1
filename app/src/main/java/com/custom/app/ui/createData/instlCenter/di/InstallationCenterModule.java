package com.custom.app.ui.createData.instlCenter.di;

import com.custom.app.ui.createData.instlCenter.InstallationCenterInteractor;
import com.user.app.data.UserManager;

import dagger.Module;
import dagger.Provides;

@Module
public class InstallationCenterModule {

    @Provides
    InstallationCenterInteractor provideInstallationCenterInteractor(UserManager userManager) {
        return new InstallationCenterInteractor(userManager);
    }
}