package com.baskoroadi.firebaseauth

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.android.synthetic.main.layout_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var view: View

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
        view = findViewById(R.id.rootview_login)
    }

    private fun login(){
        val email = editTextEmail.text.toString().trim()
        val pass = editTextPassword.text.toString().trim()

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)){
            editTextEmail.error = "Email harus diisi"
            editTextPassword.error = "Password harus diisi"
        }else{
            startProgress()
            loginProccess(email, pass)
        }
    }

    private fun loginProccess(email:String, pass:String){
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Snackbar.make(view,"Selamat, anda berhasil masuk",Snackbar.LENGTH_SHORT).setBackgroundTint(Color.GREEN).show()
                    stopProgress()
                    isLogin()
                    val thread: Thread = object : Thread() {
                        override fun run() {
                            try {
                                sleep(2000)
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                            }
                        }
                    }
                    thread.start()
                } else {
                    stopProgress()
                    if (task.exception is FirebaseAuthInvalidUserException){
                        Snackbar.make(view,"Email yang anda masukkan tidak terdaftar", Snackbar.LENGTH_SHORT).setBackgroundTint(Color.RED).show()
                    }else if (task.exception is FirebaseAuthInvalidCredentialsException){
                        Snackbar.make(view,"Email atau Password yang anda masukkan salah",Snackbar.LENGTH_SHORT).setBackgroundTint(Color.RED).show()
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
