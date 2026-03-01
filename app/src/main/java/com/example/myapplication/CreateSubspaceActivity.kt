package com.example.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class CreateSubspaceActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_subspace)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val etName = findViewById<TextInputEditText>(R.id.etSubspaceName)
        val etDescription = findViewById<TextInputEditText>(R.id.etDescription)
        val btnCreate = findViewById<MaterialButton>(R.id.btnCreateSubspace)

        btnCreate.setOnClickListener {

            val name = etName.text.toString().trim()
            val description = etDescription.text.toString().trim()

            if (name.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val inviteCode = generateInviteCode()
            val currentUserId = auth.currentUser!!.uid

            val subspaceMap = hashMapOf(
                "name" to name,
                "description" to description,
                "inviteCode" to inviteCode,
                "isActive" to true,
                "admins" to listOf(currentUserId),
                "leadAdmins" to listOf(currentUserId),
                "createdAt" to Date()
            )

            db.collection("subspaces")
                .add(subspaceMap)
                .addOnSuccessListener {
                    Toast.makeText(this, "Subspace Created! Code: $inviteCode", Toast.LENGTH_LONG).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to create subspace", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun generateInviteCode(): String {
        return (100000..999999).random().toString()
    }
}
