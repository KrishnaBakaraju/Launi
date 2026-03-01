package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)
        val btnRegister = findViewById<MaterialButton>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        btnLogin.setOnClickListener {

            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                        val db = FirebaseFirestore.getInstance()

                        db.collection("users")
                            .document(uid)
                            .get()
                            .addOnSuccessListener { doc ->

                                if (!doc.exists()) {
                                    Toast.makeText(this, "User record missing", Toast.LENGTH_SHORT).show()
                                    return@addOnSuccessListener
                                }

                                val role = doc.getString("role")

                                if (role == "admin") {
                                    startActivity(Intent(this, AdminDashboardActivity::class.java))
                                    finish()
                                }
                                else if (role == "student") {

                                    val name = doc.getString("name")

                                    if (name == null) {
                                        // Student profile incomplete
                                        startActivity(Intent(this, CompleteProfileActivity::class.java))
                                    } else {
                                        startActivity(Intent(this, UserDashboardActivity::class.java))
                                    }

                                    finish()
                                }
                            }
                    }
                    else {
                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}
