package com.baskoroadi.firebaseauth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.layout_forgot_password.*

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        auth = FirebaseAuth.getInstance()

        button_send_email_forgot.setOnClickListener {
            forgotPassword()
        }
    }

    private fun forgotPassword(){
        val email = editTextEmail_forgot.text.toString().trim()

        if (TextUtils.isEmpty(email)){
            editTextEmail_forgot.error = "Harus diisi"
        }
        else{
            auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Email terkirim, mohon cek email anda", Toast.LENGTH_SHORT).show()
                    finish()
                    startActivity(Intent(this,LoginActivity::class.java))
                }
            }
        }
    }
}
