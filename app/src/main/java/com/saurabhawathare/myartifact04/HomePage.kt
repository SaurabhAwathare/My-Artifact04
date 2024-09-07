package com.saurabhawathare.myartifact04

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class HomePage : AppCompatActivity() {

    private lateinit var logoutBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_page)

        logoutBtn = findViewById(R.id.logout_btn)

        // Set up logout button listener
        logoutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@HomePage, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish() // Optional: Close the current activity
        }
    }
}
