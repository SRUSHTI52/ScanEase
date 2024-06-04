package com.example.qr_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class HomeScreen : AppCompatActivity() {


    lateinit var scan : Button
    lateinit var generate : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homepage)

        scan = findViewById(R.id.scan)
        generate = findViewById(R.id.generate)

        scan.setOnClickListener {
            val intent : Intent
            intent = Intent(this@HomeScreen, MainActivity::class.java)
            startActivity(intent)
        }

        generate.setOnClickListener {
            val intent : Intent
            intent = Intent(this@HomeScreen, QRCodeGenerator::class.java)
            startActivity(intent)
        }
    }
}