package com.example.thdt_quangtran.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.thdt_quangtran.models.TaiKhoan

@Dao
interface TaiKhoanDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(taiKhoan: TaiKhoan)

    @Query("SELECT * FROM TaiKhoan WHERE tenDangNhap = :tenDangNhap LIMIT 1")
    suspend fun getTaiKhoanByTenDangNhap(tenDangNhap: String): TaiKhoan?

    @Query("SELECT * FROM TaiKhoan WHERE maKhachHang = :maKhachHang LIMIT 1")
    suspend fun getByMaKhachHang(maKhachHang: Int): TaiKhoan?

    @Query("SELECT * FROM TaiKhoan WHERE maNhanVien = :maNhanVien LIMIT 1")
    suspend fun getByMaNhanVien(maNhanVien: Int): TaiKhoan?

    @Query("DELETE FROM TaiKhoan WHERE maKhachHang = :maKhachHang")
    suspend fun deleteByMaKhachHang(maKhachHang: Int)

    @Query("DELETE FROM TaiKhoan WHERE maNhanVien = :maNhanVien")
    suspend fun deleteByMaNhanVien(maNhanVien: Int)

    @Query("SELECT * FROM TaiKhoan")
    suspend fun getAllTaiKhoan(): List<TaiKhoan>

    @Query("DELETE FROM TaiKhoan WHERE tenDangNhap = :tenDangNhap")
    suspend fun deleteByTenDangNhap(tenDangNhap: String)

    @androidx.room.Delete
    suspend fun delete(taiKhoan: TaiKhoan)
}