package com.example.myapplication

import android.content.Intent
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class StudentSubspacesActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var recycler: RecyclerView
    private val subspaces = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_subspaces)

        recycler = findViewById(R.id.recyclerMySubspaces)
        recycler.layoutManager = LinearLayoutManager(this)

        loadApproved()
    }

    private fun loadApproved() {

        val userId = auth.currentUser?.uid ?: return

        db.collection("student_subspaces")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { docs ->

                subspaces.clear()

                for (doc in docs) {
                    subspaces.add(doc.getString("subspaceId") ?: "")
                }

                recycler.adapter = StudentSubspaceAdapter(subspaces)
            }
    }

    inner class StudentSubspaceAdapter(
        private val list: List<String>
    ) : RecyclerView.Adapter<StudentSubspaceAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView = view.findViewById(R.id.tvSubspaceName)
            val btnQR: MaterialButton = view.findViewById(R.id.btnOpenQR)
            val btnStatus: MaterialButton = view.findViewById(R.id.btnViewStatus)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_student_subspace, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount() = list.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val subspaceId = list[position]
            holder.name.text = subspaceId

            holder.btnQR.setOnClickListener {
                val intent = Intent(this@StudentSubspacesActivity, StudentQRActivity::class.java)
                intent.putExtra("subspaceId", subspaceId)
                startActivity(intent)
            }

            holder.btnStatus.setOnClickListener {
                val intent = Intent(this@StudentSubspacesActivity, StudentLaundryStatusActivity::class.java)
                intent.putExtra("subspaceId", subspaceId)
                startActivity(intent)
            }
        }
    }
}