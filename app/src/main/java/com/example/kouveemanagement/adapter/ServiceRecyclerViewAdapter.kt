package com.example.kouveemanagement.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kouveemanagement.CustomFun
import com.example.kouveemanagement.R
import com.example.kouveemanagement.model.PetSize
import com.example.kouveemanagement.model.Service
import com.example.kouveemanagement.service.ServiceManagementActivity
import com.example.kouveemanagement.transaction.AddDetailServiceTransactionFragment
import kotlinx.android.extensions.LayoutContainer
import java.util.*


class ServiceRecyclerViewAdapter(private val services: MutableList<Service>, private val petSizes: MutableList<PetSize>, private val listener: (Service) -> Unit) : RecyclerView.Adapter<ServiceRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_service, parent, false)
        return ViewHolder(viewHolder)
    }

    override fun getItemCount(): Int = services.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(services[position], petSizes, listener)
    }

    fun filterService(input: String){
        ServiceManagementActivity.services.clear()
        if (input.isEmpty()){
            ServiceManagementActivity.services.addAll(services)
        }else{
            var i = 0
            while (i < services.size){
                if (services[i].name?.toLowerCase(Locale.getDefault())?.contains(input)!!){
                    ServiceManagementActivity.services.add(services[i])
                }
                i++
            }
        }
        notifyDataSetChanged()
    }

    fun filterForTransaction(input: String){
        AddDetailServiceTransactionFragment.services.clear()
        if (input.isEmpty()){
            AddDetailServiceTransactionFragment.services.addAll(services)
        }else{
            var i = 0
            while (i < services.size){
                if (services[i].name?.toLowerCase(Locale.getDefault())?.contains(input)!!){
                    AddDetailServiceTransactionFragment.services.add(services[i])
                }
                i++
            }
        }
        notifyDataSetChanged()
    }

    inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        private var name: TextView = itemView.findViewById(R.id.name)
        private var price: TextView = itemView.findViewById(R.id.price)

        fun bindItem(service: Service, inputSize: MutableList<PetSize>, listener: (Service) -> Unit) {
            var sizeTxt = ""
            for (size in inputSize){
                if (size.id.equals(service.id_size)){
                    sizeTxt = size.name.toString()
                }
            }

            name.text = service.name + " $sizeTxt"
            val priceS = service.price.toString()
            price.text = CustomFun.changeToRp(priceS.toDouble())
            containerView.setOnClickListener {
                listener(service)
            }
        }
    }
}