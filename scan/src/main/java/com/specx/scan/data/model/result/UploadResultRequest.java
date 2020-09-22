package com.specx.scan.data.model.result;

public class UploadResultRequest {

    private String batch_id;
    private String lot_id;
    private String sample_id;
    private String scan_id;
    private String commodity_id;
    private String commodity_name;
    private String scan_by_user_code;
    private String location;
    private String device_serial_no;
    private Double weight;
    private String quantity_unit;
    private Integer vendor_code;
    private String farmer_code;
    private String variety_id;
    private String device_type;
    private Integer device_type_id;

    private UploadResultRequest(String batch_id, String lot_id, String sample_id, String scan_id,
                                String commodity_id, String commodity_name, String scan_by_user_code, String location,
                                String device_serial_no, Double weight, String quantity_unit,
                                Integer vendor_code, String farmer_code, String variety_id,
                                String device_type, Integer device_type_id) {
        this.batch_id = batch_id;
        this.lot_id = lot_id;
        this.sample_id = sample_id;
        this.scan_id = scan_id;
        this.commodity_id = commodity_id;
        this.commodity_name = commodity_name;
        this.scan_by_user_code = scan_by_user_code;
        this.location = location;
        this.device_serial_no = device_serial_no;
        this.weight = weight;
        this.quantity_unit = quantity_unit;
        this.vendor_code = vendor_code;
        this.farmer_code = farmer_code;
        this.variety_id = variety_id;
        this.device_type = device_type;
        this.device_type_id = device_type_id;
    }

    public static class Builder {

        private String batch_id;
        private String lot_id;
        private String sample_id;
        private String scan_id;
        private String commodity_id;
        private String commodity_name;
        private String scan_by_user_code;
        private String location;
        private String device_serial_no;
        private Double weight;
        private String quantity_unit;
        private Integer vendor_code;
        private String farmer_code;
        private String variety_id;
        private String device_type;
        private Integer device_type_id;

        public Builder setBatchId(String batch_id) {
            this.batch_id = batch_id;
            return this;
        }

        public Builder setLotId(String lot_id) {
            this.lot_id = lot_id;
            return this;
        }

        public Builder setSampleId(String sample_id) {
            this.sample_id = sample_id;
            return this;
        }

        public Builder setScanId(String scan_id) {
            this.scan_id = scan_id;
            return this;
        }

        public Builder setCommodityId(String commodity_id) {
            this.commodity_id = commodity_id;
            return this;
        }

        public Builder setCommodityName(String commodity_name) {
            this.commodity_name = commodity_name;
            return this;
        }

        public Builder setScanByUserCode(String scan_by_user_code) {
            this.scan_by_user_code = scan_by_user_code;
            return this;
        }

        public Builder setLocation(String location) {
            this.location = location;
            return this;
        }

        public Builder setDeviceSerialNo(String device_serial_no) {
            this.device_serial_no = device_serial_no;
            return this;
        }

        public Builder setQuantity(Double weight) {
            this.weight = weight;
            return this;
        }

        public Builder setQuantityUnit(String quantity_unit) {
            this.quantity_unit = quantity_unit;
            return this;
        }

        public Builder setVendorCode(Integer vendor_code) {
            this.vendor_code = vendor_code;
            return this;
        }

        public Builder setFarmerCode(String farmer_code) {
            this.farmer_code = farmer_code;
            return this;
        }

        public Builder setVarietyId(String variety_id) {
            this.variety_id = variety_id;
            return this;
        }

        public Builder setDeviceType(String device_type) {
            this.device_type = device_type;
            return this;
        }

        public Builder setDeviceTypeId(Integer device_type_id) {
            this.device_type_id = device_type_id;
            return this;
        }

        public UploadResultRequest build() {
            return new UploadResultRequest(batch_id, lot_id, sample_id, scan_id, commodity_id, commodity_name,
                    scan_by_user_code, location, device_serial_no, weight, quantity_unit,
                    vendor_code, farmer_code, variety_id, device_type, device_type_id);
        }
    }
}