package com.example.thdt_quangtran.fragments.customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.thdt_quangtran.databinding.FragmentCustomerAccountBinding

class CustomerAccountFragment : Fragment() {

    private var _binding: FragmentCustomerAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomerAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Cập nhật thông tin người dùng
        binding.userName.text = "John Doe" // Thay bằng dữ liệu thực tế
        binding.userEmail.text = "john.doe@example.com" // Thay bằng dữ liệu thực tế

        // Xử lý sự kiện click cho các tùy chọn
        binding.shippingAddressesLayout.setOnClickListener {
            // Xử lý khi nhấn vào Shipping Addresses
        }

        binding.paymentMethodsLayout.setOnClickListener {
            // Xử lý khi nhấn vào Payment Methods
        }

        binding.notificationsLayout.setOnClickListener {
            // Xử lý khi nhấn vào Notifications
        }

        binding.helpSupportLayout.setOnClickListener {
            // Xử lý khi nhấn vào Help & Support
        }

        binding.logoutLayout.setOnClickListener {
            // Xử lý khi nhấn vào Log Out
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}