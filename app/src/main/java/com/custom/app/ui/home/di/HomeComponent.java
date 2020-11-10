package com.custom.app.ui.home.di;

import com.custom.app.ui.address.di.AddressModule;
import com.custom.app.ui.analysis.AnalysisFragment;
import com.custom.app.ui.business.BusinessFragment;
import com.custom.app.ui.collection.CollectionFragment;
import com.custom.app.ui.commodity.CommodityFragment;
import com.custom.app.ui.createData.analytics.analysis.numberOfFarmers.NumberOfFarmers;
import com.custom.app.ui.createData.analytics.analysis.payments.Payments;
import com.custom.app.ui.createData.analytics.analysis.quality.Quality;
import com.custom.app.ui.createData.analytics.analysis.quantity.Quantity;
import com.custom.app.ui.createData.analytics.analyticsScreen.AnalysisScreen;
import com.custom.app.ui.createData.analytics.di.AnalyticsModule;
import com.custom.app.ui.createData.coldstore.coldstoreList.ColdstoreListActivity;
import com.custom.app.ui.createData.coldstore.createColdstore.CreateColdstore;
import com.custom.app.ui.createData.coldstore.di.ColdstoreModule;
import com.custom.app.ui.createData.coldstore.updateColdstore.UpdateColdstore;
import com.custom.app.ui.createData.deviationProfile.create.CreateDeviationProfile;
import com.custom.app.ui.createData.deviationProfile.list.DeviationListActivity;
import com.custom.app.ui.createData.deviationProfile.update.UpdateDeviationProfile;
import com.custom.app.ui.createData.flcScan.season.create.Season_creation;
import com.custom.app.ui.createData.flcScan.season.list.SeasonListActivity;
import com.custom.app.ui.createData.flcScan.season.update.SeasonUpdate;
import com.custom.app.ui.createData.foodType.create.FoodTypeCreate;
import com.custom.app.ui.createData.foodType.list.FoodTypeListActivity;
import com.custom.app.ui.createData.foodType.update.FoodTypeUpdate;
import com.custom.app.ui.createData.instlCenter.centerList.InstallationCenterListActivity;
import com.custom.app.ui.createData.instlCenter.createInstallationCenter.CreateIntlCenters;
import com.custom.app.ui.createData.instlCenter.di.InstallationCenterModule;
import com.custom.app.ui.createData.instlCenter.updateCenter.UpdateIntlCenters;
import com.custom.app.ui.createData.profile.create.ProfileCreate;
import com.custom.app.ui.createData.profile.list.ProfileListActivity;
import com.custom.app.ui.createData.profile.update.ProfileUpdate;
import com.custom.app.ui.createData.profileType.create.ProfileTypeCreate;
import com.custom.app.ui.createData.profileType.list.ProfileTypeListActivity;
import com.custom.app.ui.createData.profileType.update.ProfileTypeUpdate;
import com.custom.app.ui.createData.region.create.RegionCreate;
import com.custom.app.ui.createData.region.di.SiteRegionModule;
import com.custom.app.ui.createData.region.list.RegionListActivity;
import com.custom.app.ui.createData.region.site.create.CreateSite;
import com.custom.app.ui.createData.region.site.list.SiteListActivity;
import com.custom.app.ui.createData.region.site.update.UpdateSite;
import com.custom.app.ui.createData.region.update.RegionUpdate;
import com.custom.app.ui.createData.rules.config.list.RuleConfigListActivity;
import com.custom.app.ui.customer.add.AddCustomerActivity;
import com.custom.app.ui.customer.edit.EditCustomerActivity;
import com.custom.app.ui.customer.list.CustomerListActivity;
import com.custom.app.ui.customer.list.di.CustomerModule;
import com.custom.app.ui.dashboard.DashboardFragment;
import com.custom.app.ui.dashboard.di.DashboardModule;
import com.custom.app.ui.device.add.AddDeviceActivity;
import com.custom.app.ui.device.assign.DeviceProvisionActivity;
import com.custom.app.ui.device.list.DeviceListActivity;
import com.custom.app.ui.device.list.di.DeviceModule;
import com.custom.app.ui.device.update.UpdateDeviceActivity;
import com.custom.app.ui.home.HomeActivity;
import com.custom.app.ui.home.HomeFragment;
import com.custom.app.ui.landing.LandingFragment;
import com.custom.app.ui.landing.di.QuantityModule;
import com.custom.app.ui.payment.di.PaymentModule;
import com.custom.app.ui.payment.list.PaymentHistoryActivity;
import com.custom.app.ui.quality.QualityMapFragment;
import com.custom.app.ui.quality.di.QualityModule;
import com.custom.app.ui.sample.SampleFragment;
import com.custom.app.ui.sample.di.SampleModule;
import com.custom.app.ui.sampleBLE.SampleBleFragment;
import com.custom.app.ui.sampleBLE.SimpleBleModule;
import com.custom.app.ui.scan.di.ScanModule;
import com.custom.app.ui.scan.list.detail.ScanDetailActivity;
import com.custom.app.ui.scan.list.history.ScanHistoryActivity;
import com.custom.app.ui.scan.list.scanFrg.ScanHistoryFragment;
import com.custom.app.ui.scan.select.SelectScanActivity;
import com.custom.app.ui.section.SectionModule;
import com.custom.app.ui.section.add.AddSectionActivity;
import com.custom.app.ui.section.detail.SectionDetailActivity;
import com.custom.app.ui.section.list.SectionListActivity;
import com.custom.app.ui.section.update.UpdateSectionActivity;
import com.custom.app.ui.senseNext.devicesAnalysis.SNAnalysisActivity;
import com.custom.app.ui.senseNext.di.SNDeviceModule;
import com.custom.app.ui.senseNext.list.SNDeviceListActivity;
import com.custom.app.ui.supplier.SupplierFragment;
import com.custom.app.ui.user.add.AddUserActivity;
import com.custom.app.ui.user.edit.EditUserActivity;
import com.custom.app.ui.user.list.UserListActivity;
import com.custom.app.ui.user.list.di.UserModule;

