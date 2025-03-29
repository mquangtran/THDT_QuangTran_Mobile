package com.example.thdt_quangtran.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.thdt_quangtran.models.KhachHang
import kotlinx.coroutines.flow.Flow

@Dao
interface KhachHangDao {

    @Query("SELECT * FROM KhachHang")
    fun getAll(): Flow<List<KhachHang>>

    // Trả về Flow<List<KhachHang>> cho các khách hàng có tài khoản
    @Query("""
        SELECT KhachHang.* 
        FROM KhachHang 
        INNER JOIN TaiKhoan ON KhachHang.maKhachHang = TaiKhoan.maKhachHang
    """)
    fun getAllWithTaiKhoan(): Flow<List<KhachHang>>

    @Query("SELECT * FROM KhachHang WHERE maKhachHang = :maKhachHang LIMIT 1")
    suspend fun getById(maKhachHang: Int): KhachHang?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(khachHang: KhachHang): Long

    @Update
    suspend fun update(khachHang: KhachHang)

    @Query("DELETE FROM KhachHang WHERE maKhachHang = :maKhachHang")
    suspend fun delete(maKhachHang: Int)
}