package com.example.thdt_quangtran.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.thdt_quangtran.models.GioHang

@Dao
interface GioHangDao {
    @Insert
    suspend fun insert(gioHang: GioHang)

    @Query("SELECT * FROM GioHang")
    suspend fun getAll(): List<GioHang>

    @Query("SELECT * FROM GioHang WHERE maGioHang = :maGioHang")
    suspend fun getById(maGioHang: Int): GioHang?

    @Query("SELECT * FROM GioHang WHERE maKhachHang = :maKhachHang")
    suspend fun getByMaKhachHang(maKhachHang: Int): GioHang?
}