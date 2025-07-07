package com.example.katalogumkm.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.katalogumkm.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    companion object {
        private const val RC_SIGN_IN = 9001
        private const val TAG = "LoginActivity"
    }

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth
    private lateinit var progressBar: ProgressBar
    private lateinit var googleSignInButton: Button

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    Log.d(" UID_CHECK", "UID setelah login: ${user?.uid}")
                    val uid = user?.uid ?: return@addOnCompleteListener
                    val db = FirebaseFirestore.getInstance()
                    val userRef = db.collection("users").document(uid)
                    Log.d("UID_CHECK", "User ID: ${user?.uid}")

                    userRef.get().addOnSuccessListener { document ->
                        val prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)

                        if (!document.exists()) {
                            // User belum terdaftar, buat dokumen user baru dengan role kosong
                            val newUser = hashMapOf(
                                "email" to (user?.email ?: ""),
                                "role" to "" // kosong
                            )
                            userRef.set(newUser)
                                .addOnSuccessListener {
                                    prefs.edit().putString("role", "").apply()
                                    Toast.makeText(
                                        this,
                                        "Akun baru dibuat. Akses terbatas.",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    startActivity(Intent(this, MainActivity::class.java))
                                    finish()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        this,
                                        "Gagal menyimpan akun baru",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    FirebaseAuth.getInstance().signOut()
                                    progressBar.visibility = View.GONE
                                }

                        } else {
                            val role = document.getString("role") ?: ""
                            prefs.edit().putString("role", role).apply()
                            if (role == "admin") {
                                Toast.makeText(this, "Login sebagai Admin", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                Toast.makeText(
                                    this,
                                    "Login sebagai Pengguna Biasa",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        // Masuk ke MainActivity setelah selesai cek/simpan role
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()

                    }.addOnFailureListener {
                        Toast.makeText(this, "Gagal mengambil data pengguna", Toast.LENGTH_SHORT)
                            .show()
                        FirebaseAuth.getInstance().signOut()
                        progressBar.visibility = View.GONE
                    }

                } else {
                    Toast.makeText(this, "Autentikasi gagal.", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                }
            }
    }

    private fun signIn() {
        progressBar.visibility = View.VISIBLE
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun loginManual() {
        val email = findViewById<EditText>(R.id.etEmail).text.toString().trim()
        val password = findViewById<EditText>(R.id.etPassword).text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email dan password tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        progressBar.visibility = View.VISIBLE

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(" UID_CHECK", "UID setelah login: ${mAuth.currentUser?.uid}")
                    val uid = mAuth.currentUser?.uid ?: return@addOnCompleteListener
                    val db = FirebaseFirestore.getInstance()
                    val prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)

                    db.collection("users").document(uid).get()
                        .addOnSuccessListener { document ->
                            val role = document.getString("role") ?: ""
                            prefs.edit().putString("role", role).apply()

                            if (role == "admin") {
                                Toast.makeText(this, "Login sebagai Admin", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                Toast.makeText(
                                    this,
                                    "Login sebagai Pengguna Biasa",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }.addOnFailureListener {
                            Toast.makeText(this, "Gagal mengambil data role", Toast.LENGTH_SHORT)
                                .show()
                            progressBar.visibility = View.GONE
                        }
                } else {
                    Toast.makeText(
                        this,
                        "Login gagal: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    progressBar.visibility = View.GONE
                }
            }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(this, "Google sign in gagal", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            FirebaseAuth.getInstance().signOut()
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        googleSignInButton = findViewById(R.id.googleSignInButton)
        progressBar = findViewById(R.id.progressBar)

        val btnLoginManual = findViewById<Button>(R.id.btnLoginManual)
        btnLoginManual.setOnClickListener {
            loginManual()
        }

        val btnRegister = findViewById<Button>(R.id.btnRegister)
        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        mAuth = FirebaseAuth.getInstance()

        googleSignInButton.setOnClickListener {
            signIn()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

}
