package com.raisproject.destinasi.ui.secretmenu

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.raisproject.destinasi.databinding.ActivityUploadDestinationBinding
import com.raisproject.destinasi.model.City
import java.util.*
import kotlin.collections.ArrayList

class UploadDestinationActivity : AppCompatActivity() {

    lateinit var binding: ActivityUploadDestinationBinding

    lateinit var auth: FirebaseAuth

    val REQUEST_IMAGE_CAPTURE = 1
    lateinit var imageUri: Uri

    var destinationName = ""
    var cityDom = ""
    var address = ""
    var latitude = ""
    var longitude = ""
    var description = ""

    lateinit var databaseRef: DatabaseReference

    val timestamp = System.currentTimeMillis()

    lateinit var spinner: Spinner
    lateinit var cityNameSpinnerArrayList : ArrayList<City>


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityUploadDestinationBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.ivImageDestination.setOnClickListener {
            selectImage()
        }

        binding.btnUploadDestination.setOnClickListener {
            destinationName = binding.etDestinationName.text.toString()
            cityDom = binding.etCityDom.text.toString()
            address = binding.etAddress.text.toString()
            latitude = binding.etLatitude.text.toString()
            longitude = binding.etLongitude.text.toString()
            description = binding.etDescription.text.toString()

            if (destinationName.isEmpty()) {
                binding.etDestinationName.error = "Nama Destinati harus diisi"
                binding.etDestinationName.requestFocus()
                return@setOnClickListener
            }

            if (cityDom.isEmpty()) {
                binding.etCityDom.error = "Nama Kota harus diisi"
                binding.etCityDom.requestFocus()
                return@setOnClickListener
            }

            if (address.isEmpty()) {
                binding.etAddress.error = "Alamat harus diisi"
                binding.etAddress.requestFocus()
                return@setOnClickListener
            }

            if (latitude.isEmpty()) {
                binding.etLatitude.error = "Latitude harus diisi"
                binding.etLatitude.requestFocus()
                return@setOnClickListener
            }
            if (longitude.isEmpty()) {
                binding.etLongitude.error = "Longitude harus diisi"
                binding.etLongitude.requestFocus()
                return@setOnClickListener
            }
            if (address.isEmpty()) {
                binding.etAddress.error = "Longitude harus diisi"
                binding.etAddress.requestFocus()
                return@setOnClickListener
            }

            uploadDestinationImage()
        }
    }

    private fun showDataSpinner() {
    }

    private fun uploadDestinationImage() {
        val destinationId = UUID.randomUUID().toString()
        val storageRef = FirebaseStorage.getInstance().reference.child("img_destination/${destinationId}")
        var imageLink = ""

        // update foto ke firestore
        storageRef.putFile(imageUri).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                Toast.makeText(this, "Berhasil Upload Gambar", Toast.LENGTH_SHORT).show()
                var imageLink = uri.toString()
                uploadDestination(destinationId, imageLink)
            }
        }.addOnFailureListener{
            Toast.makeText(this, "Gagal Upload Gambar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadDestination(destinationId: String, imageLink: String) {
        databaseRef = FirebaseDatabase.getInstance().getReference("destinations")
        auth = FirebaseAuth.getInstance()
        val adminId = auth.currentUser!!.uid

        val data: MutableMap<String, Any> = HashMap()
        data["destinationImage"] = imageLink
        data["destinationName"] = destinationName
        data["cityId"] = cityDom
        data["address"] = address
        data["latitude"] = latitude
        data["longitude"] = longitude
        data["description"] = description
        data["timestamp"] = timestamp
        data["uid"] =  adminId

        // add to firebase db (realtime database)
        databaseRef.child("$destinationId").setValue(data).addOnSuccessListener {
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
            binding.ivImageDestination.setImageURI(imageUri)
        }
    }
}