package com.example.thdt_quangtran.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.thdt_quangtran.databinding.ItemProductBinding
import com.example.thdt_quangtran.models.SanPham

class SanPhamAdapter(
    private val onEditClick: (SanPham) -> Unit,
    private val onDeleteClick: (SanPham) -> Unit
) : ListAdapter<SanPham, SanPhamAdapter.SanPhamViewHolder>(SanPhamDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SanPhamViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SanPhamViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SanPhamViewHolder, position: Int) {
        val sanPham = getItem(position)
        holder.bind(sanPham)
    }

    inner class SanPhamViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(sanPham: SanPham) {
            binding.productName.text = sanPham.tenSanPham
            binding.productPrice.text = "${sanPham.giaBan} VNĐ"
            binding.productStock.text = "Tồn kho: ${sanPham.soLuongTonKho}"

            // Hiển thị hình ảnh nếu có
            sanPham.hinhAnhSanPham?.let { imageBytes ->
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                binding.productImage.setImageBitmap(bitmap)
            }

            binding.editButton.setOnClickListener { onEditClick(sanPham) }
            binding.deleteButton.setOnClickListener { onDeleteClick(sanPham) }
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