package com.example.m6frontend;


import android.app.Instrumentation;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.VerificationModes.times;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ViewEventTest {
    private UiDevice mUiDevice;

    @Rule
    public IntentsTestRule<MainActivity> intentsTestRule = new IntentsTestRule<>(MainActivity.class);

    @Before
    public void before() {
        mUiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }

    @Test
    public void ViewEventTest() throws UiObjectNotFoundException, InterruptedException {
        Espresso.onView(withId(R.id.sign_in_button)).perform(click());
        UiObject mText = mUiDevice.findObject(new UiSelector().text("kellywong48357@gmail.com"));
        mText.click();
        Thread.sleep(500);

        Espresso.onView(withId(R.id.find_events_button)).perform(click());
        Thread.sleep(500);

        Espresso.onView(withId(R.id.recyclerView)).perform(RecyclerViewActions.scrollToPosition(9));
        Thread.sleep(500);

        pressBack();
        Thread.sleep(500);

        Espresso.onView(withId(R.id.find_events_button)).perform(click());
        Thread.sleep(500);

        Espresso.onView(withId(R.id.recyclerView)).perform(RecyclerViewActions.scrollToPosition(9));
        Thread.sleep(500);

        Espresso.onView(withId(R.id.recyclerView)).perform(RecyclerViewActions.scrollToPosition(19));
        Thread.sleep(500);

        pressBack();
        Thread.sleep(500);

        Espresso.onView(withId(R.id.sign_out_button)).perform(click());

    }
}
