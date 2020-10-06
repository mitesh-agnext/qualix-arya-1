package com.custom.app;

import com.base.app.BaseComponent;
import com.custom.app.fcm.FcmMessageReceiverService;
import com.custom.app.fcm.FetchFcmTokenService;
import com.custom.app.ui.address.di.AddressModule;
import com.custom.app.ui.createData.analytics.di.AnalyticsModule;
import com.custom.app.ui.createData.coldstore.di.ColdstoreModule;
import com.custom.app.ui.createData.instlCenter.di.InstallationCenterModule;
import com.custom.app.ui.createData.region.di.SiteRegionModule;
import com.custom.app.ui.customer.list.di.CustomerModule;
import com.custom.app.ui.dashboard.di.DashboardModule;
import com.custom.app.ui.device.list.di.DeviceModule;
import com.custom.app.ui.home.di.HomeComponent;
import com.custom.app.ui.home.di.HomeModule;
import com.custom.app.ui.landing.di.QuantityModule;
import com.custom.app.ui.payment.di.PaymentModule;
import com.custom.app.ui.quality.di.QualityModule;
import com.custom.app.ui.sample.di.SampleModule;
import com.custom.app.ui.scan.di.ScanModule;
import com.custom.app.ui.section.SectionModule;
import com.custom.app.ui.senseNext.di.SNDeviceModule;
import com.custom.app.ui.user.list.di.UserModule;

import dagger.Component;

@AppScope
@Component(modules = {AppInjector.class, AppModule.class}, dependencies = BaseComponent.class)
public interface AppComponent {

    void inject(CustomApp application);

    void inject(FetchFcmTokenService service);

    void inject(FcmMessageReceiverService service);

    HomeComponent plus(HomeModule homeModule,
                       ScanModule scanModule,
                       UserModule userModule,
                       QualityModule qualityModule,
                       QuantityModule quantityModule,
                       CustomerModule customerModule,
                       AddressModule addressModule,
                       SectionModule sectionModule,
                       PaymentModule paymentModule,
                       SNDeviceModule sNDeviceModule,
                       SampleModule sampleModule,
                       DeviceModule DeviceModule,
                       ColdstoreModule coldstoreModule,
                       AnalyticsModule analyticsModule,
                       SiteRegionModule siteRegionModule,
                       InstallationCenterModule installationCenterModule,
                       DashboardModule dashboardModule);
}