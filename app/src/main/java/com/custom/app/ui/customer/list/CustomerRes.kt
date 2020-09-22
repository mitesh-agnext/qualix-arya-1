package com.custom.app.ui.customer.list

import com.google.gson.annotations.SerializedName

class CustomerRes {

    @SerializedName("customer_id")
    var customer_id: Int? = null
    @SerializedName("createdOn")
    var createdOn: String? = null
    @SerializedName("gst")
    var gst: String? = null
    @SerializedName("pan")
    var pan: String? = null
    @SerializedName("contact_number")
    var contact_number: String? = null
    @SerializedName("email")
    var email: String? = null
    @SerializedName("name")
    var name: String? = null
    @SerializedName("customer_uuid")
    var customer_uuid: String? = null
    @SerializedName("updatedOn")
    var updatedOn: String? = null
    @SerializedName("customer_type_id")
    var customer_type_id: String? = null
    @SerializedName("customer_type")
    var customer_type: String? = null
    @SerializedName("status")
    var status: String? = null
    @SerializedName("status_name")
    var statusName: String? = null
    @SerializedName("address")
    var address: ArrayList<AddressData>? = null
    @SerializedName("bank_details")
    var bankDetails: ArrayList<BankDetailData>? = null
    @SerializedName("billingDetails")
    var billingDetails: ArrayList<BillingDetailData>? = null

}

class AddressData {

    @SerializedName("city")
    var city: String? = null
    @SerializedName("country")
    var country: String? = null
    @SerializedName("state")
    var state: String? = null
    @SerializedName("address_id")
    var address_id: String? = null
    @SerializedName("address1")
    var address1: String? = null
    @SerializedName("address_line2")
    var addressLine2: String? = null
    @SerializedName("created_by")
    var created_by: String? = null
    @SerializedName("created_on")
    var created_on: String? = null
    @SerializedName("updated_by")
    var updated_by: String? = null
    @SerializedName("updated_on")
    var updated_on: String? = null
    @SerializedName("pincode")
    var pincode: String? = null
    @SerializedName("address_type_id")
    var address_type_id: String? = null

}

class BankDetailData {

    @SerializedName("bank_id")
    var bank_id: String? = null
    @SerializedName("bank_account_number")
    var bank_account_number: String? = null
    @SerializedName("bank_address")
    var bank_address: String? = null
    @SerializedName("branch")
    var branch: String? = null
    @SerializedName("ifsc")
    var ifsc: String? = null
    @SerializedName("bank_name")
    var bank_name: String? = null
    @SerializedName("bank_uuid")
    var bank_uuid: String? = null
    @SerializedName("created_by")
    var created_by: String? = null
    @SerializedName("created_on")
    var created_on: String? = null
    @SerializedName("updated_by")
    var updated_by: String? = null
    @SerializedName("updated_on")
    var updated_on: String? = null
    @SerializedName("customer_id")
    var customerId: String? = null
    @SerializedName("status_id")
    var status_id: String? = null

}

class BillingDetailData {

    @SerializedName("createdOn")
    var createdOn: String? = null
    @SerializedName("billingId")
    var billingId: String? = null
    @SerializedName("billingAccountNumber")
    var billingAccountNumber: String? = null
    @SerializedName("billingAddress")
    var billingAddress: String? = null
    @SerializedName("billingUuid")
    var billingUuid: String? = null
    var createdBy: String? = null
    var updatedBy: String? = null
    var updatedOn: String? = null
    @SerializedName("customerId")
    var customerId: String? = null

}