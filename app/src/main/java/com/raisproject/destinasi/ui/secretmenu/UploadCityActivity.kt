package com.raisproject.destinasi.ui.secretmenu

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.raisproject.destinasi.databinding.ActivityUploadCityBinding
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class UploadCityActivity : AppCompatActivity() {

    lateinit var binding: ActivityUploadCityBinding

    lateinit var auth: FirebaseAuth

    val db = Firebase.firestore

    private var storageRef = Firebase.storage

    lateinit var databaseRef: DatabaseReference

    val REQUEST_IMAGE_CAPTURE = 1

    lateinit var imageUri: Uri
//    var imageLink = ""
    var cityName = ""

    val timestamp = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadCityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivImageCity.setOnClickListener {
            selectImage()
        }

        binding.btnUploadCity.setOnClickListener {
            var cityImage = binding.ivImageCity
            cityName = binding.etCityName.text.toString()

            if (cityName.isEmpty()) {
                binding.etCityName.error = "Nama Kota harus diisi"
                binding.etCityName.requestFocus()
                return@setOnClickListener
            }

//            uploadCity()
            uploadCityImage()
        }
    }

//    private fun uploadCityImage() {
//        auth = FirebaseAuth.getInstance()
//        val uid = auth.currentUser!!.uid
//
//        storageRef = FirebaseStorage.getInstance()
//        storageRef.getReference("img_city").child(uid)
//            .putFile(imageUri)
//            .addOnSuccessListener { task ->
//                task.metadata!!.reference!!.downloadUrl
//                    .addOnSuccessListener {
//                        uploadCityData()
//                    }
//            }
//    }

//    private fun uploadCityData() {
//        val uid = auth.currentUser!!.uid
//        val mapImage = mapOf(
//            "url" to toString(),
//            "cityName" to cityName
//        )
//        val databaseRef = FirebaseDatabase.getInstance().getReference("city")
//
//        databaseRef.child(uid).setValue(mapImage).addOnSuccessListener {
//            Toast.makeText(this, "Upload Success", Toast.LENGTH_SHORT).show()
//        }
//            .addOnFailureListener {
//                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
//            }
//    }


    fun uploadCityImage() {
        val cityId = UUID.randomUUID().toString()
        val storageRef = FirebaseStorage.getInstance().reference.child("img_city/${cityName}")
        var imageLink = ""

        // update foto ke firestore
        storageRef.putFile(imageUri).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                Toast.makeText(this, "Berhasil Upload Gambar", Toast.LENGTH_SHORT).show()
                var imageLink = uri.toString()
                uploadCity(cityId, imageLink)
            }
        }.addOnFailureListener{
            Toast.makeText(this, "Gagal Upload Gambar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadCity(cityId: String, imageLink: String) {
        databaseRef = FirebaseDatabase.getInstance().getReference("city")
        auth = FirebaseAuth.getInstance()
        val adminId = auth.currentUser!!.uid

        val data: MutableMap<String, Any> = HashMap()
        data["cityName"] = cityName
        data["imageUrl"] = imageLink
        data["uid"] =  adminId

        // add to firebase db (realtime database)
        databaseRef.child("$cityName").setValue(data).addOnSuccessListener {
            Toast.makeText(this, "Kota berhasil diupload", Toast.LENGTH_SHORT).show()
//            uploadCityImage(cityId)
            finish()
        }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal upload data kota", Toast.LENGTH_SHORT).show()
            }
    }

    private fun selectImage() {
        // select image from gallery
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // menerima data gambar dari galeri
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // display image from gallery
            imageUri = data?.data!!
            binding.ivImageCity.setImageURI(imageUri)
        }
    }
}