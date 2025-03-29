package com.example.thdt_quangtran.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.thdt_quangtran.R
import com.example.thdt_quangtran.models.ChiTietGioHang
import com.example.thdt_quangtran.models.SanPham
import com.example.thdt_quangtran.utils.Utils
import android.graphics.BitmapFactory

class CheckoutAdapter(
    private val selectedItems: List<ChiTietGioHang>,
    private val products: List<SanPham>
) : RecyclerView.Adapter<CheckoutAdapter.CheckoutViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckoutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_checkout, parent, false)
        return CheckoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: CheckoutViewHolder, position: Int) {
        val cartItem = selectedItems[position]
        val product = products.find { it.maSanPham == cartItem.maSanPham }
        if (product != null) {
            holder.bind(cartItem, product)
        } else {
            holder.productName.text = "Sản phẩm không tồn tại"
            holder.productPrice.text = "0 VNĐ"
            holder.quantityText.text = cartItem.soLuong.toString()
            holder.productImage.setImageResource(android.R.drawable.ic_menu_gallery)
        }
    }

    override fun getItemCount(): Int = selectedItems.size

    inner class CheckoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.product_image)
        val productName: TextView = itemView.findViewById(R.id.product_name)
        val productPrice: TextView = itemView.findViewById(R.id.product_price)
        val quantityText: TextView = itemView.findViewById(R.id.quantity_text)

        fun bind(cartItem: ChiTietGioHang, product: SanPham) {
            productName.text = product.tenSanPham
            productPrice.text = Utils.formatPrice(product.giaBan)
            quantityText.text = "x${cartItem.soLuong}"

            if (product.hinhAnhSanPham != null) {
                val bitmap = BitmapFactory.decodeByteArray(
                    product.hinhAnhSanPham,
                    0,
                    product.hinhAnhSanPham.size
                )
                productImage.setImageBitmap(bitmap)
            } else {
                productImage.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        }
    }
}