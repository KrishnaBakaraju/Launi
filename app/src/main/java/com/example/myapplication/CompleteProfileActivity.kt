package com.example.myapplication

import android.widget.EditText
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CompleteProfileActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_profile)

        val name = findViewById<EditText>(R.id.etName)
        val regNo = findViewById<EditText>(R.id.etRegNo)
        val phone = findViewById<EditText>(R.id.etPhone)
        val btnSave = findViewById<MaterialButton>(R.id.btnSave)

        btnSave.setOnClickListener {

            val uid = auth.currentUser?.uid ?: return@setOnClickListener

            val data = hashMapOf(
                "name" to name.text.toString(),
                "regNo" to regNo.text.toString(),
                "phone" to phone.text.toString(),
                "role" to "student"
            )

            db.collection("users")
                .document(uid)
                .set(data)
                .addOnSuccessListener {
                    startActivity(Intent(this, UserDashboardActivity::class.java))
                    finish()
                }
        }
    }
}