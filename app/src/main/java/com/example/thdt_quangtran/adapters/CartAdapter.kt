package com.example.thdt_quangtran.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.thdt_quangtran.R
import com.example.thdt_quangtran.models.ChiTietGioHang
import com.example.thdt_quangtran.models.SanPham
import com.example.thdt_quangtran.utils.Utils
import android.graphics.BitmapFactory
import android.widget.Toast

class CartAdapter(
    private val cartItems: MutableList<ChiTietGioHang>,
    private var products: List<SanPham>,
    private val onQuantityChanged: (ChiTietGioHang, Int) -> Unit,
    private val onDeleteClicked: (ChiTietGioHang) -> Unit,
    private val onSelectionChanged: () -> Unit // Callback khi trạng thái check thay đổi
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    // Danh sách lưu trạng thái check của từng mục
    private val selectedItems = mutableMapOf<Int, Boolean>()

    init {
        // Khởi tạo trạng thái check ban đầu (mặc định không check)
        cartItems.forEachIndexed { index, _ ->
            selectedItems[index] = false
        }
    }

    // Phương thức để cập nhật danh sách sản phẩm
    fun updateProducts(newProducts: List<SanPham>) {
        this.products = newProducts
        notifyDataSetChanged()
    }

    // Lấy danh sách các mục được check
    fun getSelectedItems(): List<ChiTietGioHang> {
        return cartItems.filterIndexed { index, _ -> selectedItems[index] == true }
    }

    // Phương thức để chọn hoặc bỏ chọn tất cả
    fun selectAll(isSelected: Boolean) {
        cartItems.forEachIndexed { index, _ ->
            selectedItems[index] = isSelected
        }
        notifyDataSetChanged()
        onSelectionChanged() // Cập nhật tổng tiền
    }

    // Kiểm tra xem tất cả các mục có được chọn hay không
    fun areAllSelected(): Boolean {
        return selectedItems.all { it.value == true }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]
        val product = products.find { it.maSanPham == cartItem.maSanPham }
        if (product != null) {
            holder.bind(cartItem, product, position)
        } else {
            // Nếu không tìm thấy sản phẩm, hiển thị thông tin mặc định
            holder.productName.text = "Sản phẩm không tồn tại"
            holder.productPrice.text = "0 VNĐ"
            holder.productImage.setImageResource(android.R.drawable.ic_menu_gallery)
            holder.quantityText.text = cartItem.soLuong.toString()
            holder.checkboxSelect.isChecked = false
        }
    }

    override fun getItemCount(): Int = cartItems.size

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.product_image)
        val productName: TextView = itemView.findViewById(R.id.product_name)
        val productPrice: TextView = itemView.findViewById(R.id.product_price)
        val quantityText: TextView = itemView.findViewById(R.id.quantity_text)
        val increaseButton: ImageButton = itemView.findViewById(R.id.increase_button)
        val decreaseButton: ImageButton = itemView.findViewById(R.id.decrease_button)
        val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)
        val checkboxSelect: CheckBox = itemView.findViewById(R.id.checkbox_select)

        fun bind(cartItem: ChiTietGioHang, product: SanPham, position: Int) {
            productName.text = product.tenSanPham
            productPrice.text = Utils.formatPrice(product.giaBan)
            quantityText.text = cartItem.soLuong.toString()

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

            // Thiết lập trạng thái checkbox
            checkboxSelect.isChecked = selectedItems[position] ?: false
            checkboxSelect.setOnCheckedChangeListener { _, isChecked ->
                selectedItems[position] = isChecked
                onSelectionChanged() // Gọi callback để cập nhật tổng tiền
            }

            // Xử lý nút tăng số lượng
            increaseButton.setOnClickListener {
                if (cartItem.soLuong < product.soLuongTonKho) {
                    cartItem.soLuong++
                    quantityText.text = cartItem.soLuong.toString()
                    onQuantityChanged(cartItem, cartItem.soLuong)
                } else {
                    Toast.makeText(
                        itemView.context,
                        "Số lượng không được vượt quá ${product.soLuongTonKho}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            // Xử lý nút giảm số lượng
            decreaseButton.setOnClickListener {
                if (cartItem.soLuong > 1) {
                    cartItem.soLuong--
                    quantityText.text = cartItem.soLuong.toString()
                    onQuantityChanged(cartItem, cartItem.soLuong)
                }
            }

            // Xử lý nút xóa với xác nhận
            deleteButton.setOnClickListener {
                AlertDialog.Builder(itemView.context)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc chắn muốn xóa ${product.tenSanPham} khỏi giỏ hàng không?")
                    .setPositiveButton("Có") { _, _ ->
                        onDeleteClicked(cartItem)
                    }
                    .setNegativeButton("Không") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setCancelable(false)
                    .show()
            }
        }
    }
}