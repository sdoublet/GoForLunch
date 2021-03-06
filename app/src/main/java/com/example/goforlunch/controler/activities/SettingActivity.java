package com.example.goforlunch.controler.activities;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.goforlunch.R;
import com.example.goforlunch.model.Api.Firebase.UserHelper;
import com.example.goforlunch.notifications.AlertReceiver;
import com.example.goforlunch.utils.DataHolder;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {

    private static final int DELETE_USER_TASK = 20;
    private static final int UPDATE_USERNAME = 30;
    private static final int UPDATE_EMAIL = 40;
    public static final int ALARM_CODE = 100;
    public static final String RADIUS_PREF = "radiusPref";
    public static final String SHARE_PREF = "sharePref";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.update_name)
    EditText updateName;
    @BindView(R.id.update_email)
    EditText updateEmail;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.radius)
    EditText radiusEditText;
    @BindView(R.id.notification_switch)
    Switch notificationSwitch;
    @BindView(R.id.setting_photo)
    ImageView settingPhoto;

    SharedPreferences mSharePreferences;
    SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        this.configureToolbar();
        this.displayPhoto();
        this.configureStatusBar();
        this.onClickSwitch();
        mSharePreferences = getSharedPreferences(SHARE_PREF, MODE_PRIVATE);
        String radius = mSharePreferences.getString(RADIUS_PREF, "1000");
        radiusEditText.setText(radius);
        notificationSwitch.setChecked(mSharePreferences.getBoolean("notificationSwitch", false));
        if (notificationSwitch.isChecked()) {
            setCalendarTime();
        }



    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_setting;
    }

    //------------
    //UI
    //------------

    private void configureToolbar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(R.string.title_toolbar_setting);
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    private void displayPhoto(){
        if (getCurrentUser().getPhotoUrl()!=null){
            Glide.with(this)
                    .load(this.getCurrentUser().getPhotoUrl())
                    .into(settingPhoto);
        }
    }

    //-------------
    //ACTION
    //-------------

    // Delete user profile after verification message
    @OnClick(R.id.delete_profile)
    public void onClickDeleteButton() {

        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.delete_message))
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    progressBar.setVisibility(View.VISIBLE);
                    deleteUserFromFirebase();
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

    // Update profile only for Firebase but not for UI
    @OnClick(R.id.update_button)
    public void onClickUpdateButton() {
        progressBar.setVisibility(View.VISIBLE);
        updateProfileInFirebase();

    }

    // Update radius
    @OnClick(R.id.update_preferences)
    public void onClickUpdatePreferences() {
        updateRadius();

    }

    // Notification switch
    private void onClickSwitch(){
        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                SharedPreferences sharedPreferences = getSharedPreferences(SHARE_PREF, MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putBoolean("notificationSwitch", true);
                editor.apply();
                setCalendarTime();
            }else {
                SharedPreferences sharedPreferences = getSharedPreferences(SHARE_PREF, MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putBoolean("notificationSwitch", false);
                editor.apply();
                cancelAlarm();
            }
        });
    }

    //--------------------
    //REST REQUEST
    //--------------------

    private void deleteUserFromFirebase() {
        if (this.getCurrentUser() != null) {
            // We also delete user from Firestore storage
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
                    Toast.makeText(this, getString(R.string.delete_profile_msg), Toast.LENGTH_LONG).show();
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
        String rad = radiusEditText.getText().toString();
        DataHolder.getInstance().setRadius(rad);
        SharedPreferences sharedPreferences = getSharedPreferences(SHARE_PREF, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(RADIUS_PREF, rad);
        editor.apply();
        progressBar.setVisibility(View.VISIBLE);
        hideProgressBar();


    }


    private void launchLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void launchMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void hideProgressBar() {
        new Handler().postDelayed(() -> {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), "Apply changed", Toast.LENGTH_SHORT).show();
            finish();
        }, 2000);

    }

    //-----------------------------
    //SET ALARM FOR NOTIFICATION
    //-----------------------------

    //set alarm to 12h
    private void setCalendarTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 1);
        calendar.set(Calendar.SECOND, 30);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        setAlarm(calendar);
    }

    // set repeating interval day and RTC_WAKEUP mode - Intent AlertReceiver
    private void setAlarm(Calendar calendar) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), ALARM_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

    }

    private void cancelAlarm(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), ALARM_CODE, intent, 0);
        alarmManager.cancel(pendingIntent);
    }
}
