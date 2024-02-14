package com.example.locater

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import android.Manifest;
import android.location.Location
import android.location.LocationListener
import android.location.LocationRequest
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.lang.Math.atan2
import java.lang.Math.cos
import java.lang.Math.sin
import java.lang.Math.sqrt
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeActivity : AppCompatActivity() {
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
        private const val company_latitude =28.4197
        private const val company_longitude =77.0386
        private const val allowed_radius =1000.0
    }
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var tv_check_in_time:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        enableEdgeToEdge()

        val btn_sign_out=findViewById<Button>(R.id.btn_sign_out)
        val btn_check_in=findViewById<Button>(R.id.btn_check_in)
        val btn_check_out=findViewById<Button>(R.id.btn_check_out)
        tv_check_in_time=findViewById(R.id.check_in_time)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)



        btn_sign_out.setOnClickListener { userSignOut() }
        btn_check_in.setOnClickListener { userCheckIn() }
        //btn_check_in.setOnClickListener { userCheckOut() }
    }

    private fun userCheckIn() {
        val permissionGranted=checkforLocationPermission()
        if(!permissionGranted) {//agar permission nhi mili to prompt dalo
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            //Toast.makeText(this,"permission not granted",Toast.LENGTH_SHORT).show()c
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { userLocation ->
                userLocation?.let {
                   // Toast.makeText(this,"fused location",Toast.LENGTH_SHORT).show()
                    val results = FloatArray(1)
                    Location.distanceBetween(
                        userLocation.latitude, userLocation.longitude,
                        company_latitude, company_longitude, results
                    )
                    val distanceInMeters = results[0]
                    val latitude=userLocation.latitude

                        //Toast.makeText(this,"distance is ${distanceInMeters} user latitude is ${latitude}",Toast.LENGTH_SHORT).show()
                    if (distanceInMeters <= allowed_radius) {
                        // User is within the allowed radius, display current time
                        val currentTime = getCurrentTime()
                        tv_check_in_time.text = currentTime
                       // Toast.makeText(this, "Checked in successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        // User is outside the allowed radius, show message
                        Toast.makeText(this, "Not allowed to check in.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun checkforLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun getCurrentTime(): String {
        val currentTime = Calendar.getInstance().time
        val formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
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