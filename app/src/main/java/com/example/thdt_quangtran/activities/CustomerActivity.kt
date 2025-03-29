package com.example.thdt_quangtran.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.thdt_quangtran.R
import com.example.thdt_quangtran.fragments.customer.CustomerAccountFragment
import com.example.thdt_quangtran.fragments.customer.CustomerCartFragment
import com.example.thdt_quangtran.fragments.customer.CustomerOrderFragment
import com.example.thdt_quangtran.fragments.customer.CustomerProductFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class CustomerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer)

        // Lấy maKhachHang từ Intent
        val maKhachHang = intent.getIntExtra("maKhachHang", -1)
        if (maKhachHang == -1) {
            // Xử lý trường hợp không nhận được maKhachHang
            finish()
            return
        }

        // Thiết lập BottomNavigationView
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, CustomerProductFragment())
                        .commit()
                    true
                }
                R.id.nav_cart -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, CustomerCartFragment())
                        .commit()
                    true
                }
                R.id.nav_orders -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, CustomerOrderFragment())
                        .commit()
                    true
                }
                R.id.nav_account -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, CustomerAccountFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }

        // Mặc định hiển thị CustomerProductFragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CustomerProductFragment())
                .commit()
            bottomNavigation.selectedItemId = R.id.nav_home
        }
    }
}