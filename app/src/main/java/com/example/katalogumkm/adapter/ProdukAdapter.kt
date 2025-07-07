package com.example.katalogumkm.adapter

import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.katalogumkm.databinding.ItemProdukBinding
import com.example.katalogumkm.model.Produk
import com.example.katalogumkm.ui.DetailProdukActivity
import com.example.katalogumkm.ui.EditProdukActivity
import com.google.firebase.firestore.FirebaseFirestore

class ProdukAdapter(
    private val listProduk: List<Produk>,
    private val role: String
) : RecyclerView.Adapter<ProdukAdapter.ProdukViewHolder>() {

    inner class ProdukViewHolder(val binding: ItemProdukBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdukViewHolder {
        val binding = ItemProdukBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProdukViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProdukViewHolder, position: Int) {
        val produk = listProduk[position]
        val context = holder.binding.root.context
        val firestore = FirebaseFirestore.getInstance()

        with(holder.binding) {
            tvNamaProduk.text = produk.nama
            tvHargaProduk.text = "Rp ${produk.harga}"

            Glide.with(imgProduk.context)
                .load(produk.gambarUrl)
                .placeholder(android.R.color.darker_gray)
                .into(imgProduk)

            // Semua pengguna bisa lihat detail produk
            root.setOnClickListener {
                val intent = if (role == "admin") {
                    Intent(context, EditProdukActivity::class.java)
                } else {
                    Intent(context, DetailProdukActivity::class.java)
                }
                intent.putExtra(DetailProdukActivity.EXTRA_PRODUK, produk)
                context.startActivity(intent)
            }

            // Admin bisa hapus produk
            if (role == "admin") {
                btnHapus.visibility = View.VISIBLE
                btnHapus.setOnClickListener {
                    AlertDialog.Builder(context).apply {
                        setTitle("Konfirmasi Hapus")
                        setMessage("Yakin ingin menghapus produk ini?")
                        setPositiveButton("Ya") { _, _ ->
                            firestore.collection("produk").document(produk.id).delete()
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Produk dihapus", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Gagal menghapus", Toast.LENGTH_SHORT).show()
                                }
                        }
                        setNegativeButton("Batal", null)
                        show()
                    }
                }
            } else {
                root.setOnClickListener(null)
                btnHapus.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int = listProduk.size
}
