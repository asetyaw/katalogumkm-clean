package com.example.katalogumkm.ui

import android.app.AlertDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.katalogumkm.databinding.ActivityEditProdukBinding
import com.example.katalogumkm.model.Produk
import com.google.firebase.firestore.FirebaseFirestore

class EditProdukActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProdukBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var produk: Produk

    companion object {
        const val EXTRA_PRODUK = "extra_produk"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProdukBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val role = prefs.getString("role", null)
        if (role != "admin") {
            Toast.makeText(this, "Akses ditolak", Toast.LENGTH_SHORT).show()
            finish()
        }

        val kategoriList = listOf("Makanan", "Minuman", "Kerajinan", "Pakaian", "Lainnya")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, kategoriList)
        binding.spinnerKategori.adapter = adapter

        produk = intent.getSerializableExtra(EXTRA_PRODUK) as Produk

        // Isi data awal
        binding.etNama.setText(produk.nama)
        binding.etHarga.setText(produk.harga.toString())
        binding.etDeskripsi.setText(produk.deskripsi)
        binding.etUrlGambar.setText(produk.gambarUrl)
        Glide.with(this).load(produk.gambarUrl).into(binding.imgPreview)

        // Pilih kategori yang sesuai
        val posisiKategori = kategoriList.indexOf(produk.kategori)
        if (posisiKategori != -1) {
            binding.spinnerKategori.setSelection(posisiKategori)
        }

        // Simpan perubahan
        binding.btnSimpan.setOnClickListener {
            updateProduk()
        }

        // Hapus produk
        binding.btnHapus.setOnClickListener {
            konfirmasiHapusProduk()
        }
    }

    private fun updateProduk() {
        val nama = binding.etNama.text.toString().trim()
        val harga = binding.etHarga.text.toString().trim().toIntOrNull()
        val deskripsi = binding.etDeskripsi.text.toString().trim()
        val gambarUrl = binding.etUrlGambar.text.toString().trim()
        val kategori = binding.spinnerKategori.selectedItem.toString()

        if (nama.isEmpty() || harga == null || deskripsi.isEmpty() || gambarUrl.isEmpty()) {
            Toast.makeText(this, "Lengkapi semua field!", Toast.LENGTH_SHORT).show()
            return
        }

        val update = mapOf(
            "nama" to nama,
            "harga" to harga,
            "deskripsi" to deskripsi,
            "gambarUrl" to gambarUrl,
            "kategori" to kategori,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("produk").document(produk.id)
            .update(update)
            .addOnSuccessListener {
                Toast.makeText(this, "Produk diperbarui!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal memperbarui produk", Toast.LENGTH_SHORT).show()
            }
    }

    private fun konfirmasiHapusProduk() {
        AlertDialog.Builder(this)
            .setTitle("Hapus Produk")
            .setMessage("Yakin ingin menghapus produk ini?")
            .setPositiveButton("Hapus") { _, _ -> hapusProduk() }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun hapusProduk() {
        db.collection("produk").document(produk.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Produk berhasil dihapus", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal menghapus produk", Toast.LENGTH_SHORT).show()
            }
    }
}
