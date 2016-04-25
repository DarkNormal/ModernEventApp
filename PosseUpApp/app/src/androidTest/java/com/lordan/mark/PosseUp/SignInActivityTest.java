package com.lordan.mark.PosseUp;

import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.lordan.mark.PosseUp.UI.MainActivityGroup.MainActivity;
import com.lordan.mark.PosseUp.UI.SigninGroup.SigninActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Mark on 26/04/2016.
 */
@RunWith(AndroidJUnit4.class)
public class SignInActivityTest {
    @Rule
    public final ActivityTestRule<SigninActivity> main = new ActivityTestRule<>(SigninActivity.class);

    @Test
    public void launchMainActivity() {
        onView(withText("Posse Up")).check(ViewAssertions.matches(isDisplayed()));
    }
}