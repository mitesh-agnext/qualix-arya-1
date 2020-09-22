package com.custom.app.ui.payment.di;

import com.custom.app.network.ApiInterface;
import com.custom.app.ui.payment.list.PaymentHistoryInteractor;

import dagger.Module;
import dagger.Provides;

@Module
public class PaymentModule {

    @Provides
    PaymentHistoryInteractor providePaymentHistoryInteractor( ApiInterface apiService) {
        return new PaymentHistoryInteractor( apiService);
    }
}