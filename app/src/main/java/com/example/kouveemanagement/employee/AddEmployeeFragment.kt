package com.example.kouveemanagement.employee


import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.example.kouveemanagement.R
import com.example.kouveemanagement.model.Employee
import com.example.kouveemanagement.model.EmployeeResponse
import com.example.kouveemanagement.presenter.EmployeePresenter
import com.example.kouveemanagement.presenter.EmployeeView
import com.example.kouveemanagement.repository.Repository
import kotlinx.android.synthetic.main.fragment_add_employee.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class AddEmployeeFragment : Fragment(), EmployeeView {

    private lateinit var employee: Employee
    private lateinit var presenter: EmployeePresenter

    companion object {
        fun newInstance() = AddEmployeeFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_employee, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_add.setOnClickListener {
            getData()

            presenter = EmployeePresenter(this, Repository())
            presenter.addEmployee(employee)
        }

        showDatePicker()
    }

    fun getData(){
        val name = name.text.toString()
        val address = address.text.toString()
        val birthdate = birthdate.text.toString()
        val phone_number = phone_number.text.toString()
        val role = role.text.toString()
        val last_emp = "0"

        employee = Employee(name, address, birthdate, phone_number, role, birthdate, last_emp)
    }

    fun showDatePicker(){

        val year = Calendar.getInstance().get(Calendar.YEAR)
        val month = Calendar.getInstance().get(Calendar.MONTH)
        val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        birthdate.setOnClickListener {
            val datePickerDialog =
                context?.let { it1 ->
                    DatePickerDialog(it1, DatePickerDialog.OnDateSetListener {
                            view, year, month, dayOfMonth -> birthdate.setText("$year-$month-$dayOfMonth")
                    }, year, month, day)
                }
            datePickerDialog?.show()
        }
    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun employeeSuccess(data: EmployeeResponse?) {
        Toast.makeText(context, "Success To Add", Toast.LENGTH_SHORT).show()
    }

    override fun employeeFailed() {
        Toast.makeText(context, "Failed To Add", Toast.LENGTH_SHORT).show()
    }


}