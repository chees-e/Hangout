package com.example.m6frontend;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
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
public class EventAddTest {
    private UiDevice mUiDevice;

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void before() {
        mUiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }

    @Test
    public void eventAddTest() throws UiObjectNotFoundException, InterruptedException {
        ViewInteraction gc = onView(
                allOf(withText("Sign In"),
                        childAtPosition(
                                allOf(withId(R.id.sign_in_button),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        gc.perform(click());

        UiObject mText = mUiDevice.findObject(new UiSelector().text("kellywong48357@gmail.com"));
        mText.click();

        Thread.sleep(500);
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.eventButton), withText("Add Event"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatButton.perform(click());
        Thread.sleep(500);

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.add_event_button), withText("Add Event"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                11),
                        isDisplayed()));
        appCompatButton2.perform(click());
        Thread.sleep(500);

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.editTextEvent),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("name"), closeSoftKeyboard());


        Thread.sleep(500);

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.add_event_button), withText("Add Event"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                11),
                        isDisplayed()));
        appCompatButton3.perform(click());
        Thread.sleep(500);

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.editTextLocation),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                5),
                        isDisplayed()));
        appCompatEditText2.perform(click());
        Thread.sleep(500);

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.editTextLocation),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                5),
                        isDisplayed()));
        appCompatEditText3.perform(click());
        Thread.sleep(500);

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.places_autocomplete_search_bar),
                        childAtPosition(
                                allOf(withId(R.id.places_autocomplete_search_bar_container),
                                        childAtPosition(
                                                withId(R.id.places_autocomplete_content),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatEditText4.perform(replaceText("ubc"), closeSoftKeyboard());
        Thread.sleep(500);

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.places_autocomplete_list),
                        childAtPosition(
                                withId(R.id.places_autocomplete_content),
                                3)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));
        Thread.sleep(500);


        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.editTextEventDescription),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        appCompatEditText5.perform(replaceText("desc"), closeSoftKeyboard());
        Thread.sleep(500);



        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.add_event_button), withText("Add Event"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                11),
                        isDisplayed()));
        appCompatButton4.perform(click());
        Thread.sleep(500);

        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.editTextAddUsers),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                4),
                        isDisplayed()));
        appCompatEditText6.perform(click());
        Thread.sleep(500);

        ViewInteraction appCompatEditText7 = onView(
                allOf(withId(R.id.editTextAddUsers),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                4),
                        isDisplayed()));
        appCompatEditText7.perform(replaceText("user"), closeSoftKeyboard());
        Thread.sleep(500);


        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.add_event_button), withText("Add Event"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                11),
                        isDisplayed()));
        appCompatButton5.perform(click());
        Thread.sleep(500);

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
        Thread.sleep(500);

        ViewInteraction appCompatButton6 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatButton6.perform(scrollTo(), click());
        Thread.sleep(500);

        ViewInteraction appCompatEditText9 = onView(
                allOf(withId(R.id.editTextStartTime),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                14),
                        isDisplayed()));
        appCompatEditText9.perform(click());
        appCompatEditText9.perform(click());
        Thread.sleep(500);

        ViewInteraction appCompatButton7 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatButton7.perform(scrollTo(), click());
        Thread.sleep(500);

        ViewInteraction appCompatEditText10 = onView(
                allOf(withId(R.id.editTextEndDate),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                12),
                        isDisplayed()));
        appCompatEditText10.perform(click());
        appCompatEditText10.perform(click());
        Thread.sleep(500);

        ViewInteraction appCompatButton8 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatButton8.perform(scrollTo(), click());
        Thread.sleep(500);

        ViewInteraction appCompatEditText11 = onView(
                allOf(withId(R.id.editTextEndTime),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText11.perform(click());
        appCompatEditText11.perform(click());
        Thread.sleep(500);

        ViewInteraction appCompatButton9 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatButton9.perform(scrollTo(), click());
        Thread.sleep(500);

        ViewInteraction appCompatButton10 = onView(
                allOf(withId(R.id.add_event_button), withText("Add Event"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                11),
                        isDisplayed()));
        appCompatButton10.perform(click());

        ViewInteraction appCompatButton11 = onView(
                allOf(withId(R.id.sign_out_button), withText("Sign out"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatButton11.perform(click());

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
}
