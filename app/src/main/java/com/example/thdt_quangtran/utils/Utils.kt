package com.example.thdt_quangtran.utils

import java.text.DecimalFormat

object Utils {
    fun formatPrice(price: Long): String {
        val formatter = DecimalFormat("#,##0")
        return "${formatter.format(price)} đ"
    }
}