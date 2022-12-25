package com.raisproject.destinasi.ui.detailpage

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.raisproject.destinasi.R
import com.raisproject.destinasi.adapter.DestinationAdapater
import com.raisproject.destinasi.databinding.ActivityDetailPageBinding
import com.raisproject.destinasi.model.Destination
import com.raisproject.destinasi.ui.destination.DestinationListActivity
import com.raisproject.destinasi.ui.profile.ProfileActivity
import java.util.HashMap

class DetailPageActivity : AppCompatActivity() {

    lateinit var binding: ActivityDetailPageBinding

    lateinit var databaseRef: DatabaseReference

//    lateinit var destinationArrayList: ArrayList<Destination>


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetailPageBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.light_blue)

        val actionBar = supportActionBar
        actionBar!!.title = intent.getStringExtra("destinationName")
        actionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.light_blue)))
        actionBar!!.setDisplayHomeAsUpEnabled(true)


        databaseRef = FirebaseDatabase.getInstance().getReference("destinations")
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // jika data ditemukan(ada)
                if (snapshot.exists()) {
                    val getDestinationId = intent.getStringExtra("destinationName")
                    for (destinationSnapshot in snapshot.children) {
                        val destination = destinationSnapshot.getValue(Destination::class.java)
                        // filter atau mencari data yang sama dengan recyclerview yang diklik
                        if (destination!!.destinationName == getDestinationId) {
                            // menampilkan data
                            val destinationImage = destinationSnapshot.child("destinationImage").value
                            val destinationName = destinationSnapshot.child("destinationName").value
                            val address = destinationSnapshot.child("address").value
                            val latitude = destinationSnapshot.child("latitude").value
                            val longitude = destinationSnapshot.child("longitude").value
                            val description = destinationSnapshot.child("description").value
                            val phoneNumber = destinationSnapshot.child("phoneNumber").value

                            Glide.with(this@DetailPageActivity)
                                .load(destinationImage)
                                .into(binding.ivDestinationImage)
                            binding.tvDestinationName.setText(destinationName.toString())
                            binding.tvDestinationAddress.setText(address.toString())
                            binding.tvDestinationDescription.setText(description.toString())
                            binding.btnSendWhatsappMessage.setOnClickListener {
                                val mobile = phoneNumber
                                val message = "Permisi, apakah saya bisa bertanya tentang ${destinationName}?"
                                startActivity(Intent(Intent.ACTION_VIEW,
                                    Uri.parse("https://api.whatsapp.com/send?phone=" + mobile + "&text=" + message)))
                            }

                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
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
