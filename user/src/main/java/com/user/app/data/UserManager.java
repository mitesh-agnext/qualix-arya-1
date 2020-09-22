package com.user.app.data;

import androidx.annotation.NonNull;

import com.data.app.prefs.BooleanPreference;
import com.data.app.prefs.IntPreference;
import com.data.app.prefs.StringPreference;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import timber.log.Timber;

import static com.data.app.reference.ReferenceType.PREVIOUS;

public class UserManager {

    @Named("userId")
    @Singleton
    StringPreference userIdPref;

    @Named("token")
    @Singleton
    StringPreference tokenPref;

    @Named("userType")
    @Singleton
    IntPreference userTypePref;

    @Named("userName")
    @Singleton
    StringPreference userNamePref;

    @Named("customerId")
    @Singleton
    StringPreference customerIdPref;

    @Named("customerType")
    @Singleton
    StringPreference customerTypePref;

    @Named("permissions")
    @Singleton
    StringPreference permissionPref;

    @Named("fcmToken")
    @Singleton
    StringPreference fcmTokenPref;

    @Named("isLoggedIn")
    @Singleton
    BooleanPreference isLoggedInPref;

    @Named("userData")
    @Singleton
    StringPreference userDataPref;

    @Named("savedDevice")
    @Singleton
    StringPreference savedDevicePref;

    @Named("resultHardcode")
    @Singleton
    StringPreference resultHardcodePref;

    @Named("profilePath")
    @Singleton
    StringPreference profilePathPref;

    @Named("referenceType")
    @Singleton
    IntPreference referenceTypePref;

    @Named("scanReference")
    @Singleton
    StringPreference scanReferencePref;

    @Named("qualixRule")
    @Singleton
    StringPreference qualixRulePref;

    @Named("isNightMode")
    @Singleton
    BooleanPreference isNightModePref;

    @Inject
    UserManager(StringPreference userIdPref, StringPreference tokenPref,
                IntPreference userTypePref, StringPreference userNamePref,
                StringPreference customerIdPref, StringPreference customerTypePref,
                StringPreference permissionPref, StringPreference fcmTokenPref,
                BooleanPreference isLoggedInPref, StringPreference userDataPref,
                StringPreference savedDevicePref, StringPreference resultHardcodePref,
                StringPreference profilePathPref, IntPreference referenceTypePref,
                StringPreference scanReferencePref, StringPreference qualixRulePref,
                BooleanPreference isNightModePref) {
        this.userIdPref = userIdPref;
        this.tokenPref = tokenPref;
        this.userTypePref = userTypePref;
        this.userNamePref = userNamePref;
        this.customerIdPref = customerIdPref;
        this.customerTypePref = customerTypePref;
        this.permissionPref = permissionPref;
        this.fcmTokenPref = fcmTokenPref;
        this.isLoggedInPref = isLoggedInPref;
        this.userDataPref = userDataPref;
        this.savedDevicePref = savedDevicePref;
        this.resultHardcodePref = resultHardcodePref;
        this.profilePathPref = profilePathPref;
        this.referenceTypePref = referenceTypePref;
        this.scanReferencePref = scanReferencePref;
        this.qualixRulePref = qualixRulePref;
        this.isNightModePref = isNightModePref;
    }

    public String getUserId() {
        return userIdPref.get();
    }

    public void setUserId(String id) {
        userIdPref.set(id);
    }

    public String getToken() {
        return tokenPref.get();
    }

    public void setToken(String token) {
        tokenPref.set(token);
    }

    public int getUserType() {
        return userTypePref.get();
    }

    public void setUserType(int type) {
        userTypePref.set(type);
    }

    public String getUsername() {
        return userNamePref.get();
    }

    public void setUsername(String username) {
        userNamePref.set(username);
    }

    public String getCustomerId() {
        return customerIdPref.get();
    }

    public void setCustomerId(String customerId) {
        customerIdPref.set(customerId);
    }

    public String getCustomerType() {
        return customerTypePref.get();
    }

    public void setCustomerType(String customerType) {
        customerTypePref.set(customerType);
    }

