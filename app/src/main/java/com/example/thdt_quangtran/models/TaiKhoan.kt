package com.example.thdt_quangtran.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "TaiKhoan",
    foreignKeys = [
        ForeignKey(
            entity = NhanVien::class,
            parentColumns = ["maNhanVien"],
            childColumns = ["maNhanVien"]
        ),
        ForeignKey(
            entity = KhachHang::class,
            parentColumns = ["maKhachHang"],
            childColumns = ["maKhachHang"]
        )
    ]
)
data class TaiKhoan(
    @PrimaryKey
    val tenDangNhap: String,
    val matKhau: String,
    val vaiTro: String?,
    val maNhanVien: Int?,
    val maKhachHang: Int?,
    val avatar: ByteArray?
)