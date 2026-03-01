package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
//import com.google.firebase.firestore.core.View


class BrowseSubspacesStudentActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var recycler: RecyclerView
    private val subspaces = mutableListOf<DocumentSnapshot>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse_subspaces_student)

        recycler = findViewById(R.id.recyclerSubspaces)
        recycler.layoutManager = LinearLayoutManager(this)

        loadSubspaces()
    }

    private fun loadSubspaces() {
        db.collection("subspaces")
            .get()
            .addOnSuccessListener { docs ->
                subspaces.clear()
                subspaces.addAll(docs)
                recycler.adapter = StudentSubspaceAdapter(subspaces)
            }
    }

    inner class StudentSubspaceAdapter(
        private val list: List<DocumentSnapshot>
    ) : RecyclerView.Adapter<StudentSubspaceAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView = view.findViewById(R.id.tvSubspaceName)
            val status: TextView = view.findViewById(R.id.tvStatus)
            val joinBtn: MaterialButton = view.findViewById(R.id.btnJoin)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_subspace_student, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount() = list.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val doc = list[position]
            val subspaceId = doc.id
            val userId = auth.currentUser?.uid ?: return
            val requestId = "${userId}_${subspaceId}"

            holder.name.text = doc.getString("name")

            // Check approval status
            db.collection("student_subspaces")
                .document(requestId)
                .get()
                .addOnSuccessListener { approvedDoc ->
                    if (approvedDoc.exists()) {
                        holder.status.text = "Approved"
                        holder.joinBtn.visibility = View.GONE
                    } else {
                        checkPending(holder, requestId, subspaceId)
                    }
                }
        }

        private fun checkPending(holder: ViewHolder, requestId: String, subspaceId: String) {

            db.collection("student_requests")
                .document(requestId)
                .get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        holder.status.text = "Pending Approval"
                        holder.joinBtn.visibility = View.GONE
                    } else {
                        holder.status.text = "Not Joined"
                        holder.joinBtn.visibility = View.VISIBLE
                        holder.joinBtn.setOnClickListener {
                            sendRequest(requestId, subspaceId)
                        }
                    }
                }
        }

        private fun sendRequest(requestId: String, subspaceId: String) {

            val data = hashMapOf(
                "userId" to auth.currentUser?.uid,
                "subspaceId" to subspaceId,
                "status" to "pending",
                "createdAt" to System.currentTimeMillis()
            )

            db.collection("student_requests")
                .document(requestId)
                .set(data)
                .addOnSuccessListener {
                    Toast.makeText(
                        this@BrowseSubspacesStudentActivity,
                        "Request Sent",
                        Toast.LENGTH_SHORT
                    ).show()
                    loadSubspaces()
                }
        }
    }
}