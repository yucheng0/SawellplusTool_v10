package com.example.sawellplustool

import android.content.Context
import android.util.Log

//val TAG = "myTag"

class AccountRecord(var remeberme: Boolean, var sgAccount: String, var sgPassword: String) {
 //回傳值內的
    var returen_remeberme = false
    var return_sgAccount:String?= ""
    var return_sgPassword:String? = ""

    fun save(context: Context) {
        val sharedPreferences = context.getSharedPreferences("MyShared", Context.MODE_PRIVATE)
        Log.d(TAG, "sharedPreferencesSave: ${sharedPreferences}")
        val editor = sharedPreferences.edit()
        editor.putBoolean("remeberme", remeberme)
        editor.putString("sgAccount", sgAccount)
        editor.putString("sgPassword", sgPassword)
        editor.apply()
    }

    fun read(context: Context) {
        val sharedPreferences = context.getSharedPreferences("MyShared", Context.MODE_PRIVATE)
        Log.d(TAG, "sharedPreferencesRead: ${sharedPreferences}")
         returen_remeberme = sharedPreferences.getBoolean("remeberme", false)       //為怕當機所以有一個默認值
         return_sgAccount = sharedPreferences.getString("sgAccount", "")
         return_sgPassword = sharedPreferences.getString("sgPassword", "")

 //       Log.d(TAG, "x,y,z: ${x},${y},${z}} ")
    }
}