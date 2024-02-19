package com.example.locater.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [LoginDetails::class], version = 1, exportSchema = false)
abstract class LoginDetailsDatabase:RoomDatabase() {

    abstract fun loginDetailsDao():LoginDetailsDao

    companion object {
        @Volatile
        private var INSTANCE: LoginDetailsDatabase? = null

        fun getDatabase(context: Context): LoginDetailsDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LoginDetailsDatabase::class.java,
                    "login_details_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}