package com.example.thdt_quangtran.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.thdt_quangtran.R

class EmployeeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_employee)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}

//package com.example.thdt_quangtran.activities
//
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//
//class EmployeeActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(androidx.appcompat.R.layout.activity_list_item) // Placeholder layout
//
//        val maNhanVien = intent.getIntExtra("maNhanVien", -1)
//        // Thêm logic cho giao diện nhân viên
//    }
//}