package com.example.kouveemanagement

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.kouveemanagement.adapter.MenuRecyclerViewAdapter
import com.example.kouveemanagement.customer.CustomerManagementActivity
import com.example.kouveemanagement.employee.EmployeeManagementActivity
import com.example.kouveemanagement.model.Menu
import com.example.kouveemanagement.persistent.AppDatabase
import com.example.kouveemanagement.pet.PetManagementActivity
import com.example.kouveemanagement.product.ProductManagementActivity
import com.example.kouveemanagement.supplier.SupplierManagementActivity
import kotlinx.android.synthetic.main.activity_owner.*
import org.jetbrains.anko.startActivity

class OwnerActivity : AppCompatActivity() {

    private var menu: MutableList<Menu> = mutableListOf()

    companion object {
        var database: AppDatabase? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        menuInitialization()

        database = Room.databaseBuilder(this, AppDatabase::class.java, "kouvee-db").build()

        setContentView(R.layout.activity_owner)
        setMenu()

        getCurrentUser()
    }

    private fun menuInitialization(){
        val name = resources.getStringArray(R.array.owner_menu)
        val desc = resources.getStringArray(R.array.owner_desc)

        menu.clear()

        for (i in name.indices){
            menu.add(Menu(name[i], desc[i]))
        }
    }

    private fun setMenu(){
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = MenuRecyclerViewAdapter(menu) {
            when(it.name) {
                //DATA MASTER
                "Employee" -> startActivity<EmployeeManagementActivity>()
                "Customer" -> startActivity<CustomerManagementActivity>()
                "Pet Type and Size" -> startActivity<PetManagementActivity>()
                "Supplier" -> startActivity<SupplierManagementActivity>()
                "Product" -> startActivity<ProductManagementActivity>()
                "Service" -> startActivity<EmployeeManagementActivity>()
                "Customer Pet" -> startActivity<EmployeeManagementActivity>()
                //TRANSACTION
                "Product Order" -> startActivity<EmployeeManagementActivity>()
                "Product Transaction" -> startActivity<EmployeeManagementActivity>()
                "Service Transaction" -> startActivity<EmployeeManagementActivity>()
            }
            Toast.makeText(this, "Yeay!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCurrentUser(){
        val thread = Thread {
            val currentUser = database?.currentUserDao()?.getCurrentuser()

            id.text = currentUser?.user_id
            name.text = currentUser?.user_name
            role.text = currentUser?.user_role
        }
        thread.start()
    }


}
