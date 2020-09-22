package com.custom.app.ui.sample.di;

import com.custom.app.ui.sample.SampleInteractor;
import com.squareup.sqlbrite3.BriteDatabase;
import com.user.app.data.UserManager;

import dagger.Module;
import dagger.Provides;

@Module
public class SampleModule {

    @Provides
    SampleInteractor provideSampleInteractor(UserManager userManager, BriteDatabase database) {
        return new SampleInteractor(userManager, database);
    }
}