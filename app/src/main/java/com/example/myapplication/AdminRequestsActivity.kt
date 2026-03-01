package com.example.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class AdminRequestsActivity : AppCompatActivity() {

    private lateinit var subspaceId: String
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdminRequestAdapter

    private val db = FirebaseFirestore.getInstance()
    private val requestList = mutableListOf<AdminRequest>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_requests)

        // 🔐 Get subspace ID from intent
        subspaceId = intent.getStringExtra("subspaceId") ?: ""

        if (subspaceId.isEmpty()) {
            Toast.makeText(this, "Invalid subspace", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        recyclerView = findViewById(R.id.recyclerRequests)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = AdminRequestAdapter(
            requestList,
            { request -> approveRequest(request) },
            { request -> rejectRequest(request) }
        )

        recyclerView.adapter = adapter

        loadRequests()
    }

    // 🔍 Load pending requests for this subspace only
    private fun loadRequests() {

        db.collection("admin_requests")
            .whereEqualTo("status", "pending")
            .whereEqualTo("subspaceId", subspaceId)
            .get()
            .addOnSuccessListener { documents ->

                requestList.clear()

                for (doc in documents) {
                    val request = AdminRequest(
                        id = doc.id,
                        userId = doc.getString("userId") ?: "",
                        subspaceId = doc.getString("subspaceId") ?: "",
                        status = doc.getString("status") ?: ""
                    )
                    requestList.add(request)
                }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load requests", Toast.LENGTH_SHORT).show()
            }
    }

    // ✅ Approve request
    private fun approveRequest(request: AdminRequest) {

        // Add user to subspace admins array
        db.collection("subspaces")
            .document(request.subspaceId)
            .update("admins", FieldValue.arrayUnion(request.userId))
            .addOnSuccessListener {

                // Update request status
                db.collection("admin_requests")
                    .document(request.id)
                    .update("status", "approved")

                Toast.makeText(this, "Request Approved", Toast.LENGTH_SHORT).show()
                loadRequests()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Approval failed", Toast.LENGTH_SHORT).show()
            }
    }

    // ❌ Reject request
    private fun rejectRequest(request: AdminRequest) {

        db.collection("admin_requests")
            .document(request.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Request Rejected", Toast.LENGTH_SHORT).show()
                loadRequests()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Rejection failed", Toast.LENGTH_SHORT).show()
            }
    }
}
