package com.example.kouveemanagement.customerpet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kouveemanagement.*
import com.example.kouveemanagement.adapter.CustomerPetRecyclerViewAdapter
import com.example.kouveemanagement.model.*
import com.example.kouveemanagement.presenter.*
import com.example.kouveemanagement.repository.Repository
import kotlinx.android.synthetic.main.activity_customer_pet_management.*
import org.jetbrains.anko.startActivity

class CustomerPetManagementActivity : AppCompatActivity(), CustomerPetView, CustomerView, PetTypeView {

    private lateinit var customerPetAdapter: CustomerPetRecyclerViewAdapter
    private lateinit var presenter: CustomerPetPresenter

    private var customerPetsList: MutableList<CustomerPet> = mutableListOf()
    private val customerPetsTemp = ArrayList<CustomerPet>()
    private var temps = ArrayList<CustomerPet>()

    private lateinit var dialog: View

    private lateinit var presenterC: CustomerPresenter
    private lateinit var presenterT: PetTypePresenter

    companion object{
        var nameCustomer: MutableList<String> = arrayListOf()
        var idCustomer: MutableList<String> = arrayListOf()
        var nameType: MutableList<String> = arrayListOf()
        var idType: MutableList<String> = arrayListOf()
        var customerPets: MutableList<CustomerPet> = mutableListOf()
        var petTypes: MutableList<PetType> = mutableListOf()
        var customers: MutableList<Customer> = mutableListOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_pet_management)
        presenterT = PetTypePresenter(this, Repository())
        presenterT.getAllPetType()
        presenterC = CustomerPresenter(this, Repository())
        presenterC.getAllCustomer()
        presenter = CustomerPetPresenter(this, Repository())
        presenter.getAllCustomerPet()
        btn_home.setOnClickListener {
            startActivity<CustomerServiceActivity>()
        }
        customerPetAdapter = CustomerPetRecyclerViewAdapter(customers,petTypes, customerPetsList){}
        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                sort_switch.isChecked = false
                recyclerview.adapter = CustomerPetRecyclerViewAdapter(customers,petTypes, customerPets){
                    showDialog(it)
                }
                query?.let { customerPetAdapter.filterCustomerPet(it) }
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                sort_switch.isChecked = false
                recyclerview.adapter = CustomerPetRecyclerViewAdapter(customers,petTypes, customerPets){
                    showDialog(it)
                }
                newText?.let { customerPetAdapter.filterCustomerPet(it) }
                return false
            }
        })
        fab_add.setOnClickListener {
            val fragment: Fragment = AddCustomerPetFragment.newInstance()
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, fragment).commit()
        }
        show_all.setOnClickListener {
            CustomFun.createToolTips(this, "L").showAlignTop(show_all)
            temps = customerPetsTemp
            getList()
        }
        show_en.setOnClickListener {
            CustomFun.createToolTips(this, "C").showAlignTop(show_en)
            val filtered = customerPetsTemp.filter { it.deleted_at === null }
            temps = filtered as ArrayList<CustomerPet>
            getList()
        }
        show_dis.setOnClickListener {
            CustomFun.createToolTips(this, "R").showAlignTop(show_dis)
            val filtered = customerPetsTemp.filter { it.deleted_at !== null }
            temps = filtered as ArrayList<CustomerPet>
            getList()
        }
        sort_switch.setOnClickListener {
            getList()
        }
        swipe_rv.setOnRefreshListener {
            presenter.getAllCustomerPet()
        }
        CustomFun.setSwipe(swipe_rv)
    }

    private fun getList(){
        if (temps.isNullOrEmpty()){
            CustomFun.warningSnackBar(container, baseContext, "Empty data")
            recyclerview.adapter = CustomerPetRecyclerViewAdapter(customers, petTypes, temps){}
        }else{
            if (sort_switch.isChecked){
                val sorted = temps.sortedBy { it.name }
                recyclerview.adapter = CustomerPetRecyclerViewAdapter(customers, petTypes, sorted as MutableList<CustomerPet>){
                    showDialog(it)
                }
            }else{
                recyclerview.adapter = CustomerPetRecyclerViewAdapter(customers, petTypes, temps){
                    showDialog(it)
                }
            }
        }
        customerPetAdapter.notifyDataSetChanged()
    }

    override fun showPetTypeLoading() {
        swipe_rv.isRefreshing = true
    }

    override fun hidePetTypeLoading() {
        swipe_rv.isRefreshing = false
    }

    override fun petTypeSuccess(data: PetTypeResponse?) {
        val temp: List<PetType> = data?.pettype ?: emptyList()
        if (temp.isEmpty()){
            CustomFun.neutralSnackBar(container, baseContext, "Pet Type empty")
        }else{
            clearType()
            petTypes.addAll(temp)
            for (i in temp.indices){
                if (temp[i].deleted_at == null){
                    nameType.add(temp[i].name.toString())
                    idType.add(temp[i].id.toString())
                }
            }
        }
    }

    override fun petTypeFailed(data: String) {
        CustomFun.failedSnackBar(container, baseContext, data)
    }

    private fun clearType(){
        nameType.clear()
        idType.clear()
        petTypes.clear()
    }

    override fun showCustomerPetLoading() {
        swipe_rv.isRefreshing = true
    }

    override fun hideCustomerPetLoading() {
        swipe_rv.isRefreshing = false
    }

    override fun customerPetSuccess(data: CustomerPetResponse?) {
        val temp: List<CustomerPet> = data?.customerpets ?: emptyList()
        if (temp.isEmpty()){
            CustomFun.neutralSnackBar(container, baseContext, "Oops, no result")
        }else{
            clearList()
            customerPetsList.addAll(temp)
            customerPetsTemp.addAll(temp)
            val filtered = customerPetsTemp.filter { it.deleted_at === null }
            temps = filtered as ArrayList<CustomerPet>
            recyclerview.layoutManager = LinearLayoutManager(this)
            recyclerview.adapter = CustomerPetRecyclerViewAdapter(customers, petTypes, temps){
                showDialog(it)
            }
            CustomFun.successSnackBar(container, baseContext, "Ok, success")
        }
    }

    override fun customerPetFailed(data: String) {
        CustomFun.failedSnackBar(container, baseContext, data)
    }

    private fun clearList(){
        customerPetsList.clear()
        customerPetsTemp.clear()
    }

    private fun showDialog(customerPet: CustomerPet){
        dialog = LayoutInflater.from(this).inflate(R.layout.dialog_detail_customer_pet, null)

        val name = dialog.findViewById<TextView>(R.id.name)
        val birthday = dialog.findViewById<TextView>(R.id.birthdate)
        val createdAt = dialog.findViewById<TextView>(R.id.created_at)
        val updatedAt = dialog.findViewById<TextView>(R.id.updated_at)
        val deletedAt = dialog.findViewById<TextView>(R.id.deleted_at)
        val lastEmp = dialog.findViewById<TextView>(R.id.last_emp)
        val btnClose = dialog.findViewById<ImageButton>(R.id.btn_close)
        val btnEdit = dialog.findViewById<Button>(R.id.btn_edit)

        name.text = customerPet.name.toString()
        birthday.text = customerPet.birthdate.toString()
        createdAt.text = customerPet.created_at
        if (customerPet.updated_at.isNullOrEmpty()){
            updatedAt.text = "-"
        }else{
            updatedAt.text = customerPet.updated_at
        }
        if (customerPet.deleted_at.isNullOrEmpty()){
            deletedAt.text = "-"
        }else{
            deletedAt.text = customerPet.deleted_at
        }
        lastEmp.text = customerPet.last_emp

        val infoDialog = AlertDialog.Builder(this)
            .setView(dialog)
            .setCancelable(false)
            .show()

        if (customerPet.deleted_at != null){
            btnEdit.visibility = View.GONE
        }

        btnEdit.setOnClickListener {
            startActivity<EditCustomerPetActivity>("customerpet" to customerPet)
        }

        btnClose.setOnClickListener {
            infoDialog.dismiss()
        }
    }

    override fun showCustomerLoading() {
        swipe_rv.isRefreshing = true
    }

    override fun hideCustomerLoading() {
        swipe_rv.isRefreshing = false
    }

    override fun customerSuccess(data: CustomerResponse?) {
        val temp: List<Customer> = data?.customers ?: emptyList()
        if (temp.isEmpty()){
            CustomFun.neutralSnackBar(container, baseContext, "Customer empty")
        }else{
            clearCustomer()
            customers.addAll(temp)
            for (i in temp.indices){
                if (temp[i].deleted_at == null){
                    nameCustomer.add(temp[i].name.toString())
                    idCustomer.add(temp[i].id.toString())
                }
            }
        }
    }

    override fun customerFailed(data: String) {
        CustomFun.failedSnackBar(container, baseContext, data)
    }

    private fun clearCustomer(){
        nameCustomer.clear()
        idCustomer.clear()
        customers.clear()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity<CustomerServiceActivity>()
    }
}
