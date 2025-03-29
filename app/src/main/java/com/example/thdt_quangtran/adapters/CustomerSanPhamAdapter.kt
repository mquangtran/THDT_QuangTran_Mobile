package com.example.thdt_quangtran.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.thdt_quangtran.R
import com.example.thdt_quangtran.models.SanPham
import com.example.thdt_quangtran.utils.Utils
import android.graphics.BitmapFactory

class CustomerSanPhamAdapter(
    private val onItemClick: (SanPham) -> Unit
) : ListAdapter<SanPham, CustomerSanPhamAdapter.SanPhamViewHolder>(SanPhamDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SanPhamViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_customer_product, parent, false)
        return SanPhamViewHolder(view)
    }

    override fun onBindViewHolder(holder: SanPhamViewHolder, position: Int) {
        val sanPham = getItem(position)
        holder.bind(sanPham)
    }

    inner class SanPhamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.product_name)
        private val priceTextView: TextView = itemView.findViewById(R.id.product_price)
        private val imageView: ImageView = itemView.findViewById(R.id.product_image)

        fun bind(sanPham: SanPham) {
            nameTextView.text = sanPham.tenSanPham
            priceTextView.text = Utils.formatPrice(sanPham.giaBan)

            if (sanPham.hinhAnhSanPham != null) {
                val bitmap = BitmapFactory.decodeByteArray(
                    sanPham.hinhAnhSanPham,
                    0,
                    sanPham.hinhAnhSanPham.size
                )
                imageView.setImageBitmap(bitmap)
            } else {
                imageView.setImageResource(R.drawable.placeholder_image)
            }

            itemView.setOnClickListener {
                onItemClick(sanPham)
            }
        }
    }

    class SanPhamDiffCallback : DiffUtil.ItemCallback<SanPham>() {
        override fun areItemsTheSame(oldItem: SanPham, newItem: SanPham): Boolean {
            return oldItem.maSanPham == newItem.maSanPham
        }

        override fun areContentsTheSame(oldItem: SanPham, newItem: SanPham): Boolean {
            return oldItem == newItem
        }
    }
}