package com.example.katalogumkm.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.katalogumkm.databinding.ActivityTambahProdukBinding
import com.example.katalogumkm.model.Produk
import com.google.firebase.firestore.FirebaseFirestore

class TambahProdukActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTambahProdukBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahProdukBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil role dari SharedPreferences
        val prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val role = prefs.getString("role", "user")

        // Inisialisasi spinner kategori
        val kategoriList = listOf("Makanan", "Minuman", "Kerajinan", "Pakaian", "Lainnya")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, kategoriList)
        binding.spinnerKategori.adapter = adapter

        binding.etUrlGambar.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val url = binding.etUrlGambar.text.toString().trim()
                if (url.isNotEmpty()) {
                    Glide.with(this)
                        .load(url)
                        .into(binding.imgPreview)
                }
            }
        }

        binding.etUrlGambar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val url = s.toString().trim()
                if (url.isNotEmpty()) {
                    Glide.with(this@TambahProdukActivity)
                        .load(url)
                        .into(binding.imgPreview)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.btnSimpan.setOnClickListener {
            simpanProduk()
        }

        // Nonaktifkan form jika bukan admin
        if (role != "admin") {
            Toast.makeText(this, "Anda tidak memiliki akses untuk menambah produk.", Toast.LENGTH_SHORT).show()
            disableForm()
        }
    }

    private fun simpanProduk() {
        val nama = binding.etNama.text.toString().trim()
        val harga = binding.etHarga.text.toString().trim().toIntOrNull()
        val deskripsi = binding.etDeskripsi.text.toString().trim()
        val kategori = binding.spinnerKategori.selectedItem.toString()
        val urlGambar = binding.etUrlGambar.text.toString().trim()

        if (nama.isEmpty() || harga == null || deskripsi.isEmpty() || urlGambar.isEmpty()) {
            Toast.makeText(this, "Lengkapi semua field!", Toast.LENGTH_SHORT).show()
            return
        }

        val produk = Produk(
            nama = nama,
            harga = harga,
            deskripsi = deskripsi,
            gambarUrl = urlGambar,
            kategori = kategori
        )

        db.collection("produk").add(produk)
            .addOnSuccessListener {
                Toast.makeText(this, "Produk berhasil disimpan!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal menyimpan ke Firestore", Toast.LENGTH_SHORT).show()
            }
    }

    private fun disableForm() {
        binding.etNama.isEnabled = false
        binding.etHarga.isEnabled = false
        binding.etDeskripsi.isEnabled = false
        binding.etUrlGambar.isEnabled = false
        binding.spinnerKategori.isEnabled = false
        binding.btnSimpan.visibility = android.view.View.GONE
    }
}
