package com.raisproject.destinasi.ui.destination

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.raisproject.destinasi.R
import com.raisproject.destinasi.adapter.DestinationAdapater
import com.raisproject.destinasi.databinding.ActivityDestinationListBinding
import com.raisproject.destinasi.model.Destination

class DestinationListActivity : AppCompatActivity() {

    lateinit var binding: ActivityDestinationListBinding
    lateinit var destinationRecyclerView: RecyclerView
    lateinit var destinationArrayList: ArrayList<Destination>
    lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDestinationListBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.light_blue)

        val actionBar = supportActionBar
        actionBar!!.title = intent.getStringExtra("cityName")
        actionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.light_blue)))
        actionBar!!.setDisplayHomeAsUpEnabled(true)

        // RecyclerView Apparel / deklarasi variable
        destinationRecyclerView = findViewById(R.id.rv_destination)
        destinationRecyclerView.layoutManager = LinearLayoutManager(this)
        destinationRecyclerView.setHasFixedSize(true)

        destinationArrayList = arrayListOf<Destination>()

        getDestinationData()
    }

    private fun getDestinationData() {
        val getCityNameValue = intent.getStringExtra("cityName")
        databaseRef = FirebaseDatabase.getInstance().getReference("destinations")
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    destinationArrayList.clear()
                    for (citySnapshot in snapshot.children) {
                        val destination = citySnapshot.getValue(Destination::class.java)
                        if (destination!!.cityId == getCityNameValue) {
                            // mendapatkan gambar dari storage
//                        val databaseStorageRef = citySnapshot.get
                            destinationArrayList.add(destination!!)
                        }
                    }

                    destinationRecyclerView.adapter = DestinationAdapater(destinationArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DestinationListActivity, "Error : "+ error.message, Toast.LENGTH_SHORT).show()
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