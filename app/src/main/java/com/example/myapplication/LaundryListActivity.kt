package com.example.myapplication

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
class LaundryListActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var subspaceId: String
    private lateinit var status: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tvTitle = findViewById<TextView>(R.id.tvTitle)

        tvTitle.text = if (status == "processing") {
            "Processing Laundry"
        } else {
            "Completed Laundry"
        }
        setContentView(R.layout.activity_laundry_list)

        subspaceId = intent.getStringExtra("subspaceId") ?: ""
        status = intent.getStringExtra("status") ?: ""

        loadLaundry()
    }

    private fun loadLaundry() {

        db.collection("laundry_records")
            .whereEqualTo("subspaceId", subspaceId)
            .whereEqualTo("status", status)
            .get()
            .addOnSuccessListener { docs ->
                // Display in RecyclerView
            }
    }
}