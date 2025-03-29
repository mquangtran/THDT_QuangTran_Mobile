package com.example.thdt_quangtran.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.thdt_quangtran.R
import com.example.thdt_quangtran.models.KhachHang
import com.example.thdt_quangtran.models.TaiKhoan

class KhachHangAdapter(
    private val onItemClick: (KhachHang) -> Unit,
    private val taiKhoanMap: Map<Int, TaiKhoan>
) : ListAdapter<KhachHang, KhachHangAdapter.KhachHangViewHolder>(KhachHangDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KhachHangViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_customer, parent, false)
        return KhachHangViewHolder(view)
    }

    override fun onBindViewHolder(holder: KhachHangViewHolder, position: Int) {
        val khachHang = getItem(position)
        holder.bind(khachHang)
    }

    inner class KhachHangViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.customerName)
        private val phoneTextView: TextView = itemView.findViewById(R.id.customerPhone)
        private val emailTextView: TextView = itemView.findViewById(R.id.customerEmail)
        private val avatarImageView: ImageView = itemView.findViewById(R.id.customerAvatar)

        fun bind(khachHang: KhachHang) {
            nameTextView.text = khachHang.tenKhachHang
            phoneTextView.text = khachHang.soDienThoai ?: "N/A"
            emailTextView.text = khachHang.email ?: "N/A"

            // Lấy avatar từ taiKhoanMap
            val taiKhoan = taiKhoanMap[khachHang.maKhachHang]
            if (taiKhoan?.avatar != null) {
                val bitmap = BitmapFactory.decodeByteArray(
                    taiKhoan.avatar,
                    0,
                    taiKhoan.avatar.size
                )
                avatarImageView.setImageBitmap(bitmap)
            } else {
                avatarImageView.setImageResource(R.drawable.ic_default_avatar)
            }

            // Xử lý sự kiện nhấn vào item
            itemView.setOnClickListener {
                onItemClick(khachHang)
            }
        }
    }

    class KhachHangDiffCallback : DiffUtil.ItemCallback<KhachHang>() {
        override fun areItemsTheSame(oldItem: KhachHang, newItem: KhachHang): Boolean {
            return oldItem.maKhachHang == newItem.maKhachHang
        }

        override fun areContentsTheSame(oldItem: KhachHang, newItem: KhachHang): Boolean {
            return oldItem == newItem
        }
    }
}