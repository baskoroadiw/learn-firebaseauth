package com.baskoroadi.firebaseauth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkLogin()

        auth = FirebaseAuth.getInstance()
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
