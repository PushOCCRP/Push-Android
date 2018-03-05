package com.pushapp.press;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.PerformException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.espresso.util.HumanReadables;
import android.support.test.espresso.util.TreeIterables;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.pushapp.press.ElapsedTimeIdlingResource;


import org.hamcrest.Matcher;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

//import tools.fastlane.screengrab.Screengrab;
//import tools.fastlane.screengrab.locale.LocaleTestRule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import com.pushapp.press.ScreengrabHelper;

import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class

BasicTest {
//    @ClassRule
//    public static final TestRule classRule = new LocaleTestRule();

    @Rule
    public final ActivityTestRule<HomeActivity> activityTestRule = new ActivityTestRule<>(HomeActivity.class);


    @Test
    public void takeScreenshot() throws Exception {
        //onView(withId(R.id.mHomeLayout)).perform(ViewActions.swipeDown());

        //onView(withId(R.id.mHomeLayout))
        //        .perform(withCustomConstraints(ViewActions.swipeDown(), ViewMatchers.isDisplayingAtLeast(10)));


        //
        // Make sure Espresso does not time out
        int waitingTime = 10000;
        IdlingPolicies.setMasterPolicyTimeout(waitingTime * 2, TimeUnit.MILLISECONDS);
        IdlingPolicies.setIdlingResourceTimeout(waitingTime * 2, TimeUnit.MILLISECONDS);

        // Now we wait
        IdlingResource idlingResource = new ElapsedTimeIdlingResource(waitingTime);
        Espresso.registerIdlingResources(idlingResource);

        // Stop and verify
        //Screengrab.screenshot("test");
        // Clean up
        Espresso.unregisterIdlingResources(idlingResource);
        //ScreengrabHelper.setDelay(10000);
        //ScreengrabHelper.delayScreenshot("mainactivity");
    }



}


