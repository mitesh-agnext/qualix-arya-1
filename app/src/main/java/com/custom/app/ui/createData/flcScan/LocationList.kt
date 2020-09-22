package com.custom.app.ui.createData.flcScan

import com.google.gson.annotations.SerializedName


class LocationList {

    @SerializedName("location_id")
    var location_id: Int? = null
    @SerializedName("city_code")
    var city_code: String? = null
    @SerializedName("code")
    var code: String? = null
    @SerializedName("location")
    var location: String? = null
    @SerializedName("name")
    var name: String? = null
    @SerializedName("commercial_location_type_id")
    var commercial_location_type_id: Int? = null
    @SerializedName("commercial_location_type_desc")
    var commercial_location_type_desc: String? = null

}

class GardenList {

    @SerializedName("garden_id")
    var garden_id: Int? = null
    @SerializedName("name")
    var name: String? = null
    @SerializedName("location_name")
    var location_name: String? = null
    @SerializedName("location_id")
    var location_id: Int? = null

}

class DevisionList {

    @SerializedName("division_id")
    var division_id: Int? = null
    @SerializedName("name")
    var division_name: String? = null
    @SerializedName("garden_id")
    var garden_id: Int? = null
    @SerializedName("garden_name")
    var garden_name: String? = null
}

class SectionData {

    @SerializedName("division_id")
    var division_id: Int? = null
    @SerializedName("section_id")
    var section_id: Int? = null
    @SerializedName("name")
    var name: String? = null
    @SerializedName("division_name")
    var division_name: String? = null
    @SerializedName("total_area")
    var total_area: Double? = null
    @SerializedName("garden_id")
    var garden_id: Int? = null
    @SerializedName("garden_name")
    var garden_name: String? = null
}