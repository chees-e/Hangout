package com.example.m6frontend;


import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;


@LargeTest
@RunWith(AndroidJUnit4.class)
public class AddEventTest {
    private UiDevice mUiDevice;
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void before() {
        mUiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.ACCESS_COARSE_LOCATION");

    @Test
    public void addEventTest() throws UiObjectNotFoundException {
        waitAsync(1000);
        ViewInteraction gc = onView(
                allOf(withText("Sign In")));
        gc.perform(click());

        waitAsync(1000);
        UiObject mText = mUiDevice.findObject(new UiSelector().text("kellywong48357@gmail.com"));
        waitAsync(1000);
        mText.click();

        waitAsync(1000);
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.eventButton)));
        appCompatButton.perform(click());

        waitAsync(1000);
        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.add_event_button)));
        appCompatButton2.perform(click());

        waitAsync(1000);
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.editTextEvent)));
        appCompatEditText.perform(replaceText("test"), closeSoftKeyboard());

        waitAsync(1000);

        waitAsync(1000);
        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.add_event_button), withText("Add Event"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                11),
                        isDisplayed()));
        appCompatButton3.perform(click());

        waitAsync(1000);
        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.editTextLocation),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                5),
                        isDisplayed()));
        appCompatEditText2.perform(click());
        appCompatEditText2.perform(click());

        waitAsync(1000);
        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.places_autocomplete_search_bar)));
        appCompatEditText3.perform(replaceText("ubc"), closeSoftKeyboard());

        waitAsync(1000);
        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.places_autocomplete_list),
                        childAtPosition(
                                withId(R.id.places_autocomplete_content),
                                3)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        waitAsync(1000);
        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.add_event_button), withText("Add Event"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                11),
                        isDisplayed()));
        appCompatButton4.perform(click());

        waitAsync(1000);
        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.editTextEventDescription),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        appCompatEditText4.perform(replaceText("test"), closeSoftKeyboard());

        waitAsync(1000);


        waitAsync(1000);
        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.add_event_button), withText("Add Event"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                11),
                        isDisplayed()));
        appCompatButton5.perform(click());

        waitAsync(1000);
        ViewInteraction multiSpinner = onView(
                allOf(withId(R.id.addUsersSpinner),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                4),
                        isDisplayed()));
        multiSpinner.perform(click());

        waitAsync(1000);
        ViewInteraction appCompatButton6 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatButton6.perform(scrollTo(), click());

        waitAsync(1000);
        ViewInteraction appCompatButton7 = onView(
                allOf(withId(R.id.add_event_button), withText("Add Event"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                11),
                        isDisplayed()));
        appCompatButton7.perform(click());

        waitAsync(1000);
        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.editTextEndDate),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                12),
                        isDisplayed()));
        appCompatEditText5.perform(click());
        appCompatEditText5.perform(click());

        waitAsync(1000);
        ViewInteraction appCompatButton8 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatButton8.perform(scrollTo(), click());

        waitAsync(1000);
        ViewInteraction appCompatButton9 = onView(
                allOf(withId(R.id.add_event_button), withText("Add Event"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                11),
                        isDisplayed()));
        appCompatButton9.perform(click());

        waitAsync(1000);
        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.editTextEndTime),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText6.perform(click());
        appCompatEditText6.perform(click());

        waitAsync(1000);
        ViewInteraction appCompatButton10 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatButton10.perform(scrollTo(), click());

        waitAsync(1000);
        ViewInteraction appCompatEditText7 = onView(
                allOf(withId(R.id.editTextStartTime),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                14),
                        isDisplayed()));
        appCompatEditText7.perform(click());
        appCompatEditText7.perform(click());

        waitAsync(1000);
        ViewInteraction appCompatButton11 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatButton11.perform(scrollTo(), click());

        waitAsync(1000);
        ViewInteraction appCompatButton12 = onView(
                allOf(withId(R.id.add_event_button), withText("Add Event"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                11),
                        isDisplayed()));
        appCompatButton12.perform(click());

        waitAsync(1000);
        ViewInteraction appCompatEditText8 = onView(
                allOf(withId(R.id.editTextStartDate),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText8.perform(click());
        appCompatEditText8.perform(click());

        waitAsync(1000);
        ViewInteraction appCompatButton13 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatButton13.perform(scrollTo(), click());

        waitAsync(1000);
        ViewInteraction appCompatButton14 = onView(
                allOf(withId(R.id.add_event_button), withText("Add Event")));
        waitAsync(1000);
        appCompatButton14.perform(click());

        waitAsync(1000);
        ViewInteraction appCompatButton15 = onView(withId(R.id.sign_out_button));
        appCompatButton15.perform(click());
    }


    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }



public static void waitAsync(long milliseconds) {
    try {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                return null;
            }
        }.get(milliseconds, TimeUnit.MILLISECONDS);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
        e.printStackTrace();
    }
}
}

