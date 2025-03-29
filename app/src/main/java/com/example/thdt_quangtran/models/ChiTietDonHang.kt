package com.example.thdt_quangtran.models

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "ChiTietDonHang",
    primaryKeys = ["maDonHang", "maSanPham"],
    foreignKeys = [
        ForeignKey(
            entity = DonHang::class,
            parentColumns = ["maDonHang"],
            childColumns = ["maDonHang"]
        ),
        ForeignKey(
            entity = SanPham::class,
            parentColumns = ["maSanPham"],
            childColumns = ["maSanPham"]
        ),
        ForeignKey(
            entity = KhuyenMai::class,
            parentColumns = ["maKhuyenMai"],
            childColumns = ["maKhuyenMai"]
        )
    ]
)
data class ChiTietDonHang(
    val maDonHang: Int,
    val maSanPham: Int,
    val soLuong: Int,
    val giaBan: Long,
    val maKhuyenMai: Int?
)