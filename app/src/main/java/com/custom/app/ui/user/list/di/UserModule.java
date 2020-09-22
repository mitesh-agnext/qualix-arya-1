package com.custom.app.ui.user.list.di;

import com.custom.app.network.ApiInterface;
import com.custom.app.network.RestService;
import com.custom.app.ui.user.list.UserInteractor;
import com.user.app.data.UserManager;

import dagger.Module;
import dagger.Provides;

@Module
public class UserModule {

    @Provides
    UserInteractor provideUserInteractor(UserManager userManager, ApiInterface apiService, RestService restService) {
        return new UserInteractor(userManager, apiService, restService);
    }
}