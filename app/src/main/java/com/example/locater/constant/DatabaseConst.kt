package com.example.locater.constant

import com.example.locater.MyAppInstance
import com.example.locater.room.LoginDetailsDatabase

object DatabaseConst {
    val database:LoginDetailsDatabase by lazy {
        LoginDetailsDatabase.getDatabase(MyAppInstance.getInstance())
    }
}