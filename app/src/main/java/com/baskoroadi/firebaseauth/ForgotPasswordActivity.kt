package com.baskoroadi.firebaseauth

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
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
            startProgress()
            auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    stopProgress()
                    Toast.makeText(this, "Email terkirim, mohon cek email anda", Toast.LENGTH_SHORT).show()
                    finish()
                    startActivity(Intent(this,LoginActivity::class.java))
                }
            }
        }
    }

    private fun startProgress(){
        bindProgressButton(button_send_email_forgot)
        button_send_email_forgot.attachTextChangeAnimator()

        button_send_email_forgot.showProgress{
            buttonTextRes = R.string.login_loading
            progressColor = Color.WHITE
        }
    }

    private fun stopProgress(){
        button_send_email_forgot.hideProgress(R.string.forgot_text)
    }
}
