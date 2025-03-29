package com.example.thdt_quangtran.fragments.admin

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.thdt_quangtran.R
import com.example.thdt_quangtran.activities.AdminActivity
import com.example.thdt_quangtran.database.AppDatabase
import com.example.thdt_quangtran.databinding.FragmentCustomerEditBinding
import com.example.thdt_quangtran.models.KhachHang
import com.example.thdt_quangtran.viewmodels.KhachHangViewModel
import com.example.thdt_quangtran.viewmodels.KhachHangViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CustomerEditFragment : Fragment() {

    private var _binding: FragmentCustomerEditBinding? = null
    private val binding get() = _binding!!
    private lateinit var khachHangViewModel: KhachHangViewModel
    private var khachHang: KhachHang? = null
    private var maKhachHang: Int = -1
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        maKhachHang = arguments?.getInt("maKhachHang", -1) ?: -1
        if (maKhachHang == -1) {
            throw IllegalArgumentException("maKhachHang is required")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomerEditBinding.inflate(inflater, container, false)

        // Khởi tạo ViewModel
        val khachHangDao = AppDatabase.getDatabase(requireContext()).khachHangDao()
        val taiKhoanDao = AppDatabase.getDatabase(requireContext()).taiKhoanDao()
        val factory = KhachHangViewModelFactory(khachHangDao, taiKhoanDao)
        khachHangViewModel = ViewModelProvider(this, factory).get(KhachHangViewModel::class.java)

        // Load thông tin khách hàng và tài khoản
        loadKhachHang()

        // Xử lý sự kiện nhấn nút "Chọn ảnh"
        binding.selectAvatarButton.setOnClickListener {
            checkAndRequestPermission()
        }

        // Xử lý sự kiện nhấn nút "Cập nhật khách hàng"
        binding.updateCustomerButton.setOnClickListener {
            if (isAdded) {
                updateCustomer()
            }
        }

        // Xử lý sự kiện nhấn nút "Xóa tài khoản"
        binding.deleteAccountButton.setOnClickListener {
            if (isAdded) {
                showDeleteConfirmationDialog()
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // Ẩn BottomNavigationView
        (activity as? AdminActivity)?.hideBottomNavigation()
        // Cập nhật tiêu đề Toolbar
        (activity as? AdminActivity)?.supportActionBar?.title = "Chỉnh sửa khách hàng"
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

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Xác nhận xóa tài khoản")
            .setMessage("Bạn có chắc chắn muốn xóa tài khoản của khách hàng này không? Hành động này không thể hoàn tác.")
            .setPositiveButton("Xóa") { _, _ ->
                deleteAccount()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun deleteAccount() {
        lifecycleScope.launch {
            val taiKhoan = khachHangViewModel.getTaiKhoanByMaKhachHang(maKhachHang)
            if (taiKhoan == null) {
                Toast.makeText(context, "Khách hàng không có tài khoản", Toast.LENGTH_SHORT).show()
                return@launch
            }

            try {
                khachHangViewModel.deleteTaiKhoanByMaKhachHang(maKhachHang)
                Toast.makeText(context, "Xóa tài khoản thành công", Toast.LENGTH_SHORT).show()

                // Quay lại CustomerListFragment
                parentFragmentManager.popBackStack()
            } catch (e: Exception) {
                Toast.makeText(context, "Lỗi khi xóa tài khoản: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadKhachHang() {
        lifecycleScope.launch {
            // Load thông tin khách hàng
            khachHang = khachHangViewModel.getById(maKhachHang)
            if (khachHang == null) {
                Toast.makeText(context, "Không tìm thấy khách hàng", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
                return@launch
            }

            // Hiển thị thông tin khách hàng
            binding.tenKhachHangInput.setText(khachHang?.tenKhachHang)
            binding.gioiTinhInput.setText(khachHang?.gioiTinh)
            binding.ngaySinhInput.setText(
                khachHang?.ngaySinh?.let {
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)
                }
            )
            binding.soDienThoaiInput.setText(khachHang?.soDienThoai)
            binding.emailInput.setText(khachHang?.email)
            binding.diaChiInput.setText(khachHang?.diaChi)

            // Load thông tin tài khoản
            val taiKhoan = khachHangViewModel.getTaiKhoanByMaKhachHang(maKhachHang)
            if (taiKhoan != null) {
                binding.tenDangNhapInput.setText(taiKhoan.tenDangNhap)
                binding.matKhauInput.setText(taiKhoan.matKhau)
                // Load avatar
                if (taiKhoan.avatar != null) {
                    val bitmap = BitmapFactory.decodeByteArray(taiKhoan.avatar, 0, taiKhoan.avatar.size)
                    binding.avatarImageView.setImageBitmap(bitmap)
                    selectedAvatar = taiKhoan.avatar
                } else {
                    binding.avatarImageView.setImageResource(R.drawable.ic_default_avatar)
                }
            } else {
                binding.avatarImageView.setImageResource(R.drawable.ic_default_avatar)
            }
        }
    }

    private fun updateCustomer() {
        if (!isAdded || _binding == null || khachHang == null) {
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

        // Tạo đối tượng KhachHang mới với thông tin đã cập nhật
        val updatedKhachHang = KhachHang(
            maKhachHang = khachHang!!.maKhachHang,
            tenKhachHang = tenKhachHang,
            gioiTinh = if (gioiTinh.isEmpty()) null else gioiTinh,
            ngaySinh = ngaySinh,
            soDienThoai = if (soDienThoai.isEmpty()) null else soDienThoai,
            email = if (email.isEmpty()) null else email,
            diaChi = if (diaChi.isEmpty()) null else diaChi
        )

        lifecycleScope.launch {
            try {
                khachHangViewModel.updateKhachHang(updatedKhachHang, tenDangNhap, matKhau, selectedAvatar)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Cập nhật khách hàng thành công", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("CustomerEditFragment", "Error updating customer: ${e.message}", e)
                    Toast.makeText(context, "Lỗi khi cập nhật khách hàng: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(maKhachHang: Int): CustomerEditFragment {
            val fragment = CustomerEditFragment()
            val args = Bundle()
            args.putInt("maKhachHang", maKhachHang)
            fragment.arguments = args
            return fragment
        }
    }
}