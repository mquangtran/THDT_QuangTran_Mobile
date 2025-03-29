package com.example.thdt_quangtran.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.thdt_quangtran.models.ChiTietGioHang
import kotlinx.coroutines.flow.Flow

@Dao
interface ChiTietGioHangDao {
    @Insert
    suspend fun insert(chiTietGioHang: ChiTietGioHang)

    @Update
    suspend fun update(chiTietGioHang: ChiTietGioHang)

    @Delete
    suspend fun delete(chiTietGioHang: ChiTietGioHang)

    @Query("SELECT * FROM ChiTietGioHang")
    suspend fun getAll(): List<ChiTietGioHang>

    @Query("SELECT * FROM ChiTietGioHang WHERE maGioHang = :maGioHang")
    fun getByMaGioHang(maGioHang: Int): Flow<List<ChiTietGioHang>>

    @Query("SELECT * FROM ChiTietGioHang WHERE maGioHang = :maGioHang")
    suspend fun getByMaGioHangSuspend(maGioHang: Int): List<ChiTietGioHang>
}