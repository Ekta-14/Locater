package com.example.locater.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.DateFormat

@Entity("login_details_table")
data class LoginDetails(
    @PrimaryKey(autoGenerate=true) var id:Int=0,
    @ColumnInfo val email:String,
    @ColumnInfo val date:String,
    @ColumnInfo val check_in_time:String,
    @ColumnInfo val check_out_time:String,
    @ColumnInfo val latitude:Double,
    @ColumnInfo val longitude:Double,
    @ColumnInfo val btn_check_in_state:Boolean,
    @ColumnInfo val btn_check_in_day_state:Boolean,
    @ColumnInfo val login_time_till_now:String,
)
