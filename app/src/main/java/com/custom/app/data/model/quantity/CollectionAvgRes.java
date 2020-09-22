package com.custom.app.data.model.quantity;

import com.custom.app.data.model.business.CommodityRateItem;

import org.parceler.Parcel;

import java.util.List;
import java.util.Map;

@Parcel
public class CollectionAvgRes {

    public CollectionAvgRes() {
    }

    List<CommodityRateItem> commodities;
    Map<String, Float> commulative_daily_data;

    public List<CommodityRateItem> getCommodities() {
        return commodities;
    }

    public Map<String, Float> getCommulativeDailyData() {
        return commulative_daily_data;
    }
}