package com.custom.app.ui.commodity;

import android.view.View;

import com.base.app.ui.base.BaseView;
import com.specx.scan.data.model.commodity.CommodityItem;

import java.util.List;

interface CommodityView extends BaseView {

    void showList(List<CommodityItem> commodities);

    void onItemClicked(View itemView, CommodityItem commodity);

}