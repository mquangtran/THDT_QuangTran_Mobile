package com.example.thdt_quangtran.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "NhanVien")
data class NhanVien(
    @PrimaryKey(autoGenerate = true)
    val maNhanVien: Int = 0,
    val tenNhanVien: String,
    val cccd: String,
    val ngaySinh: Date,
    val gioiTinh: String,
    val soDienThoai: String,
    val email: String?,
    val diaChi: String?,
    val chucVu: String?,
    val ngayVaoLam: Date
)