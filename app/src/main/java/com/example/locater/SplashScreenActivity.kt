package com.example.locater

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import com.example.locater.constant.SharedPrefObject
import com.example.locater.constant.SharedprefConstant

class SplashScreenActivity : AppCompatActivity()
{
    private val SPLASH_DELAY: Long = 3000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        enableEdgeToEdge()

        SharedPrefObject.init(this)
        Handler().postDelayed({
            val success= SharedPrefObject.getLastToogleState(SharedprefConstant.isUserLoggedIn)
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