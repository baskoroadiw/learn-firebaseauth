package com.baskoroadi.firebaseauth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkLogin()
    }

    private fun checkLogin(){
        val sharedPref = this.getSharedPreferences("LoginPref",Context.MODE_PRIVATE)
        val isLogin = sharedPref.getBoolean("isLogin",false)

        if (!isLogin){
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }
    }
}
