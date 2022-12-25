package com.raisproject.destinasi.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.raisproject.destinasi.R
import com.raisproject.destinasi.databinding.FragmentProfileBinding
import com.raisproject.destinasi.ui.changepassword.ChangePasswordActivity
import com.raisproject.destinasi.ui.loginuser.LoginActivity
import com.raisproject.destinasi.ui.profile.ProfileActivity
import com.squareup.picasso.Picasso

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {

    lateinit var binding: FragmentProfileBinding

    lateinit var auth: FirebaseAuth

    lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = auth.currentUser?.uid
        databaseRef = FirebaseDatabase.getInstance().getReference("users")

        if (user != null) {
//            binding.tvName.setText(user.email)

            if (user.isEmailVerified) {
                binding.icVerified.visibility = View.VISIBLE
                binding.btnEmailVerification.visibility = View.GONE
            } else {
                binding.icVerified.visibility = View.GONE
            }

            // mendapatkan profile picture dari firebase storage
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference
            val imageRef = storageRef.child("img_user/${FirebaseAuth.getInstance().currentUser?.email}")
            imageRef.downloadUrl.addOnSuccessListener {
                Log.d("User Profile Image", "downloadUrl success")
                Picasso.get().load(it).into(binding.ivProfileImagee)
            }
        }

        if (uid != null) {
            databaseRef.child(uid).get().addOnSuccessListener {
                if (it.exists()) {
                    var fullname = it.child("fullname").value

                    binding.tvName.setText("${fullname}")
                } else {
                    Toast.makeText(context, "Data Profile masih kosong", Toast.LENGTH_SHORT).show()
                }
            }
        }

//        binding.ivProfile.setOnClickListener {
//            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
//                activity?.packageManager?.let {
//                    intent?.resolveActivity(it).also {
//                        startActivityForResult(intent, 100)
//                    }
//                }
//            }
//        }

        binding.btnProfile.setOnClickListener {
            val intent = Intent(context, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.btnEmailVerification.setOnClickListener {
            emailVerification()
        }

        binding.btnChangePassword.setOnClickListener {
            val intent = Intent(context, ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogout.setOnClickListener {
            btnLogout()
        }
    }

    private fun emailVerification() {
        val user = auth.currentUser

        user?.sendEmailVerification()?.addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Email Verifikasi telah dikirim", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun btnLogout() {
        auth = FirebaseAuth.getInstance()
        auth.signOut()
        val intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }
}