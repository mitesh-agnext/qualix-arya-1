package com.custom.app.data.model.business;

public class PerDayAccepted {

    private String scan_date;
    private float scan_count;
    private float avg_variance_rate;
    private float avg_acceptance_rate;

    public String getScanDate() {
        return scan_date;
    }

    public float getScanCount() {
        return scan_count;
    }

    public float getAvgVarianceRate() {
        return avg_variance_rate;
    }

    public float getAvgAcceptanceRate() {
        return avg_acceptance_rate;
    }
}