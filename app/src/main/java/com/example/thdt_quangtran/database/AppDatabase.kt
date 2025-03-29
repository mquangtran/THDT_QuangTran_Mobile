package com.example.thdt_quangtran.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.thdt_quangtran.database.dao.*
import com.example.thdt_quangtran.models.*
import com.example.thdt_quangtran.utils.Converters

@Database(
    entities = [
        NhanVien::class,
        KhachHang::class,
        TaiKhoan::class,
        SanPham::class,
        GioHang::class,
        ChiTietGioHang::class,
        KhuyenMai::class,
        DonHang::class,
        ChiTietDonHang::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun nhanVienDao(): NhanVienDao
    abstract fun khachHangDao(): KhachHangDao
    abstract fun taiKhoanDao(): TaiKhoanDao
    abstract fun sanPhamDao(): SanPhamDao
    abstract fun gioHangDao(): GioHangDao
    abstract fun chiTietGioHangDao(): ChiTietGioHangDao
    abstract fun khuyenMaiDao(): KhuyenMaiDao
    abstract fun donHangDao(): DonHangDao
    abstract fun chiTietDonHangDao(): ChiTietDonHangDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "thdt_quangtran"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}