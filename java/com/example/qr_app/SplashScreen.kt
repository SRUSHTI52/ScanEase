package com.example.qr_app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SplashScreen : AppCompatActivity() {

    lateinit var logo : ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        logo = findViewById(R.id.logo)

        rotateAnimation()

        val iHome = Intent(this@SplashScreen, HomeScreen::class.java)
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(iHome)
            finish()
        }, 2000)
    }
    fun rotateAnimation(){
        var rotateAnim = AnimationUtils.loadAnimation(this, R.anim.rotate)
        logo.startAnimation(rotateAnim)

    }
}