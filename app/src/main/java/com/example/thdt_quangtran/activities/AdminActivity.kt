package com.example.thdt_quangtran.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.thdt_quangtran.R
import com.example.thdt_quangtran.databinding.ActivityAdminBinding
import com.example.thdt_quangtran.fragments.admin.*

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private var currentSection: String = "dashboard"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Thiết lập Toolbar
        setSupportActionBar(binding.toolbar)
        updateToolbarTitle()

        // Thiết lập ActionBarDrawerToggle
        drawerToggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        // Xử lý Navigation Drawer
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            // Xóa back stack trước khi chuyển section
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

            when (menuItem.itemId) {
                R.id.nav_dashboard -> {
                    currentSection = "dashboard"
                    loadFragment(DashboardFragment())
                }
                R.id.nav_products -> {
                    currentSection = "products"
                    loadFragment(ProductListFragment())
                }
                R.id.nav_customers -> {
                    currentSection = "customers"
                    loadFragment(CustomerListFragment())
                }
                R.id.nav_employees -> {
                    currentSection = "employees"
                    loadFragment(EmployeeListFragment())
                }
                R.id.nav_orders -> {
                    currentSection = "orders"
                    loadFragment(OrderListFragment())
                }
                R.id.nav_promotions -> {
                    currentSection = "promotions"
                    loadFragment(PromotionListFragment())
                }
                R.id.nav_logout -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
            updateToolbarTitle()
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Xử lý Bottom Navigation
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_list -> loadFragment(getCurrentListFragment())
                R.id.bottom_add -> loadFragment(getCurrentAddFragment())
                R.id.bottom_report -> loadFragment(getCurrentReportFragment())
            }
            true
        }

        if (savedInstanceState == null) {
            loadFragment(DashboardFragment())
            binding.navView.setCheckedItem(R.id.nav_dashboard)
        }
    }

    private fun loadFragment(fragment: Fragment): Boolean {
        if (fragment !is ProductEditFragment) {
            showBottomNavigation()
        }

        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)

        // Thêm vào back stack nếu là CustomerAddFragment hoặc EmployeeAddFragment
        if (fragment is CustomerAddFragment) {
            transaction.addToBackStack("CustomerAddFragment")
        } else if (fragment is EmployeeAddFragment) {
            transaction.addToBackStack("EmployeeAddFragment")
        }

        transaction.commit()
        return true
    }

    private fun updateToolbarTitle() {
        binding.toolbar.title = when (currentSection) {
            "dashboard" -> "Admin Dashboard"
            "products" -> "Product Management"
            "customers" -> "Customer Management"
            "employees" -> "Employee Management"
            "orders" -> "Order Management"
            "promotions" -> "Promotions Management"
            else -> "Admin Dashboard"
        }
    }

    private fun getCurrentListFragment(): Fragment {
        return when (currentSection) {
            "dashboard" -> DashboardFragment()
            "products" -> ProductListFragment()
            "customers" -> CustomerListFragment()
            "employees" -> EmployeeListFragment()
            "orders" -> OrderListFragment()
            "promotions" -> PromotionListFragment()
            else -> DashboardFragment()
        }
    }

    private fun getCurrentAddFragment(): Fragment {
        return when (currentSection) {
            "dashboard" -> QuickActionsFragment()
            "products" -> ProductAddFragment()
            "customers" -> CustomerAddFragment()
            "employees" -> EmployeeAddFragment()
            "orders" -> OrderAddFragment()
            "promotions" -> PromotionAddFragment()
            else -> QuickActionsFragment()
        }
    }

    private fun getCurrentReportFragment(): Fragment {
        return when (currentSection) {
            "dashboard" -> DashboardReportFragment()
            "products" -> ProductReportFragment()
            "customers" -> CustomerReportFragment()
            "employees" -> EmployeeReportFragment()
            "orders" -> OrderReportFragment()
            "promotions" -> PromotionReportFragment()
            else -> DashboardReportFragment()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun hideBottomNavigation() {
        binding.bottomNavigation.animate()
            .translationY(binding.bottomNavigation.height.toFloat())
            .setDuration(300)
            .withEndAction { binding.bottomNavigation.visibility = View.GONE }
            .start()
    }

    fun showBottomNavigation() {
        binding.bottomNavigation.visibility = View.VISIBLE
        binding.bottomNavigation.animate()
            .translationY(0f)
            .setDuration(300)
            .start()
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}