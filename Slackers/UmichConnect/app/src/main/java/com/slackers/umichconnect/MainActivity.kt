package com.slackers.umichconnect

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask

class MainActivity : AppCompatActivity() {
    private val signInProviders =
        listOf(
            AuthUI.IdpConfig.EmailBuilder()
                .setAllowNewAccounts(true)
                .setRequireName(true)
                .build())
    private val RC_SIGN_IN = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }
        val buttonConnectionsPage = findViewById<Button>(R.id.buttonConnectionsPage)
        buttonConnectionsPage.setOnClickListener {
            val intent = Intent(this, ConnectionsPage::class.java)
            startActivity(intent)
        }
        //findViewById<Button>(R.id.buttonSignIn).setOnClickListener({startActivity(Intent(this, SignInActivity::class.java))})
        val buttonaccount_sign_in = findViewById<Button>(R.id.buttonSignIn)
        buttonaccount_sign_in.setOnClickListener {
            val intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(signInProviders)
                .setLogo(R.drawable.ic_launcher_background)
                .build()

            startActivityForResult(intent, RC_SIGN_IN)
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                val progressDialog = indeterminateProgressDialog("Setting up your account")
                startActivity(intentFor<MainActivity>().newTask().clearTask())
                progressDialog.dismiss()
            }
            else if (resultCode == Activity.RESULT_CANCELED) {
                if (response == null) return
                when (response.error?.errorCode) {
//                    ErrorCodes.NO_NETWORK ->
//                        longSnackbar(constraint_layout, "No network")
//                    ErrorCodes.UNKNOWN_ERROR ->
//                        longSnackbar(constraint_layout, "Unknown error")
                }
            }
        }
    }

}