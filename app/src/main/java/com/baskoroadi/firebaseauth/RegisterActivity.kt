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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.android.synthetic.main.layout_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

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

    }

    private fun signUp(){
        val email = edittext_signup_email.text.toString().trim()
        val pass = edittext_signup_pass.text.toString().trim()

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)){
            Toast.makeText(this, "Lengkapi Isian", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this, "Register Sukses", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,LoginActivity::class.java))
                } else {
                    stopProgress()
                    if (task.exception is FirebaseAuthWeakPasswordException){
                        Toast.makeText(this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show()
                    }else if (task.exception is FirebaseAuthInvalidCredentialsException){
                        Toast.makeText(this, "Email tidak valid", Toast.LENGTH_SHORT).show()
                    }else if (task.exception is FirebaseAuthUserCollisionException){
                        Toast.makeText(this, "Email sudah terdaftar", Toast.LENGTH_SHORT).show()
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
