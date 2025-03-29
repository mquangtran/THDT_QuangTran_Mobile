package com.example.thdt_quangtran.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.thdt_quangtran.models.ChiTietDonHang

@Dao
interface ChiTietDonHangDao {
    @Insert
    suspend fun insert(chiTietDonHang: ChiTietDonHang)

    @Query("SELECT * FROM ChiTietDonHang")
    suspend fun getAll(): List<ChiTietDonHang>

    @Query("SELECT * FROM ChiTietDonHang WHERE maDonHang = :maDonHang")
    suspend fun getByMaDonHang(maDonHang: Int): List<ChiTietDonHang>
}