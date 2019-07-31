package com.example.goforlunch.controler.activities;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.goforlunch.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebViewRestaurant extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.web_view)
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);
        this.displayWebsite();
        this.configureToolBar();
    }

    private void configureToolBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Website");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
       // actionBar.setDisplayHomeAsUpEnabled(true);

    }

    private void displayWebsite() {
        String website = getIntent().getStringExtra("website");
        if (website!=null){
            webView.loadUrl(website);
            webView.setWebViewClient(new WebViewClient());
        }

    }

}
