package com.example.locater.mvvm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.locater.room.LoginDetails
import com.example.locater.room.LoginDetailsDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewModelImp(application: Application):AndroidViewModel(application),ViewModelInterface {

    private val repositoryImp:RepositoryImp
    init {
        val dao=LoginDetailsDatabase.getDatabase(application).loginDetailsDao()
        repositoryImp= RepositoryImp(dao)
    }

    override fun getAllLoginDetails(email: String): LiveData<List<LoginDetails>> {
            return repositoryImp.getAllLoginDetails(email)
    }

    override fun insertLoginDetail(loginDetails: LoginDetails) {
        viewModelScope.launch(Dispatchers.IO){
            repositoryImp.insertLoginDetail(loginDetails)
        }
    }

    override fun deleteAllLoginDetails(email: String) {
        viewModelScope.launch(Dispatchers.IO){
            repositoryImp.deleteAllLoginDetails(email)
        }
    }

    override fun updateTheCheckOutTime(email: String, checkOutTime: String) {
        viewModelScope.launch(Dispatchers.IO){
            repositoryImp.updateTheCheckOutTime(email,checkOutTime)
        }
    }
    override suspend fun getCurrentCheckInTime(email: String): String {
        return repositoryImp.getCurrentCheckInTime(email)
    }

    override suspend fun getCurrentStateOfCheckInButton(email: String): Boolean? {
        return repositoryImp.getCurrentStateOfCheckInButton(email)
    }

    override fun updateCurrentStateOfCheckInButton(
        btn_check_in_state: Boolean?,
        email: String,
    ) {
        viewModelScope.launch(Dispatchers.IO){
            repositoryImp.updateCurrentStateOfCheckInButton(btn_check_in_state,email)
        }
    }

    override suspend fun getCurrentStateOfCheckInDayButton(email: String): Boolean? {
        return  repositoryImp.getCurrentStateOfCheckInDayButton(email)
    }

    override fun updateCurrentStateOfCheckInDayButton(
        btn_check_in_day_state: Boolean?,
        email: String,
    ) {
        viewModelScope.launch(Dispatchers.IO){
            repositoryImp.updateCurrentStateOfCheckInDayButton(btn_check_in_day_state,email)
        }
    }

    override suspend fun getCurrentLoginTimeTillNow(email: String): String? {
       return  repositoryImp.getCurrentLoginTimeTillNow(email)
    }

    override suspend fun updateCurrentLoginTimeTillNow(
        email: String,
        new_login_time_till_now: String,
    ) {
        viewModelScope.launch(Dispatchers.IO){
            repositoryImp.updateCurrentLoginTimeTillNow(email,new_login_time_till_now)
        }
    }


}