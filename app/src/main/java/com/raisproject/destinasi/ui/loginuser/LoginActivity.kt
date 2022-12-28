package com.raisproject.destinasi.ui.loginuser

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.raisproject.destinasi.MainActivity
import com.raisproject.destinasi.R
import com.raisproject.destinasi.databinding.ActivityLoginBinding
import com.raisproject.destinasi.ui.registeruser.RegisterActivity
import com.raisproject.destinasi.ui.secretmenu.SecretMenuActivity

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar!!.hide()

        window.statusBarColor = ContextCompat.getColor(this, R.color.light_blue)

        auth = FirebaseAuth.getInstance()

        binding.tvForgotPassword.setOnClickListener {
            // do nothing
        }

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            // validasi email
            if (email.isEmpty()) {
                binding.etEmail.error = "Email Harus Diisi"
                binding.etEmail.requestFocus()
                return@setOnClickListener
            }
            // validasi email tidak sesuai
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.etEmail.error = "Email Tidak Valid"
                binding.etEmail.requestFocus()
                return@setOnClickListener
            }
            // validasi password
            if (password.isEmpty()) {
                binding.etPassword.error = "Password Harus Diisi"
                binding.etPassword.requestFocus()
                return@setOnClickListener
            }
            // validasi panjang password
            if (password.length < 6) {
                binding.etPassword.error = "Password Minimal 6 karakter"
                binding.etPassword.requestFocus()
                return@setOnClickListener
            }

            loginUser(email, password)
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                checkUser()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Login Failed due to ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkUser() {
        // check user
        /* userType
            * if user - move to user dashboard
            * if admin - move to admin dashboard
         */

        val user = auth.currentUser

        val databaseRef = FirebaseDatabase.getInstance().getReference("users")
        databaseRef.child(user!!.uid).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                // get type user or admin
                val userType = snapshot.child("userType").value
                if (userType == "user") {
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java).also {
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(it)
//                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
//                    finish()
                    })
                } else if (userType == "admin") {
                    // its admin, open admin dashboard
                    startActivity(Intent(this@LoginActivity, SecretMenuActivity::class.java).also {
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(it)
                        finish()
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onStart() {
            super.onStart()
            val user = auth.currentUser

            if (user != null) {
//            Intent(this@LoginActivity, MainActivity::class.java).also {
//                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                startActivity(it)
//            }

                val databaseRef = FirebaseDatabase.getInstance().getReference("users")
                databaseRef.child(user!!.uid)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            // get type user or admin
                            val userType = snapshot.child("userType").value
                            if (userType == "user") {
                                startActivity(
                                    Intent(
                                        this@LoginActivity,
                                        MainActivity::class.java
                                    ).also {
                                        intent.flags =
                                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        startActivity(it)
                                    })
                            } else if (userType == "admin") {
                                // its admin, open admin dashboard
                                startActivity(
                                    Intent(
                                        this@LoginActivity,
                                        SecretMenuActivity::class.java
                                    ).also {
                                        intent.flags =
                                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        startActivity(it)
                                        finish()
                                    })
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })
            }
    }
}