package com.raisproject.destinasi.ui.profile

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.raisproject.destinasi.R
import com.raisproject.destinasi.databinding.ActivityProfileBinding
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfileBinding

    lateinit var auth: FirebaseAuth

    lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.light_blue)

        val actionBar = supportActionBar
        actionBar!!.title = "Biodata"
        actionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.light_blue)))
        actionBar!!.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = auth.currentUser?.uid
        databaseRef = FirebaseDatabase.getInstance().getReference("users")


        if (user != null) {
//            binding.tvName.setText(user.email)
            binding.tvEmail.setText(user.email)

            if (user.isEmailVerified) {
                binding.icVerified.visibility = View.VISIBLE
            } else {
                binding.icVerified.visibility = View.GONE
            }

            // mendapatkan profile picture dari firebase storage
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference
            val imageRef = storageRef.child("img_user/${FirebaseAuth.getInstance().currentUser?.email}")
            imageRef.downloadUrl.addOnSuccessListener {
                Log.d("User Profile Image", "downloadUrl success")
                Picasso.get().load(it).into(binding.ivProfileImage)
            }
        }

        if (uid != null) {
            databaseRef.child(uid).get().addOnSuccessListener {
                if (it.exists()) {
                    var fullname = it.child("fullname").value
                    var username = it.child("username").value

                    binding.tvFullName.setText("${fullname}")
//                    binding.tvUsername.setText("${username}")
                    binding.tvName.setText("${fullname}")
                } else {
                    Toast.makeText(this, "Data Profile masih kosong", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnEditProfile.setOnClickListener {
            if (user!!.isEmailVerified) {
                val intent = Intent(this@ProfileActivity, EditProfileActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Verifikasi email terlebih dahulu", Toast.LENGTH_SHORT).show()
            }

        }
    }

    // action bar back button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}