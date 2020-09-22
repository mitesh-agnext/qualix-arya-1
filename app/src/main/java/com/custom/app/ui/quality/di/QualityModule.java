package com.custom.app.ui.quality.di;

import com.custom.app.network.RestService;
import com.custom.app.ui.commodity.CommodityInteractor;
import com.custom.app.ui.quality.QualityInteractor;
import com.custom.app.ui.quality.QualityInteractorImpl;
import com.custom.app.ui.quality.QualityPresenter;
import com.custom.app.ui.quality.QualityPresenterImpl;
import com.user.app.data.UserManager;

import dagger.Module;
import dagger.Provides;

@Module
public class QualityModule {

    @Provides
    QualityInteractor provideQualityInteractor(RestService restService, UserManager userManager) {
        return new QualityInteractorImpl(restService, userManager);
    }

    @Provides
    QualityPresenter provideQualityPresenter(QualityInteractor qualityInteractor,
                                             CommodityInteractor commodityInteractor) {
        return new QualityPresenterImpl(qualityInteractor, commodityInteractor);
    }
}