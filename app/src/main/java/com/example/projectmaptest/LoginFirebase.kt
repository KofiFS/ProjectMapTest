package com.example.projectmaptest

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginFirebase : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signUpTextView: TextView

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize FirebaseAuth instance
        firebaseAuth = FirebaseAuth.getInstance()

        // Initialize views
        emailEditText = findViewById(R.id.editTextEmail)
        passwordEditText = findViewById(R.id.editTextPassword)
        loginButton = findViewById(R.id.buttonLogin)
        signUpTextView = findViewById(R.id.textViewSignUp)

        // Set click listener for login button
        loginButton.setOnClickListener {
            // Call loginUser function when login button is clicked
            loginUser()
        }

        // Set click listener for sign-up text view
        signUpTextView.setOnClickListener {
            // Navigate to SignUpActivity when sign-up text view is clicked
            navigateToSignUp()
        }
    }

    private fun loginUser() {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        // Validate email and password
        if (email.isEmpty() || password.isEmpty()) {
            // Show a toast message if email or password is empty
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        // Sign in user using FirebaseAuth
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = firebaseAuth.currentUser
                    // Show a toast message indicating successful authentication
                    Toast.makeText(this, "Authentication succeeded.", Toast.LENGTH_SHORT).show()

                    // Redirect user to MainActivity or any other activity
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    val errorMessage = task.exception?.message ?: "Unknown error"
                    // Show a toast message indicating authentication failure along with error message
                    Toast.makeText(this, "Authentication failed.  $errorMessage", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun navigateToSignUp() {
        // Start SignUpActivity
        startActivity(Intent(this, SignUpActivity::class.java))
    }
}
