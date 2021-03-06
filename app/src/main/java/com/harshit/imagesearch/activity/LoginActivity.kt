package com.harshit.imagesearch.activity

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager

import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.harshit.imagesearch.R


class LoginActivity : AppCompatActivity() {
    var btnLogin: Button? = null
    var etLoginEmail: EditText? = null
    var etLoginPassword: EditText? = null
    var txtSignUp: TextView? = null


    var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        btnLogin = findViewById<Button>(R.id.btnLogin)
        etLoginEmail = findViewById<EditText>(R.id.etLoginEmail)
        etLoginPassword = findViewById<EditText>(R.id.etLoginPassword)
        txtSignUp = findViewById<TextView>(R.id.txtSignUp)
        mAuth = FirebaseAuth.getInstance()
        btnLogin!!.setOnClickListener { loginUser() }
        txtSignUp!!.setOnClickListener { view: View? ->
            startActivity(
                Intent(
                    this@LoginActivity,
                    RegisterActivity::class.java
                )

            )
            finish()
        }
    }

    private fun loginUser() {
        val loginEmail = etLoginEmail!!.text.toString()
        val loginPassword = etLoginPassword!!.text.toString()
        when {
            TextUtils.isEmpty(loginEmail) -> {
                etLoginEmail!!.error = "Email cannot be empty.."
                etLoginEmail!!.requestFocus()
            }
            TextUtils.isEmpty(loginPassword) -> {
                etLoginPassword!!.error = "Password cannot be empty.."
                etLoginPassword!!.requestFocus()
            }
            else -> {
                mAuth?.signInWithEmailAndPassword(loginEmail, loginPassword)?.addOnCompleteListener(
                    OnCompleteListener<AuthResult?> { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this@LoginActivity, "Welcome!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                "Login error: Your user ID or Password is incorrect ",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            }
        }
    }


    override fun onStart() {
        super.onStart()
        val user: FirebaseUser? = mAuth?.currentUser
        if (user != null) {
            startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
            finish()
        }
    }

    fun isOnline(): Boolean {
        val conMgr =
            getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = conMgr.activeNetworkInfo
        if ((netInfo == null) || !netInfo.isConnected || !netInfo.isAvailable) {
            AlertDialog.Builder(this@LoginActivity)
                .setTitle("No Internet Connection!")
                .setMessage("Please Connect to Internet..")
                .setCancelable(true)
                .setPositiveButton("Ok",
                    DialogInterface.OnClickListener { dialog, which ->
                        this@LoginActivity.startActivity(
                            Intent(
                                Settings.ACTION_WIRELESS_SETTINGS
                            )
                        )
                    }).show()
            return false
        }
        return true
    }

    override fun onResume() {
        isOnline()
        super.onResume()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        etLoginEmail!!.setText("")
        etLoginPassword!!.setText("")
    }
}
