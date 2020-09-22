package com.custom.app;

import com.custom.app.ui.farmer.FarmerModule;
import com.custom.app.ui.farmer.detail.FarmerDetailActivity;
import com.custom.app.ui.farmer.scan.FarmerScanFragment;
import com.custom.app.ui.login.LoginActivity;
import com.custom.app.ui.login.di.LoginModule;
import com.custom.app.ui.login.di.LoginScope;
import com.custom.app.ui.logout.LogoutDialog;
import com.custom.app.ui.logout.di.LogoutModule;
import com.custom.app.ui.logout.di.LogoutScope;
import com.custom.app.ui.otp.OtpDialog;
import com.custom.app.ui.password.PasswordModule;
import com.custom.app.ui.password.PasswordScope;
import com.custom.app.ui.password.change.ChangePasswordFragment;
import com.custom.app.ui.password.forgot.ForgotPasswordDialog;
import com.specx.scan.ScanModule;
import com.specx.scan.ScanScope;
import com.specx.scan.ui.chemical.result.ChemicalResultActivity;
import com.specx.scan.ui.result.base.ResultActivity;
import com.specx.scan.ui.result.search.SearchResultActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Module(includes = AndroidSupportInjectionModule.class)
abstract class AppInjector {

    @LoginScope
    @ContributesAndroidInjector(modules = {LoginModule.class})
    abstract LoginActivity contributeLoginInjector();

    @LoginScope
    @ContributesAndroidInjector(modules = {LoginModule.class})
    abstract OtpDialog contributeOtpInjector();

    @PasswordScope
    @ContributesAndroidInjector(modules = {PasswordModule.class})
    abstract ForgotPasswordDialog contributeForgotPasswordInjector();

    @PasswordScope
    @ContributesAndroidInjector(modules = {PasswordModule.class, LogoutModule.class})
    abstract ChangePasswordFragment contributeChangePasswordInjector();

    @ScanScope
    @ContributesAndroidInjector(modules = {FarmerModule.class})
    abstract FarmerScanFragment contributeFarmerScanInjector();

    @ScanScope
    @ContributesAndroidInjector(modules = {FarmerModule.class})
    abstract FarmerDetailActivity contributeFarmerDetailInjector();

    @ScanScope
    @ContributesAndroidInjector(modules = {ScanModule.class})
    abstract SearchResultActivity contributeSearchResultInjector();

    @ScanScope
    @ContributesAndroidInjector(modules = {ScanModule.class})
    abstract ChemicalResultActivity contributeChemicalResultInjector();

    @ScanScope
    @ContributesAndroidInjector(modules = {ScanModule.class})
    abstract ResultActivity contributeResultInjector();

    @LogoutScope
    @ContributesAndroidInjector(modules = {LogoutModule.class})
    abstract LogoutDialog contributeLogoutInjector();

}