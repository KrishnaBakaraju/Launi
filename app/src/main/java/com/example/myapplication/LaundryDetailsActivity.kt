package com.example.myapplication

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class LaundryDetailsActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var recordId: String
    private lateinit var btnComplete: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_laundry_details)

        db = FirebaseFirestore.getInstance()

        recordId = intent.getStringExtra("recordId") ?: ""

        if (recordId.isEmpty()) {
            finish()
            return
        }

        btnComplete = findViewById(R.id.btnComplete)

        loadLaundryDetails()

        btnComplete.setOnClickListener {
            markComplete()
        }
    }

    private fun loadLaundryDetails() {

        db.collection("laundry_records")
            .document(recordId)
            .get()
            .addOnSuccessListener { doc ->

                if (!doc.exists()) return@addOnSuccessListener

                findViewById<TextView>(R.id.tvClothes)
                    .text = doc.getLong("clothesCount").toString()

                val createdAt = doc.getLong("createdAt") ?: 0L
                val sdf = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())

                findViewById<TextView>(R.id.tvCreatedAt)
                    .text = sdf.format(Date(createdAt))
            }
    }

    private fun markComplete() {

        db.collection("laundry_records")
            .document(recordId)
            .update(
                mapOf(
                    "status" to "completed",
                    "completedAt" to System.currentTimeMillis()
                )
            )
            .addOnSuccessListener {
                Toast.makeText(this, "Marked Complete", Toast.LENGTH_SHORT).show()
                finish()
            }
    }
}