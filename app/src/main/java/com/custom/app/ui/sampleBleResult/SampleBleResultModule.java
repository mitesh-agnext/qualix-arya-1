package com.custom.app.ui.sampleBleResult;

import com.custom.app.ui.sampleBLE.SampleBleInteractor;
import com.user.app.data.UserManager;

import dagger.Module;
import dagger.Provides;

@Module
public class SampleBleResultModule {
    @Provides
    SampleBleResultInteractor provideSampleBleResultInteractor(UserManager userManager) {
        return new SampleBleResultInteractor(userManager);
    }
}
