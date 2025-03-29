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
import com.example.thdt_quangtran.adapters.NhanVienAdapter
import com.example.thdt_quangtran.database.AppDatabase
import com.example.thdt_quangtran.databinding.FragmentEmployeeListBinding
import com.example.thdt_quangtran.models.NhanVien
import com.example.thdt_quangtran.models.TaiKhoan
import com.example.thdt_quangtran.viewmodels.NhanVienViewModel
import com.example.thdt_quangtran.viewmodels.NhanVienViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EmployeeListFragment : Fragment() {

    private var _binding: FragmentEmployeeListBinding? = null
    private val binding get() = _binding!!
    private lateinit var nhanVienViewModel: NhanVienViewModel
    private lateinit var adapter: NhanVienAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmployeeListBinding.inflate(inflater, container, false)

        val nhanVienDao = AppDatabase.getDatabase(requireContext()).nhanVienDao()
        val taiKhoanDao = AppDatabase.getDatabase(requireContext()).taiKhoanDao()
        val factory = NhanVienViewModelFactory(nhanVienDao, taiKhoanDao)
        nhanVienViewModel = ViewModelProvider(this, factory).get(NhanVienViewModel::class.java)

        viewLifecycleOwner.lifecycleScope.launch {
            val taiKhoanMap = nhanVienViewModel.getAllTaiKhoan()
                .filter { it.maNhanVien != null }
                .associateBy { it.maNhanVien!! }

            adapter = NhanVienAdapter(
                onItemClick = { nhanVien ->
                    (activity as? AdminActivity)?.hideBottomNavigation()
                    parentFragmentManager.popBackStack("EmployeeEditFragment", 0)
                    val fragment = EmployeeEditFragment.newInstance(nhanVien.maNhanVien)
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack("EmployeeEditFragment")
                        .commit()
                },
                taiKhoanMap = taiKhoanMap
            )
            binding.employeeList.layoutManager = LinearLayoutManager(context)
            binding.employeeList.adapter = adapter

            nhanVienViewModel.allNhanVien.collectLatest { nhanVienList ->
                if (_binding != null) {
                    if (nhanVienList.isEmpty()) {
                        binding.employeeList.visibility = View.GONE
                    } else {
                        binding.employeeList.visibility = View.VISIBLE
                        adapter.submitList(nhanVienList)
                    }
                }
            }
        }

        binding.addEmployeeFab.setOnClickListener {
            (activity as? AdminActivity)?.hideBottomNavigation()
            parentFragmentManager.popBackStack("EmployeeAddFragment", 0)
            val fragment = EmployeeAddFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack("EmployeeAddFragment")
                .commit()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as? AdminActivity)?.showBottomNavigation()
        (activity as? AdminActivity)?.supportActionBar?.title = "Employee Management"

        viewLifecycleOwner.lifecycleScope.launch {
            val newTaiKhoanMap = nhanVienViewModel.getAllTaiKhoan()
                .filter { it.maNhanVien != null }
                .associateBy { it.maNhanVien!! }
            adapter = NhanVienAdapter(
                onItemClick = { nhanVien ->
                    (activity as? AdminActivity)?.hideBottomNavigation()
                    parentFragmentManager.popBackStack("EmployeeEditFragment", 0)
                    val fragment = EmployeeEditFragment.newInstance(nhanVien.maNhanVien)
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack("EmployeeEditFragment")
                        .commit()
                },
                taiKhoanMap = newTaiKhoanMap
            )
            binding.employeeList.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}