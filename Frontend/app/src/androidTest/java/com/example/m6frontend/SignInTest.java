package com.example.m6frontend;


import android.os.AsyncTask;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.google.android.gms.auth.api.signin.internal.SignInHubActivity;

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
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.VerificationModes.times;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SignInTest {
    private UiDevice mUiDevice;


    @Rule
    public IntentsTestRule<MainActivity> intentsTestRule =
            new IntentsTestRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.ACCESS_COARSE_LOCATION");

    @Before
    public void before() {
        mUiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }

    @Test
    public void signInTest() throws UiObjectNotFoundException{
        waitAsync(1000);
        ViewInteraction gb = onView(allOf(withId(R.id.sign_in_button)));
        gb.perform(click());
        waitAsync(1000);
        mUiDevice.pressBack();


        gb.perform(click());
        waitAsync(1000);

        UiObject mText = mUiDevice.findObject(new UiSelector().text("kellywong48357@gmail.com"));
        mText.click();

        waitAsync(1000);
        pressBack();

        waitAsync(1000);
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.sign_out_button)));
        appCompatButton.perform(click());

        gb.perform(click());
        waitAsync(1000);

        mText.click();
        waitAsync(1000);

        appCompatButton.perform(click());

        intended(hasComponent(SignInHubActivity.class.getName()), times(3));
    }


    public static void waitAsync(long milliseconds) {
        try {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    return null;
                }
            }.get(milliseconds, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
