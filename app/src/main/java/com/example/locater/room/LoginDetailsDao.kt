package com.example.locater.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LoginDetailsDao {

    @Query("SELECT * FROM login_details_table where email=:email")
    fun getAllLoginDetails(email:String): LiveData<List<LoginDetails>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertLoginDetail(loginDetails: LoginDetails)

    @Query("DELETE FROM login_details_table where email=:email")
    fun deleteAllLoginDetails(email:String)

    @Query("UPDATE login_details_table SET check_out_time = :checkOutTime WHERE id=(Select MAX(id) from login_details_table where email=:email)")
    fun updateTheCheckOutTime(email:String,checkOutTime:String)

    @Query("Select check_in_time From login_details_table where id=(Select MAX(id) from login_details_table where email=:email)")
    suspend fun getCurrentCheckInTime(email:String):String

    @Query("Select btn_check_in_state from login_details_table where id=(Select Max(id) from login_details_table where email=:email)")
    suspend fun getCurrentStateOfCheckInButton(email:String):Boolean?
    @Query("Update login_details_table set btn_check_in_state=:btn_check_in_state where id=(Select Max(id) from login_details_table where email=:email)")
    fun updateCurrentStateOfCheckInButton(btn_check_in_state:Boolean?, email:String)

    @Query("Select btn_check_in_day_state from login_details_table where id=(Select Max(id) from login_details_table where email=:email)")
    suspend fun getCurrentStateOfCheckInDayButton(email:String):Boolean?
    @Query("Update login_details_table set btn_check_in_day_state=:btn_check_in_day_state where id=(Select Max(id) from login_details_table where email=:email)")
    fun updateCurrentStateOfCheckInDayButton(btn_check_in_day_state:Boolean,email:String)

    @Query("Select login_time_till_now from login_details_table where id=(Select MAX(id) from login_details_table where email=:email)")
    suspend fun getCurrentLoginTimeTillNow(email:String):String?
    @Query("Update login_details_table set login_time_till_now=:new_login_time_till_now where id=(Select MAX(id) from login_details_table where email=:email)")
    fun updateCurrentLoginTimeTillNow(email:String, new_login_time_till_now:String)
}