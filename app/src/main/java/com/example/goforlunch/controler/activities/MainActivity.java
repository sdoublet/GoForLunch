package com.example.goforlunch.controler.activities;


import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.goforlunch.R;
import com.example.goforlunch.controler.fragments.ChatFragment;
import com.example.goforlunch.controler.fragments.MapFragment;
import com.example.goforlunch.controler.fragments.RestoListFragment;
import com.example.goforlunch.controler.fragments.WorkmatesFragment;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private static final int SIGN_OUT_TASK = 100;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout_activity_main)
    DrawerLayout drawerLayout;
    @BindView(R.id.bottom_view)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.nav_view)
    NavigationView navigationView;


    protected FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }


    // FOR DATA
    // Fragment identifier
    public static final int FRAGMENT_MAP = 0;
    public static final int FRAGMENT_LISTVIEW = 1;
    public static final int FRAGMENT_WORKMATES = 2;
    public static final int FRAGMENT_CHAT = 3;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        displayFragment(FRAGMENT_MAP);
        this.configureToolbar();
        this.configureDrawerLayout();
        this.configureBottomView();
        this.configureStatusBar();
        updateUIWhenCreating();
        navigationView.setNavigationItemSelectedListener(this);

    }

    //DISPLAY FRAGMENTS
    private void displayFragment(int fragmentIdentifier) {
        Fragment fragment = new Fragment();
        switch (fragmentIdentifier) {
            case MainActivity.FRAGMENT_MAP:
                fragment = MapFragment.newInstance();
                break;
            case MainActivity.FRAGMENT_LISTVIEW:
                fragment = RestoListFragment.newInstance();
                break;
            case MainActivity.FRAGMENT_WORKMATES:
                fragment = WorkmatesFragment.newInstance();
                break;
            case MainActivity.FRAGMENT_CHAT:
                fragment = ChatFragment.newInstance();
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_view, fragment);
        fragmentTransaction.commit();
    }

    // CONFIGURATION


    private void configureToolbar() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.title_toolbar));

    }

    private void configureStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
    }

    private void configureDrawerLayout() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();
        //updateUIWhenCreating();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else
            super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                this.signOutFromFirebase();
                break;
            case R.id.setting:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void configureBottomView() {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.map_view:
                    displayFragment(FRAGMENT_MAP);
                    break;
                case R.id.list_view:
                    displayFragment(FRAGMENT_LISTVIEW);
                    break;
                case R.id.workmates:
                    displayFragment(FRAGMENT_WORKMATES);
                    break;
                case R.id.chat:
                    displayFragment(FRAGMENT_CHAT);
                    break;
            }
            return true;
        });
    }


    //---------------------
    //REST REQUESTS
    //---------------------
    // Create http requests (SignOut & delete)
    private void signOutFromFirebase() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
    }


    // Create onCompleteListener called after tasks ended
    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin) {
        return aVoid -> {
            switch (origin) {
                case SIGN_OUT_TASK:
                    finish();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    break;

            }
        };
    }
    private void updateUIWhenCreating(){

        View headerView = navigationView.getHeaderView(0);
        ImageView imageViewProfile = headerView.findViewById(R.id.image_view_profile);
        TextView usernameProfile = headerView.findViewById(R.id.username_profile);
        TextView emailProfile = headerView.findViewById(R.id.email_profile);
        if (this.getCurrentUser()!=null){
            if (this.getCurrentUser().getPhotoUrl()!=null) {
                Glide.with(this)
                        .load(this.getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(imageViewProfile);
            }

            //Get email and username from Firebase
            String email = TextUtils.isEmpty(this.getCurrentUser().getEmail())?
                    getString(R.string.info_no_email_found): this.getCurrentUser().getEmail();

            String userName = TextUtils.isEmpty(this.getCurrentUser().getDisplayName())?
                    getString(R.string.info_no_username_found): this.getCurrentUser().getDisplayName();

            //Update navHeader with data
            usernameProfile.setText(userName);
            emailProfile.setText(email);

        }


    }
}


