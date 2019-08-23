package com.example.goforlunch.controler.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;

import android.widget.Toast;

import androidx.annotation.Nullable;


import com.example.goforlunch.R;
import com.example.goforlunch.model.Api.Firebase.UserHelper;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;


import java.util.Arrays;
import java.util.Objects;

import butterknife.ButterKnife;

public class LoginActivity extends BaseActivity {

    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        startApp();

    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_login;
    }

    //Launch activity and send a welcome back message
    private void startApp() {
        if (getCurrentUser()!=null){
            this.launchMainActivity();
            Toast toast = Toast.makeText(this, "Welcome back "+getCurrentUser().getDisplayName(), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();

        }else {
            this.startSignInActivity();
        }
    }


    //------------------
    //UTILS
    //------------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }


    // Sign-in methods
    private void startSignInActivity() {
        startActivityForResult(AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.GoogleBuilder().build(), //By google
                                        new AuthUI.IdpConfig.EmailBuilder().build(), // By mail
                                        new AuthUI.IdpConfig.FacebookBuilder().build()) //By Facebook
                        )
                        .setLogo(R.drawable.meal_v3_final)
                        .setIsSmartLockEnabled(false, true)
                        .build(),
                RC_SIGN_IN);
    }


    //show snack bar with a message after sign-in
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, getString(R.string.success_auth), Toast.LENGTH_LONG).show();
                launchMainActivity();
                this.createUserInFirestore();
            } else if (response == null) {
                Toast.makeText(this, getString(R.string.cancelled_auth), Toast.LENGTH_LONG).show();
            } else if (Objects.requireNonNull(response.getError()).getErrorCode() == ErrorCodes.NO_NETWORK) {
                Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_LONG).show();
            } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                Toast.makeText(this, getString(R.string.error_happened), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void launchMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    //--------------------
    //REST REQUEST
    //--------------------

    // http request that create user in firestore
    private void createUserInFirestore(){
        if (getCurrentUser()!=null){
            String urlPicture = (this.getCurrentUser().getPhotoUrl()!=null)? this.getCurrentUser().getPhotoUrl().toString() : null;
            String username = this.getCurrentUser().getDisplayName();
            String uid = this.getCurrentUser().getUid();
            String email = this.getCurrentUser().getEmail();
            String restoName = null;
            String restoId = null;
            Boolean like = false;

            UserHelper.createUser(uid, username, urlPicture, email,restoId, restoName, like).addOnFailureListener(this.onFailureListener());

        }
    }

    }
