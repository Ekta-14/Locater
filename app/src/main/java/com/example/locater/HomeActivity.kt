package com.example.locater

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.attendanceapp.RecyclerAdapter
import com.example.locater.constant.DatabaseConst
import com.example.locater.constant.FirebaseAuthConst
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
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
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
    lateinit var btn_check_in: Button
    lateinit var btn_check_out: Button
    lateinit var btn_sign_out: Button
    lateinit var btn_check_in_day: Button
    lateinit var btn_check_out_day: Button
    lateinit var tv_dashboard_name: TextView
    lateinit var tv_login_time: TextView
    private val currentUser = FirebaseAuthConst.auth.currentUser
    private var total_login_time_now: String = "00:00:00"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        enableEdgeToEdge()

        btn_sign_out = findViewById(R.id.btn_sign_out)
        btn_check_in = findViewById(R.id.btn_check_in)
        btn_check_out = findViewById(R.id.btn_check_out)
        btn_check_in_day = findViewById(R.id.btn_check_in_day)
        btn_check_out_day = findViewById(R.id.btn_check_out_day)
        tv_dashboard_name = findViewById(R.id.tv_user_dashboard)
        tv_login_time = findViewById(R.id.tv_login_time)
        recyclerView = findViewById(R.id.rv_login_details)


        adapter = RecyclerAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        showTheDetailsInRecyclerView()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        tv_dashboard_name.text = "Hello ${currentUser?.displayName.toString()}"

