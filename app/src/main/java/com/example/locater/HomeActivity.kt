package com.example.locater

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        enableEdgeToEdge()

        val btn_sign_out=findViewById<Button>(R.id.btn_sign_out)
        btn_sign_out.setOnClickListener { userSignOut() }
    }

    //sign out the user
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