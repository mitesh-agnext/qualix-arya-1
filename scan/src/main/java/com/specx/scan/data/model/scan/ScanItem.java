package com.specx.scan.data.model.scan;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class ScanItem {

    List<Integer> intensities;
    List<Double> reflectances;

    public ScanItem() {
    }

    public List<Integer> getIntensities() {
        return intensities;
    }

    public void setIntensities(List<Integer> intensities) {
        this.intensities = intensities;
    }

    public List<Double> getReflectances() {
        return reflectances;
    }

    public void setReflectances(List<Double> reflectances) {
        this.reflectances = reflectances;
    }
}