package com.custom.app.ui.dashboard.di;

import com.custom.app.ui.dashboard.DashboardInteractor;
import com.custom.app.ui.device.list.DeviceInteractor;
import com.user.app.data.UserManager;

import dagger.Module;
import dagger.Provides;

@Module
public class DashboardModule {

    @Provides
    DashboardInteractor provideUserInteractor(UserManager userManager) {
        return new DashboardInteractor(userManager);

    }
}