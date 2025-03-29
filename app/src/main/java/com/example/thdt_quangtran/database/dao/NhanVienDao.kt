package com.example.thdt_quangtran.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.thdt_quangtran.models.NhanVien

@Dao
interface NhanVienDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(nhanVien: NhanVien): Long

    @Update
    suspend fun update(nhanVien: NhanVien)

    @Query("SELECT * FROM NhanVien")
    suspend fun getAll(): List<NhanVien>

    @Query("SELECT * FROM NhanVien WHERE maNhanVien = :maNhanVien")
    suspend fun getById(maNhanVien: Int): NhanVien?
}