package com.example.myapplication

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

class StudentQRActivity : AppCompatActivity() {

    private lateinit var qrImage: ImageView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_qr)

        qrImage = findViewById(R.id.qrImage)

        generateQR()
    }

    private fun generateQR() {

        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        db.collection("student_subspaces")
            .whereEqualTo("userId", userId)
            .whereEqualTo("status", "approved")
            .get()
            .addOnSuccessListener { docs ->

                if (!docs.isEmpty) {

                    val subspaceId = docs.documents[0].getString("subspaceId") ?: return@addOnSuccessListener

                    val qrContent = "$subspaceId:$userId"

                    val writer = QRCodeWriter()
                    val bitMatrix = writer.encode(qrContent, BarcodeFormat.QR_CODE, 600, 600)

                    val bitmap = Bitmap.createBitmap(600, 600, Bitmap.Config.RGB_565)

                    for (x in 0 until 600) {
                        for (y in 0 until 600) {
                            bitmap.setPixel(
                                x,
                                y,
                                if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
                            )
                        }
                    }

                    qrImage.setImageBitmap(bitmap)
                }
            }
    }
}