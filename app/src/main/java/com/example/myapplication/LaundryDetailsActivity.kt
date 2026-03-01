package com.example.myapplication

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class LaundryDetailsActivity : AppCompatActivity() {

    private lateinit var tvName: TextView
    private lateinit var tvRegNo: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvClothes: TextView
    private lateinit var tvCreatedAt: TextView
    private lateinit var btnComplete: Button

    private val db = FirebaseFirestore.getInstance()

    private lateinit var recordId: String
    private lateinit var studentId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_laundry_details)

        recordId = intent.getStringExtra("recordId") ?: ""

        if (recordId.isEmpty()) {
            Toast.makeText(this, "Invalid Record", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        tvName = findViewById(R.id.tvName)
        tvRegNo = findViewById(R.id.tvRegNo)
        tvPhone = findViewById(R.id.tvPhone)
        tvClothes = findViewById(R.id.tvClothes)
        tvCreatedAt = findViewById(R.id.tvCreatedAt)
        btnComplete = findViewById(R.id.btnComplete)

        loadLaundryDetails()

        btnComplete.setOnClickListener {
            markAsComplete()
        }
    }

    private fun loadLaundryDetails() {

        db.collection("laundry_records")
            .document(recordId)
            .get()
            .addOnSuccessListener { doc ->

                if (!doc.exists()) {
                    Toast.makeText(this, "Record not found", Toast.LENGTH_SHORT).show()
                    finish()
                    return@addOnSuccessListener
                }

                studentId = doc.getString("studentId") ?: ""
                val clothesCount = doc.getLong("clothesCount") ?: 0
                val createdAt = doc.getLong("createdAt") ?: 0L

                tvClothes.text = clothesCount.toString()

                val sdf = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
                tvCreatedAt.text = sdf.format(Date(createdAt))

                loadStudentDetails(studentId)
            }
    }

    private fun loadStudentDetails(studentId: String) {

        db.collection("users")
            .document(studentId)
            .get()
            .addOnSuccessListener { doc ->
                tvName.text = doc.getString("name") ?: "N/A"
                tvRegNo.text = doc.getString("regNo") ?: "N/A"
                tvPhone.text = doc.getString("phone") ?: "N/A"
            }
    }

    private fun markAsComplete() {

        db.collection("laundry_records")
            .document(recordId)
            .update(
                mapOf(
                    "status" to "completed",
                    "completedAt" to System.currentTimeMillis()
                )
            )
            .addOnSuccessListener {
                Toast.makeText(this, "Laundry Completed", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }
    }
}