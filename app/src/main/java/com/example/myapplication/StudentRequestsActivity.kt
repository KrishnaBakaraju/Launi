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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class StudentRequestsActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var recycler: RecyclerView
    private val requestList = mutableListOf<DocumentSnapshot>()
    private lateinit var subspaceId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_requests)

        subspaceId = intent.getStringExtra("subspaceId") ?: ""

        recycler = findViewById(R.id.recyclerRequests)
        recycler.layoutManager = LinearLayoutManager(this)

        loadRequests()
    }

    private fun loadRequests() {
        db.collection("student_requests")
            .whereEqualTo("subspaceId", subspaceId)
            .whereEqualTo("status", "pending")
            .get()
            .addOnSuccessListener { docs ->
                requestList.clear()
                requestList.addAll(docs)
                recycler.adapter = RequestAdapter(requestList)
            }
    }

    inner class RequestAdapter(
        private val list: List<DocumentSnapshot>
    ) : RecyclerView.Adapter<RequestAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val studentId: TextView = view.findViewById(R.id.tvStudentId)
            val approve: MaterialButton = view.findViewById(R.id.btnApprove)
            val reject: MaterialButton = view.findViewById(R.id.btnReject)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_student_request, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount() = list.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val doc = list[position]
            val userId = doc.getString("userId") ?: ""
            val requestId = doc.id

            db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { userDoc ->

                    val name = userDoc.getString("name") ?: "Unknown"
                    val regNo = userDoc.getString("regNo") ?: ""

                    holder.studentId.text = "$name\nReg No: $regNo"
                }
            holder.approve.setOnClickListener {
                approveStudent(userId, requestId)
            }

            holder.reject.setOnClickListener {
                rejectStudent(requestId)
            }
        }
    }

    private fun approveStudent(userId: String, requestId: String) {

        val membership = hashMapOf(
            "userId" to userId,
            "subspaceId" to subspaceId,
            "Status" to System.currentTimeMillis()
        )

        db.collection("student_subspaces")
            .document(requestId)
            .set(membership)
            .addOnSuccessListener {

                db.collection("student_requests")
                    .document(requestId)
                    .delete()

                loadRequests()
            }
    }

    private fun rejectStudent(requestId: String) {
        db.collection("student_requests")
            .document(requestId)
            .delete()
            .addOnSuccessListener {
                loadRequests()
            }
    }
}