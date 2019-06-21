package com.example.goforlunch.controler.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.example.goforlunch.R;
import com.example.goforlunch.model.Api.Firebase.UserHelper;
import com.example.goforlunch.utils.DataHolder;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {

    private static final int DELETE_USER_TASK = 20;
    private static final int UPDATE_USERNAME = 30;
    private static final int UPDATE_EMAIL = 40;
    private static final int SIGN_OUT_TASK = 10;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.update_name)
    EditText updateName;
    @BindView(R.id.update_email)
    EditText updateEmail;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.radius)
    EditText radius;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        this.configureToolbar();
        this.configureStatusBar();
        radius.setText(DataHolder.getInstance().getRadius());
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_setting;
    }

    private void configureToolbar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(R.string.title_toolbar_setting);
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    //-------------
    //ACTION
    //-------------

    @OnClick(R.id.delete_profile)
    public void onClickDeleteButton() {

        new AlertDialog.Builder(this)
                .setMessage("are you sure")
                .setPositiveButton("yes", (dialog, which) -> {
                    progressBar.setVisibility(View.VISIBLE);
                    deleteUserFromFirebase();
                })
                .setNegativeButton("no", null)
                .show();
    }

    @OnClick(R.id.update_button)
    public void onClickUpdateButton() {
        progressBar.setVisibility(View.VISIBLE);
        updateProfileInFirebase();

    }

    @OnClick(R.id.update_preferences)
    public void onClickUpdatePreferences() {
        updateRadius();
        Log.e("button", "butoon checked");
    }


    //--------------------
    //REST REQUEST
    //--------------------


    private void deleteUserFromFirebase() {
        if (this.getCurrentUser() != null) {
            // We also delete user from firestore storage
            UserHelper.deleteUser(this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener());

            AuthUI.getInstance()
                    .delete(this)
                    .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(DELETE_USER_TASK));
        }
    }

    private void updateProfileInFirebase() {
        String username = this.updateName.getText().toString();
        String email = this.updateEmail.getText().toString();

        if (this.getCurrentUser() != null) {
            if (!username.isEmpty()) {
                UserHelper.updateUser(username, this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener()).addOnSuccessListener(this.updateUIAfterRESTRequestsCompleted(UPDATE_USERNAME));
                Log.e("name", username);
            }
            if (this.getCurrentUser() != null) {
                if (!email.isEmpty()) {
                    UserHelper.updateEmail(email, this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener()).addOnSuccessListener(this.updateUIAfterRESTRequestsCompleted(UPDATE_EMAIL));
                }
            }
        }

    }

    private OnSuccessListener<? super Void> updateUIAfterRESTRequestsCompleted(final int origin) {
        return (OnSuccessListener<Void>) aVoid -> {
            switch (origin) {
                case DELETE_USER_TASK:
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(this, "your profile was deleted", Toast.LENGTH_LONG).show();
                    finish();
                    launchLoginActivity();
                    break;
                case UPDATE_USERNAME:
                    progressBar.setVisibility(View.INVISIBLE);
                    launchMainActivity();
                    break;
                case UPDATE_EMAIL:
                    progressBar.setVisibility(View.INVISIBLE);
                    launchMainActivity();
                    break;
            }
        };
    }

    private void updateRadius() {
        String rad = radius.getText().toString();
        DataHolder.getInstance().setRadius(rad);

    }

    private void launchLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void launchMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
