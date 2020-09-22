package com.custom.app.ui.supplier;

import com.base.app.ui.base.BaseView;
import com.custom.app.data.model.supplier.SupplierItem;
import com.specx.scan.data.model.commodity.CommodityItem;

import java.util.List;

interface SupplierView extends BaseView {

    void showCommodities(List<CommodityItem> commodities);

    void showList(List<SupplierItem> suppliers);

    void onItemClicked(SupplierItem supplier);

}