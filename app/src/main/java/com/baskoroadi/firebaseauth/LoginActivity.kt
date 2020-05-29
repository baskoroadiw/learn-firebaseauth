package com.baskoroadi.firebaseauth

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import kotlinx.android.synthetic.main.layout_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        cirLoginButton.setOnClickListener {
            bindProgressButton(cirLoginButton)
            cirLoginButton.attachTextChangeAnimator()

            cirLoginButton.showProgress{
                buttonTextRes = R.string.login_loading
                progressColor = Color.WHITE
            }
        }

        btn_signup.setOnClickListener {
//            cirLoginButton.hideProgress(R.string.login_text)
            startActivity(Intent(this,RegisterActivity::class.java))
        }
    }
}
