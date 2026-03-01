package com.example.myapplication

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent

import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth


class UserDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_dashboard)

        findViewById<MaterialButton>(R.id.btnJoinSubspace).setOnClickListener {
            startActivity(Intent(this, BrowseSubspacesStudentActivity::class.java))
        }

        findViewById<MaterialButton>(R.id.btnMySubspaces).setOnClickListener {
            startActivity(Intent(this, StudentSubspacesActivity::class.java))
        }
    }
}