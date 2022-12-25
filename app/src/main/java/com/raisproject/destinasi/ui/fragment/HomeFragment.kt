package com.raisproject.destinasi.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.raisproject.destinasi.R
import com.raisproject.destinasi.adapter.CityAdapter
//import com.raisproject.destinasi.adapter.CityAdapter
import com.raisproject.destinasi.databinding.FragmentHomeBinding
import com.raisproject.destinasi.model.City
import com.raisproject.destinasi.ui.destination.DestinationListActivity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding

    lateinit var cityRecyclerView: RecyclerView

    lateinit var cityArrayList: ArrayList<City>

//    var cityArrayList: ArrayList<City> = ArrayList()

//    lateinit var cityAdapter: CityAdapter

    var db = Firebase.firestore

    lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView Apparel / deklarasi variable
        cityRecyclerView = view.findViewById(R.id.rv_city)
        cityRecyclerView.layoutManager = GridLayoutManager(context, 2)
        cityRecyclerView.setHasFixedSize(true)

        cityArrayList = arrayListOf<City>()

        getCityData()

//        db = FirebaseFirestore.getInstance()
//
//
//        db.collection("city")
//            .get()
//            .addOnSuccessListener { result ->
//                cityArrayList.clear()
//                for (document in result) {
//                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
//
//                    cityArrayList.add(
//                        City(
//                            document.id as String,
//                            document.data.get("cityName") as String
//                        )
//                    )
//                }
//                recyclerView.adapter = CityAdapter(cityArrayList )
//            }
//            .addOnFailureListener { exception ->
//                Log.d(ContentValues.TAG, "Error getting documents: ", exception)
//            }

//        loadCity()
    }

    private fun getCityData() {
        databaseRef = FirebaseDatabase.getInstance().getReference("city")
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    cityArrayList.clear()
                    for (citySnapshot in snapshot.children) {
                        val city = citySnapshot.getValue(City::class.java)

                        // mendapatkan gambar dari storage
//                        val databaseStorageRef = citySnapshot.get
                        cityArrayList.add(city!!)
                    }

                    cityRecyclerView.adapter = CityAdapter(cityArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error : "+ error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
//
//    private fun loadCity() {
//        cityArrayList = arrayListOf()
//
//        // get all city from firebase database - realtime database > City
//        databaseRef = FirebaseDatabase.getInstance().getReference("city")
//        databaseRef.addValueEventListener(object : ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                // clear list before starting adding data into it
//                cityArrayList.clear()
//                for (ds in snapshot.children) {
//                    // get data as model
//                    val model = ds.getValue(City::class.java)
//
//                    // add to arrayList
//                    cityArrayList.add(model!!)
//                }
//                recyclerView.adapter = CityAdapter(this@HomeFragment, cityArrayList)
//                // setup adapter
//                cityAdapter = CityAdapter(context, cityArrayList)
//                // set adapter to recyclerview
//                binding.rvCity.adapter = cityAdapter
//            }
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//        })
//    }
}