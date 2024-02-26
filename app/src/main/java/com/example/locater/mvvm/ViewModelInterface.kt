package com.example.locater.mvvm

import androidx.lifecycle.LiveData
import com.example.locater.room.LoginDetails

interface ViewModelInterface {

    fun getAllLoginDetails(email:String): LiveData<List<LoginDetails>>

    fun insertLoginDetail(loginDetails: LoginDetails)

    fun deleteAllLoginDetails(email:String)

    fun updateTheCheckOutTime(email:String,checkOutTime:String)

    suspend fun getCurrentCheckInTime(email:String):String

    suspend fun getCurrentStateOfCheckInButton(email:String):Boolean?
    fun updateCurrentStateOfCheckInButton(btn_check_in_state:Boolean?, email:String)


    suspend fun getCurrentStateOfCheckInDayButton(email:String):Boolean?
    fun updateCurrentStateOfCheckInDayButton(btn_check_in_day_state:Boolean?,email:String)

    suspend fun getCurrentLoginTimeTillNow(email:String):String?
    suspend fun updateCurrentLoginTimeTillNow(email:String, new_login_time_till_now:String)
}