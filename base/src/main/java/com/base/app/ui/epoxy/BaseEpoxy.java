package com.base.app.ui.epoxy;

import androidx.annotation.NonNull;

import com.airbnb.epoxy.EpoxyController;

import timber.log.Timber;

public abstract class BaseEpoxy extends EpoxyController {

    protected BaseEpoxy() {
        setFilterDuplicates(true);
    }

    @Override
    protected void onExceptionSwallowed(@NonNull RuntimeException e) {
        Timber.e(e);
    }
}