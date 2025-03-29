package com.example.thdt_quangtran.fragments.customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.thdt_quangtran.database.AppDatabase
import com.example.thdt_quangtran.database.dao.ChiTietGioHangDao
import com.example.thdt_quangtran.database.dao.GioHangDao
import com.example.thdt_quangtran.databinding.FragmentProductDetailBinding
import com.example.thdt_quangtran.models.ChiTietGioHang
import com.example.thdt_quangtran.models.GioHang
import com.example.thdt_quangtran.models.SanPham
import com.example.thdt_quangtran.utils.Utils
import com.example.thdt_quangtran.viewmodels.SanPhamViewModel
import com.example.thdt_quangtran.viewmodels.SanPhamViewModelFactory
import android.graphics.BitmapFactory
import androidx.fragment.app.viewModels
import kotlinx.coroutines.launch

class ProductDetailFragment : Fragment() {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var gioHangDao: GioHangDao
    private lateinit var chiTietGioHangDao: ChiTietGioHangDao

    private val sanPhamViewModel: SanPhamViewModel by viewModels {
        SanPhamViewModelFactory(AppDatabase.getDatabase(requireContext()).sanPhamDao())
    }

    private var maSanPham: Int = -1
    private var sanPham: SanPham? = null
    private var quantity: Int = 1 // Biến để lưu số lượng

    companion object {
        private const val ARG_MA_SAN_PHAM = "maSanPham"

        fun newInstance(maSanPham: Int): ProductDetailFragment {
            val fragment = ProductDetailFragment()
            val args = Bundle()
            args.putInt(ARG_MA_SAN_PHAM, maSanPham)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            maSanPham = it.getInt(ARG_MA_SAN_PHAM)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Khởi tạo DAO
        gioHangDao = AppDatabase.getDatabase(requireContext()).gioHangDao()
        chiTietGioHangDao = AppDatabase.getDatabase(requireContext()).chiTietGioHangDao()

        // Thiết lập số lượng ban đầu
        binding.quantityText.text = quantity.toString()

        // Tải thông tin sản phẩm
        lifecycleScope.launch {
            sanPham = sanPhamViewModel.getById(maSanPham)
            sanPham?.let { product ->
                binding.productName.text = product.tenSanPham
                binding.productPrice.text = Utils.formatPrice(product.giaBan) // giaBan giờ là Long
                binding.productDescription.text = product.moTaSanPham ?: "Không có mô tả"

                if (product.hinhAnhSanPham != null) {
                    val bitmap = BitmapFactory.decodeByteArray(
                        product.hinhAnhSanPham,
                        0,
                        product.hinhAnhSanPham.size
                    )
                    binding.productImage.setImageBitmap(bitmap)
                } else {
                    binding.productImage.setImageResource(android.R.drawable.ic_menu_gallery)
                }

                // Kiểm tra số lượng tồn kho
                if (product.soLuongTonKho == 0) {
                    // Hết hàng: Hiển thị "Hết hàng" và vô hiệu hóa numeric up/down
                    binding.addToCartButton.visibility = View.GONE
                    binding.outOfStockText.visibility = View.VISIBLE
                    binding.quantityLayout.visibility = View.GONE
                } else {
                    // Còn hàng: Giới hạn số lượng tối đa bằng số lượng tồn kho
                    binding.addToCartButton.visibility = View.VISIBLE
                    binding.outOfStockText.visibility = View.GONE
                    binding.quantityLayout.visibility = View.VISIBLE

                    // Xử lý nút tăng số lượng
                    binding.increaseButton.setOnClickListener {
                        if (quantity < product.soLuongTonKho) {
                            quantity++
                            binding.quantityText.text = quantity.toString()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Số lượng không được vượt quá ${product.soLuongTonKho}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    // Xử lý nút giảm số lượng
                    binding.decreaseButton.setOnClickListener {
                        if (quantity > 1) {
                            quantity--
                            binding.quantityText.text = quantity.toString()
                        }
                    }
                }
            }
        }

        // Xử lý nút "Thêm vào giỏ hàng"
        binding.addToCartButton.setOnClickListener {
            sanPham?.let { product ->
                addToCart(product, quantity)
            }
        }

        // Xử lý nút Back
        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun addToCart(sanPham: SanPham, quantity: Int) {
        lifecycleScope.launch {
            // Giả sử maKhachHang là 1 (cần thay bằng ID khách hàng thực tế)
            val maKhachHang = 1
            var gioHang = gioHangDao.getByMaKhachHang(maKhachHang)
            if (gioHang == null) {
                gioHang = GioHang(maKhachHang = maKhachHang)
                gioHangDao.insert(gioHang)
                gioHang = gioHangDao.getByMaKhachHang(maKhachHang)
            }

            if (gioHang != null) {
                val chiTietList = chiTietGioHangDao.getByMaGioHangSuspend(gioHang.maGioHang)
                val existingItem = chiTietList.firstOrNull { it.maSanPham == sanPham.maSanPham }

                if (existingItem != null) {
                    existingItem.soLuong += quantity
                    chiTietGioHangDao.update(existingItem)
                } else {
                    val chiTietGioHang = ChiTietGioHang(
                        maGioHang = gioHang.maGioHang,
                        maSanPham = sanPham.maSanPham,
                        soLuong = quantity,
                        giaBan = sanPham.giaBan // giaBan giờ là Long
                    )
                    chiTietGioHangDao.insert(chiTietGioHang)
                }

                Toast.makeText(
                    requireContext(),
                    "Đã thêm ${sanPham.tenSanPham} (Số lượng: $quantity) vào giỏ hàng",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}