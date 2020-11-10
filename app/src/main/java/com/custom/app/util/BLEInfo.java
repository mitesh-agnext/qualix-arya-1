package com.custom.app.util;

import java.io.Serializable;

public class BLEInfo implements Serializable {

  String machineId;
  String commodity;
  String temperature;
  String weight;
  String moisture;


  public String getMachineId() {
    return machineId;
  }

  public void setMachineId(String machineId) {
    this.machineId = machineId;
  }

  public String getCommodity() {
    return commodity;
  }

  public void setCommodity(String commodity) {
    this.commodity = commodity;
  }

  public String getTemperature() {
    return temperature;
  }

  public void setTemperature(String temperature) {
    this.temperature = temperature;
  }

  public String getWeight() {
    return weight;
  }

  public void setWeight(String weight) {
    this.weight = weight;
  }

  public String getMoisture() {
    return moisture;
  }

  public void setMoisture(String moisture) {
    this.moisture = moisture;
  }

  @Override
  public String toString() {
    return "BLEInfo{" +
        "machineId='" + machineId + '\'' +
        ", commodity='" + commodity + '\'' +
        ", temperature='" + temperature + '\'' +
        ", weight='" + weight + '\'' +
        ", moisture='" + moisture + '\'' +
        '}';
  }
}