    public String getPermissions() {
        return permissionPref.get();
    }

    public void setPermissions(String permissions) {
        permissionPref.set(permissions);
    }

    public String getFcmToken() {
        return fcmTokenPref.get();
    }

    public void setFcmToken(@NonNull String fcmToken) {
        fcmTokenPref.set(fcmToken);
    }

    public boolean isLoggedIn() {
        return isLoggedInPref != null && isLoggedInPref.get();
    }

    public void login(String userData) {
        isLoggedInPref.set(true);
        updateUserData(userData);
    }

    public void updateUserData(String userData) {
        userDataPref.set(userData);
    }

    public String getUserData() {
        return userDataPref.get();
    }

    public String getSavedDevice() {
        return savedDevicePref.get();
    }

    public void setSavedDevice(String deviceAddress) {
        Timber.d("Nano device saved: %s", deviceAddress);
        savedDevicePref.set(deviceAddress);
    }

    public void clearSavedDevice() {
        Timber.d("Clearing saved device");
        savedDevicePref.delete();
    }

    public String getResultHardcode() {
        return resultHardcodePref.get();
    }

    public void setResultHardcode(String resultHardcode) {
        Timber.d("Setting result hardcode to %s", resultHardcode);
        resultHardcodePref.set(resultHardcode);
    }

    public void clearResultHardcode() {
        Timber.d("Setting result hardcode to actual");
        resultHardcodePref.delete();
    }

    public String getProfilePath() {
        return profilePathPref.get();
    }

    public void setProfilePath(String path) {
        profilePathPref.set(path);
    }

    public int getReferenceType() {
        return referenceTypePref.get();
    }

    public void setReferenceType(int referenceType) {
        Timber.d("Setting reference type to %d", referenceType);
        referenceTypePref.set(referenceType);
    }

    public String getScanReference() {
        return scanReferencePref.get();
    }

    public void setScanReference(@NonNull String scanReference) {
        Timber.d("Setting scan reference to %s", scanReference);
        scanReferencePref.set(scanReference);
        setReferenceType(PREVIOUS.code);
    }

    public boolean isNightMode() {
        return isNightModePref.get();
    }

    public void setNightMode(boolean isDark) {
        Timber.d("Setting night mode to %b", isDark);
        isNightModePref.set(isDark);
    }

    public String getQualixRules() {
        return "[{\"commodity_code\":\"MILK\",\"rule_code\":\"rule_milk_1\",\"rule_type_id\":1,\"rule_type\":\"MATRIX\",\"rule_params\":[\"snf\",\"fat\"],\"rule_data\":{\"6.4-3.0\":110,\"6.1-3.0\":800.01,\"6.2-3.1\":900.02,\"6.3-3.2\":100,\"6.0-3.1\":600,\"6.0-3.0\":500,\"6.0-3.2\":700}},{\"commodity_code\":\"TURMERIC\",\"rule_code\":\"rule_turmeric_1\",\"rule_type_id\":3,\"rule_type\":\"RANGE\",\"rule_params\":[\"Curcumin\"],\"rule_data\":{\"6.1-7.0\":1100,\"9.1-10.0\":1400,\"2.1-3.0\":700,\"5.1-6.0\":1000,\"0.1-1.0\":500,\"7.1-8.0\":1200,\"8.1-9.0\":1300,\"3.1-4.0\":800,\"4.1-5.0\":900,\"1.1-2.0\":600}}]";
//      return qualixRulePref.get();
    }

    public void setQualixRule(String qualixRules) {
        qualixRulePref.set(qualixRules);
    }

    public void clearData() {
        userIdPref.delete();
        tokenPref.delete();
        userTypePref.delete();
        userNamePref.delete();
        customerIdPref.delete();
        customerTypePref.delete();
        permissionPref.delete();
        userDataPref.delete();
        isLoggedInPref.delete();
        qualixRulePref.delete();
        savedDevicePref.delete();
        profilePathPref.delete();
        isNightModePref.delete();
        referenceTypePref.delete();
        scanReferencePref.delete();
        resultHardcodePref.delete();
    }
}