import dagger.Subcomponent;

@HomeScope
@Subcomponent(modules = {HomeModule.class, ScanModule.class, UserModule.class, QualityModule.class,
        QuantityModule.class, CustomerModule.class, AddressModule.class, SectionModule.class,
        PaymentModule.class, SNDeviceModule.class, SampleModule.class, DeviceModule.class,
        ColdstoreModule.class, AnalyticsModule.class, SiteRegionModule.class,
        InstallationCenterModule.class, DashboardModule.class, SimpleBleModule.class})

public interface HomeComponent {

    void inject(HomeActivity activity);

    void inject(HomeFragment fragment);

    void inject(LandingFragment fragment);

    void inject(DashboardFragment fragment);

    void inject(BusinessFragment fragment);

    void inject(CollectionFragment fragment);

    void inject(SelectScanActivity activity);

    void inject(CommodityFragment fragment);

    void inject(SampleFragment fragment);

    void inject(AnalysisFragment fragment);

    void inject(UserListActivity activity);

    void inject(EditUserActivity activity);

    void inject(AddUserActivity activity);

    void inject(CustomerListActivity activity);

    void inject(EditCustomerActivity activity);

    void inject(AddCustomerActivity activity);

    void inject(SectionListActivity activity);

    void inject(AddSectionActivity activity);

    void inject(UpdateSectionActivity activity);

    void inject(SectionDetailActivity activity);

    void inject(CreateColdstore activity);

    void inject(UpdateColdstore activity);

    void inject(SiteListActivity activity);

    void inject(CreateSite activity);

    void inject(UpdateSite activity);

    void inject(CreateIntlCenters activity);

    void inject(UpdateIntlCenters activity);

    void inject(CreateDeviationProfile activity);

    void inject(UpdateDeviationProfile activity);

    void inject(DeviationListActivity activity);

    void inject(AnalysisScreen activity);

    void inject(RegionCreate activity);

    void inject(RegionUpdate activity);

    void inject(RegionListActivity activity);

    void inject(ProfileTypeListActivity activity);

    void inject(ColdstoreListActivity activity);

    void inject(SeasonListActivity activity);

    void inject(SeasonUpdate activity);

    void inject(Season_creation activity);

    void inject(FoodTypeListActivity activity);

    void inject(FoodTypeCreate activity);

    void inject(FoodTypeUpdate activity);

    void inject(InstallationCenterListActivity activity);

    void inject(ProfileCreate activity);

    void inject(ProfileListActivity activity);

    void inject(ProfileUpdate activity);

    void inject(ProfileTypeCreate activity);

    void inject(ProfileTypeUpdate activity);

    void inject(RuleConfigListActivity activity);

    void inject(QualityMapFragment fragment);

    void inject(SupplierFragment fragment);

    void inject(ScanHistoryActivity activity);

    void inject(ScanDetailActivity activity);

    void inject(PaymentHistoryActivity activity);

    void inject(SNDeviceListActivity activity);

    void inject(DeviceListActivity activity);

    void inject(SNAnalysisActivity activity);

    void inject(UpdateDeviceActivity activity);

    void inject(AddDeviceActivity activity);

    void inject(DeviceProvisionActivity activity);

    void inject(ScanHistoryFragment fragment);

    void inject(NumberOfFarmers fragment);

    void inject(Payments fragment);

    void inject(Quality fragment);

    void inject(Quantity fragment);

    void inject(SampleBleFragment fragment);

}