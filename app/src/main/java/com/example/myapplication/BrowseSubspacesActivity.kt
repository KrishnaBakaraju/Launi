package com.example.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class BrowseSubspacesActivity : AppCompatActivity() {

    private val subspaceList = mutableListOf<Subspace>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BrowseSubspaceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse_subspaces)

        recyclerView = findViewById(R.id.recyclerBrowse)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadSubspaces()
    }

    private fun loadSubspaces() {

        val db = FirebaseFirestore.getInstance()

        db.collection("subspaces")
            .whereEqualTo("isActive", true)
            .get()
            .addOnSuccessListener { documents ->

                subspaceList.clear()

                for (doc in documents) {
                    val subspace = Subspace(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        description = doc.getString("description") ?: "",
                        inviteCode = doc.getString("inviteCode") ?: ""
                    )
                    subspaceList.add(subspace)
                }

                adapter = BrowseSubspaceAdapter(
                    subspaceList,
                    true,
                    { subspace ->
                        sendAdminRequest(subspace)
                    },
                    { } // no item click in browse
                )

                recyclerView.adapter = adapter
            }
    }

    private fun sendAdminRequest(subspace: Subspace) {

        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val db = FirebaseFirestore.getInstance()

        // Step 1: Check if already admin
        db.collection("subspaces")
            .document(subspace.id)
            .get()
            .addOnSuccessListener { doc ->

                val admins = doc.get("admins") as? List<String> ?: emptyList()

                if (admins.contains(userId)) {
                    Toast.makeText(this, "You are already an admin", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                // Step 2: Check if request already exists
                db.collection("admin_requests")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("subspaceId", subspace.id)
                    .get()
                    .addOnSuccessListener { query ->

                        if (!query.isEmpty) {

                            val existingStatus = query.documents[0].getString("status")

                            when (existingStatus) {
                                "pending" -> {
                                    Toast.makeText(this, "Request already pending", Toast.LENGTH_SHORT).show()
                                    return@addOnSuccessListener
                                }
                                "approved" -> {
                                    Toast.makeText(this, "Already approved", Toast.LENGTH_SHORT).show()
                                    return@addOnSuccessListener
                                }
                                "rejected" -> {
                                    Toast.makeText(this, "Request was rejected", Toast.LENGTH_SHORT).show()
                                    return@addOnSuccessListener
                                }
                            }
                        }

                        // Step 3: Create new request
                        val requestMap = hashMapOf(
                            "userId" to userId,
                            "subspaceId" to subspace.id,
                            "status" to "pending",
                            "timestamp" to System.currentTimeMillis()
                        )

                        db.collection("admin_requests")
                            .add(requestMap)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Request Sent", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                            }
                    }
            }
    }

}
