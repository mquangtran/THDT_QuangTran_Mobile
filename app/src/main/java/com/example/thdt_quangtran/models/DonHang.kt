package com.example.thdt_quangtran.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "DonHang",
    foreignKeys = [
        ForeignKey(
            entity = KhachHang::class,
            parentColumns = ["maKhachHang"],
            childColumns = ["maKhachHang"]
        ),
        ForeignKey(
            entity = NhanVien::class,
            parentColumns = ["maNhanVien"],
            childColumns = ["maNhanVien"]
        )
    ]
)
data class DonHang(
    @PrimaryKey(autoGenerate = true)
    val maDonHang: Int = 0,
    val maKhachHang: Int,
    val maNhanVien: Int?,
    val tenNguoiNhanHang: String,
    val soDienThoaiNguoiNhan: String,
    val diaChiNguoiNhan: String,
    val ngayDatHang: Date,
    val ngayDuKienNhanHang: Date?,
    val phuongThucThanhToan: String,
    val trangThai: String
)