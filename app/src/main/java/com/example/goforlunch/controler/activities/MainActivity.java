package com.example.goforlunch.controler.activities;




import com.example.goforlunch.R;
import com.firebase.ui.auth.AuthUI;

import java.util.Arrays;

import butterknife.OnClick;


public class MainActivity extends BaseActivity {

    private static final int RC_SIGN_IN = 123;



    private void startSignInActivity(){
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

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_main;
    }
    @OnClick(R.id.button_connection_main_activity)
    public void onClickButtonConnection(){
        startSignInActivity();
    }
}
