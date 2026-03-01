package com.example.myapplication

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import android.content.Intent
import android.widget.Toast

class SubspaceDetailActivity : AppCompatActivity() {

    private lateinit var subspaceId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subspace_detail)

        subspaceId = intent.getStringExtra("subspaceId") ?: ""

        findViewById<MaterialButton>(R.id.btnScan).setOnClickListener {
            val intent = Intent(this, AdminScanActivity::class.java)
            intent.putExtra("subspaceId", subspaceId)
            startActivity(intent)
        }

        findViewById<MaterialButton>(R.id.btnRequests).setOnClickListener {
            val intent = Intent(this, AdminRequestsActivity::class.java)
            intent.putExtra("subspaceId", subspaceId)
            startActivity(intent)
        }

        findViewById<MaterialButton>(R.id.btnProcessing).setOnClickListener {
            val intent = Intent(this, LaundryListActivity::class.java)
            intent.putExtra("subspaceId", subspaceId)
            intent.putExtra("status", "processing")
            startActivity(intent)
        }

        findViewById<MaterialButton>(R.id.btnCompleted).setOnClickListener {
            val intent = Intent(this, LaundryListActivity::class.java)
            intent.putExtra("subspaceId", subspaceId)
            intent.putExtra("status", "completed")
            startActivity(intent)
        }
    }
}