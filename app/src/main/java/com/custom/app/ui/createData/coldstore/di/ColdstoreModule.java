package com.custom.app.ui.createData.coldstore.di;

import com.custom.app.ui.createData.coldstore.createColdstore.ColdstoreInteractor;
import com.user.app.data.UserManager;

import dagger.Module;
import dagger.Provides;

@Module
public class ColdstoreModule {

    @Provides
    ColdstoreInteractor provideColdstoreInteractor(UserManager userManager) {
        return new ColdstoreInteractor(userManager);
    }
}