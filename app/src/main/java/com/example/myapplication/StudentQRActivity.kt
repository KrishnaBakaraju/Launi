package com.example.myapplication

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

class StudentQRActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_qr)

        val subspaceId = intent.getStringExtra("subspaceId") ?: return
        val userId = auth.currentUser?.uid ?: return

        val qrContent = "$subspaceId:$userId"

        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(qrContent, BarcodeFormat.QR_CODE, 600, 600)

        val bitmap = Bitmap.createBitmap(600, 600, Bitmap.Config.RGB_565)

        for (x in 0 until 600) {
            for (y in 0 until 600) {
                bitmap.setPixel(
                    x, y,
                    if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                )
            }
        }

        findViewById<ImageView>(R.id.imgQR).setImageBitmap(bitmap)
    }
}