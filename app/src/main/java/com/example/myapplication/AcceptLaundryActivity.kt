package com.example.myapplication

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AcceptLaundryActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String
    private lateinit var subspaceId: String
    private lateinit var etClothes: EditText
    private lateinit var btnAccept: MaterialButton

    private var name: String? = null
    private var regNo: String? = null
    private var phone: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accept_laundry)

        db = FirebaseFirestore.getInstance()

        userId = intent.getStringExtra("userId") ?: ""
        subspaceId = intent.getStringExtra("subspaceId") ?: ""

        if (userId.isEmpty() || subspaceId.isEmpty()) {
            Toast.makeText(this, "Invalid Data", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        etClothes = findViewById(R.id.etClothes)
        btnAccept = findViewById(R.id.btnAccept)

        loadStudentProfile()

        btnAccept.setOnClickListener {

            val clothesCount = etClothes.text.toString()

            if (clothesCount.isEmpty()) {
                Toast.makeText(this, "Enter clothes count", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val data = hashMapOf(
                "userId" to userId,
                "subspaceId" to subspaceId,
                "name" to name,
                "regNo" to regNo,
                "phone" to phone,
                "clothesCount" to clothesCount.toLong(),
                "status" to "processing",
                "createdAt" to System.currentTimeMillis()
            )

            db.collection("laundry_records")
                .add(data)
                .addOnSuccessListener {
                    Toast.makeText(this, "Laundry Accepted", Toast.LENGTH_SHORT).show()
                    finish()
                }
        }
    }

    private fun loadStudentProfile() {

        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { doc ->

                name = doc.getString("name")
                regNo = doc.getString("regNo")
                phone = doc.getString("phone")

                findViewById<TextView>(R.id.tvName).text = name
                findViewById<TextView>(R.id.tvRegNo).text = regNo
                findViewById<TextView>(R.id.tvPhone).text = phone

                val date = SimpleDateFormat(
                    "dd MMM yyyy, hh:mm a",
                    Locale.getDefault()
                ).format(Date())

                findViewById<TextView>(R.id.tvDate).text = date
            }
    }
}