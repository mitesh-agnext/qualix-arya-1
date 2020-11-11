package com.custom.app.ui.sampleBLE;


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
