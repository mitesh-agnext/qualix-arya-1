package com.custom.app.ui.createData.rules.config.list

import com.google.gson.annotations.SerializedName

class RuleConfigRes{

    @SerializedName("rule_config_id")
    var rule_config_id:Int?=null
    @SerializedName("rule_config_name")
    var rule_config_name:String?=null
    @SerializedName("customer_id")
    var customer_id:Int?=null

}