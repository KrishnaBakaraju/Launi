package com.example.myapplication

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AcceptLaundryActivity : AppCompatActivity() {

    private lateinit var tvName: TextView
    private lateinit var tvRegNo: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvDate: TextView
    private lateinit var etClothes: EditText
    private lateinit var btnAccept: Button

    private val db = FirebaseFirestore.getInstance()

    private lateinit var studentId: String
    private lateinit var subspaceId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accept_laundry)

        studentId = intent.getStringExtra("studentId") ?: ""
        subspaceId = intent.getStringExtra("subspaceId") ?: ""

        if (studentId.isEmpty() || subspaceId.isEmpty()) {
            Toast.makeText(this, "Invalid Data", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        val name = intent.getStringExtra("name")
        val regNo = intent.getStringExtra("regNo")
        val phone = intent.getStringExtra("phone")
        val userId = FirebaseAuth.getInstance().currentUser!!.uid


        findViewById<TextView>(R.id.tvName).text = name
        findViewById<TextView>(R.id.tvRegNo).text = regNo
        findViewById<TextView>(R.id.tvPhone).text = phone

        val currentDate = SimpleDateFormat(
            "dd MMM yyyy, hh:mm a",
            Locale.getDefault()
        ).format(Date())

        findViewById<TextView>(R.id.tvDate).text = currentDate

        //tvName = findViewById(R.id.tvName)
        //tvRegNo = findViewById(R.id.tvRegNo)
        //tvPhone = findViewById(R.id.tvPhone)
        //tvDate = findViewById(R.id.tvDate)
        etClothes = findViewById(R.id.etClothes)
        btnAccept = findViewById(R.id.btnAccept)

        setCurrentDate()
        loadStudentDetails()

        btnAccept.setOnClickListener {
            val clothesCount = etClothes.text.toString().toLong()

            val data = hashMapOf(
                "userId" to userId,
                "subspaceId" to subspaceId,
                "name" to name,
                "regNo" to regNo,
                "phone" to phone,
                "clothesCount" to clothesCount,
                "status" to "processing",
                "createdAt" to System.currentTimeMillis()
            )

            db.collection("laundry_records")
                .add(data)
            createLaundryRecord()
        }
    }

    private fun setCurrentDate() {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        tvDate.text = sdf.format(Date())
    }

    private fun loadStudentDetails() {

        db.collection("users")
            .document(studentId)
            .get()
            .addOnSuccessListener { doc ->
                tvName.text = doc.getString("name") ?: "N/A"
                tvRegNo.text = doc.getString("regNo") ?: "N/A"
                tvPhone.text = doc.getString("phone") ?: "N/A"
            }
    }

    private fun createLaundryRecord() {

        val clothesCount = etClothes.text.toString().trim()

        if (clothesCount.isEmpty()) {
            Toast.makeText(this, "Enter clothes count", Toast.LENGTH_SHORT).show()
            return
        }

        val record = hashMapOf(
            "studentId" to studentId,
            "subspaceId" to subspaceId,
            "clothesCount" to clothesCount.toInt(),
            "status" to "processing",
            "createdAt" to System.currentTimeMillis(),
            "completedAt" to null
        )

        db.collection("laundry_records")
            .add(record)
            .addOnSuccessListener {
                Toast.makeText(this, "Laundry Accepted", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }
    }
}