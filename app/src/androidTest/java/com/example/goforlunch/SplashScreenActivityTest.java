package com.example.goforlunch;


import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import com.example.goforlunch.controler.activities.SplashScreenActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)

public class SplashScreenActivityTest {
    @Rule
    public ActivityTestRule<SplashScreenActivity> loginActivityTestActivityTestRule = new ActivityTestRule<>(SplashScreenActivity.class);

    @Test
    public void isDisplayed(){
        onView(withId(R.id.text_view_go4lunch)).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.image_view_go4lunch)).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.progress_bar_splash_screen)).check(matches(ViewMatchers.isDisplayed()));
    }
}
