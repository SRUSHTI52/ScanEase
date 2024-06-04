package com.example.qr_app

import android.content.pm.PackageManager
import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.util.SparseArray
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.util.isNotEmpty
import androidx.core.view.isVisible
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector

class MainActivity : AppCompatActivity() {

    private val requestCodeCameraPermission = 1005
    private lateinit var cameraSource: CameraSource
    private lateinit var detector: BarcodeDetector
    private lateinit var sv: SurfaceView
    private lateinit var tv: TextView
    private lateinit var btn: Button
    private lateinit var home: ImageView
    private lateinit var gal: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sv = findViewById(R.id.sv)
        tv = findViewById(R.id.tv)
        btn = findViewById(R.id.btn)
        home = findViewById(R.id.home)
        gal = findViewById(R.id.gal)
        btn.isVisible = false

        gal.setOnClickListener {
            val intent = Intent(this@MainActivity, Gallery::class.java)
            startActivity(intent)
        }

        home.setOnClickListener {
            val intent = Intent(this@MainActivity, HomeScreen::class.java)
            startActivity(intent)
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            askForCameraPermission()
        } else {
            setupControls()
        }

    }

    private fun setupControls() {
        detector = BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build()
        cameraSource = CameraSource.Builder(this, detector)
            .setAutoFocusEnabled(true)
            .setRequestedPreviewSize(640, 480)
            .build()
        sv.holder.addCallback(surfaceCallback)
        detector.setProcessor(processor)
    }

    private fun askForCameraPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), requestCodeCameraPermission)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCodeCameraPermission && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupControls()
            } else {
                Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val surfaceCallback = object : SurfaceHolder.Callback {
        override fun surfaceDestroyed(holder: SurfaceHolder) {
            cameraSource.stop()
        }

        override fun surfaceCreated(holder: SurfaceHolder) {
            try {
                if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    cameraSource.start(holder)
                }
            } catch (exception: Exception) {
                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

        }
    }

    private val processor = object : Detector.Processor<Barcode> {
        override fun release() {

        }

        override fun receiveDetections(detections: Detector.Detections<Barcode>) {
            if (detections.detectedItems.isNotEmpty()) {
                val qrCodes: SparseArray<Barcode> = detections.detectedItems
                val code = qrCodes.valueAt(0)
                runOnUiThread {
                    try {
                        val text = code.displayValue
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
                    } catch (e: Exception) {
                        Toast.makeText(this@MainActivity, "Invalid URL", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                runOnUiThread {
                    tv.text = ""
                }
            }
        }
    }
}