//        SharedPrefObject.init(this)
//        val lastEnabledId=SharedPrefObject.getLastEnabledButton()
//
//        if(lastEnabledId==-1)//activity initialised first time
//        {
//            SharedPrefObject.putLastEnabledButton(SharedprefConstant.last_enabled_button,R.id.btn_check_in)
//            btn_check_in.isEnabled=true
//            btn_check_out.isEnabled=false
//        }
//        else//activty has been initialized previous button enabled to be preserved
//        {
//            if(lastEnabledId==R.id.btn_check_in)
//            {
//                btn_check_in.isEnabled=true
//                btn_check_out.isEnabled=false
//            }
//            else
//            {
//                btn_check_in.isEnabled=false
//                btn_check_out.isEnabled=true
//            }
//        }

        //to preserve state of buttons of current user
        CoroutineScope(Dispatchers.IO).launch {//for preserving state of buttons of current user

            val dao = DatabaseConst.database.loginDetailsDao()
            val btn_check_in_state =
                dao.getCurrentStateOfCheckInButton(currentUser?.email.toString())
            val btn_check_in_day_state =
                dao.getCurrentStateOfCheckInDayButton(currentUser?.email.toString())
            val login_time_till_now = dao.getCurrentLoginTimeTillNow(currentUser?.email.toString())

            withContext(Dispatchers.Main) {
                if (btn_check_in_day_state == null) {
                    btn_check_in_day.isEnabled = true
                    btn_check_out_day.isEnabled = false
                    btn_check_in.isEnabled = false
                    btn_check_out.isEnabled = false
                } else {
                    btn_check_in_day.isEnabled = btn_check_in_day_state
                    btn_check_out_day.isEnabled = btn_check_in_day_state != true

                    if (login_time_till_now == null) {
                        tv_login_time.text = total_login_time_now
                    } else {
                        total_login_time_now = login_time_till_now
                        tv_login_time.text = total_login_time_now
                    }

                    if (btn_check_in_state == null) {
                        btn_check_in.isEnabled = true
                        btn_check_out.isEnabled = false
                    } else {
                        btn_check_in.isEnabled = btn_check_in_state
                        btn_check_out.isEnabled = btn_check_in_state != true
                    }
                }
            }
        }

        btn_sign_out.setOnClickListener { userSignOut() }
        btn_check_in.setOnClickListener { userCheckIn() }
        btn_check_out.setOnClickListener { userCheckOut() }
        btn_check_in_day.setOnClickListener { userCheckInForTheDay() }
        btn_check_out_day.setOnClickListener { userCheckOutForTheday() }
    }

    private fun userCheckOutForTheday() {
        if (LocalTime.parse(total_login_time_now).hour >= 9) {
            CoroutineScope(Dispatchers.IO).launch {
                val dao = DatabaseConst.database.loginDetailsDao()
                dao.updateCurrentStateOfCheckInDayButton(true, currentUser?.email.toString())
            }
            btn_check_in_day.isEnabled = true
            btn_check_out_day.isEnabled = false
        } else
            Toast.makeText(this, "Login Time less than 9 hours", Toast.LENGTH_SHORT).show()
    }

    private fun userCheckInForTheDay() {
        Toast.makeText(this, "You are allowed to check in now", Toast.LENGTH_SHORT).show()
        btn_check_in_day.isEnabled = false
        btn_check_out_day.isEnabled = true
        btn_check_in.isEnabled = true
        btn_check_out.isEnabled = false
    }

    private fun userCheckOut() {
        val currentCheckOutTime = getCurrentTime()
        CoroutineScope(Dispatchers.IO).launch {

            val dao = DatabaseConst.database.loginDetailsDao()

            dao.updateTheCheckOutTime(currentUser?.email.toString(), currentCheckOutTime)

            dao.updateCurrentStateOfCheckInButton(true, currentUser?.email.toString())

            val login_time_till_now = dao.getCurrentLoginTimeTillNow(currentUser?.email.toString())
            val currentCheckInTime = dao.getCurrentCheckInTime(currentUser?.email.toString())
            val currentLoginTimeCalculated =
                calculateDiffInTime(currentCheckOutTime, currentCheckInTime)
            total_login_time_now = addTwoTime(currentLoginTimeCalculated, login_time_till_now)
            dao.updateCurrentLoginTimeTillNow(currentUser?.email.toString(), total_login_time_now)

            withContext(Dispatchers.Main)
            {
                tv_login_time.text = total_login_time_now
                showTheDetailsInRecyclerView()
            }
        }

//        //shared preferences for button
//        SharedPrefObject.init(this@HomeActivity)
//        SharedPrefObject.putLastEnabledButton(SharedprefConstant.last_enabled_button,R.id.btn_check_in)
        //enabling and disabling the buttons

        btn_check_out.isEnabled = false
        btn_check_in.isEnabled = true
    }

    private fun addTwoTime(currentLoginTimeCalculated: String, loginTimeTillNow: String?): String {

        val currentLoginTime =
            LocalTime.parse(currentLoginTimeCalculated, DateTimeFormatter.ofPattern("HH:mm:ss"))
        val loginTimeTotal =
            LocalTime.parse(loginTimeTillNow, DateTimeFormatter.ofPattern("HH:mm:ss"))
        val resultTime = currentLoginTime.plusHours(loginTimeTotal.hour.toLong())
            .plusMinutes(loginTimeTotal.minute.toLong())
            .plusSeconds(loginTimeTotal.second.toLong())

        return resultTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
    }

    private fun calculateDiffInTime(
        currentCheckOutTime: String,
        currentCheckInTime: String,
    ): String {
        val checkInTime =
            LocalTime.parse(currentCheckInTime, DateTimeFormatter.ofPattern("HH:mm:ss"))
        val checkOutTime =
            LocalTime.parse(currentCheckOutTime, DateTimeFormatter.ofPattern("HH:mm:ss"))

        val duration = Duration.between(checkInTime, checkOutTime)

        val hrs = duration.toHours().toInt()
        val minute = duration.toMinutes() % 60
        val seconds = duration.toSeconds() % 60

        return String.format("%02d:%02d:%02d", hrs, minute, seconds)
    }

    private fun userCheckIn() {
        val permissionGranted = checkForLocationPermission()
        if (!permissionGranted) {//agar permission nhi mili to prompt dalo
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
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

//                        SharedPrefObject.init(this@HomeActivity)
//                        SharedPrefObject.putLastEnabledButton(SharedprefConstant.last_enabled_button, R.id.btn_check_out)

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

    private fun addLoginDetailInRoom(userLocation: Location) {

        val check_in_time = getCurrentTime()
        val latitude = userLocation.latitude
        val longitude = userLocation.longitude
        //date
        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat.getDateInstance()
        val formattedDate = formatter.format(date).toString()

        val login_detail_check_in = LoginDetails(
            email = currentUser?.email.toString(),
            date = formattedDate,
            check_in_time = check_in_time,
            check_out_time = " ",
            latitude = latitude,
            longitude = longitude,
            btn_check_in_state = false,
            btn_check_in_day_state = false,
            login_time_till_now = total_login_time_now
        )

        CoroutineScope(Dispatchers.IO).launch {
            val dao = DatabaseConst.database.loginDetailsDao()
            dao.insertLoginDetail(login_detail_check_in)
        }
    }

    private fun showTheDetailsInRecyclerView() {
        CoroutineScope(Dispatchers.IO).launch {
            val updatedList = DatabaseConst.database.loginDetailsDao()
                .getAllLoginDetails(currentUser?.email.toString()) // Retrieve the updated list from the database
            withContext(Dispatchers.Main)
            {
                adapter.updateList(updatedList)
            }
        }
    }

    private fun checkForLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getCurrentTime(): String {
        val currentTime = LocalTime.now()
        val formatter =
            DateTimeFormatter.ofPattern("HH:mm:ss") // Define a custom format without milliseconds
        return currentTime.format(formatter)
    }

    private fun userSignOut() {
        FirebaseAuth.getInstance().signOut()
        SharedPrefObject.init(this)
        SharedPrefObject.putBooleanLogin(SharedprefConstant.isUserLoggedIn, false)
        revokeAccess()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun revokeAccess() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString((R.string.your_web_client_id)))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        googleSignInClient.revokeAccess()
            .addOnCompleteListener(this) {
                SharedPrefObject.init(this)
                SharedPrefObject.putBooleanLogin(SharedprefConstant.isUserLoggedIn, false)
            }
    }
}