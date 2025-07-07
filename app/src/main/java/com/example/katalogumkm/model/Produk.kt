package com.example.katalogumkm.model

import java.io.Serializable

data class Produk(
    var id: String = "",
    var nama: String = "",
    var harga: Int = 0,
    var deskripsi: String = "",
    var gambarUrl: String = "",
    var kategori: String = "",
    var timestamp: Long = System.currentTimeMillis()
) : Serializable
