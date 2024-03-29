package com.example.kouveemanagement.customerpet

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.kouveemanagement.CustomFun
import com.example.kouveemanagement.CustomerServiceActivity
import com.example.kouveemanagement.MainActivity
import com.example.kouveemanagement.R
import com.example.kouveemanagement.model.CustomerPet
import com.example.kouveemanagement.model.CustomerPetResponse
import com.example.kouveemanagement.presenter.CustomerPetPresenter
import com.example.kouveemanagement.presenter.CustomerPetView
import com.example.kouveemanagement.repository.Repository
import kotlinx.android.synthetic.main.activity_edit_customet_pet.*
import org.jetbrains.anko.startActivity
import java.util.*

class EditCustomerPetActivity : AppCompatActivity(), CustomerPetView {

    private lateinit var id: String
    private lateinit var lastEmp: String
    private lateinit var presenter: CustomerPetPresenter
    private lateinit var customerpet: CustomerPet

    private var nameDropdown: MutableList<String> = arrayListOf()
    private var idCustomerList: MutableList<String> = arrayListOf()
    private lateinit var idCustomer: String

    private var typeDropdown: MutableList<String> = arrayListOf()
    private var idTypeList: MutableList<String> = arrayListOf()
    private lateinit var idType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_customet_pet)
        setData()
        showDatePicker()
        presenter = CustomerPetPresenter(this, Repository())
        lastEmp = MainActivity.currentUser?.user_id.toString()
        btn_save.setOnClickListener {
            if (isValid()){
                getData()
                presenter.editCustomerPet(id, customerpet)
            }
        }
        btn_cancel.setOnClickListener {
            presenter.deleteCustomerPet(id, lastEmp)
        }
        btn_home.setOnClickListener {
            startActivity<CustomerServiceActivity>()
        }
    }

    private fun setDropdown(customerPet: CustomerPet){
        var positionC = 0
        var positionT = 0
        nameDropdown = CustomerPetManagementActivity.nameCustomer
        idCustomerList = CustomerPetManagementActivity.idCustomer
        typeDropdown = CustomerPetManagementActivity.nameType
        idTypeList = CustomerPetManagementActivity.idType
        for (i in idCustomerList.indices){
            if (idCustomerList[i] == customerPet.id_customer){
                positionC = i
                idCustomer = idCustomerList[i]
            }
        }
        for (i in idTypeList.indices){
            if (idTypeList[i] == customerPet.id_type){
                positionT = i
                idType = idTypeList[i]
            }
        }
        val adapterC = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, nameDropdown)
        customer_dropdown.setAdapter(adapterC)
        customer_dropdown.setText(nameDropdown[positionC], true)
        customer_dropdown.setOnItemClickListener { _, _, position, _ ->
            idCustomer = idCustomerList[position]
            val name = nameDropdown[position]
            Toast.makeText(this, "Customer : $name", Toast.LENGTH_LONG).show()
        }
        val adapterT = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, typeDropdown)
        type_dropdown.setAdapter(adapterT)
        type_dropdown.setText(typeDropdown[positionT], true)
        type_dropdown.setOnItemClickListener { _, _, position, _ ->
            idType = idTypeList[position]
            val name = typeDropdown[position]
            Toast.makeText(this, "Type : $name", Toast.LENGTH_LONG).show()
        }
    }

    private fun setData(){
        val customerPet: CustomerPet? = intent.getParcelableExtra("customerpet")
        id = customerPet?.id.toString()
        name.setText(customerPet?.name)
        birthdate.setText(customerPet?.birthdate)
        created_at.setText(customerPet?.created_at)
        if (customerPet?.updated_at.isNullOrEmpty()){
            updated_at.setText("-")
        }else{
            updated_at.setText(customerPet?.updated_at)
        }
        if (customerPet?.deleted_at.isNullOrBlank()){
            deleted_at.setText("-")
        }else{
            deleted_at.setText(customerPet?.deleted_at)
        }
        last_emptv.setText(customerPet?.last_emp)
        customerPet?.let { setDropdown(it) }
    }

    fun getData(){
        val name = name.text.toString()
        val birthdate = birthdate.text.toString()
        customerpet = CustomerPet(id, idCustomer, idType, name, birthdate, null, null, null, lastEmp)
    }

    private fun isValid(): Boolean {
        if (name.text.isNullOrEmpty()){
            name.error = getString(R.string.error_name)
            return false
        }
        if (birthdate.text.isNullOrEmpty()){
            birthdate.error = getString(R.string.error_birthdate)
            return false
        }
        return true
    }


    private fun showDatePicker(){
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val month = Calendar.getInstance().get(Calendar.MONTH)
        val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        birthdate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                birthdate.setText("$year-$month-$dayOfMonth")
            }, year, month,day)
            datePickerDialog.show()
        }
    }

    override fun showCustomerPetLoading() {
        btn_save.startAnimation()
        btn_cancel.visibility = View.INVISIBLE
    }

    override fun hideCustomerPetLoading() {
    }

    override fun customerPetSuccess(data: CustomerPetResponse?) {
        startActivity<CustomerPetManagementActivity>()
    }

    override fun customerPetFailed(data: String) {
        btn_save.revertAnimation()
        btn_cancel.visibility = View.VISIBLE
        CustomFun.failedSnackBar(container, baseContext, data)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity<CustomerPetManagementActivity>()
    }
}
