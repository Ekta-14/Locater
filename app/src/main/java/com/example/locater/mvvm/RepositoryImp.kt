package com.example.locater.mvvm

import androidx.lifecycle.LiveData
import com.example.locater.room.LoginDetails
import com.example.locater.room.LoginDetailsDao

class RepositoryImp(private val loginDetailsDao: LoginDetailsDao):RepositoryInterface{

    override fun getAllLoginDetails(email: String): LiveData<List<LoginDetails>> {
        return loginDetailsDao.getAllLoginDetails(email) }

    override fun insertLoginDetail(loginDetails: LoginDetails) {
       loginDetailsDao.insertLoginDetail(loginDetails) }

    override fun deleteAllLoginDetails(email: String) {
       loginDetailsDao.deleteAllLoginDetails(email) }

    override fun updateTheCheckOutTime(email: String, checkOutTime: String) {
       loginDetailsDao.updateTheCheckOutTime(email,checkOutTime) }

    override suspend fun getCurrentCheckInTime(email: String): String {
        return loginDetailsDao.getCurrentCheckInTime(email) }

    override suspend fun getCurrentStateOfCheckInButton(email: String): Boolean? {
       return loginDetailsDao.getCurrentStateOfCheckInButton(email) }

    override fun updateCurrentStateOfCheckInButton(btn_check_in_state: Boolean?, email: String, ) {
       loginDetailsDao.updateCurrentStateOfCheckInButton(btn_check_in_state,email) }

    override suspend fun getCurrentStateOfCheckInDayButton(email: String): Boolean? {
        return loginDetailsDao.getCurrentStateOfCheckInDayButton(email) }

    override fun updateCurrentStateOfCheckInDayButton(btn_check_in_day_state: Boolean?, email: String, ) {
        loginDetailsDao.updateCurrentStateOfCheckInButton(btn_check_in_day_state,email) }

    override suspend fun getCurrentLoginTimeTillNow(email: String): String? {
        return loginDetailsDao.getCurrentLoginTimeTillNow(email)
    }

    override fun updateCurrentLoginTimeTillNow(email: String, new_login_time_till_now: String, ) {
       loginDetailsDao.updateCurrentLoginTimeTillNow(email,new_login_time_till_now) }
}