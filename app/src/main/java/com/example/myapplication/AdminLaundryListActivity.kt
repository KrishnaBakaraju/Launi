package com.example.myapplication

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class AdminLaundryListActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LaundryAdapter
    private lateinit var subspaceId: String

    private var currentStatus = "processing"
    private var listener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_laundry_list)

        db = FirebaseFirestore.getInstance()
        subspaceId = intent.getStringExtra("subspaceId") ?: ""

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = LaundryAdapter()
        recyclerView.adapter = adapter

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentStatus = if (tab?.position == 0) "processing" else "completed"
                loadLaundry()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        loadLaundry()
    }

    private fun loadLaundry() {

        listener?.remove()

        listener = db.collection("laundry_records")
            .whereEqualTo("subspaceId", subspaceId)
            .whereEqualTo("status", currentStatus)
            .addSnapshotListener { snapshots, _ ->

                val list = mutableListOf<LaundryModel>()

                snapshots?.forEach { doc ->
                    list.add(
                        LaundryModel(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            regNo = doc.getString("regNo") ?: "",
                            clothesCount = doc.getLong("clothesCount") ?: 0
                        )
                    )
                }

                adapter.submitList(list)
            }
    }

    override fun onDestroy() {
        listener?.remove()
        super.onDestroy()
    }
}