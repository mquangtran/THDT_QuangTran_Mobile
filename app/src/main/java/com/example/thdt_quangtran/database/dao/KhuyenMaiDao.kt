package com.example.thdt_quangtran.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.thdt_quangtran.models.KhuyenMai

@Dao
interface KhuyenMaiDao {
    @Insert
    suspend fun insert(khuyenMai: KhuyenMai)

    @Query("SELECT * FROM KhuyenMai")
    suspend fun getAll(): List<KhuyenMai>

    @Query("SELECT * FROM KhuyenMai WHERE maKhuyenMai = :maKhuyenMai")
    suspend fun getById(maKhuyenMai: Int): KhuyenMai?
}