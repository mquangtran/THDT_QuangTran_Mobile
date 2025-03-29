package com.example.thdt_quangtran.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Query
import androidx.room.Delete
import com.example.thdt_quangtran.models.SanPham
import kotlinx.coroutines.flow.Flow

@Dao
interface SanPhamDao {
    @Insert
    suspend fun insert(sanPham: SanPham)

    @Update
    suspend fun update(sanPham: SanPham)

    @Query("SELECT * FROM SanPham")
    fun getAllSanPham(): Flow<List<SanPham>>

    @Query("SELECT * FROM SanPham WHERE maSanPham = :maSanPham")
    suspend fun getById(maSanPham: Int): SanPham?

    @Delete
    suspend fun delete(sanPham: SanPham)
}