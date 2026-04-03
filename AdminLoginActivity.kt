package com.example.dessertcorner4

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class AdminLoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login)

        val etUsername = findViewById<TextInputEditText>(R.id.etAdminUsername)
        val etPassword = findViewById<TextInputEditText>(R.id.etAdminPassword)
        val btnSignIn = findViewById<Button>(R.id.btnAdminSignIn)

        btnSignIn.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (username == ADMIN_USERNAME && password == ADMIN_PASSWORD) {
                // Redirect to Admin Dashboard
                val intent = Intent(this, AdminMainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val ADMIN_USERNAME = "admin"
        private const val ADMIN_PASSWORD = "dessert123"
    }
}
