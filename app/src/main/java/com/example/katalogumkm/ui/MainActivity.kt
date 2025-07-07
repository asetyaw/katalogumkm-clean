package com.example.katalogumkm.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.widget.SearchView
import com.example.katalogumkm.R
import com.example.katalogumkm.adapter.ProdukAdapter
import com.example.katalogumkm.databinding.ActivityMainBinding
import com.example.katalogumkm.model.Produk
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var produkAdapter: ProdukAdapter
    private val db = FirebaseFirestore.getInstance()
    private var listProduk: MutableList<Produk> = mutableListOf()
    private var filteredList: MutableList<Produk> = mutableListOf()
    private var userRole: String = "user" // Default role

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance()
        )
        userRole
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)
        mAuth = FirebaseAuth.getInstance()

        // Ambil role dari SharedPreferences
        val prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        userRole = prefs.getString("role", "user") ?: "unknown"

        // Tampilkan atau sembunyikan tombol tambah produk
        if (userRole == "admin") {
            binding.fabTambah.visibility = View.VISIBLE
            binding.fabTambah.setOnClickListener {
                startActivity(Intent(this, TambahProdukActivity::class.java))
            }
        } else {
            binding.fabTambah.visibility = View.GONE
        }

        setupRecyclerView()
        setupFilterUI()
        getDataProduk()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_Logout) {
            mAuth.signOut()
            Toast.makeText(this, "Logout berhasil", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupRecyclerView() {
        produkAdapter = ProdukAdapter(filteredList, userRole) // Kirim role ke adapter
        binding.rvProduk.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = produkAdapter
        }
    }

    private fun setupFilterUI() {
        val kategoriList = listOf("Semua", "Makanan", "Minuman", "Kerajinan", "Pakaian", "Lainnya")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, kategoriList)
        binding.spinnerFilter.adapter = spinnerAdapter

        binding.spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                applyFilter()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = true

            override fun onQueryTextChange(newText: String?) : Boolean {
                applyFilter()
                return true
            }
        })
    }

    private fun getDataProduk() {
        db.collection("produk")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, _ ->
                listProduk.clear()
                snapshot?.forEach {
                    val produk = it.toObject(Produk::class.java)
                    produk.id = it.id
                    listProduk.add(produk)
                }
                applyFilter()
            }
    }

    private fun applyFilter() {
        val query = binding.searchView.query.toString().lowercase()
        val selectedKategori = binding.spinnerFilter.selectedItem.toString()

        filteredList.clear()
        filteredList.addAll(listProduk.filter {
            val matchNama = it.nama.lowercase().contains(query)
            val matchKategori = selectedKategori == "Semua" || it.kategori == selectedKategori
            matchNama && matchKategori
        })

        produkAdapter.notifyDataSetChanged()
    }
}
