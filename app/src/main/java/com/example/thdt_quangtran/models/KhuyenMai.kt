package com.example.thdt_quangtran.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "KhuyenMai",
    foreignKeys = [
        ForeignKey(
            entity = SanPham::class,
            parentColumns = ["maSanPham"],
            childColumns = ["maSanPham"]
        )
    ]
)
data class KhuyenMai(
    @PrimaryKey(autoGenerate = true)
    val maKhuyenMai: Int = 0,
    val maGiamGia: String?,
    val loaiKhuyenMai: String,
    val maSanPham: Int?,
    val phanTramGiam: Float,
    val ngayBatDau: Date,
    val ngayKetThuc: Date,
    val trangThai: String
)