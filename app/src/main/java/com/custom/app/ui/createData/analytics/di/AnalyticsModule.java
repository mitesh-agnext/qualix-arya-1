package com.custom.app.ui.createData.analytics.di;

import com.custom.app.ui.createData.analytics.analyticsScreen.AnalyticsInteractor;
import com.user.app.data.UserManager;

import dagger.Module;
import dagger.Provides;

@Module
public class AnalyticsModule {

    @Provides
    AnalyticsInteractor provideAnalyticsInteractor(UserManager userManager) {
        return new AnalyticsInteractor(userManager);
    }
}