package com.custom.app.ui.commodity;

import android.view.View;

import com.base.app.ui.epoxy.BaseEpoxy;
import com.core.app.ui.custom.DelayedClickListener;
import com.specx.scan.data.model.commodity.CommodityItem;

import java.util.List;

public class CommodityController extends BaseEpoxy {

    private CommodityView view;
    private boolean isNightMode;
    private List<CommodityItem> commodities;

    CommodityController(CommodityView view) {
        this.view = view;
    }

    void setList(boolean isNightMode, List<CommodityItem> commodities) {
        this.isNightMode = isNightMode;
        this.commodities = commodities;
        requestModelBuild();
    }

    @Override
    protected void buildModels() {
        if (commodities != null) {
            for (CommodityItem commodity : commodities) {
                new CommodityItemModel_()
                        .view(view)
                        .id(commodity.getId())
                        .title(commodity.getName())
                        .desc(commodity.getCategory())
                        .clickListener(new DelayedClickListener() {
                            @Override
                            public void onClicked(View itemView) {
                                view.onItemClicked(itemView, commodity);
                            }
                        })
                        .addTo(this);
            }
        }
    }
}