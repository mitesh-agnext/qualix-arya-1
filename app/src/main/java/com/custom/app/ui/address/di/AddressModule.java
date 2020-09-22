package com.custom.app.ui.address.di;

import com.custom.app.network.ApiInterface;
import com.custom.app.ui.address.AddressInteractor;
import com.user.app.data.UserManager;

import dagger.Module;
import dagger.Provides;

@Module
public class AddressModule {

    @Provides
    AddressInteractor provideAddressInteractor(ApiInterface apiService, UserManager userManager) {
        return new AddressInteractor(apiService,userManager);
    }
}