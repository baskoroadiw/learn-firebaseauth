package com.baskoroadi.firebaseauth

import android.content.Context
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.android.synthetic.main.layout_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btn_signup.setOnClickListener {
            startActivity(Intent(this,RegisterActivity::class.java))
        }

        cirLoginButton.setOnClickListener {
            login()
        }

        tv_forgot_password.setOnClickListener {
            startActivity(Intent(this,ForgotPasswordActivity::class.java))
        }

        auth = FirebaseAuth.getInstance()
    }

    private fun login(){
        val email = editTextEmail.text.toString().trim()
        val pass = editTextPassword.text.toString().trim()

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)){
            Toast.makeText(this, "Lengkapi Isian", Toast.LENGTH_SHORT).show()
        }else{
            startProgress()
            loginProccess(email, pass)
        }
    }

    private fun loginProccess(email:String, pass:String){
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    stopProgress()
                    isLogin()
                    Toast.makeText(this, "Login Sukses", Toast.LENGTH_SHORT).show()
                    val thread: Thread = object : Thread() {
                        override fun run() {
                            try {
                                sleep(1500)
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                finish()
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                            }
                        }
                    }
                    thread.start()
                } else {
                    stopProgress()
                    if (task.exception is FirebaseAuthInvalidUserException){
                        Toast.makeText(this, "Email tidak terdaftar", Toast.LENGTH_SHORT).show()
                    }else if (task.exception is FirebaseAuthInvalidCredentialsException){
                        Toast.makeText(this, "Email atau Password salah", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun startProgress(){
        bindProgressButton(cirLoginButton)
        cirLoginButton.attachTextChangeAnimator()

        cirLoginButton.showProgress{
            buttonTextRes = R.string.login_loading
            progressColor = Color.WHITE
        }
    }

    private fun stopProgress(){
        cirLoginButton.hideProgress(R.string.login_text)
    }

    private fun isLogin(){
        val sharedPref = this.getSharedPreferences("LoginPref",Context.MODE_PRIVATE)?: return
        with(sharedPref.edit()){
            putBoolean("isLogin",true)
            commit()
        }
    }
}
