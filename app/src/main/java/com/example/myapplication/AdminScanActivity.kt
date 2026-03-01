package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class AdminScanActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var currentSubspaceId: String

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            handleQR(result.contents)
        } else {
            Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentSubspaceId = intent.getStringExtra("subspaceId") ?: ""

        startScanner()
    }

    private fun startScanner() {
        val options = ScanOptions()
        options.setPrompt("Scan Student QR")
        options.setBeepEnabled(true)
        options.setOrientationLocked(true)
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)

        barcodeLauncher.launch(options)
    }

    private fun handleQR(qrContent: String?) {

        if (qrContent.isNullOrBlank()) {
            Toast.makeText(this, "Invalid QR", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val cleaned = qrContent.trim()
        val parts = cleaned.split(":")

        if (parts.size != 2) {
            Toast.makeText(this, "Invalid QR format", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val qrSubspaceId = parts[0]
        val userId = parts[1]

        if (qrSubspaceId != currentSubspaceId) {
            Toast.makeText(this, "Wrong subspace QR", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        verifyStudent(qrSubspaceId, userId)
    }

    private fun verifyStudent(subspaceId: String, userId: String) {

        val docId = "${userId}_${subspaceId}"

        db.collection("student_subspaces")
            .document(docId)
            .get()
            .addOnSuccessListener { doc ->

                if (!doc.exists()) {
                    Toast.makeText(this, "Student not approved", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    checkActiveLaundry(subspaceId, userId)
                }
            }
    }

    private fun checkActiveLaundry(subspaceId: String, userId: String) {

        db.collection("laundry_records")
            .whereEqualTo("userId", userId)
            .whereEqualTo("subspaceId", subspaceId)
            .whereEqualTo("status", "processing")
            .get()
            .addOnSuccessListener { docs ->
                Log.d("SCAN_DEBUG", "Docs found: ${docs.size()}")
                if (docs.isEmpty) {

                    val intent = Intent(this, AcceptLaundryActivity::class.java)
                    intent.putExtra("userId", userId)
                    intent.putExtra("subspaceId", subspaceId)
                    startActivity(intent)

                } else {

                    val recordId = docs.documents[0].id

                    val intent = Intent(this, LaundryDetailsActivity::class.java)
                    intent.putExtra("recordId", recordId)
                    startActivity(intent)
                }

                finish()
            }
    }
}