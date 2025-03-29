package com.example.thdt_quangtran.fragments.customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thdt_quangtran.adapters.CheckoutAdapter
import com.example.thdt_quangtran.databinding.FragmentCustomerCheckoutBinding
import com.example.thdt_quangtran.models.ChiTietGioHang
import com.example.thdt_quangtran.models.SanPham
import com.example.thdt_quangtran.utils.Utils

class CustomerCheckoutFragment : Fragment() {

    private var _binding: FragmentCustomerCheckoutBinding? = null
    private val binding get() = _binding!!

    private lateinit var checkoutAdapter: CheckoutAdapter
    private var selectedItems: List<ChiTietGioHang> = emptyList()
    private var products: List<SanPham> = emptyList()
    private var totalPrice: Long = 0

    companion object {
        private const val ARG_SELECTED_ITEMS = "selected_items"
        private const val ARG_PRODUCTS = "products"
        private const val ARG_TOTAL_PRICE = "total_price"

        fun newInstance(selectedItems: List<ChiTietGioHang>, products: List<SanPham>, totalPrice: Long): CustomerCheckoutFragment {
            val fragment = CustomerCheckoutFragment()
            val args = Bundle()
            args.putParcelableArrayList(ARG_SELECTED_ITEMS, ArrayList(selectedItems))
            args.putParcelableArrayList(ARG_PRODUCTS, ArrayList(products))
            args.putLong(ARG_TOTAL_PRICE, totalPrice)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedItems = it.getParcelableArrayList<ChiTietGioHang>(ARG_SELECTED_ITEMS) ?: emptyList()
            products = it.getParcelableArrayList<SanPham>(ARG_PRODUCTS) ?: emptyList()
            totalPrice = it.getLong(ARG_TOTAL_PRICE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomerCheckoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Khởi tạo RecyclerView và Adapter
        checkoutAdapter = CheckoutAdapter(selectedItems, products)
        binding.recyclerViewCheckout.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = checkoutAdapter
        }

        // Hiển thị tổng tiền
        binding.totalPrice.text = Utils.formatPrice(totalPrice)

        // Xử lý nút quay lại
        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Xử lý nút "Xác nhận thanh toán"
        binding.confirmButton.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Đã xác nhận thanh toán ${selectedItems.size} sản phẩm. Tổng tiền: ${Utils.formatPrice(totalPrice)}",
                Toast.LENGTH_LONG
            ).show()
            // TODO: Thêm logic thanh toán ở đây (ví dụ: lưu đơn hàng vào cơ sở dữ liệu)
            parentFragmentManager.popBackStack() // Quay lại sau khi thanh toán
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}