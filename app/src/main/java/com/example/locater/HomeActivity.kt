package com.example.locater

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.attendanceapp.RecyclerAdapter
import com.example.locater.constant.DatabaseConst
import com.example.locater.constant.SharedPrefObject
import com.example.locater.constant.SharedprefConstant
import com.example.locater.room.LoginDetails
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeActivity : AppCompatActivity() {
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
        private var company_latitude = 28.4197
        private var company_longitude = 77.0386
        private const val allowed_radius = 50.0
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecyclerAdapter
    lateinit var btn_check_in:Button
    lateinit var btn_check_out: Button
    lateinit var btn_clear_all:Button
    lateinit var switch_toggle:SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        enableEdgeToEdge()

        val btn_sign_out = findViewById<Button>(R.id.btn_sign_out)
        btn_check_in = findViewById(R.id.btn_check_in)
        btn_check_out = findViewById(R.id.btn_check_out)
        btn_clear_all=findViewById(R.id.btn_clear_all)
        switch_toggle=findViewById(R.id.switch_toggle)
        recyclerView = findViewById(R.id.rv_login_details)


        adapter = RecyclerAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        showTheDetailsInRecyclerView()


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        SharedPrefObject.init(this)
        val lastEnabledId=SharedPrefObject.getLastEnabledButton()

        if(lastEnabledId==-1)//activity initialised first time
        {
            SharedPrefObject.putLastEnabledButton(SharedprefConstant.last_enabled_button,R.id.btn_check_in)
            btn_check_in.isEnabled=true
            btn_check_out.isEnabled=false
        }
        else//activty has been initialized previous button enabled to be preserved
        {
            if(lastEnabledId==R.id.btn_check_in)
            {
                btn_check_in.isEnabled=true
                btn_check_out.isEnabled=false
            }
            else
            {
                btn_check_in.isEnabled=false
                btn_check_out.isEnabled=true
            }
        }

        btn_sign_out.setOnClickListener { userSignOut() }
        btn_check_in.setOnClickListener { userCheckIn() }
        btn_check_out.setOnClickListener { userCheckOut() }
        btn_clear_all.setOnClickListener { deleteLoginHistory() }
        switch_toggle.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                company_latitude = 28.4287
                company_longitude = 77.0370
            }
            else {
                company_latitude = 28.4197
                company_longitude = 77.0386
            }
        }
    }

    private fun deleteLoginHistory() {
        CoroutineScope(Dispatchers.IO).launch {
            DatabaseConst.database.loginDetailsDao().deleteAllLoginDetails()
            withContext(Dispatchers.Main)
            {
                showTheDetailsInRecyclerView()
            }
        }
    }

    private fun userCheckOut()
    {
        val getCurrentTime=getCurrentTime()
        CoroutineScope(Dispatchers.IO).launch {
           val dao= DatabaseConst
                .database
                .loginDetailsDao()
            val lastRowId=dao.getLastLoginDetailId()
            dao.updateTheCheckOutTime(lastRowId, getCurrentTime)
            withContext(Dispatchers.Main)
            {
                showTheDetailsInRecyclerView()
            }
        }

        //shared preferences for button
        SharedPrefObject.init(this@HomeActivity)
        SharedPrefObject.putLastEnabledButton(SharedprefConstant.last_enabled_button,R.id.btn_check_in)
        //enabling and disabling the buttons
        btn_check_out.isEnabled=false
        btn_check_in.isEnabled=true
    }

    private fun userCheckIn() {
        val permissionGranted = checkForLocationPermission()
        if (!permissionGranted) {//agar permission nhi mili to prompt dalo
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }

            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val results = FloatArray(1)
                    Location.distanceBetween(
                        location.latitude,
                        location.longitude,
                        company_latitude,
                        company_longitude,
                        results
                    )
                    val distanceInMeters = results[0]

                    if (distanceInMeters <= allowed_radius) {

                        addLoginDetailInRoom(location)

                        SharedPrefObject.init(this@HomeActivity)
                        SharedPrefObject.putLastEnabledButton(
                            SharedprefConstant.last_enabled_button,
                            R.id.btn_check_out
                        )
                        btn_check_out.isEnabled = true
                        btn_check_in.isEnabled = false
                    } else {
                        Toast.makeText(
                            this,
                            "You are too far away to check in",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    showTheDetailsInRecyclerView()
                } else {
                    Toast.makeText(this, "last known location is not available", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun showTheDetailsInRecyclerView()
    {
        CoroutineScope(Dispatchers.IO).launch {
            val updatedList = DatabaseConst.database.loginDetailsDao().getAllLoginDetails() // Retrieve the updated list from the database
            withContext(Dispatchers.Main)
            {
                adapter.updateList(updatedList)
            }
        }
    }

    private fun addLoginDetailInRoom(userLocation: Location)
    {

        val check_in_time=getCurrentTime()
        val latitude=userLocation.latitude
        val longitude=userLocation.longitude
        //date
        val date=Calendar.getInstance().time
        val formatter=SimpleDateFormat.getDateInstance()
        val formattedDate=formatter.format(date).toString()

        val login_detail_check_in=LoginDetails(date=formattedDate, check_in_time = check_in_time, check_out_time = " ", latitude = latitude, longitude = longitude)

        CoroutineScope(Dispatchers.IO).launch {
            val dao = DatabaseConst.database.loginDetailsDao()
            dao.insertLoginDetail(login_detail_check_in)
        }
    }


    private fun checkForLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun getCurrentTime(): String {
        val currentTime = Calendar.getInstance().time
        val formatter = SimpleDateFormat("hh:mm:ss", Locale.getDefault())
        return formatter.format(currentTime)
    }
    private fun userSignOut() {
        FirebaseAuth.getInstance().signOut()
        SharedPrefObject.init(this)
        SharedPrefObject.putBooleanLogin(SharedprefConstant.isUserLoggedIn,false)
        revokeAccess()
        startActivity(Intent(this,LoginActivity::class.java))
        finish()
    }
    private fun revokeAccess() {
        val googleSignInOptions=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString((R.string.your_web_client_id)))
            .requestEmail()
            .build()

        val googleSignInClient=GoogleSignIn.getClient(this,googleSignInOptions)

        googleSignInClient.revokeAccess()
            .addOnCompleteListener(this){
                SharedPrefObject.init(this)
                SharedPrefObject.putBooleanLogin(SharedprefConstant.isUserLoggedIn,false)
            }
    }
}