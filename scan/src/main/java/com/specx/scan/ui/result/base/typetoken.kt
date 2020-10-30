package com.specx.scan.ui.result.base

import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.specx.scan.data.model.result.ResultItem

class typetoken {
    fun moveData(context: Context, result: ResultItem, intent: Intent) {
        val gson = Gson()
        val type = object : TypeToken<ResultItem>() {}.type
        val json = gson.toJson(result, type)
        intent.putExtra("selectObject", json)
        context.startActivity(intent)

    }
}