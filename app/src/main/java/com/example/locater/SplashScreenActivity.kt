package com.example.locater

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.activity.enableEdgeToEdge

class SplashScreenActivity : AppCompatActivity()
{
    private val SPLASH_DELAY: Long = 3000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        enableEdgeToEdge()

        SharedPrefObject.init(this)
        Handler().postDelayed({
            val success=SharedPrefObject.getBooleanLogin(SharedprefConstant.isUserLoggedIn)
            if(success) {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
            else{
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            finish()
        }, SPLASH_DELAY)
    }
}