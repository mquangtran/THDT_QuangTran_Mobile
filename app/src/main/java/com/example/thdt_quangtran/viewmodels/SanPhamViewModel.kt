package com.example.thdt_quangtran.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.thdt_quangtran.database.dao.SanPhamDao
import com.example.thdt_quangtran.models.SanPham
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SanPhamViewModel(private val sanPhamDao: SanPhamDao) : ViewModel() {

    // Chuyển Flow thành LiveData
    val allSanPham: LiveData<List<SanPham>> = sanPhamDao.getAllSanPham().asLiveData()

    fun insertSanPham(sanPham: SanPham) {
        viewModelScope.launch {
            sanPhamDao.insert(sanPham)
        }
    }

    fun updateSanPham(sanPham: SanPham) {
        viewModelScope.launch {
            sanPhamDao.update(sanPham)
        }
    }

    fun deleteSanPham(sanPham: SanPham) {
        viewModelScope.launch {
            sanPhamDao.delete(sanPham)
        }
    }

    suspend fun getById(maSanPham: Int): SanPham? {
        return sanPhamDao.getById(maSanPham)
    }
}

class SanPhamViewModelFactory(private val sanPhamDao: SanPhamDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SanPhamViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SanPhamViewModel(sanPhamDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}