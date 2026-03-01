package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StudentLaundryStatusActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var recycler: RecyclerView
    private val laundryList = mutableListOf<DocumentSnapshot>()

    private lateinit var subspaceId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_laundry_status)

        subspaceId = intent.getStringExtra("subspaceId") ?: ""

        recycler = findViewById(R.id.recyclerLaundry)
        recycler.layoutManager = LinearLayoutManager(this)

        loadLaundry()
    }

    private fun loadLaundry() {

        val userId = auth.currentUser?.uid ?: return

        db.collection("laundry_records")
            .whereEqualTo("userId", userId)
            .whereEqualTo("subspaceId", subspaceId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { docs ->
                laundryList.clear()
                laundryList.addAll(docs)
                recycler.adapter = LaundryAdapter(laundryList)
            }
    }

    inner class LaundryAdapter(
        private val list: List<DocumentSnapshot>
    ) : RecyclerView.Adapter<LaundryAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val status: TextView = view.findViewById(R.id.tvStatus)
            val clothes: TextView = view.findViewById(R.id.tvClothes)
            val created: TextView = view.findViewById(R.id.tvCreated)
            val completed: TextView = view.findViewById(R.id.tvCompleted)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_student_laundry, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount() = list.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val doc = list[position]

            val statusText = doc.getString("status") ?: ""
            val clothesCount = doc.getLong("clothesCount") ?: 0
            val createdAt = doc.getLong("createdAt") ?: 0
            val completedAt = doc.getLong("completedAt")

            holder.status.text = "Status: $statusText"
            holder.clothes.text = "Clothes: $clothesCount"
            holder.created.text = "Accepted: ${formatDate(createdAt)}"

            if (completedAt != null) {
                holder.completed.text = "Completed: ${formatDate(completedAt)}"
            } else {
                holder.completed.text = ""
            }
        }
    }

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}