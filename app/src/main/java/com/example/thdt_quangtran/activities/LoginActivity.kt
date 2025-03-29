package com.example.thdt_quangtran.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.thdt_quangtran.database.AppDatabase
import com.example.thdt_quangtran.database.dao.TaiKhoanDao
import com.example.thdt_quangtran.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var db: AppDatabase
    private lateinit var taiKhoanDao: TaiKhoanDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Khởi tạo Room Database
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "thdt_quangtran"
        ).build()
        taiKhoanDao = db.taiKhoanDao()

        // Xử lý nút Đăng nhập
        binding.loginButton.setOnClickListener {
            val username = binding.usernameInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val taiKhoan = taiKhoanDao.getTaiKhoanByTenDangNhap(username)
                if (taiKhoan != null && taiKhoan.matKhau == password) {
                    when (taiKhoan.vaiTro) {
                        "customer" -> {
                            val intent = Intent(this@LoginActivity, CustomerActivity::class.java)
                            intent.putExtra("maKhachHang", taiKhoan.maKhachHang)
                            startActivity(intent)
                            finish()
                        }
                        "employee" -> {
                            val intent = Intent(this@LoginActivity, EmployeeActivity::class.java)
                            intent.putExtra("maNhanVien", taiKhoan.maNhanVien)
                            startActivity(intent)
                            finish()
                        }
                        "admin" -> {
                            val intent = Intent(this@LoginActivity, AdminActivity::class.java)
                            intent.putExtra("maNhanVien", taiKhoan.maNhanVien)
                            startActivity(intent)
                            finish()
                        }
                        else -> {
                            Toast.makeText(this@LoginActivity, "Vai trò không hợp lệ", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Tên đăng nhập hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Xử lý nút Đăng ký
        binding.registerButton.setOnClickListener {
            Toast.makeText(this, "Chức năng đăng ký chưa được triển khai", Toast.LENGTH_SHORT).show()
            // Intent đến RegisterActivity nếu bạn triển khai sau
        }

        // Xử lý Quên mật khẩu
        binding.forgotPasswordText.setOnClickListener {
            Toast.makeText(this, "Chức năng quên mật khẩu chưa được triển khai", Toast.LENGTH_SHORT).show()
        }
    }
}