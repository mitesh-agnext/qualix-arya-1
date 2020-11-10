package com.custom.app;

import android.app.Activity;

import androidx.fragment.app.Fragment;

import com.base.app.BaseApplication;
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
import com.custom.app.ui.sampleBLE.SimpleBleModule;
import com.custom.app.ui.scan.di.ScanModule;
import com.custom.app.ui.section.SectionModule;
import com.custom.app.ui.senseNext.di.SNDeviceModule;
import com.custom.app.ui.user.list.di.UserModule;
import com.user.app.data.UserManager;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class CustomApp extends BaseApplication implements HasActivityInjector, HasSupportFragmentInjector {

    private AppComponent appComponent;
    private HomeComponent homeComponent;

    @Inject
    UserManager userManager;

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingActivityInjector;

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingFragmentInjector;

    @Override
    public void onCreate() {
        super.onCreate();

        getAppComponent().inject(this);

    }

    public AppComponent getAppComponent() {
        if (appComponent == null) {
            appComponent = DaggerAppComponent.builder()
                    .baseComponent(getBaseComponent())
                    .appModule(new AppModule())
                    .build();
        }
        return appComponent;
    }

    public HomeComponent getHomeComponent() {
        if (homeComponent == null) {
            homeComponent = getAppComponent().plus(
                    new HomeModule(),
                    new ScanModule(),
                    new UserModule(),
                    new QualityModule(),
                    new QuantityModule(),
                    new CustomerModule(),
                    new AddressModule(),
                    new SectionModule(),
                    new PaymentModule(),
                    new SNDeviceModule(),
                    new SampleModule(),
                    new DeviceModule(),
                    new ColdstoreModule(),
                    new AnalyticsModule(),
                    new SiteRegionModule(),
                    new InstallationCenterModule(),
                    new DashboardModule(),
                    new SimpleBleModule());
        }
        return homeComponent;
    }

    public void releaseHomeComponent() {
        homeComponent = null;
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingActivityInjector;
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingFragmentInjector;
    }
}