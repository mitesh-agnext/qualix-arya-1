package com.user.app.data;

import android.content.SharedPreferences;

import com.data.app.prefs.BooleanPreference;
import com.data.app.prefs.IntPreference;
import com.data.app.prefs.StringPreference;
import com.data.app.reference.ReferenceType;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class UserModule {

    @Provides
    @Singleton
    UserManager provideUserManager(@Named("userId") StringPreference userIdPref,
                                   @Named("token") StringPreference tokenPref,
                                   @Named("userType") IntPreference userTypePref,
                                   @Named("userName") StringPreference userNamePref,
                                   @Named("customerId") StringPreference customerIdPref,
                                   @Named("customerType") StringPreference customerTypePref,
                                   @Named("permissions") StringPreference permissionsPref,
                                   @Named("fcmToken") StringPreference fcmTokenPref,
                                   @Named("isLoggedIn") BooleanPreference isLoggedInPref,
                                   @Named("userData") StringPreference userDataPref,
                                   @Named("savedDevice") StringPreference savedDevicePref,
                                   @Named("resultHardcode") StringPreference resultHardcodePref,
                                   @Named("profilePath") StringPreference profilePathPref,
                                   @Named("referenceType") IntPreference referenceTypePref,
                                   @Named("scanReference") StringPreference scanReferencePref,
                                   @Named("qualixRule") StringPreference qualixRulePref,
                                   @Named("isNightMode") BooleanPreference isNightModePref) {
        return new UserManager(userIdPref, tokenPref, userTypePref, userNamePref, customerIdPref,
                customerTypePref, permissionsPref, fcmTokenPref, isLoggedInPref, userDataPref,
                savedDevicePref, resultHardcodePref, profilePathPref, referenceTypePref,
                scanReferencePref, qualixRulePref, isNightModePref);
    }

    @Provides
    @Named("userId")
    @Singleton
    StringPreference provideUserId(SharedPreferences prefs) {
        return new StringPreference(prefs, "userId");
    }

    @Provides
    @Named("token")
    @Singleton
    StringPreference provideToken(SharedPreferences prefs) {
        return new StringPreference(prefs, "token");
    }

    @Provides
    @Named("userType")
    @Singleton
    IntPreference provideUserType(SharedPreferences prefs) {
        return new IntPreference(prefs, "userType");
    }

    @Provides
    @Named("userName")
    @Singleton
    StringPreference provideUserName(SharedPreferences prefs) {
        return new StringPreference(prefs, "userName");
    }

    @Provides
    @Named("customerId")
    @Singleton
    StringPreference provideCustomerId(SharedPreferences prefs) {
        return new StringPreference(prefs, "customerId");
    }

    @Provides
    @Named("customerType")
    @Singleton
    StringPreference provideCustomerType(SharedPreferences prefs) {
        return new StringPreference(prefs, "customerType");
    }

    @Provides
    @Named("permissions")
    @Singleton
    StringPreference providePermissions(SharedPreferences prefs) {
        return new StringPreference(prefs, "permissions");
    }

    @Provides
    @Named("fcmToken")
    @Singleton
    StringPreference provideFcmToken(SharedPreferences prefs) {
        return new StringPreference(prefs, "fcmToken");
    }

    @Provides
    @Named("isLoggedIn")
    @Singleton
    BooleanPreference provideIsLoggedIn(SharedPreferences prefs) {
        return new BooleanPreference(prefs, "isLoggedIn");
    }

    @Provides
    @Named("userData")
    @Singleton
    StringPreference provideUserData(SharedPreferences prefs) {
        return new StringPreference(prefs, "userData");
    }

    @Provides
    @Named("savedDevice")
    @Singleton
    StringPreference provideSavedDevice(SharedPreferences prefs) {
        return new StringPreference(prefs, "savedDevice");
    }

    @Provides
    @Named("resultHardcode")
    @Singleton
    StringPreference provideResultHardcode(SharedPreferences prefs) {
        return new StringPreference(prefs, "resultHardcode");
    }

    @Provides
    @Named("profilePath")
    @Singleton
    StringPreference provideProfilePath(SharedPreferences prefs) {
        return new StringPreference(prefs, "profilePath");
    }

    @Provides
    @Named("referenceType")
    @Singleton
    IntPreference provideReferenceType(SharedPreferences prefs) {
        return new IntPreference(prefs, "referenceType", ReferenceType.FACTORY.code);
    }

    @Provides
    @Named("scanReference")
    @Singleton
    StringPreference provideScanReference(SharedPreferences prefs) {
        return new StringPreference(prefs, "scanReference");
    }

    @Provides
    @Named("qualixRule")
    @Singleton
    StringPreference provideQualixRule(SharedPreferences prefs) {
        return new StringPreference(prefs, "qualixRule");
    }

    @Provides
    @Named("isNightMode")
    @Singleton
    BooleanPreference provideIsNightMode(SharedPreferences prefs) {
        return new BooleanPreference(prefs, "isNightMode");
    }
}