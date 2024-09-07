@file:Suppress("DEPRECATION")

package com.saurabhawathare.myartifact04

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var googleSignInBtn: ImageView
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var gsc: GoogleSignInClient
    private lateinit var gso: GoogleSignInOptions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views and Firebase
        googleSignInBtn = findViewById(R.id.googlelogo)
        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Set the language code for Firebase
        firebaseAuth.setLanguageCode(Locale.getDefault().language)

        // Configure Google Sign-In
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        gsc = GoogleSignIn.getClient(this, gso)

        // Set up the Activity Result API for sign-in
        val googleSignInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleSignInResult(task)
        }

        googleSignInBtn.setOnClickListener {
            googleSignInLauncher.launch(gsc.signInIntent)
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            if (account != null) {
                firebaseAuthWithGoogle(account.idToken!!)
            }
        } catch (e: ApiException) {
            Toast.makeText(this, "Google Sign-In failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    Toast.makeText(this, "Sign In Successful", Toast.LENGTH_SHORT).show()
                    val user: FirebaseUser? = firebaseAuth.currentUser
                    val map = mapOf(
                        "id" to user?.uid,
                        "name" to user?.displayName,
                        "profile" to user?.photoUrl.toString()
                    )
                    user?.let {
                        // Updated database reference path to "users"
                        database.getReference("users").child(it.uid).setValue(map)
                    }
                    startActivity(Intent(this, HomePage::class.java))
                } else {
                    // Sign in fails
                    Toast.makeText(this, "Sign In Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
