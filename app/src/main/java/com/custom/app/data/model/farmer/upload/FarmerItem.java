package com.custom.app.data.model.farmer.upload;

import com.google.android.gms.maps.model.LatLng;

import org.parceler.Parcel;

import java.util.ArrayList;

@Parcel
public class FarmerItem {

    String account_number;
    String vendor_name;
    String vendor_email;
    String contact_number;
    String field;
    ArrayList<LatLng> plot;
    String vendor_address;
    boolean isUploaded;

    public FarmerItem() {
    }

    public FarmerItem(String account_number) {
        this.account_number = account_number;
    }

    private FarmerItem(String code, String name, String email, String mobile,
                       String field, ArrayList<LatLng> plot, String vendor_address) {
        this.account_number = code;
        this.vendor_name = name;
        this.vendor_email = email;
        this.contact_number = mobile;
        this.field = field;
        this.plot = plot;
        this.vendor_address = vendor_address;
    }

    public String getCode() {
        return account_number;
    }

    public void setCode(String code) {
        this.account_number = code;
    }

    public String getName() {
        return vendor_name;
    }

    public String getEmail() {
        return vendor_email;
    }

    public String getMobile() {
        return contact_number;
    }

    public String getField() {
        return field;
    }

    public ArrayList<LatLng> getPlot() {
        return plot;
    }

    public String getLocation() {
        return vendor_address;
    }

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
    }

    public static class Builder {

        private String code;
        private String name;
        private String email;
        private String mobile;
        private String field;
        private ArrayList<LatLng> plot;
        private String location;

        public Builder setCode(String code) {
            this.code = code;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setMobile(String mobile) {
            this.mobile = mobile;
            return this;
        }

        public Builder setField(String field) {
            this.field = field;
            return this;
        }

        public Builder setPlot(ArrayList<LatLng> plot) {
            this.plot = plot;
            return this;
        }

        public Builder setLocation(String location) {
            this.location = location;
            return this;
        }

        public FarmerItem build() {
            return new FarmerItem(code, name, email, mobile, field, plot, location);
        }
    }
}