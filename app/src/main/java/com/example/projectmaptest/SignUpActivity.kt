package com.example.projectmaptest

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    // Declaring EditText, Button, and FirebaseAuth variables
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var retypePasswordEditText: EditText
    private lateinit var signUpButton: Button
    private lateinit var backButton: Button
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initializing FirebaseAuth instance
        firebaseAuth = FirebaseAuth.getInstance()

        // Initializing views
        emailEditText = findViewById(R.id.editTextEmail)
        passwordEditText = findViewById(R.id.editTextPassword)
        retypePasswordEditText = findViewById(R.id.editTextRetypePassword)
        signUpButton = findViewById(R.id.buttonSignUp)
        backButton = findViewById(R.id.buttonBack)

        // Setting click listener for sign-up button
        signUpButton.setOnClickListener {
            signUpUser() // Calling signUpUser function when sign-up button is clicked
        }

        // Setting click listener for back button
        backButton.setOnClickListener {
            // Navigating back to the login screen
            startActivity(Intent(this, LoginFirebase::class.java))
            finish()
        }
    }

    // Function to sign up the user
    private fun signUpUser() {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        val retypePassword = retypePasswordEditText.text.toString()

        // Validating email format
        if (!isValidEmail(email)) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return
        }

        // Validating email and passwords
        if (email.isEmpty() || password.isEmpty() || retypePassword.isEmpty()) {
            Toast.makeText(this, "Please enter email and passwords", Toast.LENGTH_SHORT).show()
            return
        }

        // Checking if passwords match
        if (password != retypePassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        // Signing up user using FirebaseAuth
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign up success, updating UI with the signed-up user's information
                    val user = firebaseAuth.currentUser
                    Toast.makeText(this, "Sign up succeeded.", Toast.LENGTH_SHORT).show()

                    // Redirecting user to the login screen
                    startActivity(Intent(this, LoginFirebase::class.java))
                    finish()
                } else {
                    // If sign up fails, displaying a message to the user
                    Toast.makeText(this, "${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    // Function to validate email format
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
        return email.matches(emailRegex.toRegex())
    }
}
