package com.baskoroadi.firebaseauth

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_reauthenticate.view.*

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var view: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkLogin()

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null){
            updateUIAkun()
        }

        tv_send_verification.setOnClickListener {
            sendEmailVerification()
        }
        view = findViewById(R.id.rootview_mainactivity)
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
                    Snackbar.make(view,"Email verifikasi terkirim, mohon cek email anda",Snackbar.LENGTH_SHORT).setBackgroundTint(Color.GREEN).show()
                }
            }
    }

    private fun checkLogin(){
        val sharedPref = this.getSharedPreferences("LoginPref",Context.MODE_PRIVATE)
        val isLogin = sharedPref.getBoolean("isLogin",false)

        if (!isLogin){
            finish()
            startActivity(Intent(this,LoginActivity::class.java))
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
        Snackbar.make(view,"Berhasil Keluar",Snackbar.LENGTH_SHORT).setBackgroundTint(Color.GREEN).show()
        val thread: Thread = object : Thread() {
            override fun run() {
                try {
                    sleep(2000)
                    finish()
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
        thread.start()
    }

    private fun showDialogDeleteAccount() {
        val builder = AlertDialog.Builder(this)

        val inflater = this.layoutInflater

        val dialogView = inflater.inflate(R.layout.layout_reauthenticate, null)

        builder.setTitle("Konfimasi Akun")

        builder.setView(dialogView)
            .setPositiveButton("Continue") { dialog, id ->
                val pass = dialogView.edittext_reauth_pass.text.toString()
                reauthenticate(pass)
            }
            .setNegativeButton("Cancel") { dialog, id ->
                    dialog.dismiss()
                }
        builder.create().show()
    }

    private fun reauthenticate(password:String){
        val email = user.email.toString()

        val credential = EmailAuthProvider
            .getCredential(email, password)

        user.reauthenticate(credential)
            .addOnSuccessListener { task ->
                deleteAccount()
            }
            .addOnFailureListener { exception: Exception ->
                Log.d(TAG, exception.toString())
                if (exception is FirebaseAuthInvalidCredentialsException){
                    Snackbar.make(view,"Password yang anda masukkan salah",Snackbar.LENGTH_SHORT).setBackgroundTint(Color.RED).show()
                }
            }
    }

    private fun deleteAccount(){
        user.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    sharedPrefNoLogin()
                    Snackbar.make(view,"Akun berhasil terhapus",Snackbar.LENGTH_SHORT).setBackgroundTint(Color.GREEN).show()
                    val thread: Thread = object : Thread() {
                        override fun run() {
                            try {
                                sleep(2000)
                                finish()
                                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                            }
                        }
                    }
                    thread.start()
                }
            }
    }

    private fun sharedPrefNoLogin(){
        val sharedPref = this.getSharedPreferences("LoginPref",Context.MODE_PRIVATE)?: return
        with(sharedPref.edit()){
            putBoolean("isLogin",false)
            commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_logout -> showDialogLogout()
            R.id.menu_hapusakun -> showDialogDeleteAccount()
        }

        return super.onOptionsItemSelected(item)
    }
}
