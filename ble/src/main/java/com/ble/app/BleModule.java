package com.ble.app;

import android.content.Context;

import com.polidea.rxandroidble2.RxBleClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class BleModule {

    @Provides
    @Singleton
    RxBleClient provideRxBleClient(Context context) {
        return RxBleClient.create(context);
    }
}