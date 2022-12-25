package com.raisproject.destinasi.ui.splashscreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.raisproject.destinasi.MainActivity
import com.raisproject.destinasi.R
import com.raisproject.destinasi.databinding.ActivitySplashScreenBinding
import com.raisproject.destinasi.ui.loginuser.LoginActivity


class SplashScreenActivity : AppCompatActivity() {

    lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.hide()

        window.statusBarColor = ContextCompat.getColor(this, R.color.light_blue)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this@SplashScreenActivity, LoginActivity::class.java))
        },3000)
    }
}