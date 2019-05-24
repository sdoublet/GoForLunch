package com.example.goforlunch.controler.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.goforlunch.R;
import com.example.goforlunch.model.Api.Firebase.UserHelper;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    private void startSignInActivity() {
        startActivityForResult(AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.GoogleBuilder().build(),
                                        new AuthUI.IdpConfig.EmailBuilder().build(),
                                        new AuthUI.IdpConfig.FacebookBuilder().build())
                        )
                        .setLogo(R.drawable.meal_v3_final)
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
                launchMainActivity();
                this.createUserInFirestore();
                // TODO: 07/05/2019   mettre intent vers mainactivity
            } else if (response == null) {
                Toast.makeText(this, "Authentificaiton annulée", Toast.LENGTH_LONG).show();
            } else if (Objects.requireNonNull(response.getError()).getErrorCode() == ErrorCodes.NO_NETWORK) {
                Toast.makeText(this, "Aucune connexion internet", Toast.LENGTH_LONG).show();
            } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                Toast.makeText(this, "Une érreur s'est produite", Toast.LENGTH_LONG).show();
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

    //   http request that create user in firestore
    private void createUserInFirestore(){
        if (getCurrentUser()!=null){
            String urlPicture = (this.getCurrentUser().getPhotoUrl()!=null)? this.getCurrentUser().getPhotoUrl().toString() : null;
            String username = this.getCurrentUser().getDisplayName();
            String uid = this.getCurrentUser().getUid();
            String email = this.getCurrentUser().getEmail();

            UserHelper.createUser(uid, username, urlPicture, email).addOnFailureListener(this.onFailureListener());
        }
    }

    }
