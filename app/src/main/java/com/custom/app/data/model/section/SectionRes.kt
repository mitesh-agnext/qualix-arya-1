package com.custom.app.data.model.section

import com.google.gson.annotations.SerializedName

class SectionRes {

    @SerializedName("division_id")
    var division_id: String? = null
    @SerializedName("section_id")
    var section_id: String? = null
    @SerializedName("division_name")
    var division_name: String? = null
    @SerializedName("name")
    var name: String? = null
    @SerializedName("total_area")
    var total_area: String? = null
    @SerializedName("garden_id")
    var garden_id: String? = null
    @SerializedName("garden_name")
    var garden_name: String? = null
    @SerializedName("section_indices")
    var section_indices: ArrayList<SectionIndices>? = null

}

class  SectionIndices
{

    @SerializedName("section_indice_id")
    var section_indice_id: String? = null
    @SerializedName("latitude")
    var latitude: String? = null
    @SerializedName("longitude")
    var longitude: String? = null

}