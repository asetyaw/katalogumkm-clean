package com.example.katalogumkm.utils

import java.text.NumberFormat
import java.util.*

object Utils {

    fun formatRupiah(angka: Number): String {
        val localeID = Locale("in", "ID")
        val formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(localeID)
        return formatRupiah.format(angka)
            .replace("Rp", "Rp ")
            .replace(",00", "")
    }

    fun formatRentangHarga(hargaList: List<Int>): String {
        if (hargaList.isEmpty()) return "Rp 0"

        val minHarga = hargaList.minOrNull() ?: 0
        val maxHarga = hargaList.maxOrNull() ?: 0

        return if (minHarga == maxHarga) {
            formatRupiah(minHarga)
        } else {
            "${formatRupiah(minHarga)} - ${formatRupiah(maxHarga)}"
        }
    }

}
