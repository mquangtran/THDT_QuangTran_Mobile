package com.example.thdt_quangtran.fragments.customer

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.thdt_quangtran.R
import com.example.thdt_quangtran.adapters.CustomerSanPhamAdapter
import com.example.thdt_quangtran.database.AppDatabase
import com.example.thdt_quangtran.databinding.FragmentCustomerProductBinding
import com.example.thdt_quangtran.models.SanPham
import com.example.thdt_quangtran.viewmodels.SanPhamViewModel
import com.example.thdt_quangtran.viewmodels.SanPhamViewModelFactory
import com.google.android.material.tabs.TabLayout

class CustomerProductFragment : Fragment() {

    private var _binding: FragmentCustomerProductBinding? = null
    private val binding get() = _binding!!

    private lateinit var sanPhamAdapter: CustomerSanPhamAdapter
    private var sanPhamList: List<SanPham> = listOf()
    private var filteredList: List<SanPham> = listOf()

    private val sanPhamViewModel: SanPhamViewModel by viewModels {
        SanPhamViewModelFactory(AppDatabase.getDatabase(requireContext()).sanPhamDao())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomerProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Khởi tạo RecyclerView
        sanPhamAdapter = CustomerSanPhamAdapter(
            onItemClick = { sanPham ->
                // Chuyển sang ProductDetailFragment khi nhấn vào sản phẩm
                val fragment = ProductDetailFragment.newInstance(sanPham.maSanPham)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack("ProductDetailFragment")
                    .commit()
            }
        )

        binding.recyclerViewProducts.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = sanPhamAdapter
        }

        // Tải dữ liệu sản phẩm từ ViewModel
        sanPhamViewModel.allSanPham.observe(viewLifecycleOwner) { sanPhams ->
            sanPhamList = sanPhams
            filteredList = sanPhamList
            sanPhamAdapter.submitList(filteredList)
            setupTabLayout()
        }

        // Xử lý tìm kiếm
        binding.searchInputLayout.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val searchText = s.toString()
                filterProducts(searchText, binding.tabLayout.selectedTabPosition)
            }
        })

        // Xử lý TabLayout
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val searchText = binding.searchInputLayout.editText?.text.toString()
                filterProducts(searchText, tab?.position ?: 0)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupTabLayout() {
        binding.tabLayout.removeAllTabs()
        // Thêm tab "ALL"
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("ALL"))
        // Thêm các tab phân loại từ danh sách sản phẩm
        val categories = sanPhamList.map { it.phanLoai }.distinct()
        categories.forEach { category ->
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(category))
        }
    }

    private fun filterProducts(searchText: String, tabPosition: Int) {
        val category = if (tabPosition == 0) null else binding.tabLayout.getTabAt(tabPosition)?.text.toString()
        filteredList = sanPhamList.filter { sanPham ->
            val matchesCategory = category == null || sanPham.phanLoai == category
            val matchesSearch = searchText.isEmpty() ||
                    sanPham.tenSanPham.contains(searchText, ignoreCase = true) ||
                    sanPham.moTaSanPham?.contains(searchText, ignoreCase = true) == true ||
                    sanPham.maSanPham.toString().contains(searchText)
            matchesCategory && matchesSearch
        }
        sanPhamAdapter.submitList(filteredList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}