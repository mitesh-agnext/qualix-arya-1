package com.specx.scan.data.model.sample;

import com.specx.scan.data.model.variety.VarietyItem;

import org.parceler.Parcel;

@Parcel
public class SampleItem {

    String id;
    String lotId;
    String scanId;
    Double weight;
    String truckNumber;
    String quantityUnit;
    VarietyItem variety;

    public SampleItem() {
    }

    public SampleItem(String id, double quantity) {
        this.id = id;
        this.weight = quantity;
    }

    public String getId() {
        return id;
    }

    public String getLotId() {
        return lotId;
    }

    public void setLotId(String lotId) {
        this.lotId = lotId;
    }

    public String getScanId() {
        return scanId;
    }

    public void setScanId(String scanId) {
        this.scanId = scanId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getQuantity() {
        return weight;
    }

    public void setQuantity(Double weight) {
        this.weight = weight;
    }

    public String getQuantityUnit() {
        return quantityUnit;
    }

    public void setQuantityUnit(String quantityUnit) {
        this.quantityUnit = quantityUnit;
    }

    public String getTruckNumber() {
        return truckNumber;
    }

    public void setTruckNumber(String truckNumber) {
        this.truckNumber = truckNumber;
    }

    public VarietyItem getVariety() {
        return variety;
    }

    public void setVariety(VarietyItem variety) {
        this.variety = variety;
    }
}