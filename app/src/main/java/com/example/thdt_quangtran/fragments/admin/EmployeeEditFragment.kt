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
import com.example.thdt_quangtran.databinding.FragmentEmployeeEditBinding
import com.example.thdt_quangtran.models.NhanVien
import com.example.thdt_quangtran.viewmodels.NhanVienViewModel
import com.example.thdt_quangtran.viewmodels.NhanVienViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EmployeeEditFragment : Fragment() {

    private var _binding: FragmentEmployeeEditBinding? = null
    private val binding get() = _binding!!
    private lateinit var nhanVienViewModel: NhanVienViewModel
    private var nhanVien: NhanVien? = null
    private var maNhanVien: Int = -1
    private var selectedAvatar: ByteArray? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
                    binding.avatarImageView.setImageBitmap(bitmap)
                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    selectedAvatar = stream.toByteArray()
                } catch (e: Exception) {
                    Toast.makeText(context, "Lỗi khi chọn ảnh: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            openFilePicker()
        } else {
            Toast.makeText(context, "Quyền truy cập bộ nhớ bị từ chối", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        maNhanVien = arguments?.getInt("maNhanVien", -1) ?: -1
        if (maNhanVien == -1) {
            throw IllegalArgumentException("maNhanVien is required")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmployeeEditBinding.inflate(inflater, container, false)

        val nhanVienDao = AppDatabase.getDatabase(requireContext()).nhanVienDao()
        val taiKhoanDao = AppDatabase.getDatabase(requireContext()).taiKhoanDao()
        val factory = NhanVienViewModelFactory(nhanVienDao, taiKhoanDao)
        nhanVienViewModel = ViewModelProvider(this, factory).get(NhanVienViewModel::class.java)

        loadNhanVien()

        binding.selectAvatarButton.setOnClickListener {
            checkAndRequestPermission()
        }

        binding.updateEmployeeButton.setOnClickListener {
            if (isAdded) {
                updateEmployee()
            }
        }

        binding.deleteAccountButton.setOnClickListener {
            if (isAdded) {
                showDeleteConfirmationDialog()
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as? AdminActivity)?.hideBottomNavigation()
        (activity as? AdminActivity)?.supportActionBar?.title = "Chỉnh sửa nhân viên"
    }

    private fun checkAndRequestPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED -> {
                openFilePicker()
            }
            shouldShowRequestPermissionRationale(permission) -> {
                Toast.makeText(context, "Cần quyền truy cập bộ nhớ để chọn ảnh", Toast.LENGTH_SHORT).show()
                requestPermissionLauncher.launch(permission)
            }
            else -> {
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
            .setMessage("Bạn có chắc chắn muốn xóa tài khoản của nhân viên này không? Hành động này không thể hoàn tác.")
            .setPositiveButton("Xóa") { _, _ ->
                deleteAccount()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun deleteAccount() {
        lifecycleScope.launch {
            val taiKhoan = nhanVienViewModel.getTaiKhoanByMaNhanVien(maNhanVien)
            if (taiKhoan == null) {
                Toast.makeText(context, "Nhân viên không có tài khoản", Toast.LENGTH_SHORT).show()
                return@launch
            }

            try {
                nhanVienViewModel.deleteTaiKhoanByMaNhanVien(maNhanVien)
                Toast.makeText(context, "Xóa tài khoản thành công", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            } catch (e: Exception) {
                Toast.makeText(context, "Lỗi khi xóa tài khoản: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadNhanVien() {
        lifecycleScope.launch {
            nhanVien = nhanVienViewModel.getById(maNhanVien)
            if (nhanVien == null) {
                Toast.makeText(context, "Không tìm thấy nhân viên", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
                return@launch
            }

            binding.tenNhanVienInput.setText(nhanVien?.tenNhanVien)
            binding.cccdInput.setText(nhanVien?.cccd)
            binding.ngaySinhInput.setText(
                nhanVien?.ngaySinh?.let {
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)
                }
            )
            binding.gioiTinhInput.setText(nhanVien?.gioiTinh)
            binding.soDienThoaiInput.setText(nhanVien?.soDienThoai)
            binding.emailInput.setText(nhanVien?.email)
            binding.diaChiInput.setText(nhanVien?.diaChi)
            binding.chucVuInput.setText(nhanVien?.chucVu)
            binding.ngayVaoLamInput.setText(
                nhanVien?.ngayVaoLam?.let {
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)
                }
            )

            val taiKhoan = nhanVienViewModel.getTaiKhoanByMaNhanVien(maNhanVien)
            if (taiKhoan != null) {
                binding.tenDangNhapInput.setText(taiKhoan.tenDangNhap)
                binding.matKhauInput.setText(taiKhoan.matKhau)
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

    private fun updateEmployee() {
        if (!isAdded || _binding == null || nhanVien == null) {
            return
        }

        val tenNhanVien = binding.tenNhanVienInput.text.toString().trim()
        val cccd = binding.cccdInput.text.toString().trim()
        val ngaySinhStr = binding.ngaySinhInput.text.toString().trim()
        val gioiTinh = binding.gioiTinhInput.text.toString().trim()
        val soDienThoai = binding.soDienThoaiInput.text.toString().trim()
        val email = binding.emailInput.text.toString().trim()
        val diaChi = binding.diaChiInput.text.toString().trim()
        val chucVu = binding.chucVuInput.text.toString().trim()
        val ngayVaoLamStr = binding.ngayVaoLamInput.text.toString().trim()
        val tenDangNhap = binding.tenDangNhapInput.text.toString().trim()
        val matKhau = binding.matKhauInput.text.toString().trim()

        if (tenNhanVien.isEmpty()) {
            Toast.makeText(context, "Vui lòng nhập tên nhân viên", Toast.LENGTH_SHORT).show()
            return
        }
        if (cccd.isEmpty()) {
            Toast.makeText(context, "Vui lòng nhập CCCD", Toast.LENGTH_SHORT).show()
            return
        }
        if (ngaySinhStr.isEmpty()) {
            Toast.makeText(context, "Vui lòng nhập ngày sinh", Toast.LENGTH_SHORT).show()
            return
        }
        if (gioiTinh.isEmpty()) {
            Toast.makeText(context, "Vui lòng nhập giới tính", Toast.LENGTH_SHORT).show()
            return
        }
        if (soDienThoai.isEmpty()) {
            Toast.makeText(context, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show()
            return
        }
        if (chucVu.isEmpty()) {
            Toast.makeText(context, "Vui lòng nhập chức vụ", Toast.LENGTH_SHORT).show()
            return
        }
        if (ngayVaoLamStr.isEmpty()) {
            Toast.makeText(context, "Vui lòng nhập ngày vào làm", Toast.LENGTH_SHORT).show()
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

        val ngaySinh: Date = try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.parse(ngaySinhStr) ?: throw IllegalArgumentException("Ngày sinh không hợp lệ")
        } catch (e: Exception) {
            Toast.makeText(context, "Ngày sinh không hợp lệ (dd/MM/yyyy)", Toast.LENGTH_SHORT).show()
            return
        }

        val ngayVaoLam: Date = try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.parse(ngayVaoLamStr) ?: throw IllegalArgumentException("Ngày vào làm không hợp lệ")
        } catch (e: Exception) {
            Toast.makeText(context, "Ngày vào làm không hợp lệ (dd/MM/yyyy)", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedNhanVien = NhanVien(
            maNhanVien = nhanVien!!.maNhanVien,
            tenNhanVien = tenNhanVien,
            cccd = cccd,
            ngaySinh = ngaySinh,
            gioiTinh = gioiTinh,
            soDienThoai = soDienThoai,
            email = if (email.isEmpty()) null else email,
            diaChi = if (diaChi.isEmpty()) null else diaChi,
            chucVu = chucVu,
            ngayVaoLam = ngayVaoLam
        )

        lifecycleScope.launch {
            try {
                nhanVienViewModel.updateNhanVien(updatedNhanVien, tenDangNhap, matKhau, selectedAvatar)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Cập nhật nhân viên thành công", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("EmployeeEditFragment", "Error updating employee: ${e.message}", e)
                    Toast.makeText(context, "Lỗi khi cập nhật nhân viên: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(maNhanVien: Int): EmployeeEditFragment {
            val fragment = EmployeeEditFragment()
            val args = Bundle()
            args.putInt("maNhanVien", maNhanVien)
            fragment.arguments = args
            return fragment
        }
    }
}