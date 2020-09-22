package com.agnext.sensenextmyadmin.ui.auth.login

import android.content.Context


class RefreshInteractor(private val context: Context) {

    interface OnRefreshFinishedListener {
        fun onRefreshSuccess(token:String)
        fun onRefreshFailed()
    }

    fun getRefreshedToken(oldToken: String, success:Boolean, listner:OnRefreshFinishedListener){

        if(success){
            listner.onRefreshSuccess(oldToken)
        }else{
            listner.onRefreshFailed()
        }

    }

}