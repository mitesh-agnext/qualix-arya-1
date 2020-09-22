package com.custom.app.ui.createData.flcScan

import com.google.gson.annotations.SerializedName

class ImageUploadResult {

    @SerializedName("success")
    var success: String? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("white_image_uploaded")
    var white_image_uploaded: String? = null

}