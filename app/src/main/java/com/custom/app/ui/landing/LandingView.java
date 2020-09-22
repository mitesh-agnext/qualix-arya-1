package com.custom.app.ui.landing;

import com.base.app.ui.base.BaseView;
import com.custom.app.data.model.category.CategoryDetailItem;
import com.custom.app.data.model.quantity.CenterDetailItem;
import com.custom.app.data.model.quantity.QuantityDetailRes;

import java.util.List;

interface LandingView extends BaseView {

    void showDatePicker();

    void showCategories(List<CategoryDetailItem> categories);

    void showDetails(QuantityDetailRes detail);

    void onCenterClicked(CenterDetailItem center);

}