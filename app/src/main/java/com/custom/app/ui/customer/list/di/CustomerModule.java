package com.custom.app.ui.customer.list.di;

import com.custom.app.network.ApiInterface;
import com.custom.app.ui.customer.list.CustomerInteractor;
import com.user.app.data.UserManager;

import dagger.Module;
import dagger.Provides;

@Module
public class CustomerModule {

    @Provides
    CustomerInteractor provideCustomerInteractor(ApiInterface apiService, UserManager userManager ) {
        return new CustomerInteractor(apiService,userManager);
    }
}