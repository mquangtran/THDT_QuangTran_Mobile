package com.example.thdt_quangtran.fragments.admin

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.thdt_quangtran.database.AppDatabase
import com.example.thdt_quangtran.databinding.FragmentProductAddBinding
import com.example.thdt_quangtran.models.SanPham
import com.example.thdt_quangtran.viewmodels.SanPhamViewModel
import com.example.thdt_quangtran.viewmodels.SanPhamViewModelFactory
import java.io.ByteArrayOutputStream

class ProductAddFragment : Fragment() {

    private var _binding: FragmentProductAddBinding? = null
    private val binding get() = _binding!!
    private lateinit var sanPhamViewModel: SanPhamViewModel
    private var selectedImageUri: Uri? = null

    // Activity result launcher để chọn file ảnh
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                binding.imagePreview.setImageURI(uri)
                binding.imagePreview.visibility = View.VISIBLE
                binding.addImageIcon.visibility = View.GONE
                binding.imageContainer.findViewById<View>(android.R.id.text1)?.visibility = View.GONE
            }
        }
    }

    // Activity result launcher để yêu cầu quyền truy cập bộ nhớ
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            openFilePicker()
        } else {
            Toast.makeText(context, "Cần quyền truy cập bộ nhớ để chọn file ảnh", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductAddBinding.inflate(inflater, container, false)

        // Khởi tạo ViewModel
        val sanPhamDao = AppDatabase.getDatabase(requireContext()).sanPhamDao()
        val factory = SanPhamViewModelFactory(sanPhamDao)
        sanPhamViewModel = ViewModelProvider(this, factory).get(SanPhamViewModel::class.java)

        // Xử lý sự kiện chọn file ảnh
        binding.imageContainer.setOnClickListener {
            checkStoragePermissionAndPickFile()
        }

        // Xử lý sự kiện nút "Thêm sản phẩm"
        binding.addProductButton.setOnClickListener {
            addProduct()
        }

        return binding.root
    }

    private fun checkStoragePermissionAndPickFile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 trở lên: Yêu cầu quyền READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openFilePicker()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            // Android 12 trở xuống: Yêu cầu quyền READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openFilePicker()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        pickImageLauncher.launch(intent)
    }

    private fun addProduct() {
        val tenSanPham = binding.tenSanPhamInput.text.toString().trim()
        val model = binding.modelInput.text.toString().trim()
        val donViTinh = binding.donViTinhInput.text.toString().trim()
        val phanLoai = binding.phanLoaiInput.text.toString().trim()
        val soLuongTonKho = binding.soLuongTonKhoInput.text.toString().trim()
        val giaBan = binding.giaBanInput.text.toString().trim()
        val giaNhap = binding.giaNhapInput.text.toString().trim()
        val moTa = binding.moTaInput.text.toString().trim()

        // Kiểm tra dữ liệu đầu vào
        if (tenSanPham.isEmpty() || model.isEmpty() || donViTinh.isEmpty() || phanLoai.isEmpty() ||
            soLuongTonKho.isEmpty() || giaBan.isEmpty() || giaNhap.isEmpty()) {
            Toast.makeText(context, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        // Chuyển đổi dữ liệu
        val soLuongTonKhoInt = soLuongTonKho.toIntOrNull()
        val giaBanLong = giaBan.toLongOrNull()
        val giaNhapLong = giaNhap.toLongOrNull()

        if (soLuongTonKhoInt == null || giaBanLong == null || giaNhapLong == null) {
            Toast.makeText(context, "Số lượng, giá bán và giá nhập phải là số hợp lệ", Toast.LENGTH_SHORT).show()
            return
        }

        // Xử lý hình ảnh
        val hinhAnhSanPham: ByteArray? = selectedImageUri?.let { uri ->
            try {
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.toByteArray()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Lỗi khi xử lý hình ảnh", Toast.LENGTH_SHORT).show()
                null
            }
        }

        // Tạo đối tượng SanPham
        val sanPham = SanPham(
            tenSanPham = tenSanPham,
            model = model,
            donViTinh = donViTinh,
            phanLoai = phanLoai,
            soLuongTonKho = soLuongTonKhoInt,
            giaBan = giaBanLong,
            giaNhap = giaNhapLong,
            moTaSanPham = if (moTa.isEmpty()) null else moTa,
            hinhAnhSanPham = hinhAnhSanPham
        )

        // Thêm sản phẩm vào database
        sanPhamViewModel.insertSanPham(sanPham)
        Toast.makeText(context, "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show()

        // Xóa dữ liệu trong form
        clearForm()
    }

    private fun clearForm() {
        binding.tenSanPhamInput.text?.clear()
        binding.modelInput.text?.clear()
        binding.donViTinhInput.text?.clear()
        binding.phanLoaiInput.text?.clear()
        binding.soLuongTonKhoInput.text?.clear()
        binding.giaBanInput.text?.clear()
        binding.giaNhapInput.text?.clear()
        binding.moTaInput.text?.clear()
        binding.imagePreview.visibility = View.GONE
        binding.addImageIcon.visibility = View.VISIBLE
        binding.imageContainer.findViewById<View>(android.R.id.text1)?.visibility = View.VISIBLE
        selectedImageUri = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}