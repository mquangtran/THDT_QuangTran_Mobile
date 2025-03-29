package com.example.thdt_quangtran.fragments.admin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
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
import com.example.thdt_quangtran.activities.AdminActivity
import com.example.thdt_quangtran.database.AppDatabase
import com.example.thdt_quangtran.databinding.FragmentProductEditBinding
import com.example.thdt_quangtran.models.SanPham
import com.example.thdt_quangtran.viewmodels.SanPhamViewModel
import com.example.thdt_quangtran.viewmodels.SanPhamViewModelFactory
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.IOException

class ProductEditFragment : Fragment() {

    private var _binding: FragmentProductEditBinding? = null
    private val binding get() = _binding!!
    private lateinit var sanPhamViewModel: SanPhamViewModel
    private var selectedImageUri: Uri? = null
    private var sanPham: SanPham? = null
    private var maSanPham: Int = -1

    // Activity result launcher để chọn file ảnh
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Lấy maSanPham từ arguments
        maSanPham = arguments?.getInt("maSanPham", -1) ?: -1
        if (maSanPham == -1) {
            throw IllegalArgumentException("maSanPham is required")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductEditBinding.inflate(inflater, container, false)

        // Khởi tạo ViewModel
        val sanPhamDao = AppDatabase.getDatabase(requireContext()).sanPhamDao()
        val factory = SanPhamViewModelFactory(sanPhamDao)
        sanPhamViewModel = ViewModelProvider(this, factory).get(SanPhamViewModel::class.java)

        // Reset selectedImageUri
        selectedImageUri = null

        // Load sản phẩm từ database
        loadSanPham()

        // Xử lý sự kiện chọn file ảnh
        binding.imageContainer.setOnClickListener {
            checkStoragePermissionAndPickFile()
        }

        // Xử lý sự kiện nút "Cập nhật sản phẩm"
        binding.updateProductButton.setOnClickListener {
            if (isAdded) { // Kiểm tra fragment còn gắn với activity không
                updateProduct()
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // Đảm bảo BottomNavigationView ẩn khi ProductEditFragment đang hiển thị
        (activity as? AdminActivity)?.hideBottomNavigation()
        // Cập nhật tiêu đề Toolbar
        (activity as? AdminActivity)?.supportActionBar?.title = "Chỉnh sửa sản phẩm"
    }

    private fun loadSanPham() {
        lifecycleScope.launch {
            sanPham = sanPhamViewModel.getById(maSanPham)
            if (sanPham == null) {
                Toast.makeText(context, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
                return@launch
            }

            // Hiển thị thông tin sản phẩm
            binding.tenSanPhamInput.setText(sanPham?.tenSanPham)
            binding.modelInput.setText(sanPham?.model)
            binding.donViTinhInput.setText(sanPham?.donViTinh)
            binding.phanLoaiInput.setText(sanPham?.phanLoai)
            binding.soLuongTonKhoInput.setText(sanPham?.soLuongTonKho?.toString())
            binding.giaBanInput.setText(sanPham?.giaBan?.toString())
            binding.giaNhapInput.setText(sanPham?.giaNhap?.toString())
            binding.moTaInput.setText(sanPham?.moTaSanPham ?: "")

            // Hiển thị hình ảnh nếu có
            sanPham?.hinhAnhSanPham?.let { imageBytes ->
                try {
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    binding.imagePreview.setImageBitmap(bitmap)
                    binding.imagePreview.visibility = View.VISIBLE
                    binding.addImageIcon.visibility = View.GONE
                    binding.imageContainer.findViewById<View>(android.R.id.text1)?.visibility = View.GONE
                } catch (e: Exception) {
                    Log.e("ProductEditFragment", "Error displaying image: ${e.message}", e)
                    Toast.makeText(context, "Lỗi khi hiển thị ảnh", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                // Nếu không có ảnh, reset giao diện
                binding.imagePreview.visibility = View.GONE
                binding.addImageIcon.visibility = View.VISIBLE
                binding.imageContainer.findViewById<View>(android.R.id.text1)?.visibility = View.VISIBLE
            }
        }
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

    private fun updateProduct() {
        // Kiểm tra fragment còn tồn tại không
        if (!isAdded || _binding == null) {
            Log.e("ProductEditFragment", "Fragment is detached or binding is null")
            return
        }

        // Kiểm tra sanPham đã được load chưa
        if (sanPham == null) {
            Toast.makeText(context, "Đang tải thông tin sản phẩm, vui lòng thử lại", Toast.LENGTH_SHORT).show()
            return
        }

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
        val hinhAnhSanPham: ByteArray? = if (selectedImageUri != null) {
            try {
                val inputStream = requireContext().contentResolver.openInputStream(selectedImageUri!!)
                if (inputStream == null) {
                    Log.e("ProductEditFragment", "Failed to open input stream for image URI: $selectedImageUri")
                    Toast.makeText(context, "Không thể đọc file ảnh", Toast.LENGTH_SHORT).show()
                    return
                }

                // Giảm kích thước ảnh để tránh OutOfMemoryError
                val options = BitmapFactory.Options().apply {
                    inSampleSize = 2 // Giảm kích thước ảnh xuống 1/2
                }
                val bitmap = BitmapFactory.decodeStream(inputStream, null, options)
                inputStream.close()

                if (bitmap == null) {
                    Log.e("ProductEditFragment", "Failed to decode bitmap from URI: $selectedImageUri")
                    Toast.makeText(context, "Không thể xử lý ảnh", Toast.LENGTH_SHORT).show()
                    return
                }

                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 80, outputStream) // Giảm chất lượng ảnh
                val imageBytes = outputStream.toByteArray()
                outputStream.close()
                bitmap.recycle() // Giải phóng bộ nhớ
                imageBytes
            } catch (e: IOException) {
                Log.e("ProductEditFragment", "Error processing image: ${e.message}", e)
                Toast.makeText(context, "Lỗi khi xử lý hình ảnh: ${e.message}", Toast.LENGTH_SHORT).show()
                null
            } catch (e: OutOfMemoryError) {
                Log.e("ProductEditFragment", "Out of memory while processing image: ${e.message}", e)
                Toast.makeText(context, "Ảnh quá lớn, vui lòng chọn ảnh nhỏ hơn", Toast.LENGTH_SHORT).show()
                null
            } catch (e: Exception) {
                Log.e("ProductEditFragment", "Unexpected error while processing image: ${e.message}", e)
                Toast.makeText(context, "Lỗi không xác định khi xử lý ảnh", Toast.LENGTH_SHORT).show()
                null
            }
        } else {
            sanPham?.hinhAnhSanPham // Giữ nguyên hình ảnh cũ nếu không chọn ảnh mới
        }

        // Log trạng thái để debug
        Log.d("ProductEditFragment", "selectedImageUri: $selectedImageUri")
        Log.d("ProductEditFragment", "hinhAnhSanPham: ${hinhAnhSanPham?.size ?: "null"}")

        // Tạo đối tượng SanPham mới với thông tin đã cập nhật
        val updatedSanPham = SanPham(
            maSanPham = sanPham!!.maSanPham, // Giữ nguyên maSanPham để cập nhật đúng sản phẩm
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

        try {
            // Cập nhật sản phẩm vào database
            sanPhamViewModel.updateSanPham(updatedSanPham)
            Toast.makeText(context, "Cập nhật sản phẩm thành công", Toast.LENGTH_SHORT).show()

            // Load lại sản phẩm từ database để đảm bảo trạng thái mới nhất
            loadSanPham()

            // Quay lại ProductListFragment
            parentFragmentManager.popBackStack()
        } catch (e: Exception) {
            Log.e("ProductEditFragment", "Error updating product: ${e.message}", e)
            Toast.makeText(context, "Lỗi khi cập nhật sản phẩm: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(maSanPham: Int): ProductEditFragment {
            val fragment = ProductEditFragment()
            val args = Bundle()
            args.putInt("maSanPham", maSanPham)
            fragment.arguments = args
            return fragment
        }
    }
}