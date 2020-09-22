package com.custom.app.ui.landing;

import com.base.app.ui.base.BasePresenter;

public abstract class QuantityPresenter extends BasePresenter<LandingView> {

    abstract void fetchCategories();

    abstract void fetchQuantityDetail(String categoryId, String from, String to);

}