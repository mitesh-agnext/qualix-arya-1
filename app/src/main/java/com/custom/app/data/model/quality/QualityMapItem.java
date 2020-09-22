package com.custom.app.data.model.quality;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class QualityMapItem {

    private String center_name;
    private String collector;
    private String avg_quality;
    private List<LatLng> coordinates;

    public String getCenter_name() {
        return center_name;
    }

    public String getCollector() {
        return collector;
    }

    public String getAvg_quality() {
        return avg_quality;
    }

    public List<LatLng> getCoordinates() {
        return coordinates;
    }
}