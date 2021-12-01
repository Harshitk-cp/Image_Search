package com.harshit.imagesearch.activity

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.harshit.imagesearch.R

class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    private lateinit var actionBarToggle: ActionBarDrawerToggle
    private lateinit var drawer: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawer = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener(this)

        mAuth = FirebaseAuth.getInstance()

        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null) {
            openHome()
        }

    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> openHome()
            R.id.nav_faqs -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    FAQsFragment()
                ).commit()
                supportActionBar!!.setTitle("FAQs")
            }
            R.id.nav_about_us -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    AboutUsFragment()
                ).commit()
                supportActionBar!!.setTitle("About Us")
            }
            R.id.nav_logout -> AlertDialog.Builder(this@DashboardActivity)
                .setTitle("Logout!")
                .setMessage("Are you sure you want to Logout?")
                .setCancelable(true)
                .setNegativeButton(
                    "No"
                ) { dialogInterface, i -> dialogInterface.dismiss() }
                .setPositiveButton(
                    "Yes"
                ) { _, _ ->
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this@DashboardActivity, LoginActivity::class.java))
                    finish()
                }.show()
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun openHome() {
        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener(this)
        navigationView.setCheckedItem(R.id.nav_home)
        supportFragmentManager.beginTransaction().replace(
            R.id.fragment_container,
            HomeFragment()
        ).commit()
        supportActionBar!!.setTitle("Home")
    }

}