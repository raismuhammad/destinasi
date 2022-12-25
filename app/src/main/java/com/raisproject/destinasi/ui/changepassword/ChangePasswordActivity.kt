package com.raisproject.destinasi.ui.changepassword

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.raisproject.destinasi.R
import com.raisproject.destinasi.databinding.ActivityChangePasswordBinding
import com.raisproject.destinasi.ui.loginuser.LoginActivity

class ChangePasswordActivity : AppCompatActivity() {

    lateinit var binding: ActivityChangePasswordBinding

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.light_blue)

        val actionBar = supportActionBar
        actionBar!!.title = "Ubah Password"
        actionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.light_blue)))
        actionBar!!.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        // click btn check current password
        binding.btnCheckCurrentPassword.setOnClickListener {
            val currentPassword = binding.etCurrentPassword.text.toString()

            if (currentPassword.isEmpty()) {
                binding.etCurrentPassword.error = "Current Password tidak boleh kosong"
                binding.etCurrentPassword.requestFocus()
                return@setOnClickListener
            }

            // Credential user (current password)
            user.let {
                val userCredential = EmailAuthProvider.getCredential(it?.email!!, currentPassword)
                it.reauthenticate(userCredential).addOnCompleteListener { task ->
                    when {
                        task.isSuccessful -> {
                            Toast.makeText(this@ChangePasswordActivity, "Password benar", Toast.LENGTH_SHORT).show()
                            binding.btnCheckCurrentPassword.setBackgroundColor(Color.GREEN)
                        }
                        task.exception is FirebaseAuthInvalidCredentialsException -> {
                            binding.etCurrentPassword.error = "Password Salah"
                            binding.etCurrentPassword.requestFocus()
                            binding.btnCheckCurrentPassword.setBackgroundColor(Color.RED)
                        }
                        else -> {
                            Toast.makeText(this@ChangePasswordActivity, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        }

        binding.btnUpdatePassword.setOnClickListener {
            val newPassword = binding.etNewPassword.text.toString()
            val confirmNewPassword = binding.etConfirmNewPassword.text.toString()

            if (newPassword.isEmpty()) {
                binding.etNewPassword.error = "Password baru tidak boleh kosong"
                binding.etNewPassword.requestFocus()
                return@setOnClickListener
            }

            if (newPassword.length < 6 ) {
                binding.etNewPassword.error = "password minimal 6 karakter"
                binding.etNewPassword.requestFocus()
                return@setOnClickListener
            }

            if (confirmNewPassword.isEmpty()) {
                binding.etConfirmNewPassword.error = "Ulangi password baru"
                binding.etConfirmNewPassword.requestFocus()
                return@setOnClickListener
            }

            if (confirmNewPassword.length < 6 ) {
                binding.etConfirmNewPassword.error = "password minimal 6 karakter"
                binding.etConfirmNewPassword.requestFocus()
                return@setOnClickListener
            }

            if (newPassword != confirmNewPassword) {
                binding.etConfirmNewPassword.error = "password tidak sama"
                binding.etConfirmNewPassword.requestFocus()
            }

            user?.updatePassword(newPassword)?.addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this@ChangePasswordActivity, "Password berhasil di update", Toast.LENGTH_SHORT).show()
                    successLogout()
                } else {
                    Toast.makeText(this@ChangePasswordActivity, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    // jika sukses mengubah password maka akun akan logout secara otomatis
    private fun successLogout() {
        auth = FirebaseAuth.getInstance()
        auth.signOut()

        val intent = Intent(this@ChangePasswordActivity, LoginActivity::class.java)
        startActivity(intent)
        this.finish()

        Toast.makeText(this@ChangePasswordActivity, "Silahkan login kembali", Toast.LENGTH_SHORT).show()
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