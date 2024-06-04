package com.example.qr_app

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.oned.Code128Writer

class QRCodeGenerator : AppCompatActivity() {

    private lateinit var etInput: EditText
    private lateinit var btnGenerate: Button
    private lateinit var ivCode: ImageView
    private lateinit var spFormat: Spinner
    private lateinit var home : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code_generator)

        etInput = findViewById(R.id.etInput)
        btnGenerate = findViewById(R.id.btnGenerate)
        ivCode = findViewById(R.id.ivCode)
        spFormat = findViewById(R.id.spFormat)
        home = findViewById(R.id.home)

        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            this,
            R.array.code_formats,
            R.layout.spinner_item
        )
        adapter.setDropDownViewResource(R.layout.spinner_item)
        spFormat.adapter = adapter

        home.setOnClickListener {
            val intent : Intent = Intent(this@QRCodeGenerator, HomeScreen::class.java)
            startActivity(intent)
        }
        btnGenerate.setOnClickListener {
            val text = etInput.text.toString().trim()
            if (text.isNotEmpty()) {
                val format = spFormat.selectedItem.toString()
                generateCode(text, format)
            } else {
                Toast.makeText(this, "Please enter some text", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun generateCode(text: String, format: String) {
        try {
            val bitMatrix: BitMatrix = when (format) {
                "QR Code" -> QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, 512, 512)
                "Barcode" -> Code128Writer().encode(text, BarcodeFormat.CODE_128, 512, 256)
                else -> throw IllegalArgumentException("Unsupported format")
            }

            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
                }
            }
            ivCode.setImageBitmap(bmp)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }
}
