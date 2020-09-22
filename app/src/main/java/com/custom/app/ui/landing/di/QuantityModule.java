package com.custom.app.ui.landing.di;

import com.custom.app.network.RestService;
import com.custom.app.ui.business.BusinessPresenter;
import com.custom.app.ui.business.BusinessPresenterImpl;
import com.custom.app.ui.collection.CollectionPresenter;
import com.custom.app.ui.collection.CollectionPresenterImpl;
import com.custom.app.ui.commodity.CommodityInteractor;
import com.custom.app.ui.landing.QuantityInteractor;
import com.custom.app.ui.landing.QuantityInteractorImpl;
import com.custom.app.ui.landing.QuantityPresenter;
import com.custom.app.ui.landing.QuantityPresenterImpl;
import com.custom.app.ui.quality.QualityInteractor;
import com.custom.app.ui.supplier.SupplierPresenter;
import com.custom.app.ui.supplier.SupplierPresenterImpl;
import com.user.app.data.UserManager;

import dagger.Module;
import dagger.Provides;

@Module
public class QuantityModule {

    @Provides
    QuantityInteractor provideQuantityInteractor(RestService restService, UserManager userManager) {
        return new QuantityInteractorImpl(restService, userManager);
    }

    @Provides
    QuantityPresenter provideQuantityPresenter(QuantityInteractor interactor) {
        return new QuantityPresenterImpl(interactor);
    }

    @Provides
    BusinessPresenter provideBusinessPresenter(QuantityInteractor interactor) {
        return new BusinessPresenterImpl(interactor);
    }

    @Provides
    CollectionPresenter provideCollectionPresenter(QualityInteractor qualityInteractor,
                                                   QuantityInteractor quantityInteractor,
                                                   CommodityInteractor commodityInteractor) {
        return new CollectionPresenterImpl(qualityInteractor, quantityInteractor, commodityInteractor);
    }

    @Provides
    SupplierPresenter provideSupplierPresenter(QuantityInteractor quantityInteractor,
                                               CommodityInteractor commodityInteractor) {
        return new SupplierPresenterImpl(quantityInteractor, commodityInteractor);
    }
}