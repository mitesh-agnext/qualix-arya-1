package com.custom.app.util;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * BLE INFO Reader
 */
public class BLEInfoReader {

  private static List<String> commodityList = Arrays
      .asList("raw rice", "rice raw", "paddy", "wheat", "maize", "maije", "barley", "groundnut",
          "ground nut", "moong", "moongdal", "moong dal");


  /**
   * Read BLE INFO
   *
   * @param bleList
   * @return
   */
  public BLEInfo readBLEInfo(List<String> bleList) {
    BLEInfo bleInfo = new BLEInfo();
    for (String str : bleList) {
      if (str.startsWith("c1")) {
        populateDeviceInfo(str, bleInfo);
      } else if (str.startsWith("c2")) {
        populateCommodityInfo(str,bleInfo);
      } else if (str.startsWith("c3")) {
        populateAnalytics(str,bleInfo);
      }

    }
    return bleInfo;
  }

  /**
   * Populate Analytics
   *
   * @param str
   * @param bleInfo
   */
  private void populateAnalytics(String str, BLEInfo bleInfo) {
    final String analyticsInfo = getInfoString(str);
    System.out.println(analyticsInfo);
    if(isAnalytics(analyticsInfo)){
      final String[] analyticsString = analyticsInfo.split(",");
      bleInfo.setMoisture(analyticsString[0]);
      bleInfo.setTemperature(analyticsString[1]);
      if(analyticsString[2] != null && isInteger(analyticsString[2].trim())){
        bleInfo.setWeight(analyticsString[2]);
      }else{
        bleInfo.setWeight("0.00");
      }
    }
  }

  /**
   * Populate Commodity Info
   * @param str
   * @param bleInfo
   */
  private void populateCommodityInfo(String str, BLEInfo bleInfo) {
    final String commodityInfo = getInfoString(str);
    bleInfo.setCommodity(commodityInfo!=null?commodityInfo.trim():null);
  }

  /**
   * Populate Device INfo
   * @param str
   * @param bleInfo
   */
  private void populateDeviceInfo(String str, BLEInfo bleInfo) {
    final String deviceInfo = getInfoString(str);
    bleInfo.setMachineId(deviceInfo!=null?deviceInfo.trim():null);
  }

  /**
   * get Truncated info String
   *
   * @param str
   * @return
   */
  private String getInfoString(String str){
    byte[] bytes = hexStringToByteArray(str.substring(2));
    return new String(bytes, StandardCharsets.UTF_8);
  }

  /**
   *
   * @param hex
   * @return
   */
  private static byte[] hexStringToByteArray(String hex) {
    int l = hex.length();
    byte[] data = new byte[l / 2];
    for (int i = 0; i < l; i += 2) {
      data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
          + Character.digit(hex.charAt(i + 1), 16));
    }
    return data;
  }

  /**
   * IS analytics Check
   *
   * @param bleString
   * @return
   */
  private boolean isAnalytics(String bleString){
    Boolean isAnalytics = false;
    if(bleString.contains(",")){
      isAnalytics = true;
    }
    return isAnalytics;
  }

  /**
   *
   * @param bleString
   * @return
   */
  private boolean isDeviceInfo(String bleString){
    Boolean isDevice = false;
    if(!isAnalytics(bleString) && bleString.contains("BLE")){
      isDevice=true;
    }
    return isDevice;
  }

  /**
   *
   * @param bleString
   * @return
   */
  private boolean isCommodity(String bleString) {
    Boolean isCommodity = false;
    for (String commodity : commodityList) {
      if (bleString.contains(commodity)) {
        isCommodity = true;
        break;
      }
    }
    return isCommodity;
  }

  /**
   *
   * @param s
   * @return
   */
  private static boolean isInteger(String s) {
    return isInteger(s,10);
  }

  /**
   *
   * @param s
   * @param radix
   * @return
   */
  private static boolean isInteger(String s, int radix) {
    if(s.isEmpty()) return false;
    for(int i = 0; i < s.length(); i++) {
      if(i == 0 && s.charAt(i) == '-') {
        if(s.length() == 1) return false;
        else continue;
      }
      if(Character.digit(s.charAt(i),radix) < 0) return false;
    }
    return true;
  }
}