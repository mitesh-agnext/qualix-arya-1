package com.custom.app.data.model.bleScan.response;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sensor_data")
public class SensorData {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "sample_id")
    private String sampleId;

    @NonNull
    @ColumnInfo(name = "client_id")
    private int clientId;

    @NonNull
    @ColumnInfo(name = "commodity_name")
    private String commodityName;

    @NonNull
    @ColumnInfo(name = "moisture")
    private String moisture;

    @NonNull
    @ColumnInfo(name = "temperature")
    private String temperature;

    @NonNull
    @ColumnInfo(name = "truck_number")
    private String truckNumber;

    @NonNull
    @ColumnInfo(name = "issynchronized")
    private int isSynchronized ;

    public SensorData(@NonNull String sampleId, int clientId, @NonNull String commodityName, @NonNull String moisture, @NonNull String temperature, @NonNull String truckNumber) {
        this.sampleId = sampleId;
        this.clientId = clientId;
        this.commodityName = commodityName;
        this.moisture = moisture;
        this.temperature = temperature;
        this.truckNumber = truckNumber;
    }

    @NonNull
    public String getSampleId() {
        return sampleId;
    }

    public void setSampleId(@NonNull String sampleId) {
        this.sampleId = sampleId;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    @NonNull
    public String getCommodityName() {
        return commodityName;
    }

    public void setCommodityName(@NonNull String commodityName) {
        this.commodityName = commodityName;
    }

    @NonNull
    public String getMoisture() {
        return moisture;
    }

    public void setMoisture(@NonNull String moisture) {
        this.moisture = moisture;
    }

    @NonNull
    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(@NonNull String temperature) {
        this.temperature = temperature;
    }

    @NonNull
    public String getTruckNumber() {
        return truckNumber;
    }

    public void setTruckNumber(@NonNull String truckNumber) {
        this.truckNumber = truckNumber;
    }

    public int getIsSynchronized() {
        return isSynchronized;
    }

    public void setIsSynchronized(int isSynchronized) {
        this.isSynchronized = isSynchronized;
    }
}

