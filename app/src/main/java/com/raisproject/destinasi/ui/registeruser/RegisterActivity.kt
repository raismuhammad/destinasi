package com.raisproject.destinasi.ui.registeruser

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.model.DatabaseId
import com.google.firebase.ktx.Firebase
import com.raisproject.destinasi.MainActivity
import com.raisproject.destinasi.R
import com.raisproject.destinasi.databinding.ActivityRegisterBinding
import com.raisproject.destinasi.ui.loginuser.LoginActivity
import com.raisproject.destinasi.ui.secretmenu.SecretMenuActivity

class RegisterActivity : AppCompatActivity() {

    lateinit var binding: ActivityRegisterBinding

    lateinit var auth: FirebaseAuth

    lateinit var databaseRef: DatabaseReference

    private var email = ""
    private var fullname = ""
    private var password = ""
    private var confPassword = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar!!.setHomeButtonEnabled(true)

        auth = FirebaseAuth.getInstance()

        window.statusBarColor = ContextCompat.getColor(this, R.color.light_blue)

        // actionbar
        val actionBar = supportActionBar
        // set actionbar tittle
        actionBar!!.title = "Register Account"
        actionBar.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.light_blue)))
        // set back button
        actionBar.setDisplayHomeAsUpEnabled(true)

        binding.btnLogin.setOnClickListener{
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnRegister.setOnClickListener {
            /* step
                * input data
                * validate data
                * create account
                * save data info - firebase realtime database
             */

            validateData()

        }
    }

    private fun validateData() {
        // 2) validate data
        fullname = binding.etFullName.text.toString()
        email = binding.etEmail.text.toString()
        password = binding.etPassword.text.toString()
        confPassword = binding.etConfPassword.text.toString()

        if (fullname.isEmpty()) {
            binding.etFullName.error = "Nama Lengkap harus diisi"
            binding.etFullName.requestFocus()
            return
        }
        else if (email.isEmpty()) {
            binding.etEmail.error = "Email harus diisi"
            binding.etEmail.requestFocus()
            return
        }
        else if (password.isEmpty()) {
            binding.etPassword.error = "Password harus diisi"
            binding.etPassword.requestFocus()
            return
        }
        else if (password.length < 6) {
            binding.etPassword.error = "Password minimal 6 karakter"
            binding.etPassword.requestFocus()
            return
        }
        else if (confPassword.isEmpty()) {
            binding.etConfPassword.error = "Confirmasi Password harus diisi"
            binding.etConfPassword.requestFocus()
            return
        }
        else if (password != confPassword) {
            binding.etConfPassword.error = "Password tidak sama"
            binding.etConfPassword.requestFocus()
            return
        }
        else {
            registerUser()
        }
    }

    fun registerUser() {
        // create account - firebase auth
            auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                updateUserInfo()
            }
                .addOnFailureListener {
                    Toast.makeText(this, "Register Gagal ${it.message}", Toast.LENGTH_SHORT).show()
                }
    }

    private fun updateUserInfo() {
        // 4) save user info - firebase realtime database

        val timestamp = System.currentTimeMillis()

        val uid = auth.currentUser!!.uid

        // setup data to add in db
        val hashMap: HashMap<String, Any?> = HashMap()
//        hashMap["uid"] = uid
        hashMap["fullname"] = fullname
        hashMap["email"] = email
        hashMap["userType"] = "user"
        hashMap["timestamp"] = timestamp

        databaseRef = FirebaseDatabase.getInstance().getReference("users")
        databaseRef.child(uid).setValue(hashMap)
            .addOnSuccessListener {
                // user info saved, open user dashboard
                Toast.makeText(this, "Berhasil membuat Akun", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                // failed adding data to db
                Toast.makeText(this, "Gagal menyimpan user info ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onStart() {
        super.onStart()

        val user = auth.currentUser

        if (user != null) {
//            Intent(this@RegisterActivity, MainActivity::class.java).also {
//                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                startActivity(it)
//            }

            val databaseRef = FirebaseDatabase.getInstance().getReference("users")
            databaseRef.child(user!!.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // get type user or admin
                    val userType = snapshot.child("userType").value
                    if (userType == "user") {
                        startActivity(Intent(this@RegisterActivity, MainActivity::class.java).also {
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(it)
                        })
                    } else if (userType == "admin") {
                        // its admin, open admin dashboard
                        startActivity(Intent(this@RegisterActivity, SecretMenuActivity::class.java).also {
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

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        return;
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}