package com.example.locater.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface LoginDetailsDao {

    @Query("SELECT * FROM login_details_table")
     fun getAllLoginDetails(): List<LoginDetails>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertLoginDetail(loginDetails: LoginDetails)

    @Query("DELETE FROM login_details_table")
    fun deleteAllLoginDetails()

    @Query("UPDATE login_details_table SET check_out_time = :checkOutTime WHERE id= :id")
    fun updateTheCheckOutTime(id:Int,checkOutTime:String)

    @Query("Select Max(id) From login_details_table")
    fun getLastLoginDetailId():Int
}