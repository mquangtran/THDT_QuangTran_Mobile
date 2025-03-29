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
import com.example.thdt_quangtran.models.NhanVien
import com.example.thdt_quangtran.models.TaiKhoan

class NhanVienAdapter(
    private val onItemClick: (NhanVien) -> Unit,
    private val taiKhoanMap: Map<Int, TaiKhoan>
) : ListAdapter<NhanVien, NhanVienAdapter.NhanVienViewHolder>(NhanVienDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NhanVienViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_employee, parent, false)
        return NhanVienViewHolder(view)
    }

    override fun onBindViewHolder(holder: NhanVienViewHolder, position: Int) {
        val nhanVien = getItem(position)
        holder.bind(nhanVien)
    }

    inner class NhanVienViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.employeeName)
        private val positionTextView: TextView = itemView.findViewById(R.id.employeePosition)
        private val phoneTextView: TextView = itemView.findViewById(R.id.employeePhone)
        private val emailTextView: TextView = itemView.findViewById(R.id.employeeEmail)
        private val avatarImageView: ImageView = itemView.findViewById(R.id.employeeAvatar)

        fun bind(nhanVien: NhanVien) {
            nameTextView.text = nhanVien.tenNhanVien
            positionTextView.text = nhanVien.chucVu ?: "N/A"
            phoneTextView.text = nhanVien.soDienThoai
            emailTextView.text = nhanVien.email ?: "N/A"

            val taiKhoan = taiKhoanMap[nhanVien.maNhanVien]
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

            itemView.setOnClickListener {
                onItemClick(nhanVien)
            }
        }
    }

    class NhanVienDiffCallback : DiffUtil.ItemCallback<NhanVien>() {
        override fun areItemsTheSame(oldItem: NhanVien, newItem: NhanVien): Boolean {
            return oldItem.maNhanVien == newItem.maNhanVien
        }

        override fun areContentsTheSame(oldItem: NhanVien, newItem: NhanVien): Boolean {
            return oldItem == newItem
        }
    }
}