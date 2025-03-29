package com.example.thdt_quangtran.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Transaction
import com.example.thdt_quangtran.database.dao.KhachHangDao
import com.example.thdt_quangtran.database.dao.TaiKhoanDao
import com.example.thdt_quangtran.models.KhachHang
import com.example.thdt_quangtran.models.TaiKhoan
import kotlinx.coroutines.flow.Flow

class KhachHangViewModel(
    private val khachHangDao: KhachHangDao,
    private val taiKhoanDao: TaiKhoanDao
) : ViewModel() {

    val allKhachHang: Flow<List<KhachHang>> = khachHangDao.getAllWithTaiKhoan()

    fun getAll(): Flow<List<KhachHang>> {
        return khachHangDao.getAllWithTaiKhoan()
    }

    suspend fun getAllTaiKhoan(): List<TaiKhoan> {
        return taiKhoanDao.getAllTaiKhoan()
    }

    suspend fun getById(maKhachHang: Int): KhachHang? {
        return khachHangDao.getById(maKhachHang)
    }

    @Transaction
    suspend fun insertKhachHang(khachHang: KhachHang, tenDangNhap: String, matKhau: String, avatar: ByteArray?) {
        try {
            Log.d("KhachHangViewModel", "Inserting KhachHang: $khachHang")
            val maKhachHang = khachHangDao.insert(khachHang).toInt()
            Log.d("KhachHangViewModel", "Inserted KhachHang with maKhachHang: $maKhachHang")

            if (maKhachHang <= 0) {
                throw IllegalStateException("Failed to insert KhachHang: maKhachHang is $maKhachHang")
            }

            val taiKhoan = TaiKhoan(
                maKhachHang = maKhachHang,
                maNhanVien = null,
                tenDangNhap = tenDangNhap,
                matKhau = matKhau,
                vaiTro = "user",
                avatar = avatar
            )
            Log.d("KhachHangViewModel", "Inserting TaiKhoan with avatar size: ${avatar?.size ?: 0} bytes")
            taiKhoanDao.insert(taiKhoan)
            Log.d("KhachHangViewModel", "Inserted TaiKhoan successfully")
        } catch (e: Exception) {
            Log.e("KhachHangViewModel", "Error inserting KhachHang and TaiKhoan: ${e.message}", e)
            throw RuntimeException("Error inserting KhachHang and TaiKhoan: ${e.message}", e)
        }
    }

    @Transaction
    suspend fun updateKhachHang(khachHang: KhachHang, tenDangNhap: String, matKhau: String, avatar: ByteArray?) {
        try {
            khachHangDao.update(khachHang)

            val existingTaiKhoan = taiKhoanDao.getByMaKhachHang(khachHang.maKhachHang)
            val taiKhoan = existingTaiKhoan?.copy(
                tenDangNhap = tenDangNhap,
                matKhau = matKhau,
                avatar = avatar ?: existingTaiKhoan.avatar
            ) ?: TaiKhoan(
                maKhachHang = khachHang.maKhachHang,
                maNhanVien = null,
                tenDangNhap = tenDangNhap,
                matKhau = matKhau,
                vaiTro = "customer",
                avatar = avatar
            )
            Log.d("KhachHangViewModel", "Updating TaiKhoan with avatar size: ${taiKhoan.avatar?.size ?: 0} bytes")
            taiKhoanDao.insert(taiKhoan)
            Log.d("KhachHangViewModel", "Updated TaiKhoan successfully")
        } catch (e: Exception) {
            Log.e("KhachHangViewModel", "Error updating KhachHang and TaiKhoan: ${e.message}", e)
            throw RuntimeException("Error updating KhachHang and TaiKhoan: ${e.message}", e)
        }
    }

    suspend fun getTaiKhoanByMaKhachHang(maKhachHang: Int): TaiKhoan? {
        return taiKhoanDao.getByMaKhachHang(maKhachHang)
    }

    suspend fun deleteTaiKhoanByMaKhachHang(maKhachHang: Int) {
        taiKhoanDao.deleteByMaKhachHang(maKhachHang)
    }
}

class KhachHangViewModelFactory(
    private val khachHangDao: KhachHangDao,
    private val taiKhoanDao: TaiKhoanDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KhachHangViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return KhachHangViewModel(khachHangDao, taiKhoanDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}