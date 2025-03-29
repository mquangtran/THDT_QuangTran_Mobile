package com.example.thdt_quangtran.fragments.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thdt_quangtran.R
import com.example.thdt_quangtran.activities.AdminActivity
import com.example.thdt_quangtran.database.AppDatabase
import com.example.thdt_quangtran.databinding.FragmentProductListBinding
import com.example.thdt_quangtran.models.SanPham
import com.example.thdt_quangtran.adapters.SanPhamAdapter
import com.example.thdt_quangtran.viewmodels.SanPhamViewModel
import com.example.thdt_quangtran.viewmodels.SanPhamViewModelFactory

class ProductListFragment : Fragment() {

    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding!!
    private lateinit var sanPhamViewModel: SanPhamViewModel
    private lateinit var adapter: SanPhamAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductListBinding.inflate(inflater, container, false)

        // Khởi tạo ViewModel
        val sanPhamDao = AppDatabase.getDatabase(requireContext()).sanPhamDao()
        val factory = SanPhamViewModelFactory(sanPhamDao)
        sanPhamViewModel = ViewModelProvider(this, factory).get(SanPhamViewModel::class.java)

        // Khởi tạo RecyclerView
        adapter = SanPhamAdapter(
            onEditClick = { sanPham ->
                // Ẩn BottomNavigationView
                (activity as? AdminActivity)?.hideBottomNavigation()

                // Xóa ProductEditFragment cũ khỏi back stack (nếu có)
                parentFragmentManager.popBackStack("ProductEditFragment", 0)

                // Chuyển sang ProductEditFragment mới
                val fragment = ProductEditFragment.newInstance(sanPham.maSanPham)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack("ProductEditFragment")
                    .commit()
            },
            onDeleteClick = { sanPham ->
                showDeleteConfirmationDialog(sanPham)
            }
        )
        binding.productList.layoutManager = LinearLayoutManager(context)
        binding.productList.adapter = adapter

        // Quan sát dữ liệu từ ViewModel, sử dụng LiveData với observe
        sanPhamViewModel.allSanPham.observe(viewLifecycleOwner) { sanPhamList ->
            // Kiểm tra _binding trước khi truy cập binding
            if (_binding != null) {
                if (sanPhamList.isEmpty()) {
                    binding.emptyView.visibility = View.VISIBLE
                    binding.productList.visibility = View.GONE
                } else {
                    binding.emptyView.visibility = View.GONE
                    binding.productList.visibility = View.VISIBLE
                    adapter.submitList(sanPhamList)
                }
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // Hiển thị lại BottomNavigationView khi quay lại ProductListFragment
        (activity as? AdminActivity)?.showBottomNavigation()
        // Cập nhật tiêu đề Toolbar
        (activity as? AdminActivity)?.supportActionBar?.title = "Product Management"
    }

    private fun showDeleteConfirmationDialog(sanPham: SanPham) {
        AlertDialog.Builder(requireContext(), R.style.AppTheme_Dialog)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa sản phẩm \"${sanPham.tenSanPham}\" không?")
            .setPositiveButton("Xóa") { _, _ ->
                // Người dùng xác nhận xóa
                sanPhamViewModel.deleteSanPham(sanPham)
            }
            .setNegativeButton("Hủy") { dialog, _ ->
                // Người dùng hủy, đóng dialog
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}