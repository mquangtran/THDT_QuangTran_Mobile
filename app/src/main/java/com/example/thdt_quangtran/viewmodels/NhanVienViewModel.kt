package com.example.thdt_quangtran.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Transaction
import com.example.thdt_quangtran.database.dao.NhanVienDao
import com.example.thdt_quangtran.database.dao.TaiKhoanDao
import com.example.thdt_quangtran.models.NhanVien
import com.example.thdt_quangtran.models.TaiKhoan
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NhanVienViewModel(
    private val nhanVienDao: NhanVienDao,
    private val taiKhoanDao: TaiKhoanDao
) : ViewModel() {

    val allNhanVien: Flow<List<NhanVien>> = flow {
        val nhanVienList = nhanVienDao.getAll()
        val filteredList = nhanVienList.filter { nhanVien ->
            taiKhoanDao.getByMaNhanVien(nhanVien.maNhanVien) != null
        }
        emit(filteredList)
    }

    fun getAll(): Flow<List<NhanVien>> {
        return allNhanVien
    }

    suspend fun getAllTaiKhoan(): List<TaiKhoan> {
        return taiKhoanDao.getAllTaiKhoan()
    }

    suspend fun getById(maNhanVien: Int): NhanVien? {
        return nhanVienDao.getById(maNhanVien)
    }

    @Transaction
    suspend fun insertNhanVien(nhanVien: NhanVien, tenDangNhap: String, matKhau: String, avatar: ByteArray?) {
        try {
            Log.d("NhanVienViewModel", "Inserting NhanVien: $nhanVien")
            val maNhanVien = nhanVienDao.insert(nhanVien).toInt()
            Log.d("NhanVienViewModel", "Inserted NhanVien with maNhanVien: $maNhanVien")

            if (maNhanVien <= 0) {
                throw IllegalStateException("Failed to insert NhanVien: maNhanVien is $maNhanVien")
            }

            val taiKhoan = TaiKhoan(
                tenDangNhap = tenDangNhap,
                matKhau = matKhau,
                vaiTro = "employee",
                maNhanVien = maNhanVien,
                maKhachHang = null,
                avatar = avatar
            )
            Log.d("NhanVienViewModel", "Inserting TaiKhoan with avatar size: ${avatar?.size ?: 0} bytes")
            taiKhoanDao.insert(taiKhoan)
            Log.d("NhanVienViewModel", "Inserted TaiKhoan successfully")
        } catch (e: Exception) {
            Log.e("NhanVienViewModel", "Error inserting NhanVien and TaiKhoan: ${e.message}", e)
            throw RuntimeException("Error inserting NhanVien and TaiKhoan: ${e.message}", e)
        }
    }

    @Transaction
    suspend fun updateNhanVien(nhanVien: NhanVien, tenDangNhap: String, matKhau: String, avatar: ByteArray?) {
        try {
            nhanVienDao.update(nhanVien)

            val existingTaiKhoan = taiKhoanDao.getByMaNhanVien(nhanVien.maNhanVien)
            val taiKhoan = existingTaiKhoan?.copy(
                tenDangNhap = tenDangNhap,
                matKhau = matKhau,
                avatar = avatar ?: existingTaiKhoan.avatar
            ) ?: TaiKhoan(
                tenDangNhap = tenDangNhap,
                matKhau = matKhau,
                vaiTro = "employee",
                maNhanVien = nhanVien.maNhanVien,
                maKhachHang = null,
                avatar = avatar
            )
            Log.d("NhanVienViewModel", "Updating TaiKhoan with avatar size: ${taiKhoan.avatar?.size ?: 0} bytes")
            taiKhoanDao.insert(taiKhoan)
            Log.d("NhanVienViewModel", "Updated TaiKhoan successfully")
        } catch (e: Exception) {
            Log.e("NhanVienViewModel", "Error updating NhanVien and TaiKhoan: ${e.message}", e)
            throw RuntimeException("Error updating NhanVien and TaiKhoan: ${e.message}", e)
        }
    }

    suspend fun getTaiKhoanByMaNhanVien(maNhanVien: Int): TaiKhoan? {
        return taiKhoanDao.getByMaNhanVien(maNhanVien)
    }

    suspend fun deleteTaiKhoanByMaNhanVien(maNhanVien: Int) {
        taiKhoanDao.deleteByMaNhanVien(maNhanVien)
    }
}

class NhanVienViewModelFactory(
    private val nhanVienDao: NhanVienDao,
    private val taiKhoanDao: TaiKhoanDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NhanVienViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NhanVienViewModel(nhanVienDao, taiKhoanDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}