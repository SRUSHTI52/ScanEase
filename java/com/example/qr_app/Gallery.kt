package com.example.qr_app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class Gallery : AppCompatActivity() {

    lateinit var iv: ImageView
    lateinit var btn: Button
    lateinit var gal: Button
    lateinit var tv: TextView
    lateinit var home: ImageView
    lateinit var cam: ImageView

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.img_gallery)
        cam = findViewById(R.id.cam)
        iv = findViewById(R.id.iv)
        btn = findViewById(R.id.btn)
        gal = findViewById(R.id.gal)
        tv = findViewById(R.id.tv)
        home = findViewById(R.id.home)

        cam.setOnClickListener {
            val intent = Intent(this@Gallery, MainActivity::class.java)
            startActivity(intent)
        }

        home.setOnClickListener {
            val intent = Intent(this@Gallery, HomeScreen::class.java)
            startActivity(intent)
        }

        result()
        gal.setOnClickListener {pickImage() }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultLauncher.launch(intent)
    }

    private fun result() {
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                try {
                    val imgUri: Uri? = result.data?.data
                    if (imgUri != null) {
                        iv.setImageURI(imgUri)
                        scanQRCode(imgUri)
                    } else {
                        Toast.makeText(this@Gallery, "No image found", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@Gallery, "No image found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun scanQRCode(imageUri: Uri) {
        try {
            val image = InputImage.fromFilePath(this, imageUri)
            val scanner = BarcodeScanning.getClient()
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.isNotEmpty()) {
                        val barcode = barcodes[0]
                        val text = barcode.displayValue
                        tv.text = text
                        if (Patterns.WEB_URL.matcher(text).matches()) {
                            val uri = Uri.parse(text)
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            btn.isVisible = true
                            btn.setOnClickListener {
                                startActivity(intent)
                            }
                        } else {
                            btn.isVisible = false
                        }
                    } else {
                        Toast.makeText(this@Gallery, "No QR code found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this@Gallery, "Failed to scan QR code", Toast.LENGTH_SHORT).show()
                }
        } catch (e: Exception) {
            Toast.makeText(this, "Error scanning QR code", Toast.LENGTH_SHORT).show()
        }
    }

}

