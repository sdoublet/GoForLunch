package com.example.goforlunch.controler.activities;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.goforlunch.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlaceDetailActivity extends AppCompatActivity {

    public static final String PLACEDETAILRESTO= "resto_place_id";
    public static final String PHOTO = "photo";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.floating_button)
    FloatingActionButton floatingButton;
    private String restoPlaceId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);
        ButterKnife.bind(this);
        restoPlaceId = getIntent().getStringExtra(PLACEDETAILRESTO);
       // photo = getIntent().getStringExtra("photo");


    }
    @OnClick(R.id.floating_button)
    public void OnclickFloatingActionButton(){
        if (!floatingButton.isActivated()){
            Drawable drawable = getResources().getDrawable(R.drawable.check_circle).mutate();
            drawable.setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_ATOP);
            Toast.makeText(getBaseContext(), "You choose this restaurant", Toast.LENGTH_SHORT).show();
            floatingButton.setImageDrawable(drawable);
            floatingButton.setActivated(true);
        }else {
            Drawable drawable = getResources().getDrawable(R.drawable.add_circle).mutate();
            Toast.makeText(getBaseContext(), "Make your choice", Toast.LENGTH_SHORT).show();
            floatingButton.setImageDrawable(drawable);
            floatingButton.setActivated(false);
        }
    }
    private void configureToolbar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(R.string.title_toolbar_setting);
        actionBar.setDisplayHomeAsUpEnabled(true);

    }
    // recupere mon intent
    // lance httprequest detail


}
