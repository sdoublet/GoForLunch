package com.example.goforlunch.controler.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.goforlunch.R;
import com.example.goforlunch.controler.fragments.ChatFragment;
import com.example.goforlunch.controler.fragments.MapFragment;
import com.example.goforlunch.controler.fragments.RestoListFragment;
import com.example.goforlunch.controler.fragments.WorkmatesFragment;
import com.example.goforlunch.views.viewPager.PageAdapter;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

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
    @BindView(R.id.nav_view)NavigationView navigationView;


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
        this.configureViewPager();
        this.configureToolbar();
        this.configureDrawerLayout();
        this.configureBottomView();
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
    private void configureViewPager() {
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new PageAdapter(getSupportFragmentManager()));
    }

    private void configureToolbar() {
        setSupportActionBar(toolbar);
    }

    private void configureDrawerLayout() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();
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
}


