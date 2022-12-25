package com.raisproject.destinasi.ui.profile

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.MenuItem
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.auth.User
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.raisproject.destinasi.R
import com.raisproject.destinasi.databinding.ActivityEditProfileBinding
import com.raisproject.destinasi.model.Users
import com.raisproject.destinasi.ui.loginuser.LoginActivity
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class EditProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivityEditProfileBinding

    lateinit var auth: FirebaseAuth

//    lateinit var database: FirebaseDatabase

    lateinit var databaseRef: DatabaseReference

    val REQUEST_IMAGE_CAPTURE = 1

    lateinit var imageUri: Uri

    var fullname = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.light_blue)

        val actionBar = supportActionBar
        actionBar!!.title = "Ubah Biodata"
        actionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.light_blue)))
        actionBar!!.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = auth.currentUser?.uid
        databaseRef = FirebaseDatabase.getInstance().getReference("users")
//        databaseRef = Firebase.database.reference
        val userId = databaseRef.push().key

        if (user != null) {
            binding.etEmail.setText(user.email)

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

                    binding.etFullName.setText("${fullname}")
//                    binding.etUsername.setText("${username}")
                } else {
                    Toast.makeText(this, "Data Profile masih kosong", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.ivProfileImage.setOnClickListener {
            selectImage()
        }

        binding.btnUpdateProfile.setOnClickListener {
            val fullName = binding.etFullName.text.toString()
//            val username = binding.etUsername.text.toString()

            val user = Users(fullName)
            if (uid != null) {
                databaseRef.child(uid).setValue(user).addOnCompleteListener {
                    if (it.isSuccessful) {
                        updateProfilePict()
                        Toast.makeText(this@EditProfileActivity, "Profil berhasil di update", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@EditProfileActivity, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            updateUserInfo()
        }
    }

    private fun updateUserInfo() {
        // 4) save user info - firebase realtime database

        val timestamp = System.currentTimeMillis()
        val userEmail = auth.currentUser!!.email
        fullname = binding.etFullName.text.toString()

        val uid = auth.currentUser!!.uid

        // setup data to add in db
        val hashMap: HashMap<String, Any?> = HashMap()
//        hashMap["uid"] = uid
        hashMap["fullname"] = fullname
        hashMap["email"] = userEmail
        hashMap["userType"] = "user"
        hashMap["timestamp"] = timestamp

        databaseRef = FirebaseDatabase.getInstance().getReference("users")
        databaseRef.child(uid).setValue(hashMap)
            .addOnSuccessListener {
                // user info saved, open user dashboard
                Toast.makeText(this, "Profile berhasil di update", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@EditProfileActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                // failed adding data to db
                Toast.makeText(this, "Gagal update profile ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

//    private fun changeEmail() {
//
//        auth = FirebaseAuth.getInstance()
//        val user = auth.currentUser
//
//        var email = binding.etEmail.text.toString()
//
//        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            binding.etEmail.error = "Email tidak valid"
//            binding.etEmail.requestFocus()
//            return
//        }
//
//        user?.let {
//            user.updateEmail(email).addOnCompleteListener {
//                if (it.isSuccessful) {
//                    Toast.makeText(this, "Email berhasil diubah", Toast.LENGTH_SHORT).show()
//                } else {
//                    Toast.makeText(this, "Email gagal diubah", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//
//    }

    // select image from gallery
    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    // display picture to circle image view
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            // display image from gallery
            imageUri = data?.data!!
            binding.ivProfileImage.setImageURI(imageUri)
        }
    }

    private fun updateProfilePict() {
        val ref = FirebaseStorage.getInstance().reference.child("img_user/${FirebaseAuth.getInstance().currentUser?.email}")

        // update foto profile ke firestore
        ref.putFile(imageUri).addOnSuccessListener {
            Toast.makeText(this@EditProfileActivity, "Berhasil Upload Gambar", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Toast.makeText(this@EditProfileActivity, "Gagal Upload Gambar", Toast.LENGTH_SHORT).show()
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