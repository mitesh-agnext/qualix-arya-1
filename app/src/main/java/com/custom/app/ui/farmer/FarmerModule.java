package com.custom.app.ui.farmer;

import com.custom.app.network.RestService;
import com.custom.app.ui.farmer.detail.FarmerDetailPresenter;
import com.custom.app.ui.farmer.detail.FarmerDetailPresenterImpl;
import com.custom.app.ui.farmer.scan.FarmerScanPresenter;
import com.custom.app.ui.farmer.scan.FarmerScanPresenterImpl;
import com.squareup.sqlbrite3.BriteDatabase;
import com.user.app.data.UserManager;

import dagger.Module;
import dagger.Provides;

@Module
public class FarmerModule {

    @Provides
    FarmerInteractor provideFarmerInteractor(RestService restService, UserManager userManager,
                                             BriteDatabase database) {
        return new FarmerInteractorImpl(restService, userManager, database);
    }

    @Provides
    FarmerScanPresenter provideFarmerScanPresenter(FarmerInteractor interactor) {
        return new FarmerScanPresenterImpl(interactor);
    }

    @Provides
    FarmerDetailPresenter provideFarmerDetailPresenter(FarmerInteractor interactor) {
        return new FarmerDetailPresenterImpl(interactor);
    }
}