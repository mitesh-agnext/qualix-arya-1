package com.custom.app.ui.device.list.di;

import com.custom.app.ui.device.list.DeviceInteractor;
import com.user.app.data.UserManager;

import dagger.Module;
import dagger.Provides;

@Module
public class DeviceModule {

    @Provides
    DeviceInteractor provideUserInteractor(UserManager userManager) {
        return new DeviceInteractor(userManager);
    }
}