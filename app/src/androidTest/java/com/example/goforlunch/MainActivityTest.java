package com.example.goforlunch;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static org.hamcrest.core.IsNot.not;

import com.example.goforlunch.controler.activities.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class MainActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void navigationDrawerOpen(){
       onView(withContentDescription(R.string.navigation_drawer_open)).perform(click());
       onView(withId(R.id.nav_view)).check(matches(isDisplayed()));
    }

    @Test
    public void navigationDrawerClose(){
        onView(withContentDescription(R.string.navigation_drawer_open)).perform(click());
        Espresso.pressBack();
        onView(withId(R.id.nav_view)).check(matches((not(isDisplayed()))));
    }

    @Test
    public void navigationClickItem(){
        onView(withId(R.id.drawer_layout_activity_main)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.setting));
    }

    @Test
    public void checkBottomViewItem(){
        onView(withId(R.id.list_view)).check(matches(isDisplayed()));
        onView(withId(R.id.chat)).perform(click());
        onView(withId(R.id.chat)).check(matches(isDisplayed()));
        onView(withId(R.id.map_view)).perform(click());
        onView(withId(R.id.workmates)).perform(click());
        onView(withId(R.id.workmates)).check(matches(isDisplayed()));

    }

}
