package com.raisproject.destinasi.ui.secretmenu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.raisproject.destinasi.R
import com.raisproject.destinasi.databinding.ActivitySecretMenuBinding
import com.raisproject.destinasi.ui.loginuser.LoginActivity

class SecretMenuActivity : AppCompatActivity() {

    lateinit var binding: ActivitySecretMenuBinding

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecretMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnUploadDestination.setOnClickListener {
            val intent = Intent(this, UploadDestinationActivity::class.java)
            startActivity(intent)
        }

        binding.btnUploadCity.setOnClickListener {
            val intent = Intent(this, UploadCityActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogout.setOnClickListener {
            btnLogout()
        }
    }

    private fun btnLogout() {
        auth = FirebaseAuth.getInstance()
        auth.signOut()
        val intent = Intent(this@SecretMenuActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}