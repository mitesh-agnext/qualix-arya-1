package com.custom.app.ui.home.di;

import com.custom.app.network.RestService;
import com.custom.app.ui.home.HomeInteractor;
import com.custom.app.ui.home.HomePresenter;
import com.custom.app.ui.home.HomePresenterImpl;
import com.user.app.data.UserManager;

import dagger.Module;
import dagger.Provides;

@Module
public class HomeModule {

    @Provides
    HomeInteractor provideHomeInteractor(UserManager userManager, RestService restService) {
        return new HomeInteractor(userManager, restService);
    }

    @Provides
    HomePresenter provideHomePresenter(HomeInteractor interactor) {
        return new HomePresenterImpl(interactor);
    }
}