package com.example.dessertcorner4

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    private lateinit var ivBack: ImageView
    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var cbTerms: CheckBox
    private lateinit var btnSignUp: Button
    private lateinit var tvLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        ivBack = findViewById(R.id.ivBack)
        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        cbTerms = findViewById(R.id.cbTerms)
        btnSignUp = findViewById(R.id.btnSignUp)
        tvLogin = findViewById(R.id.tvLogin)
    }

    private fun setupClickListeners() {
        ivBack.setOnClickListener { finish() }

        btnSignUp.setOnClickListener { validateAndRegister() }

        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun validateAndRegister() {
        val fullName = etFullName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()

        if (TextUtils.isEmpty(fullName)) {
            etFullName.error = "Full name is required"
            return
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.error = "Email is required"
            return
        } else if (!isValidEmail(email)) {
            etEmail.error = "Invalid email format"
            return
        }

        if (TextUtils.isEmpty(phone)) {
            etPhone.error = "Phone number is required"
            return
        } else if (!isValidPhone(phone)) {
            etPhone.error = "Invalid phone number"
            return
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.error = "Password is required"
            return
        } else if (password.length < 6) {
            etPassword.error = "Password must be at least 6 characters"
            return
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.error = "Please confirm your password"
            return
        } else if (password != confirmPassword) {
            etConfirmPassword.error = "Passwords do not match"
            return
        }

        if (!cbTerms.isChecked) {
            Toast.makeText(this, "Please agree to Terms & Conditions", Toast.LENGTH_SHORT).show()
            return
        }

        registerUser(fullName, email, phone, password)
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return Pattern.matches(emailPattern, email)
    }

    private fun isValidPhone(phone: String): Boolean {
        val phonePattern = "^01[0-9]{8,9}$"
        return Pattern.matches(phonePattern, phone)
    }

    private fun registerUser(fullName: String, email: String, phone: String, password: String) {
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: ""
                    val newUser = User(
                        id = userId,
                        fullName = fullName,
                        email = email,
                        phone = phone,
                        password = password,
                        userType = "customer"
                    )

                    firestore.collection("users").document(userId).set(newUser)
                        .addOnSuccessListener {
                            saveUserToPrefs(newUser)
                            Toast.makeText(this, "Registration successful!", Toast.LENGTH_LONG).show()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Firestore Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }

                } else {
                    Toast.makeText(this, "Auth Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
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
