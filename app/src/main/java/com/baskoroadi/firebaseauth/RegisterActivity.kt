package com.baskoroadi.firebaseauth

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
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.android.synthetic.main.layout_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var view: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        txtHaveAccount.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }

        auth = FirebaseAuth.getInstance()

        buttonRegister.setOnClickListener {
            signUp()
        }
        view = findViewById(R.id.rootview_register)
    }

    private fun signUp(){
        val email = edittext_signup_email.text.toString().trim()
        val pass = edittext_signup_pass.text.toString().trim()

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)){
            edittext_signup_email.error = "Email harus diisi"
            edittext_signup_pass.error = "Password harus diisi"
        }else{
            startProgress()
            signUpProcess(email, pass)
        }
    }

    private fun signUpProcess(email:String, pass:String){
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    stopProgress()
                    Snackbar.make(view,"Selamat, anda berhasil mendaftar",Snackbar.LENGTH_SHORT).setBackgroundTint(Color.GREEN).show()
                    val thread: Thread = object : Thread() {
                        override fun run() {
                            try {
                                sleep(2000)
                                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                            }
                        }
                    }
                    thread.start()
                } else {
                    stopProgress()
                    if (task.exception is FirebaseAuthWeakPasswordException){
                        Snackbar.make(view,"Masukkan password minimal 6 karakter",Snackbar.LENGTH_SHORT).setBackgroundTint(Color.RED).show()
                    }else if (task.exception is FirebaseAuthInvalidCredentialsException){
                        Snackbar.make(view,"Email yang anda masukkan tidak valid",Snackbar.LENGTH_SHORT).setBackgroundTint(Color.RED).show()
                    }else if (task.exception is FirebaseAuthUserCollisionException){
                        Snackbar.make(view,"Email yang anda masukkan sudah terdaftar",Snackbar.LENGTH_SHORT).setBackgroundTint(Color.RED).show()
                    }
                }
            }
    }

    private fun startProgress(){
        bindProgressButton(buttonRegister)
        buttonRegister.attachTextChangeAnimator()

        buttonRegister.showProgress{
            buttonTextRes = R.string.login_loading
            progressColor = Color.WHITE
        }
    }

    private fun stopProgress(){
        buttonRegister.hideProgress(R.string.register)
    }
}
