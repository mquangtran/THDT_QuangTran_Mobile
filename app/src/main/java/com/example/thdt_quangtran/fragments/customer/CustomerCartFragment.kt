package com.example.thdt_quangtran.fragments.customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thdt_quangtran.R
import com.example.thdt_quangtran.adapters.CartAdapter
import com.example.thdt_quangtran.database.AppDatabase
import com.example.thdt_quangtran.database.dao.ChiTietGioHangDao
import com.example.thdt_quangtran.database.dao.GioHangDao
import com.example.thdt_quangtran.databinding.FragmentCustomerCartBinding
import com.example.thdt_quangtran.models.ChiTietGioHang
import com.example.thdt_quangtran.models.SanPham
import com.example.thdt_quangtran.utils.Utils
import com.example.thdt_quangtran.viewmodels.SanPhamViewModel
import com.example.thdt_quangtran.viewmodels.SanPhamViewModelFactory
import kotlinx.coroutines.launch

class CustomerCartFragment : Fragment() {

    private var _binding: FragmentCustomerCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var gioHangDao: GioHangDao
    private lateinit var chiTietGioHangDao: ChiTietGioHangDao
    private lateinit var cartAdapter: CartAdapter
    private val cartItems = mutableListOf<ChiTietGioHang>()
    private var products = listOf<SanPham>()

    private val sanPhamViewModel: SanPhamViewModel by viewModels {
        SanPhamViewModelFactory(AppDatabase.getDatabase(requireContext()).sanPhamDao())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomerCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Khởi tạo DAO
        gioHangDao = AppDatabase.getDatabase(requireContext()).gioHangDao()
        chiTietGioHangDao = AppDatabase.getDatabase(requireContext()).chiTietGioHangDao()

        // Khởi tạo RecyclerView và Adapter
        cartAdapter = CartAdapter(
            cartItems,
            products,
            onQuantityChanged = { cartItem, newQuantity ->
                updateCartItem(cartItem, newQuantity)
            },
            onDeleteClicked = { cartItem ->
                deleteCartItem(cartItem)
            },
            onSelectionChanged = {
                updateTotalPrice()
                updateSelectAllCheckbox() // Cập nhật trạng thái "Chọn tất cả"
            }
        )

        binding.recyclerViewCart.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = cartAdapter
        }

        // Xử lý "Chọn tất cả"
        binding.checkboxSelectAll.setOnCheckedChangeListener { _, isChecked ->
            cartAdapter.selectAll(isChecked)
        }

        // Tải dữ liệu sản phẩm trước, sau đó tải giỏ hàng
        sanPhamViewModel.allSanPham.observe(viewLifecycleOwner) { sanPhams ->
            products = sanPhams
            // Cập nhật lại adapter với danh sách sản phẩm mới
            cartAdapter.updateProducts(products)
            loadCartItems()
        }

        // Xử lý nút "Thanh toán"
        binding.checkoutButton.setOnClickListener {
            val selectedItems = cartAdapter.getSelectedItems()
            if (selectedItems.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng chọn ít nhất một sản phẩm để thanh toán!", Toast.LENGTH_SHORT).show()
            } else {
                // Tính tổng tiền của các sản phẩm được chọn
                var total: Long = 0
                selectedItems.forEach { cartItem ->
                    val product = products.find { it.maSanPham == cartItem.maSanPham }
                    product?.let {
                        total += it.giaBan * cartItem.soLuong
                    }
                }

                // Chuyển sang CustomerCheckoutFragment
                val checkoutFragment = CustomerCheckoutFragment.newInstance(selectedItems, products, total)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, checkoutFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    private fun loadCartItems() {
        lifecycleScope.launch {
            // Giả sử maKhachHang là 1 (cần thay bằng ID khách hàng thực tế)
            val maKhachHang = 1
            val gioHang = gioHangDao.getByMaKhachHang(maKhachHang)
            if (gioHang != null) {
                val items = chiTietGioHangDao.getByMaGioHangSuspend(gioHang.maGioHang)
                cartItems.clear()
                cartItems.addAll(items)
                cartAdapter.notifyDataSetChanged()
                updateTotalPrice()
                updateSelectAllCheckbox()
            } else {
                cartItems.clear()
                cartAdapter.notifyDataSetChanged()
                updateTotalPrice()
                updateSelectAllCheckbox()
            }
        }
    }

    private fun updateCartItem(cartItem: ChiTietGioHang, newQuantity: Int) {
        lifecycleScope.launch {
            cartItem.soLuong = newQuantity
            chiTietGioHangDao.update(cartItem)
            cartAdapter.notifyDataSetChanged() // Cập nhật giao diện
            updateTotalPrice()
        }
    }

    private fun deleteCartItem(cartItem: ChiTietGioHang) {
        lifecycleScope.launch {
            chiTietGioHangDao.delete(cartItem)
            cartItems.remove(cartItem)
            cartAdapter.notifyDataSetChanged()
            updateTotalPrice()
            updateSelectAllCheckbox()
            Toast.makeText(requireContext(), "Đã xóa sản phẩm khỏi giỏ hàng", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateTotalPrice() {
        var total: Long = 0
        val selectedItems = cartAdapter.getSelectedItems()
        selectedItems.forEach { cartItem ->
            val product = products.find { it.maSanPham == cartItem.maSanPham }
            product?.let {
                total += it.giaBan * cartItem.soLuong
            }
        }
        binding.totalPrice.text = Utils.formatPrice(total)
    }

    private fun updateSelectAllCheckbox() {
        binding.checkboxSelectAll.isChecked = cartAdapter.areAllSelected()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}