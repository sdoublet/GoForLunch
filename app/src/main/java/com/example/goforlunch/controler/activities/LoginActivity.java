package com.example.goforlunch.controler.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.goforlunch.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        if (isCurrentUserLogged()){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }else {
            this.startSignInActivity();
        }
    }

    private static final int RC_SIGN_IN = 123;

    //------------------
    //UTILS
    //------------------
    protected FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }
    protected Boolean isCurrentUserLogged(){
        return (this.getCurrentUser()!=null);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    private void startSignInActivity() {
        startActivityForResult(AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.GoogleBuilder().build(),
                                        new AuthUI.IdpConfig.EmailBuilder().build(),
                                        new AuthUI.IdpConfig.FacebookBuilder().build())
                        )
                        .setLogo(R.drawable.avatar2)
                        .setIsSmartLockEnabled(false, true)
                        .build(),
                RC_SIGN_IN);
    }
    //show snack bar with a message

    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Authentification réussie", Toast.LENGTH_LONG).show();
                // TODO: 07/05/2019   mettre intent vers mainactivity
            } else if (response == null) {
                Toast.makeText(this, "Authentificaiton annulée", Toast.LENGTH_LONG).show();
            } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                Toast.makeText(this, "Aucune connexion internet", Toast.LENGTH_LONG).show();
            } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                Toast.makeText(this, "Une érreur s'est produite", Toast.LENGTH_LONG).show();
            }
        }
    }

}
