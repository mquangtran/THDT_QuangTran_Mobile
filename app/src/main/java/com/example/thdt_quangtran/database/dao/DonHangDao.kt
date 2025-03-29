package com.example.thdt_quangtran.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.thdt_quangtran.models.DonHang
import kotlinx.coroutines.flow.Flow

@Dao
interface DonHangDao {
    @Insert
    suspend fun insert(donHang: DonHang)

    @Query("SELECT * FROM DonHang")
    suspend fun getAll(): List<DonHang>

    @Query("SELECT * FROM DonHang WHERE maDonHang = :maDonHang")
    suspend fun getById(maDonHang: Int): DonHang?

    @Query("SELECT * FROM DonHang WHERE maKhachHang = :maKhachHang")
    fun getByMaKhachHang(maKhachHang: Int): Flow<List<DonHang>>
}