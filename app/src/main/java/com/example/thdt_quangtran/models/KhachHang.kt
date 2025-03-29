package com.example.thdt_quangtran.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "KhachHang")
data class KhachHang(
    @PrimaryKey(autoGenerate = true)
    val maKhachHang: Int = 0,
    val tenKhachHang: String,
    val gioiTinh: String?,
    val ngaySinh: Date?,
    val soDienThoai: String?,
    val email: String?,
    val diaChi: String?
)