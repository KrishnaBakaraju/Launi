package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth

class AdminDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        val btnCreate = findViewById<MaterialButton>(R.id.btnCreateSubspace)
        val btnLogout = findViewById<MaterialButton>(R.id.btnLogout)

        btnCreate.setOnClickListener {
            startActivity(Intent(this, CreateSubspaceActivity::class.java))
        }

        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        val btnMySubspaces = findViewById<MaterialButton>(R.id.btnMySubspaces)

        btnMySubspaces.setOnClickListener {
            startActivity(Intent(this, MySubspacesActivity::class.java))
        }

        val btnBrowse = findViewById<MaterialButton>(R.id.btnBrowseSubspaces)

        btnBrowse.setOnClickListener {
            startActivity(Intent(this, BrowseSubspacesActivity::class.java))
        }


    }
}
