package com.lordan.mark.PosseUp

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.lordan.mark.PosseUp.UI.SigninGroup.SigninActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/*
 * Created by Mark on 26/04/2016.
 */
@RunWith(AndroidJUnit4::class)
class SignInActivityTest {
    @Rule @JvmField val main = ActivityTestRule(SigninActivity::class.java)

    @Test
    fun launchMainActivity() {
        onView(withText("Posse Up")).check(ViewAssertions.matches(isDisplayed()))
    }
}