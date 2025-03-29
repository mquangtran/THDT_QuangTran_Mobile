package com.example.thdt_quangtran.fragments.customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thdt_quangtran.databinding.FragmentCustomerOrderBinding

class CustomerOrderFragment : Fragment() {

    private var _binding: FragmentCustomerOrderBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomerOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Khởi tạo RecyclerView
        binding.recyclerViewOrders.apply {
            layoutManager = LinearLayoutManager(context)
            // Thêm adapter cho danh sách đơn hàng (tạo adapter nếu cần)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}