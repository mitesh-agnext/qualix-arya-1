package com.custom.app.ui.sampleBLE;

import com.custom.app.network.ApiInterface;
import com.custom.app.network.RestService;
import com.custom.app.ui.user.list.UserInteractor;
import com.user.app.data.UserManager;

import dagger.Module;
import dagger.Provides;

@Module
public class SimpleBleModule {
    @Provides
    SampleBleInteractor provideSampleBleInteractor(UserManager userManager) {
        return new SampleBleInteractor(userManager);
    }
}
