package com.example.thdt_quangtran.fragments.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thdt_quangtran.R
import com.example.thdt_quangtran.activities.AdminActivity
import com.example.thdt_quangtran.adapters.KhachHangAdapter
import com.example.thdt_quangtran.database.AppDatabase
import com.example.thdt_quangtran.databinding.FragmentCustomerListBinding
import com.example.thdt_quangtran.models.KhachHang
import com.example.thdt_quangtran.models.TaiKhoan
import com.example.thdt_quangtran.viewmodels.KhachHangViewModel
import com.example.thdt_quangtran.viewmodels.KhachHangViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CustomerListFragment : Fragment() {

    private var _binding: FragmentCustomerListBinding? = null
    private val binding get() = _binding!!
    private lateinit var khachHangViewModel: KhachHangViewModel
    private lateinit var adapter: KhachHangAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomerListBinding.inflate(inflater, container, false)

        // Khởi tạo ViewModel
        val khachHangDao = AppDatabase.getDatabase(requireContext()).khachHangDao()
        val taiKhoanDao = AppDatabase.getDatabase(requireContext()).taiKhoanDao()
        val factory = KhachHangViewModelFactory(khachHangDao, taiKhoanDao)
        khachHangViewModel = ViewModelProvider(this, factory).get(KhachHangViewModel::class.java)

        // Lấy danh sách TaiKhoan và tạo Map để tra cứu
        viewLifecycleOwner.lifecycleScope.launch {
            // Lọc các TaiKhoan có maKhachHang không null và tạo Map
            val taiKhoanMap = khachHangViewModel.getAllTaiKhoan()
                .filter { it.maKhachHang != null }
                .associateBy { it.maKhachHang!! }

            // Khởi tạo RecyclerView
            adapter = KhachHangAdapter(
                onItemClick = { khachHang ->
                    // Chuyển sang CustomerEditFragment
                    (activity as? AdminActivity)?.hideBottomNavigation()

                    // Xóa CustomerEditFragment cũ khỏi back stack (nếu có)
                    parentFragmentManager.popBackStack("CustomerEditFragment", 0)

                    val fragment = CustomerEditFragment.newInstance(khachHang.maKhachHang)
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack("CustomerEditFragment")
                        .commit()
                },
                taiKhoanMap = taiKhoanMap
            )
            binding.customerList.layoutManager = LinearLayoutManager(context)
            binding.customerList.adapter = adapter

            // Quan sát dữ liệu từ ViewModel
            khachHangViewModel.allKhachHang.collectLatest { khachHangList ->
                if (_binding != null) {
                    if (khachHangList.isEmpty()) {
                        binding.customerList.visibility = View.GONE
                        // Ví dụ: binding.emptyMessage.visibility = View.VISIBLE
                        // binding.emptyMessage.text = "Không có khách hàng nào để hiển thị"
                    } else {
                        binding.customerList.visibility = View.VISIBLE
                        // binding.emptyMessage.visibility = View.GONE
                        adapter.submitList(khachHangList)
                    }
                }
            }
        }

//        // Xử lý sự kiện nhấn nút FAB
//        binding.addCustomerFab.setOnClickListener {
//            // Chuyển sang CustomerAddFragment
//            (activity as? AdminActivity)?.hideBottomNavigation()
//
//            // Xóa CustomerAddFragment cũ khỏi back stack (nếu có)
//            parentFragmentManager.popBackStack("CustomerAddFragment", 0)
//
//            val fragment = CustomerAddFragment()
//            parentFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, fragment)
//                .addToBackStack("CustomerAddFragment")
//                .commit()
//        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // Hiển thị lại BottomNavigationView khi quay lại CustomerListFragment
        (activity as? AdminActivity)?.showBottomNavigation()
        // Cập nhật tiêu đề Toolbar
        (activity as? AdminActivity)?.supportActionBar?.title = "Customer Management"

        // Làm mới taiKhoanMap khi quay lại fragment
        viewLifecycleOwner.lifecycleScope.launch {
            val newTaiKhoanMap = khachHangViewModel.getAllTaiKhoan()
                .filter { it.maKhachHang != null }
                .associateBy { it.maKhachHang!! }
            adapter = KhachHangAdapter(
                onItemClick = { khachHang ->
                    (activity as? AdminActivity)?.hideBottomNavigation()
                    parentFragmentManager.popBackStack("CustomerEditFragment", 0)
                    val fragment = CustomerEditFragment.newInstance(khachHang.maKhachHang)
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack("CustomerEditFragment")
                        .commit()
                },
                taiKhoanMap = newTaiKhoanMap
            )
            binding.customerList.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}