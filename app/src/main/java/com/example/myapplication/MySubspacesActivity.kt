package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MySubspacesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BrowseSubspaceAdapter
    private val subspaceList = mutableListOf<Subspace>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_subspaces)

        recyclerView = findViewById(R.id.recyclerSubspaces)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadSubspaces()
    }

    private fun loadSubspaces() {

        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        val db = FirebaseFirestore.getInstance()

        db.collection("subspaces")
            .whereArrayContains("admins", currentUserId)
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
                    false,
                    { },
                    { subspace ->
                        val intent = Intent(this, SubspaceDetailActivity::class.java)
                        intent.putExtra("subspaceId", subspace.id)
                        startActivity(intent)
                    }
                )



                recyclerView.adapter = adapter
            }
    }
}
