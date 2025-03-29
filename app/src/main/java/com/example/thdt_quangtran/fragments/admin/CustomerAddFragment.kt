package com.example.thdt_quangtran.fragments.admin

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.thdt_quangtran.R
import com.example.thdt_quangtran.activities.AdminActivity
import com.example.thdt_quangtran.database.AppDatabase
import com.example.thdt_quangtran.databinding.FragmentCustomerAddBinding
import com.example.thdt_quangtran.models.KhachHang
import com.example.thdt_quangtran.viewmodels.KhachHangViewModel
import com.example.thdt_quangtran.viewmodels.KhachHangViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CustomerAddFragment : Fragment() {

    private var _binding: FragmentCustomerAddBinding? = null
    private val binding get() = _binding!!
    private lateinit var khachHangViewModel: KhachHangViewModel
    private var selectedAvatar: ByteArray? = null

    // Activity result launcher để chọn ảnh
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
                    binding.avatarImageView.setImageBitmap(bitmap)

                    // Chuyển bitmap thành ByteArray để lưu vào database
                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    selectedAvatar = stream.toByteArray()
                } catch (e: Exception) {
                    Toast.makeText(context, "Lỗi khi chọn ảnh: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Activity result launcher để yêu cầu quyền
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            // Quyền được cấp, mở file picker
            openFilePicker()
        } else {
            Toast.makeText(context, "Quyền truy cập bộ nhớ bị từ chối", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomerAddBinding.inflate(inflater, container, false)

        // Khởi tạo ViewModel
        val khachHangDao = AppDatabase.getDatabase(requireContext()).khachHangDao()
        val taiKhoanDao = AppDatabase.getDatabase(requireContext()).taiKhoanDao()
        val factory = KhachHangViewModelFactory(khachHangDao, taiKhoanDao)
        khachHangViewModel = ViewModelProvider(this, factory).get(KhachHangViewModel::class.java)

        // Xử lý sự kiện nhấn nút "Chọn ảnh"
        binding.selectAvatarButton.setOnClickListener {
            checkAndRequestPermission()
        }

        // Xử lý sự kiện nhấn nút "Thêm khách hàng"
        binding.addCustomerButton.setOnClickListener {
            if (isAdded) {
                addCustomer()
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // Ẩn BottomNavigationView
        (activity as? AdminActivity)?.hideBottomNavigation()
        // Cập nhật tiêu đề Toolbar
        (activity as? AdminActivity)?.supportActionBar?.title = "Thêm khách hàng"
    }

    private fun checkAndRequestPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED -> {
                // Quyền đã được cấp, mở file picker
                openFilePicker()
            }
            shouldShowRequestPermissionRationale(permission) -> {
                // Hiển thị lý do cần quyền
                Toast.makeText(context, "Cần quyền truy cập bộ nhớ để chọn ảnh", Toast.LENGTH_SHORT).show()
                requestPermissionLauncher.launch(permission)
            }
            else -> {
                // Yêu cầu quyền
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        pickImageLauncher.launch(intent)
    }

    private fun addCustomer() {
        if (!isAdded || _binding == null) {
            return
        }

        val tenKhachHang = binding.tenKhachHangInput.text.toString().trim()
        val gioiTinh = binding.gioiTinhInput.text.toString().trim()
        val ngaySinhStr = binding.ngaySinhInput.text.toString().trim()
        val soDienThoai = binding.soDienThoaiInput.text.toString().trim()
        val email = binding.emailInput.text.toString().trim()
        val diaChi = binding.diaChiInput.text.toString().trim()
        val tenDangNhap = binding.tenDangNhapInput.text.toString().trim()
        val matKhau = binding.matKhauInput.text.toString().trim()

        // Kiểm tra dữ liệu đầu vào
        if (tenKhachHang.isEmpty()) {
            Toast.makeText(context, "Vui lòng nhập tên khách hàng", Toast.LENGTH_SHORT).show()
            return
        }
        if (tenDangNhap.isEmpty()) {
            Toast.makeText(context, "Vui lòng nhập tên đăng nhập", Toast.LENGTH_SHORT).show()
            return
        }
        if (matKhau.isEmpty()) {
            Toast.makeText(context, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show()
            return
        }

        // Chuyển đổi ngày sinh
        val ngaySinh: Date? = try {
            if (ngaySinhStr.isNotEmpty()) {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                sdf.parse(ngaySinhStr)
            } else {
                null
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Ngày sinh không hợp lệ (dd/MM/yyyy)", Toast.LENGTH_SHORT).show()
            return
        }

        // Tạo đối tượng KhachHang
        val khachHang = KhachHang(
            tenKhachHang = tenKhachHang,
            gioiTinh = if (gioiTinh.isEmpty()) null else gioiTinh,
            ngaySinh = ngaySinh,
            soDienThoai = if (soDienThoai.isEmpty()) null else soDienThoai,
            email = if (email.isEmpty()) null else email,
            diaChi = if (diaChi.isEmpty()) null else diaChi
        )

        // Sử dụng coroutine để gọi insertKhachHang
        lifecycleScope.launch {
            try {
                khachHangViewModel.insertKhachHang(khachHang, tenDangNhap, matKhau, selectedAvatar)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Thêm khách hàng thành công", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                    (activity as? AdminActivity)?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.selectedItemId = R.id.bottom_list
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("CustomerAddFragment", "Error inserting customer: ${e.message}", e)
                    Toast.makeText(context, "Lỗi khi thêm khách hàng: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}