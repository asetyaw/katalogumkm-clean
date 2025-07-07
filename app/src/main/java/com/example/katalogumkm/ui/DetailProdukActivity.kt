package com.example.katalogumkm.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.katalogumkm.databinding.ActivityDetailProdukBinding
import com.example.katalogumkm.model.Produk

class DetailProdukActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailProdukBinding
    private lateinit var produk: Produk

    companion object {
        const val EXTRA_PRODUK = "extra_produk"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailProdukBinding.inflate(layoutInflater)
        setContentView(binding.root)

        produk = intent.getSerializableExtra(EXTRA_PRODUK) as Produk

        binding.tvNamaProduk.text = produk.nama
        binding.tvHargaProduk.text = "Rp ${produk.harga}"
        binding.tvDeskripsi.text = produk.deskripsi

        Glide.with(this)
            .load(produk.gambarUrl)
            .into(binding.imgProduk)
    }
}
