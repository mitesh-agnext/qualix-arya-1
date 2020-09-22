package com.custom.app.ui.supplier;

import com.base.app.ui.base.BasePresenter;

public abstract class SupplierPresenter extends BasePresenter<SupplierView> {

    abstract void fetchCommodity(String... categoryId);

    abstract void fetchSuppliers(String commodityId, String regionId, String from, String to);

}