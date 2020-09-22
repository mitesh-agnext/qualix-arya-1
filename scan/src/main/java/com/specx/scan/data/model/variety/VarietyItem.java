package com.specx.scan.data.model.variety;

import org.parceler.Parcel;

@Parcel
public class VarietyItem {

    String commodity_id;
    String variety_name;
    String commodity__name;
    boolean is_default_variety;
    String commodity_variety_id;

    public VarietyItem() {
    }

    public String getId() {
        return commodity_variety_id;
    }

    public String getName() {
        return variety_name;
    }
}