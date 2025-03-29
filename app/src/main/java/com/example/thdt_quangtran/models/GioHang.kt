package com.example.thdt_quangtran.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "GioHang",
    foreignKeys = [
        ForeignKey(
            entity = KhachHang::class,
            parentColumns = ["maKhachHang"],
            childColumns = ["maKhachHang"]
        )
    ]
)
data class GioHang(
    @PrimaryKey(autoGenerate = true)
    val maGioHang: Int = 0,
    val maKhachHang: Int
)