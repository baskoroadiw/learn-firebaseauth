package com.baskoroadi.firebaseauth

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkLogin()

        auth = FirebaseAuth.getInstance()

        updateUIAkun()

        tv_send_verification.setOnClickListener {
            sendEmailVerification()
        }
    }

    private fun updateUIAkun(){
        user = auth.currentUser!!
        user.let {
            val email = user.email
            val emailVerified = user.isEmailVerified
            val statusVerified:String

            if (emailVerified == false){
                statusVerified = "Belum Terverifikasi"
                tv_verified_akun.setTextColor(Color.RED)
            }
            else{
                statusVerified = "Sudah Terverifikasi"
                tv_verified_akun.setTextColor(Color.GREEN)
                tv_send_verification.visibility = View.GONE
            }

            tv_email_akun.text = "Email\t\t\t\t\t\t\t: $email"
            tv_verified_akun.text ="\t"+statusVerified
        }
    }

    private fun sendEmailVerification(){
        user.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Email terkirim, mohon cek email anda", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkLogin(){
        val sharedPref = this.getSharedPreferences("LoginPref",Context.MODE_PRIVATE)
        val isLogin = sharedPref.getBoolean("isLogin",false)

        if (!isLogin){
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }
    }

    private fun showDialogLogout() {
        val builder = AlertDialog.Builder(this, R.style.ThemeOverlay_AppCompat_Dialog_Alert)
            .setTitle("Logout")
            .setMessage("Ingin Logout?")
            .setPositiveButton("Ya") { dialog, which ->
                logout()
            }
            .setNegativeButton("Tidak", null)
        builder.create().show()
    }

    private fun logout(){
        auth.signOut()

        val sharedPref = this.getSharedPreferences("LoginPref",Context.MODE_PRIVATE)?: return
        with(sharedPref.edit()){
            putBoolean("isLogin",false)
            commit()
        }
        Toast.makeText(this, "Sukses Logout", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this,LoginActivity::class.java))
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_logout -> showDialogLogout()
        }

        return super.onOptionsItemSelected(item)
    }
}
