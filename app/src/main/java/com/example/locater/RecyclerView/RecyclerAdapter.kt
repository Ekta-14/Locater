package com.example.attendanceapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.locater.R
import com.example.locater.room.LoginDetails


class RecyclerAdapter():RecyclerView.Adapter<LoginDetailsViewHolder>()
{
    private val loginDetailsList=ArrayList<LoginDetails>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoginDetailsViewHolder {
        val layoutInflater=LayoutInflater.from(parent.context)
        val listItem=layoutInflater.inflate(R.layout.rv_list_items,parent,false)
        return LoginDetailsViewHolder(listItem)
    }
    override fun getItemCount(): Int {
        return loginDetailsList.size
    }
    override fun onBindViewHolder(holder: LoginDetailsViewHolder, position: Int) {
        holder.bind(loginDetailsList[position])
    }

    fun updateList(newList: List<LoginDetails>) {
            loginDetailsList.clear()
            loginDetailsList.addAll(newList)
            notifyDataSetChanged()
    }
}
class LoginDetailsViewHolder(private val view: View):RecyclerView.ViewHolder(view){
    val login_date=view.findViewById<TextView>(R.id.tv_rv_date)
    val check_in_time=view.findViewById<TextView>(R.id.tv_rv_check_in_time)
    val check_out_time=view.findViewById<TextView>(R.id.tv_rv_check_out_time)
    val latitude=view.findViewById<TextView>(R.id.tv_rv_latitude)
    val longitude=view.findViewById<TextView>(R.id.tv_rv_longitude)

    fun bind(loginDetails: LoginDetails)
    {
        login_date.text= loginDetails.date
        check_in_time.text=loginDetails.check_in_time
        check_out_time.text=loginDetails.check_out_time
        latitude.text=loginDetails.latitude.toString()
        longitude.text=loginDetails.longitude.toString()
    }
}