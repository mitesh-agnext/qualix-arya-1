package com.specx.device.ui.select;

public class SelectDeviceItem {

    private String name;
    private String mac;
    private int rssi;

    public SelectDeviceItem(String name, String mac, int rssi) {
        this.name = name;
        this.mac = mac;
        this.rssi = rssi;
    }

    public String getName() {
        return name;
    }

    public String getMac() {
        return mac;
    }

    public int getRssi() {
        return rssi;
    }
}