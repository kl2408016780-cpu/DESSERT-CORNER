package com.example.dessertcorner4

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnSignIn: Button
    private lateinit var tvSignUp: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnSignIn = findViewById(R.id.btnSignIn)
        tvSignUp = findViewById(R.id.tvSignUp)
    }

    private fun setupListeners() {
        btnSignIn.setOnClickListener { loginUser() }

        tvSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

    }

    private fun loginUser() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (TextUtils.isEmpty(email)) {
            etEmail.error = "Email is required"
            return
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.error = "Password is required"
            return
        }

        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: ""
                    checkUserRole(userId)
                } else {
                    Toast.makeText(this, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkUserRole(userId: String) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val user = document.toObject(User::class.java)
                    if (user != null) {
                        saveUserToPrefs(user)
                        
                        if (user.userType == "admin" || user.email == "admin@gmail.com") {
                            Toast.makeText(this, "Welcome Admin!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, AdminMainActivity::class.java))
                        } else {
                            Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                        }
                        finish()
                    }
                } else {
                    // Fallback for special admin
                    if (FirebaseAuth.getInstance().currentUser?.email == "admin@gmail.com") {
                        val adminUser = User(userId, "Admin User", "admin@gmail.com", "", "", "admin")
                        saveUserToPrefs(adminUser)
                        startActivity(Intent(this, AdminMainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "User data not found!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveUserToPrefs(user: User) {
        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        sharedPref.edit().apply {
            putString("user_id", user.id)
            putString("user_name", user.fullName)
            putString("user_email", user.email)
            putString("user_password", user.password)
            putString("user_phone", user.phone)
            putString("user_type", user.userType)
            apply()
        }
    }
}
